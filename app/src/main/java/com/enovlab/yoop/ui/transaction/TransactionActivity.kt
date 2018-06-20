package com.enovlab.yoop.ui.transaction

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import com.enovlab.yoop.R
import com.enovlab.yoop.ble.UserScanner
import com.enovlab.yoop.ui.auth.AuthActivity
import com.enovlab.yoop.ui.auth.AuthNavigator
import com.enovlab.yoop.ui.base.state.StateActivity
import com.enovlab.yoop.ui.main.MainNavigator
import com.enovlab.yoop.ui.main.mytickets.MyTicketsNavigation
import com.enovlab.yoop.ui.payments.PaymentsActivity
import com.enovlab.yoop.ui.profile.ProfileEditActivity
import com.enovlab.yoop.ui.profile.ProfileEditNavigator
import com.enovlab.yoop.ui.transaction.TransactionNavigator.Navigation
import com.enovlab.yoop.ui.transaction.confirmation.TransactionConfirmationFragment
import com.enovlab.yoop.ui.transaction.count.TransactionCountFragment
import com.enovlab.yoop.ui.transaction.details.TransactionDetailsFragment
import com.enovlab.yoop.ui.transaction.edit.TransactionEditFragment
import com.enovlab.yoop.ui.transaction.review.TransactionReviewFragment
import com.enovlab.yoop.ui.transaction.ticket.accept.TicketAcceptFragment
import com.enovlab.yoop.ui.transaction.ticket.details.TicketDetailsFragment
import com.enovlab.yoop.ui.transaction.ticket.transfer.TransferFragment
import com.enovlab.yoop.ui.transaction.ticket.transfer.TransferState
import com.enovlab.yoop.ui.widget.YoopSnackbar
import com.enovlab.yoop.utils.WeakHandler
import com.enovlab.yoop.utils.ext.loadImage
import com.enovlab.yoop.utils.ext.send
import kotlinx.android.synthetic.main.activity_transaction.*

