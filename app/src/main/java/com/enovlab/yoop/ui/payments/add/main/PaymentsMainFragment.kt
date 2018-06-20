package com.enovlab.yoop.ui.payments.add.main

import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.StackableFragment
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.payments.PaymentsFragment
import com.enovlab.yoop.ui.payments.add.billing.BillingDetailsFragment
import com.enovlab.yoop.ui.payments.add.billing.BillingDetailsViewModel
import com.enovlab.yoop.ui.payments.add.card.CardDetailsFragment
import com.enovlab.yoop.ui.payments.add.card.CardDetailsViewModel
import com.enovlab.yoop.ui.payments.add.main.PaymentsMainViewModel.ScanResult
import com.enovlab.yoop.ui.payments.add.main.PaymentsMainViewModel.Step
import com.enovlab.yoop.ui.widget.StatefulFloatingActionButton.State
import kotlinx.android.synthetic.main.fragment_payments_main.*
import kotlinx.android.synthetic.main.layout_payment_app_bar.*

/**
 * @author vishaan
 */
class PaymentsMainFragment : PaymentsFragment<PaymentsMainView, PaymentsMainViewModel>(), PaymentsMainView, StackableFragment {
    override val viewModelOwner = ViewModelOwner.ACTIVITY
    override val vmClass = PaymentsMainViewModel::class.java

    private val cardViewModel by lazy { obtainViewModel(CardDetailsViewModel::class.java) }
    private val billingViewModel by lazy { obtainViewModel(BillingDetailsViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        childFragmentManager.addOnBackStackChangedListener {
            viewModel.step = Step.valueOf(childFragmentManager.findFragmentById(CONTAINER).tag!!)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_payments_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        close.setOnClickListener { navigator.navigateBack.go(true to 0L) }
        back.setOnClickListener { navigator.navigateBack.go(true to 0L) }
        next_step.setOnClickListener { viewModel.nextStepClicked(next_step.state) }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.initialStep()
    }

    override fun showCardDetails() {
        showCardDetails(null)
    }

    override fun showCardDetails(scanResult: ScanResult?) {
        childFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, CardDetailsFragment.newInstance(scanResult), Step.CARD_DETAILS.name)
            .addToBackStack(null)
            .commit()
    }

    override fun showBillingDetails() {
        childFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(CONTAINER, BillingDetailsFragment.newInstance(), Step.BILLING_DETAILS.name)
            .addToBackStack(null)
            .commit()
    }

    override fun showNextStepEnabled(enabled: Boolean) {
        next_step.state = if (enabled) State.ENABLED else State.DISABLED
    }

    override fun showNextStepLoading(active: Boolean) {
        next_step.state = if (active) {
            State.LOADING
        } else State.ENABLED
    }

    override fun showNextStepLoadingSuccess() {
        next_step.state = State.SUCCESS
    }

    override fun showPaymentAdded() {
        navigator.navigateBack.go(true to 1500L)
    }

    override fun showCardValidateInput() {
    }

    override fun showBillingValidateInput() {
    }

    override fun showClearFocus() {
        billingViewModel.clearFocus()
    }

    override fun showAddPayment() {
        val country = billingViewModel.country?.code

        billingViewModel.addPayment(
            number(cardViewModel.cardNumber),
            billingViewModel.cardHolderName,
            cvv(cardViewModel.cvv),
            month(cardViewModel.expiryDate),
            year(cardViewModel.expiryDate),
            country,
            billingViewModel.zipCode,
            billingViewModel.isDefault
        )
    }

    fun cardScannedResult(scanResult: PaymentsMainViewModel.ScanResult) {
        val fragment = childFragmentManager.findFragmentById(CONTAINER)
        if (fragment != null) {
            if (fragment !is CardDetailsFragment) {
                viewModel.initialStep()
            }

            val cardNumber = scanResult.cardNumber ?: ""
            val expiryDate = scanResult.expiryDate ?: ""
            cardViewModel.resetView(cardNumber, expiryDate)
        }
    }

    override fun onBackPressed() {
        childFragmentManager.popBackStack()
    }

    companion object {
        private fun number(number: String) = number.replace(" ", "")
        private fun cvv(cvv: String) = cvv.trim({ it <= ' ' })
        private fun month(expiryDate: String) = expiryDate.substring(0, 2).toInt()
        private fun year(expiryDate: String) = 2000 + (expiryDate.substring(3, 5)).toInt()

        private const val CONTAINER = R.id.container_payments_main

        fun newInstance(): PaymentsMainFragment {
            return PaymentsMainFragment()
        }
    }

}