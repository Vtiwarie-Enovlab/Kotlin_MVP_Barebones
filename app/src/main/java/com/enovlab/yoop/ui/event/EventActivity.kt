package com.enovlab.yoop.ui.event

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.support.v4.app.FragmentTransaction
import android.view.KeyEvent
import android.view.View
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.CalenderInfo
import com.enovlab.yoop.ui.base.BaseActivity
import com.enovlab.yoop.ui.event.landing.EventLandingFragment
import com.enovlab.yoop.ui.transaction.TransactionActivity
import com.enovlab.yoop.ui.transaction.TransactionNavigator
import com.enovlab.yoop.ui.widget.LoopImageView
import com.enovlab.yoop.utils.ext.createActivityOptions

class EventActivity : BaseActivity<EventNavigator>() {
    override val navigatorClass = EventNavigator::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        val eventId = intent.getStringExtra(ARG_EVENT_ID)
        when {
            eventId != null -> navigateToEventLanding(eventId)
            else -> navigateBack(true)
        }
    }

    override fun setupNavigation(navigator: EventNavigator) {
        navigator.navigateBack.observeNavigation(::navigateBack)
        navigator.navigateToDiscover.observeNavigation { navigateToDiscover() }
        navigator.navigateToEventLanding.observeNavigation { navigateToEventLanding(it) }
        navigator.navigateToCalendar.observeNavigation { navigateToCalendar(it) }
        navigator.navigateToShare.observeNavigation { navigateToShare(it) }
        navigator.navigateToTransactionDetails.observeNavigation { navigateToTransactionDetails(it.first, it.second) }
        navigator.navigateToTransactionEdit.observeNavigation { navigateToTransactionEdit(it.first, it.second, it.third) }
        navigator.navigateToGoogleMapsApp.observeNavigation { navigateToGoogleMapsApp(it) }
        navigator.navigateToTicketDetails.observeNavigation { navigateToTicketDetails(it.first, it.second) }
        navigator.navigateToTransactionFixPayment.observeNavigation {
            navigateToTransaction(it.first, it.second, it.third, TransactionNavigator.Navigation.REVIEW_FIX_PAYMENT)
        }
        navigator.navigateToTransactionClaimTickets.observeNavigation {
            navigateToTransaction(it.first, it.second, it.third, TransactionNavigator.Navigation.REVIEW_CLAIM_TICKETS)
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val fragment = supportFragmentManager.findFragmentById(CONTAINER)
        if (fragment is EventLandingFragment) fragment.dispatchKeyEvent(event)
        return super.dispatchKeyEvent(event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_ADD_CALENDAR) {
            val fragment = supportFragmentManager.findFragmentById(CONTAINER)
            if (fragment is EventLandingFragment) fragment.eventAddedToCalendar()
        }
    }

    private fun navigateToEventLanding(eventId: String) {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, EventLandingFragment.newInstance(eventId))
            .commit()
    }

    private fun navigateToCalendar(calenderInfo: CalenderInfo) {
        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calenderInfo.eventStartDate?.time ?: 0)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calenderInfo.eventEndDate?.time ?: 0)
            .putExtra(CalendarContract.Events.TITLE, calenderInfo.eventName)
            .putExtra(CalendarContract.Events.DESCRIPTION, calenderInfo.deepLinkUrl)
            .putExtra(CalendarContract.Events.EVENT_LOCATION, calenderInfo.eventLocation)

        if (packageManager.resolveActivity(intent, 0) != null) {
            startActivityForResult(intent, RC_ADD_CALENDAR)
        }
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

    private fun navigateToDiscover() {
        setResult(Activity.RESULT_OK)
        navigateBack(true)
    }

    private fun navigateToTransactionDetails(id: String, type: String) {
        val intent = Intent(this, TransactionActivity::class.java)
        intent.putExtra(TransactionActivity.EXTRA_EVENT_ID, id)
        intent.putExtra(TransactionActivity.EXTRA_MARKETPLACE_TYPE, type)
        startActivity(intent/*, createLoopTransitionAnimationOptions().toBundle()*/)
    }

    private fun navigateToTransactionEdit(id: String, type: String, offerGroupId: String) {
        val intent = Intent(this, TransactionActivity::class.java)
        intent.putExtra(TransactionActivity.EXTRA_EVENT_ID, id)
        intent.putExtra(TransactionActivity.EXTRA_MARKETPLACE_TYPE, type)
        intent.putExtra(TransactionActivity.EXTRA_OFFER_GROUP_ID, offerGroupId)
        intent.putExtra(TransactionActivity.EXTRA_NAVIGATION, TransactionNavigator.Navigation.EDIT.name)
        startActivity(intent/*, createLoopTransitionAnimationOptions().toBundle()*/)
    }

    private fun navigateToGoogleMapsApp(uri: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(uri)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun navigateToTicketDetails(id: String, ticketId: String) {
        val intent = Intent(this, TransactionActivity::class.java)
        intent.putExtra(TransactionActivity.EXTRA_EVENT_ID, id)
        intent.putExtra(TransactionActivity.EXTRA_TICKET_ID, ticketId)
        intent.putExtra(TransactionActivity.EXTRA_NAVIGATION, TransactionNavigator.Navigation.TICKET_DETAILS.name)
        startActivity(intent/*, createLoopTransitionAnimationOptions().toBundle()*/)
    }

    private fun navigateToTransaction(id: String, type: String, offerGroupId: String,
                                      navigation: TransactionNavigator.Navigation) {
        val intent = Intent(this, TransactionActivity::class.java)
        intent.putExtra(TransactionActivity.EXTRA_EVENT_ID, id)
        intent.putExtra(TransactionActivity.EXTRA_MARKETPLACE_TYPE, type)
        intent.putExtra(TransactionActivity.EXTRA_OFFER_GROUP_ID, offerGroupId)
        intent.putExtra(TransactionActivity.EXTRA_NAVIGATION, navigation.name)
        startActivity(intent)
    }

    private fun createLoopTransitionAnimationOptions(): ActivityOptions {
        val performerPicture = findViewById<LoopImageView>(R.id.picture_performer)
        val profilePicture = findViewById<LoopImageView>(R.id.picture_profile)
        val loopLine = findViewById<View>(R.id.loop_line)

        return createActivityOptions(
            performerPicture to getString(R.string.transition_name_performer_picture),
            profilePicture to getString(R.string.transition_name_profile_picture),
            loopLine to getString(R.string.transition_name_loop_line)
        )
    }

    companion object {
        const val ARG_EVENT_ID = "ARG_EVENT_ID"

        private const val CONTAINER = R.id.container_event
        private const val RC_ADD_CALENDAR = 643
    }
}