class TransactionActivity : StateActivity<TransactionView, TransactionViewModel, TransactionNavigator>(),
    TransactionView {

    override val vmClass = TransactionViewModel::class.java
    override val navigatorClass = TransactionNavigator::class.java

    private lateinit var weakHandler: WeakHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        weakHandler = WeakHandler()

        val eventId = intent.getStringExtra(EXTRA_EVENT_ID)
        val type = intent.getStringExtra(EXTRA_MARKETPLACE_TYPE)
        val offerGroupId = intent.getStringExtra(EXTRA_OFFER_GROUP_ID)
        val navigation = intent.getStringExtra(EXTRA_NAVIGATION)?.let { Navigation.valueOf(it) }

        viewModel.id = eventId
        viewModel.type = type
        navigator.offerGroupId = offerGroupId

        supportFragmentManager.addOnBackStackChangedListener {
            val fragment = supportFragmentManager.findFragmentById(CONTAINER)
            if (fragment is TransactionCountFragment && navigator.editMode) {
                navigator.editMode = false

                weakHandler.post {
                    supportFragmentManager.beginTransaction()
                        .remove(fragment).commitNow()
                    supportFragmentManager.popBackStack()

                    if (navigator.offerGroupId != null) {
                        navigateToTransactionEdit(viewModel.id, viewModel.type!!, navigator.offerGroupId!!)
                    } else {
                        navigateBack(true)
                    }
                }
            }
        }

        when (navigation) {
            Navigation.EDIT -> navigateToTransactionEdit(eventId, type, offerGroupId)
            Navigation.COUNT -> navigateToTransactionCount(eventId, type, offerGroupId)
            Navigation.REVIEW_FIX_PAYMENT -> navigateToTransactionReview(
                TransactionNavigator.ReviewParams(eventId, type, offerGroupId, isFixPayment = true))
            Navigation.REVIEW_CLAIM_TICKETS-> navigateToTransactionReview(
                TransactionNavigator.ReviewParams(eventId, type, offerGroupId, isClaimTickets = true))
            Navigation.TICKET_DETAILS -> {
                val ticketId = intent.getStringExtra(EXTRA_TICKET_ID)
                navigateToTicketDetails(eventId, ticketId)
            }
            Navigation.ACCEPT_TOKEN -> {
                val assignmentToken = intent.getStringExtra(EXTRA_ASSIGNMENT_TOKEN)
                val email = intent.getStringExtra(EXTRA_EMAIL)
                navigateToTokenAccept(eventId, assignmentToken, email)
            }
            else -> navigateToTransactionDetails(eventId, type)
        }
    }

    override fun showBackgroundImage(url: String?) {
        transaction_background.loadImage(url)
//        transaction_background.loadImageBlurred(url)
    }

    override fun setupNavigation(navigator: TransactionNavigator) {
        navigator.navigateToTransactionDetails.observeNavigation { navigateToTransactionDetails(it.first, it.second) }
        navigator.navigateToTransactionCount.observeNavigation { navigateToTransactionCount(it.first, it.second, it.third) }
        navigator.navigateToTransactionEdit.observeNavigation { navigateToTransactionEdit(it.first, it.second, it.third) }
        navigator.navigateToShare.observeNavigation { navigateToShare(it) }
        navigator.navigateToTransactionReview.observeNavigation { navigateToTransactionReview(it) }
        navigator.navigateToWebUrl.observeNavigation { navigateToWebUrl(it) }
        navigator.navigateToLogin.observeNavigation { navigateToLogin() }
        navigator.navigateToSignup.observeNavigation { navigateToSignup() }
        navigator.navigateToConfirmation.observeNavigation { navigateToConfirmation(it) }
        navigator.navigateToInbox.observeNavigation { navigateToInbox() }
        navigator.navigateBack.observeNavigation {
            weakHandler.postDelayed({ navigateBack(it.first) }, it.second)
        }
        navigator.navigateToPayments.observeNavigation { navigateToPayments() }
        navigator.navigateToMyTicketsRequested.observeNavigation(::navigateToMyTicketsRequested)
        navigator.navigateToMyTicketsSecured.observeNavigation(::navigateToMyTicketsSecured)
        navigator.navigateToTicketDetails.observeNavigation {
            weakHandler.postDelayed({ navigateToTicketDetails(it.id, it.ticketId)  }, it.delay)
        }
        navigator.navigateToTicketTransferContacts.observeNavigation {
            navigateToTicketTransfer(it, TransferState.CONTACTS)
        }
        navigator.navigateToTicketTransferManual.observeNavigation {
            navigateToTicketTransfer(it, TransferState.EMAIL)
        }
        navigator.navigateToProfileCapture.observeNavigation(::navigateToProfileCapture)
        navigator.navigateToProfileCaptureIntro.observeNavigation(::navigateToProfileCaptureIntro)
        navigator.navigateToDiscover.observeNavigation(::navigateToDiscover)

        navigator.navigateToBluetoothSettings.observeNavigation(::navigateToBluetoothSettings)
        navigator.navigateToRequestPermissions.observeNavigation(::navigateToRequestPermissions)
    }

    private fun navigateToTransactionDetails(id: String, type: String) {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .replace(CONTAINER, TransactionDetailsFragment.newInstance(id, type))
            .commit()
    }

    private fun navigateToTransactionCount(id: String, type: String, offerGroupId: String) {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .replace(CONTAINER, TransactionCountFragment.newInstance(id, type, offerGroupId))
            .commit()
    }

    private fun navigateToTransactionEdit(id: String, type: String, offerGroupId: String) {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .replace(CONTAINER, TransactionEditFragment.newInstance(id, type, offerGroupId))
            .commit()
    }

    private fun navigateToTransactionReview(params: TransactionNavigator.ReviewParams) {
        weakHandler.postDelayed({
            supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(null)
                .replace(CONTAINER, TransactionReviewFragment.newInstance(
                    params.id, params.type, params.offerGroupId, params.ticketId,
                    params.count, params.amount, params.chanceToken,
                    params.isUpdate, params.isOverview,
                    params.isFixPayment, params.isClaimTickets))
                .commit()
        }, params.delay)
    }

    private fun navigateToWebUrl(url: String?) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .addDefaultShareMenuItem()
            .setToolbarColor(ContextCompat.getColor(this, R.color.color_white))
            .setShowTitle(true)
            .build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    private fun navigateToLogin() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.putExtra(AuthActivity.EXTRA_NAVIGATION, AuthNavigator.Navigation.LOGIN.name)
        startActivity(intent)
    }

    private fun navigateToSignup() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.putExtra(AuthActivity.EXTRA_NAVIGATION, AuthNavigator.Navigation.SIGNUP.name)
        startActivity(intent)
    }

    private fun navigateToConfirmation(params: TransactionNavigator.ConfirmationParams) {
        weakHandler.postDelayed({
            supportFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(CONTAINER, TransactionConfirmationFragment.newInstance(params.id, params.type, params.hasPaid))
                .commit()
        }, params.delay)
    }

    private fun navigateToShare(data: Pair<Uri, String>) {
        val intent = Intent()
            .setAction(Intent.ACTION_SEND)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            .setDataAndType(data.first, contentResolver.getType(data.first))
            .putExtra(Intent.EXTRA_STREAM, data.first)
            .putExtra(Intent.EXTRA_TEXT, data.second)
        startActivity(Intent.createChooser(intent, ""))
    }

    private fun navigateToPayments() {
        val intent = Intent(this, PaymentsActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToMyTicketsRequested(unit: Unit) {
        viewModel.saveNavigationPreference(MyTicketsNavigation.REQUESTED.name)
        finishAffinity()
    }

    private fun navigateToMyTicketsSecured(unit: Unit) {
        viewModel.saveNavigationPreference(MyTicketsNavigation.SECURED.name)
        finishAffinity()
    }

    private fun navigateToDiscover(unit: Unit) {
        viewModel.saveNavigationPreference(MainNavigator.Navigation.DISCOVER.name)
        finishAffinity()
    }

    private fun navigateToInbox() {
        val intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_EMAIL)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val errorMessage = getString(R.string.auth_verify_no_default_email)

        try {
            if(intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                YoopSnackbar.make(window.decorView).text(errorMessage).show()
            }
        } catch (e: ActivityNotFoundException) {
            YoopSnackbar.make(window.decorView).text(errorMessage).show()
        }
    }

    private fun navigateToTicketDetails(eventId: String, ticketId: String?) {
        // remove ticket assignment fragment from the stack
        val currentFragment = supportFragmentManager.findFragmentById(CONTAINER)
        if (currentFragment != null && currentFragment is TicketAcceptFragment) {
            supportFragmentManager.beginTransaction().remove(currentFragment).commitNow()
            supportFragmentManager.popBackStack()
        }

        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .replace(CONTAINER, TicketDetailsFragment.newInstance(eventId, ticketId))
            .commit()
    }

    private fun navigateToTicketTransfer(ticketId: String, state: TransferState) {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .replace(CONTAINER, TransferFragment.newInstance(ticketId, state))
            .commit()
    }

    private fun navigateToTokenAccept(eventId: String, assignmenToken: String, email: String?) {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .replace(CONTAINER, TicketAcceptFragment.newInstance(eventId, assignmenToken, email))
            .commit()
    }

    private fun navigateToProfileCapture(unit: Unit) {
        val intent = Intent(this, ProfileEditActivity::class.java)
        intent.putExtra(ProfileEditActivity.EXTRA_NAVIGATION, ProfileEditNavigator.Navigation.CAPTURE.name)
        startActivity(intent)
    }

    private fun navigateToProfileCaptureIntro(unit: Unit) {
        val intent = Intent(this, ProfileEditActivity::class.java)
        intent.putExtra(ProfileEditActivity.EXTRA_NAVIGATION, ProfileEditNavigator.Navigation.INTRO.name)
        startActivity(intent)
    }

    private fun navigateToBluetoothSettings(unit: Unit) {
        startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), RC_BLUETOOTH)
        viewModel.settingsRequested()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_BLUETOOTH) {
            viewModel.settingsResult()
        }
    }

    private fun navigateToRequestPermissions(unit: Unit) {
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION), RC_PERMISSIONS)
        viewModel.settingsRequested()
    }

    override fun showScanStateUpdated(state: UserScanner.State) {
        navigator.userScannerState.send(state)
    }

    override fun onBackPressed() {
        //disable back button for confirmation fragment
        val fragment = supportFragmentManager.findFragmentById(CONTAINER)
        when (fragment) {
            is TransferFragment -> fragment.onBackPressed()
            is TicketAcceptFragment -> fragment.onBackPressed()
            !is TransactionConfirmationFragment -> super.onBackPressed()
        }
    }

    companion object {
        const val EXTRA_EVENT_ID = "EXTRA_EVENT_ID"
        const val EXTRA_MARKETPLACE_TYPE = "EXTRA_MARKETPLACE_TYPE"
        const val EXTRA_OFFER_GROUP_ID = "EXTRA_OFFER_GROUP_ID"
        const val EXTRA_TICKET_ID = "EXTRA_TICKET_ID"
        const val EXTRA_ASSIGNMENT_TOKEN = "EXTRA_ASSIGNMENT_TOKEN"
        const val EXTRA_EMAIL = "EXTRA_EMAIL"
        const val EXTRA_NAVIGATION = "EXTRA_NAVIGATION"
        private const val RC_CONTACTS_PERMISSION = 763
        private const val RC_PERMISSIONS = 6342
        private const val RC_BLUETOOTH = 6341

        private const val CONTAINER = R.id.container_transaction
    }
}
