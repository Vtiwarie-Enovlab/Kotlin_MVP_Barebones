package com.enovlab.yoop.ui.transaction.review

import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.PaymentMethod
import com.enovlab.yoop.data.entity.enums.MarketplaceType
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.transaction.TransactionFragment
import com.enovlab.yoop.ui.transaction.TransactionNavigator
import com.enovlab.yoop.ui.transaction.review.adapter.PaymentsAdapter
import com.enovlab.yoop.ui.transaction.review.adapter.PaymentsItemDecoration
import com.enovlab.yoop.ui.widget.StatefulButton.State
import com.enovlab.yoop.utils.ext.applyToViews
import com.enovlab.yoop.utils.ext.listener
import kotlinx.android.synthetic.main.fragment_transaction_review.*
import kotlinx.android.synthetic.main.layout_edit_payments_sheet.*
import kotlinx.android.synthetic.main.layout_transaction_details_app_bar.*
import kotlinx.android.synthetic.main.layout_transaction_review_caution.*
import kotlinx.android.synthetic.main.layout_transaction_review_non_auth.*
import kotlinx.android.synthetic.main.layout_transaction_review_payments.*
import kotlinx.android.synthetic.main.layout_transaction_review_prices.*
import kotlinx.android.synthetic.main.layout_transaction_review_verify.*

