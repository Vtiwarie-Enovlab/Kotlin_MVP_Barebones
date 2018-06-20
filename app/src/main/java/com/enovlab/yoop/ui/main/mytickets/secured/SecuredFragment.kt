package com.enovlab.yoop.ui.main.mytickets.secured

import android.os.Bundle
import android.support.v7.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.main.MainFragment
import com.enovlab.yoop.ui.main.mytickets.secured.adapter.SecuredAdapter
import com.enovlab.yoop.ui.main.mytickets.secured.adapter.SecuredItemDecoration
import com.enovlab.yoop.ui.main.mytickets.secured.adapter.SecuredTokens
import com.enovlab.yoop.utils.ext.dragScrollListener
import com.enovlab.yoop.utils.ext.loadImage
import kotlinx.android.synthetic.main.fragment_my_tickets_secured.*

class SecuredFragment : MainFragment<SecuredView, SecuredViewModel>(), SecuredView {
    override val viewModelOwner = ViewModelOwner.FRAGMENT
    override val vmClass = SecuredViewModel::class.java

    private val assignmentAdapter by lazy { SecuredAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_tickets_secured, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        secured_discover.setOnClickListener { navigator.navigateToDiscover.go() }

        secured_list.adapter = assignmentAdapter
        secured_list.addItemDecoration(SecuredItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_small)))
        secured_list.dragScrollListener(::hideSnackbar)
        (secured_list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        assignmentAdapter.createIdListener = { viewModel.createIdClicked() }
        assignmentAdapter.tokenClickedListener = { eventId, tokenItem -> viewModel.assignmentClicked(eventId, tokenItem.id) }
        assignmentAdapter.eventListener = { navigator.navigateToEventLanding.go(it) }
        assignmentAdapter.pendingListener = { navigator.navigateToEventLanding.go(it) }

        refresh_layout.setOnRefreshListener { viewModel.refresh() }
    }

    override fun showEmptyEvents(show: Boolean) {
        container_empty_secured.isVisible = show
    }

    override fun showSecuredItems(items: List<SecuredTokens>) {
        assignmentAdapter.submitList(items)
    }

    override fun showUnverifiedBanner(show: Boolean) {
        banner.isVisible = show
    }

    override fun showTicketDetails(eventId: String, ticketId: String) {
        navigator.navigateToTicketDetails.go(eventId to ticketId)
    }

    override fun showActionIndicator(active: Boolean) {
        assignmentAdapter.isLoading = active
    }

    override fun showRefreshingIndicator(active: Boolean) {
        refresh_layout.isRefreshing = active
    }

    override fun showProfileCapture() {
        navigator.navigateToProfileCapture.go()
    }

    override fun showProfileIntro() {
        navigator.navigateToProfileCaptureIntro.go()
    }

    override fun showProfilePic(url: String?) {
        profile_pic_secured.loadImage(url)
    }

    companion object {
        fun newInstance() = SecuredFragment()
    }
}