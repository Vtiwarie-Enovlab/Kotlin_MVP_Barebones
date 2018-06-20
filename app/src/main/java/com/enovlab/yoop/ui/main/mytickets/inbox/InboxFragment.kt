package com.enovlab.yoop.ui.main.mytickets.inbox

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.SimpleItemAnimator
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
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.main.MainFragment
import com.enovlab.yoop.ui.main.mytickets.inbox.adapter.inbox.InboxAdapter
import com.enovlab.yoop.ui.main.mytickets.inbox.adapter.inbox.InboxItemDecoration
import com.enovlab.yoop.utils.ext.listener
import kotlinx.android.synthetic.main.fragment_my_tickets_inbox.*
import kotlinx.android.synthetic.main.layout_transaction_review_non_auth.*

class InboxFragment : MainFragment<InboxView, InboxViewModel>(), InboxView {
    override val viewModelOwner = ViewModelOwner.FRAGMENT
    override val vmClass = InboxViewModel::class.java

    private val adapter by lazy { InboxAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_tickets_inbox, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refresh_layout.setOnRefreshListener { viewModel.refresh() }

        adapter.listenerEvent = { navigator.navigateToEventLanding.go(it.id) }
        adapter.listenerNotification = viewModel::notificationClicked
        adapter.listenerArchive = viewModel::archiveNotifications
        inbox_list.adapter = adapter
        inbox_list.addItemDecoration(InboxItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_small)))
        (inbox_list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        login.setOnClickListener { navigator.navigateToAuthLogin.go() }
        signup.setOnClickListener { navigator.navigateToAuthSignup.go() }
        legal.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun showNotAuthorized(active: Boolean) {
        inbox_nonauth.isInvisible = !active
    }

    override fun showNoNotifications(active: Boolean) {
        inbox_empty.isVisible = active
        inbox_list.isVisible = !active
    }

    override fun showNotifications(items: List<InboxItem>) {
        adapter.submitList(items)
    }

    override fun showRefreshEnabled(enabled: Boolean) {
        refresh_layout.isEnabled = enabled
    }

    override fun showLegalLinks(termsUrl: String, privacyUrl: String) {
        legal.setText(createLinks(
            { navigator.navigateToWebUrl.go(termsUrl) },
            { navigator.navigateToWebUrl.go(privacyUrl) }), TextView.BufferType.SPANNABLE)
    }

    override fun showNotificationDestination(deepLink: String) {
        navigator.navigateThroughDeepLink.go(deepLink)
    }

    override fun showLoadingIndicator(active: Boolean) {
        adapter.isLoading = active
    }

    override fun showRefreshingIndicator(active: Boolean) {
        refresh_layout.isRefreshing = active
    }

    override fun showErrorNoConnection() {
        showSnackbarAction(getString(R.string.connection_error),
            getString(R.string.connection_error_refresh), { viewModel.refresh() })
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
        fun newInstance() = InboxFragment()
    }
}