class TransactionReviewFragment : TransactionFragment<TransactionReviewView, TransactionReviewViewModel>(),
        TransactionReviewView {

    override val vmClass = TransactionReviewViewModel::class.java
    override val viewModelOwner = ViewModelOwner.FRAGMENT

    private val paymentsAdapter by lazy { PaymentsAdapter() }
    private val paymentsDialog by lazy { BottomSheetDialog(context!!) }
    private val cautionDialog by lazy { BottomSheetDialog(context!!) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.id = arguments?.getString(ARG_EVENT_ID)!!
        viewModel.type = arguments?.getString(ARG_MARKETPLACE_TYPE)?.let { MarketplaceType.valueOf(it) }
        viewModel.offerGroupId = arguments?.getString(ARG_OFFER_GROUP_ID)
        viewModel.ticketId = arguments?.getString(ARG_TICKET_ID)
        viewModel.countSelected = arguments?.getInt(ARG_COUNT_SELECTED) ?: 0
        viewModel.amountEntered = arguments?.getInt(ARG_AMOUNT_ENTERED) ?: 0
        viewModel.chancesToken = arguments?.getString(ARG_CHANCE_TOKEN)
        viewModel.isUpdate = arguments?.getBoolean(ARG_IS_UPDATE) ?: false
        viewModel.isOverview = arguments?.getBoolean(ARG_IS_OVERVIEW) ?: false
        viewModel.isFixPayment = arguments?.getBoolean(ARG_IS_FIX_PAYMENT) ?: false
        viewModel.isClaimTickets = arguments?.getBoolean(ARG_IS_CLAIM_TICKETS) ?: false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaction_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        back.setOnClickListener { navigator.navigateBack.go(false to 0L) }
        share.isVisible = false

        login.setOnClickListener { navigator.navigateToLogin.go() }
        signup.setOnClickListener { navigator.navigateToSignup.go() }
        open_inbox.setOnClickListener { navigator.navigateToInbox.go() }
        resend_link.setOnClickListener { viewModel.resendLink() }

        transaction_review_submit.setOnClickListener { viewModel.submitRequest() }

        payments_autoprocess.setOnCheckedChangeListener { _, isChecked ->
            viewModel.autoPay = isChecked
        }

        legal.movementMethod = LinkMovementMethod.getInstance()

        paymentsDialog.setContentView(LayoutInflater.from(context!!)
            .inflate(R.layout.layout_edit_payments_sheet, null))
        paymentsAdapter.listener = {
            viewModel.selectPaymentClicked(it.id, paymentsAdapter.getItems())
        }
        paymentsDialog.payments_list.adapter = paymentsAdapter
        paymentsDialog.payments_list.addItemDecoration(PaymentsItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_small)))

        payments_card_details.setOnClickListener {
            paymentsDialog.show()
        }

        View.OnClickListener {
            navigator.navigateToPayments.go()
        }.applyToViews(add_payment, paymentsDialog.payments_add)

        transaction_edit.setOnClickListener { navigator.navigateBack.go(false to 0L) }
        transaction_cancel.setOnClickListener { navigator.navigateBack.go(true to 0L) }

        cautionDialog.setContentView(LayoutInflater.from(context!!)
            .inflate(R.layout.layout_transaction_review_caution, null))
        cautionDialog.caution_confirm.setOnClickListener { cautionDialog.hide() }

        claim_tickets.setOnClickListener { viewModel.claimTickets() }
        decline_tickets.setOnClickListener { viewModel.declineTickets() }
    }

    override fun showEventName(name: String?) {
        title.text = name
    }

    override fun showEventDateLocation(date: String?, location: String?) {
        event_location_date.text = "$date • $location"
    }

    override fun showDescription(count: Int, description: String?) {
        price_description.text = "$count $description"
    }

    override fun showHasOffer(active: Boolean) {
        group_transaction_edit.isVisible = active
    }

    override fun showUserHasOffer() {
        headline_edit.setText(R.string.transaction_review_edit_on_sale_headline)
        transaction_edit.setText(R.string.transaction_review_edit_on_sale)
    }

    override fun showUserHasRequest() {
        headline_edit.setText(R.string.transaction_review_edit_list_headline)
        transaction_edit.setText(R.string.transaction_review_edit_list)
    }

    override fun showReviewHeadline(active: Boolean) {
        headline.isVisible = active
    }

    override fun showPriceDetails(active: Boolean) {
        transaction_review_prices.isVisible = active
    }

    override fun showPaymentDetails(active: Boolean) {
        transaction_review_payments.isVisible = active
    }

    override fun showCheckout(active: Boolean) {
        transaction_review_submit.isVisible = active
    }

    override fun showNotVerified(active: Boolean) {
        transaction_review_verify.isVisible = active
    }

    override fun showAddPayment(active: Boolean) {
        add_payment.isVisible = active
    }

    override fun showNonAuthorized(active: Boolean) {
        transaction_review_non_auth.isVisible = active
    }

    override fun showListHeadline() {
        headline.setText(R.string.transaction_review_headline_list)
    }

    override fun showOnSaleHeadline() {
        headline.setText(R.string.transaction_review_headline_on_sale)
    }

    override fun showTicketPrice(currency: String, price: Double, count: Int) {
        price_per_ticket.text = getString(R.string.transaction_review_price, currency, price, count)
    }

    override fun showSubtotalPrice(currency: String, price: Double) {
        price_subtotal.text = getString(R.string.transaction_review_currency_price, currency, price)
    }

    override fun showTicketFee(currency: String, fee: Double, count: Int) {
        price_fee.text = getString(R.string.transaction_review_price, currency, fee, count)
    }

    override fun showTotalPrice(currency: String, price: Double) {
        price_total.text = getString(R.string.transaction_review_currency_price, currency, price)
    }

    override fun showChoseListDate(date: String?) {
        payment_choose_date.text = getString(R.string.transaction_review_chose_date, date)
    }

    override fun showChoseOnSaleDate(date: String?) {
        payment_choose_date.text = getString(R.string.transaction_review_chose_on_sale_date, date)
    }

    override fun showPaymentCard(active: Boolean) {
        payments_card_details.isVisible = active
        divider_payments.isVisible = active
    }

    override fun showAutoProcess(active: Boolean) {
        payments_autoprocess_title.isVisible = active
        payments_autoprocess_help.isVisible = active
        payments_autoprocess.isVisible = active
    }

    override fun showAutoProcessActive(active: Boolean) {
        payments_autoprocess.isEnabled = active
    }

    override fun showCardLastNumbers(lastNumbers: String) {
        payments_card_number.text = getString(R.string.transaction_review_card_numbers, lastNumbers)
    }

    override fun showCardPayAttemptFailed(active: Boolean) {
        payments_card_number.setTextColor(ContextCompat.getColor(context!!, when {
            active -> R.color.color_on_sale_chance_wont
            else -> R.color.color_white
        }))
    }

    override fun showCardTypeVisa() {
        payments_card_icon.setImageResource(R.drawable.icon_payment_card_visa)
    }

    override fun showCardTypeMasterCard() {
        payments_card_icon.setImageResource(R.drawable.icon_payment_card_mc)
    }

    override fun showAutoProcessEnabled(enabled: Boolean) {
        payments_autoprocess.isChecked = enabled
    }

    override fun showVerificationEmail(email: String) {
        email_title.text = getString(R.string.event_landing_email_message, email)
    }

    override fun showCheckoutList(update: Boolean) {
        transaction_review_submit.setText(getString(when {
            update -> R.string.event_landing_join_list_update
            else -> R.string.event_landing_join_list
        }))
    }

    override fun showCheckoutOnSale(update: Boolean) {
        transaction_review_submit.setText(getString(when {
            update -> R.string.event_landing_submit_offer_update
            else -> R.string.event_landing_submit_offer
        }))
    }

    override fun showActionIndicator(active: Boolean) {
        transaction_review_submit.state = if (active) State.LOADING else State.ENABLED
    }

    override fun showSuccessAction() {
        transaction_review_submit.state = State.SUCCESS
    }

    override fun showPayments(payments: List<PaymentMethod>) {
        paymentsAdapter.submitList(payments)
    }

    override fun showPaymentsDialog(active: Boolean) {
        when {
            active -> paymentsDialog.show()
            else -> paymentsDialog.hide()
        }
    }

    override fun showPaymentSelectingProgress(active: Boolean) {
        paymentsDialog.payments_group_list.isInvisible = active
        paymentsDialog.progress_payment_select.isVisible = active
    }

    override fun showPaymentSelectingError() {
        showError(getString(R.string.transaction_review_payment_select_error))
    }

    override fun showCautionDialog() {
        cautionDialog.show()
    }

    override fun showLimitExceedsOfferError(requested: Int, total: Int) {
        showError(getString(R.string.transaction_review_exceeds_on_sale, requested, total))
    }

    override fun showLimitExceedsRequestError(requested: Int, total: Int) {
        showError(getString(R.string.transaction_review_exceeds_list, requested, total))
    }

    override fun showTicketLimitExceeds() {
        price_description.setTextColor(ContextCompat.getColor(context!!, R.color.color_input_error))
    }

    override fun showConfirmation(id: String, type: String, hasPaid: Boolean) {
        navigator.navigateToConfirmation.go(TransactionNavigator.ConfirmationParams(id, type, hasPaid, 500L))
    }

    override fun showLegalLinks(termsUrl: String, privacyUrl: String) {
        legal.setText(createLinks(
            { navigator.navigateToWebUrl.go(termsUrl) },
            { navigator.navigateToWebUrl.go(privacyUrl) }), TextView.BufferType.SPANNABLE)
    }

    override fun setNavigationEdit() {
        navigator.editMode = true
    }

    override fun showClaimTickets(active: Boolean) {
        claim_tickets.isVisible = active
    }

    override fun showDeclineTickets(active: Boolean) {
        decline_tickets.isVisible = active
    }

    override fun showHeadlineSelected() {
        headline.setText(R.string.transaction_review_headline_selected)
    }

    override fun showHeadlineFix() {
        headline.setText(R.string.transaction_review_headline_fix)
    }

    override fun showClaimTicketsEnabled(enabled: Boolean) {
        claim_tickets.state = if (enabled) State.ENABLED else State.DISABLED
    }

    override fun showErrorCardExpired() {
        showError(getString(R.string.transaction_review_error_card_expired))
    }

    override fun showDeclinedPayment() {
        navigator.navigateBack.go(true to 0L)
    }

    override fun showPaymentFailed(retryAttempts: Int) {
        showError(getString(R.string.transaction_review_error_payment_failed, retryAttempts))
    }

    override fun showClaimProgress(active: Boolean) {
        claim_tickets.state = if (active) State.LOADING else State.ENABLED
    }

    override fun showClaimSuccess() {
        claim_tickets.state = State.SUCCESS
    }

    override fun showHeadlineReceipt() {
        headline.setText(R.string.transaction_review_headline_receipt)
    }

    override fun showPaymentEditable(editable: Boolean) {
        payments_card_details.isEnabled = editable
        divider_payments.isVisible = editable
        payments_card_arrow.isVisible = editable
    }

    override fun showPaymentProcessedDate(date: String) {
        payment_choose_date.text = getString(R.string.transaction_review_payment_processed, date)
    }

    private fun createLinks(termsOfUseListener: () -> Unit, privacyPolicyListener: () -> Unit): CharSequence {

        val termsOfService = getString(R.string.terms_of_service)
        val privacyPolicy = getString(R.string.privacy_policy)
        val amp = "&"

        val link = "$termsOfService $amp $privacyPolicy"

        val span = SpannableStringBuilder(link)

        val idxTerms = link.indexOf(termsOfService)
        span.listener(idxTerms, idxTerms + termsOfService.length, 0) { termsOfUseListener() }

        val idxPrivacy = link.indexOf(privacyPolicy)
        span.listener(idxPrivacy, idxPrivacy + privacyPolicy.length, 0) { privacyPolicyListener() }

        val idxAmp = link.indexOf(amp)
        span.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.color_white_alpha_50)),
                idxAmp, idxAmp + amp.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        return span
    }

    companion object {
        private const val ARG_OFFER_GROUP_ID = "ARG_OFFER_GROUP_ID"
        private const val ARG_TICKET_ID = "ARG_TICKET_ID"
        private const val ARG_COUNT_SELECTED = "ARG_COUNT_SELECTED"
        private const val ARG_AMOUNT_ENTERED = "ARG_AMOUNT_ENTERED"
        private const val ARG_CHANCE_TOKEN = "ARG_CHANCE_TOKEN"
        private const val ARG_IS_UPDATE = "ARG_IS_UPDATE"
        private const val ARG_IS_OVERVIEW = "ARG_IS_OVERVIEW"
        private const val ARG_IS_FIX_PAYMENT = "ARG_IS_FIX_PAYMENT"
        private const val ARG_IS_CLAIM_TICKETS = "ARG_IS_CLAIM_TICKETS"

        fun newInstance(id: String, type: String?, offerGroupId: String?, ticketId: String?,
                        count: Int?, amount: Int?, chanceToken: String?,
                        isUpdate: Boolean, isOverview: Boolean,
                        isFixPayment: Boolean, isClaimTickets: Boolean): TransactionReviewFragment {

            return TransactionReviewFragment().apply {
                arguments = Bundle(10).apply {
                    putString(ARG_EVENT_ID, id)
                    putString(ARG_MARKETPLACE_TYPE, type)
                    putString(ARG_OFFER_GROUP_ID, offerGroupId)
                    putString(ARG_TICKET_ID, ticketId)
                    putInt(ARG_COUNT_SELECTED, count ?: 0)
                    putInt(ARG_AMOUNT_ENTERED, amount ?: 0)
                    putString(ARG_CHANCE_TOKEN, chanceToken)
                    putBoolean(ARG_IS_UPDATE, isUpdate)
                    putBoolean(ARG_IS_OVERVIEW, isOverview)
                    putBoolean(ARG_IS_FIX_PAYMENT, isFixPayment)
                    putBoolean(ARG_IS_CLAIM_TICKETS, isClaimTickets)
                }
            }
        }
    }
}