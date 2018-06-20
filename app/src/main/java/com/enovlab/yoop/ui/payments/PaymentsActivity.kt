package com.enovlab.yoop.ui.payments

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.AppCompatButton
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.updatePaddingRelative
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.BaseActivity
import com.enovlab.yoop.ui.base.StackableFragment
import com.enovlab.yoop.ui.payments.add.main.PaymentsMainFragment
import com.enovlab.yoop.ui.payments.add.main.PaymentsMainViewModel.ScanResult
import com.enovlab.yoop.ui.payments.edit.EditPaymentsFragment
import com.enovlab.yoop.ui.payments.manage.ManagePaymentsFragment
import com.enovlab.yoop.utils.WeakHandler
import com.enovlab.yoop.utils.ext.registerLifecycleCallbacks
import io.card.payment.CardIOActivity
import io.card.payment.CreditCard
import timber.log.Timber
import java.lang.ref.WeakReference

class PaymentsActivity : BaseActivity<PaymentsNavigator>() {
    override val navigatorClass = PaymentsNavigator::class.java

    private var callback: Application.ActivityLifecycleCallbacks? = null
    private var cardScanActivityRef: WeakReference<CardIOActivity>? = null
    private lateinit var weakHandler: WeakHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payments)

        weakHandler = WeakHandler()
        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.extras?.get(NAVIGATE_TO_PAYMENTS).let {
            when (it) {
                NAVIGATION.MANAGE -> navigateToManagePayments()
                else -> {
                    showScanCreditCard()
                    navigateToInitialPayments()
                }
            }
        }
    }

    override fun onDestroy() {
        if (callback != null) {
            application.unregisterActivityLifecycleCallbacks(callback)
            cardScanActivityRef?.clear()
        }
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SCAN_CARD) {
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                val scanResult = data.getParcelableExtra<CreditCard>(CardIOActivity.EXTRA_SCAN_RESULT)

                val cardNumber = scanResult.cardNumber
                val cardExpiryMonth = scanResult.expiryMonth
                val cardExpiryYear = scanResult.expiryYear

                val expiryDate = "${cardExpiryMonth}/${cardExpiryYear}"

                val fragment = supportFragmentManager.findFragmentById(CONTAINER)
                if (fragment is PaymentsMainFragment) {
                    fragment.cardScannedResult(ScanResult(cardNumber, expiryDate))
                }
            }
        }
    }

    override fun setupNavigation(navigator: PaymentsNavigator) {
        navigator.navigateBack.observeNavigation {
            weakHandler.postDelayed({ navigateBack(it.first) }, it.second)
        }
        navigator.navigateToEditPayment.observeNavigation { navigateToEditPayments(it) }
        navigator.navigateToAddPayment.observeNavigation {
            showScanCreditCard()
            navigateToInitialPayments()
        }
    }

    private fun navigateToInitialPayments() {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .replace(PaymentsActivity.CONTAINER, PaymentsMainFragment.newInstance())
            .commit()
    }

    private fun navigateToEditPayments(paymentMethodId: String) {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .replace(PaymentsActivity.CONTAINER, EditPaymentsFragment.newInstance(paymentMethodId))
            .commit()
    }

    private fun navigateToManagePayments() {
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .replace(PaymentsActivity.CONTAINER, ManagePaymentsFragment.newInstance())
            .commit()
    }

    private fun showScanCreditCard() {
        val intent = Intent(this, CardIOActivity::class.java).apply {
            putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true)
            putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY, true)
            putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false)
            putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, true)
            putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON, false)
            putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, true)
            putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, true)
        }

        startActivityForResult(intent, REQUEST_CODE_SCAN_CARD)

        callback = registerLifecycleCallbacks(onActivityStarted = {
            if (it is CardIOActivity) {
                cardScanActivityRef = WeakReference(it)

                try {
                    val mainLayoutField = it.javaClass.getDeclaredField("mMainLayout")
                    mainLayoutField.isAccessible = true

                    val mainLayout = mainLayoutField.get(it) as FrameLayout
                    modifyScannerLayout(mainLayout)

                    mainLayoutField.set(it, mainLayout)

                    mainLayoutField.isAccessible = false
                } catch (e: Exception) {
                    Timber.e(e, "Error accessing field via reflection.")
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            }
        })
    }

    private fun modifyScannerLayout(layout: FrameLayout) {
        val padding = resources.getDimensionPixelSize(R.dimen.padding_large)

        val manualInputContainerParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        manualInputContainerParams.gravity = Gravity.BOTTOM
        val manualInputContainer = FrameLayout(this)
        manualInputContainer.layoutParams = manualInputContainerParams
        manualInputContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        manualInputContainer.updatePaddingRelative(padding, padding * 2, padding, padding)

        val buttonParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        buttonParams.gravity = Gravity.CENTER
        val button = AppCompatButton(ContextThemeWrapper(this, R.style.Button_CardManualInput), null, 0)
        button.setText(R.string.payments_scanner_manual_input)
        button.layoutParams = buttonParams

        button.setOnClickListener {
            cardScanActivityRef?.get()?.finish()
        }

        manualInputContainer.addView(button)

        val headerContainerParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        headerContainerParams.gravity = Gravity.TOP
        val headerContainer = FrameLayout(this)
        headerContainer.layoutParams = headerContainerParams
        headerContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        headerContainer.updatePaddingRelative(padding, padding, padding, padding * 2)

        val headerParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        headerParams.gravity = Gravity.CENTER

        val header = TextView(this)
        header.layoutParams = headerParams
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            header.setTextAppearance(R.style.Text_Medium_Large_White)
        } else {
            header.setTextAppearance(this, R.style.Text_Medium_Large_White)
        }
        header.setAllCaps(true)
        header.setText(R.string.payments_add_card)

        val closeSize = resources.getDimensionPixelSize(R.dimen.close_add_card_scanner)
        val closeParams = FrameLayout.LayoutParams(closeSize, closeSize)
        closeParams.gravity = Gravity.END

        val close = ImageView(this)
        close.layoutParams = closeParams
        close.setImageResource(R.drawable.ic_close_white_32dp)

        close.setOnClickListener {
            cardScanActivityRef?.get()?.finish()
            navigateBack(true)
        }

        headerContainer.addView(header)
        headerContainer.addView(close)

        layout.addView(manualInputContainer)
        layout.addView(headerContainer)
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(CONTAINER)
        when (fragment) {
            is StackableFragment -> when {
                fragment.childFragmentManager.backStackEntryCount <= 1 -> super.onBackPressed()
                else -> fragment.onBackPressed()
            }
            else -> super.onBackPressed()
        }
    }

    companion object {
        private const val CONTAINER = R.id.container_payments_main
        private const val REQUEST_CODE_SCAN_CARD = 1111
        const val NAVIGATE_TO_PAYMENTS = "NAVIGATE_TO_PAYMENTS"
    }

    enum class NAVIGATION {
        EDIT,
        MANAGE,
        DEFAULT
    }
}
