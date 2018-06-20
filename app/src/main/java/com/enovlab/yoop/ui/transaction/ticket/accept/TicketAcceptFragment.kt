package com.enovlab.yoop.ui.transaction.ticket.accept

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
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.transaction.TransactionFragment
import com.enovlab.yoop.ui.transaction.TransactionNavigator
import com.enovlab.yoop.utils.ext.listener
import com.enovlab.yoop.utils.ext.loadImage
import kotlinx.android.synthetic.main.fragment_ticket_details_accept.*
import kotlinx.android.synthetic.main.layout_ticket_accept_app_bar.*
import kotlinx.android.synthetic.main.layout_ticket_accept_caution.*
import kotlinx.android.synthetic.main.layout_transaction_review_non_auth.*
import kotlinx.android.synthetic.main.layout_transaction_review_verify.*

class TicketAcceptFragment : TransactionFragment<TicketAcceptView, TicketAcceptViewModel>(), TicketAcceptView {
    override val viewModelOwner = ViewModelOwner.FRAGMENT
    override val vmClass = TicketAcceptViewModel::class.java

    private val cautionDialog by lazy { BottomSheetDialog(context!!) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.id = arguments?.getString(ARG_EVENT_ID)!!
        viewModel.assignmentToken = arguments?.getString(ARG_ASSIGNMENT_TOKEN)!!
        viewModel.email = arguments?.getString(ARG_EMAIL)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ticket_details_accept, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accept_close.setOnClickListener { viewModel.closeAssignment() }

        login.setOnClickListener { navigator.navigateToLogin.go() }
        signup.setOnClickListener { navigator.navigateToSignup.go() }

        email_title.isVisible = false
        open_inbox.setOnClickListener { navigator.navigateToInbox.go() }
        resend_link.setOnClickListener { viewModel.resendLink() }
        legal.movementMethod = LinkMovementMethod.getInstance()

        accept_my_ticket.setOnClickListener { viewModel.goToMyTicket() }
        accept_help.setOnClickListener {  }

        cautionDialog.setContentView(LayoutInflater.from(context!!)
            .inflate(R.layout.layout_ticket_accept_caution, null))
        cautionDialog.caution_nevermind.setOnClickListener { cautionDialog.dismiss() }
        cautionDialog.caution_confirm.setOnClickListener { viewModel.closeAssignmentConfirmed() }
    }

    override fun showEventName(name: String?) {
        accept_title.text = name
    }

    override fun showEventDateLocation(date: String?, location: String?) {
        accept_date_location.text = "$date • $location"
    }

    override fun showPerformerPicture(url: String?) {
        accept_image.loadImage(url)
    }

    override fun showClaimTicketHeadline() {
        accept_headline.setText(R.string.ticket_details_accept_claim_ticket)
    }

    override fun showTicketClaimedHeadline() {
        accept_headline.setText(R.string.ticket_details_accept_ticket_claimed)
    }

    override fun showEmailVerification(userEmail: String) {
        accept_caption.text = getString(R.string.auth_verify_email, userEmail)
    }

    override fun showLegalLinks(termsUrl: String, privacyUrl: String) {
        legal.setText(createLinks(
            { navigator.navigateToWebUrl.go(termsUrl) },
            { navigator.navigateToWebUrl.go(privacyUrl) }), TextView.BufferType.SPANNABLE)
    }

    override fun showStateTokenInvalid(active: Boolean) {
        accept_headline.setText(R.string.ticket_details_accept_not_valid)
        accept_close.isVisible = active
    }

    override fun showStateNotAuthorized(active: Boolean) {
        accept_headline.setText(R.string.ticket_details_accept_non_auth)
        accept_non_auth.isVisible = active
    }

    override fun showStateEmailVerification(active: Boolean) {
        accept_headline.setText(R.string.ticket_details_accept_verify)
        accept_caption.isVisible = active
        accept_non_verified.isVisible = active
    }

    override fun showStateTokensAssigned(active: Boolean) {
        accept_headline.setText(R.string.ticket_details_accept_ticket_exists)
        accept_caption.setText(R.string.ticket_details_accept_exists_caption)
        accept_my_ticket.isVisible = active
        accept_caption.isVisible = active
        accept_help.isVisible = active
    }

    override fun showTicketClaimed(eventId: String, delay: Long) {
        navigator.navigateToTicketDetails.go(TransactionNavigator.TicketDetailsParams(eventId, delay = delay))
    }

    override fun showCloseAssignmentDialog() {
        cautionDialog.show()
    }

    override fun showClosedAssignment() {
        navigator.navigateBack.go(true to 0L)
    }

    override fun showErrorNoConnection() {
        showSnackbarAction(getString(R.string.connection_error),
            getString(R.string.ticket_details_accept_try_again), viewModel::startAssignment)
    }

    override fun showActionIndicator(active: Boolean) {
        accept_progress.isVisible = active
    }

    fun onBackPressed() {
        viewModel.closeAssignment()
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
        private const val ARG_ASSIGNMENT_TOKEN = "ARG_ASSIGNMENT_TOKEN"
        private const val ARG_EMAIL = "ARG_EMAIL"

        fun newInstance(id: String, assignmentToken: String, email: String?) = TicketAcceptFragment().apply {
            arguments = Bundle(3).apply {
                putString(ARG_EVENT_ID, id)
                putString(ARG_ASSIGNMENT_TOKEN, assignmentToken)
                putString(ARG_EMAIL, email)
            }
        }
    }
}