package com.enovlab.yoop.ui.main.mytickets.requested

import android.os.Bundle
import android.support.v7.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.main.MainFragment
import com.enovlab.yoop.ui.main.mytickets.requested.adapter.RequestedAdapter
import com.enovlab.yoop.ui.main.mytickets.requested.adapter.RequestedItem
import com.enovlab.yoop.ui.main.mytickets.requested.adapter.RequestedItemDecoration
import com.enovlab.yoop.utils.ext.dragScrollListener
import kotlinx.android.synthetic.main.fragment_my_tickets_requested.*
import kotlinx.android.synthetic.main.layout_my_tickets_requested_filters.*

class RequestedFragment : MainFragment<RequestedView, RequestedViewModel>(), RequestedView {
    override val viewModelOwner = ViewModelOwner.FRAGMENT
    override val vmClass = RequestedViewModel::class.java

    private val adapter by lazy { RequestedAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_tickets_requested, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.listener = { navigator.navigateToEventLanding.go(it.id) }
        adapter.editListener = {
            navigator.navigateToTransactionEdit.go(Triple(it.id, it.marketplaceType.name, it.offerGroupId))
        }
        adapter.detailsListener = {
            navigator.navigateToTransactionDetails.go(it.id to it.marketplaceType.name)
        }
        adapter.fixListener = {
            navigator.navigateToTransactionFixPayment.go(Triple(it.id, it.marketplaceType.name, it.offerGroupId))
        }
        adapter.claimListener = {
            navigator.navigateToTransactionClaimTickets.go(Triple(it.id, it.marketplaceType.name, it.offerGroupId))
        }
        adapter.archiveListener = { viewModel.archive(it) }
        adapter.setHasStableIds(true)
        requested_list.adapter = adapter
        requested_list.addItemDecoration(RequestedItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_small)))
        requested_list.dragScrollListener(::hideSnackbar)
        (requested_list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        filter_all.setOnClickListener { viewModel.filterAllClicked() }
        filter_list.setOnClickListener { viewModel.filterYoopListClicked() }
        filter_on_sale.setOnClickListener { viewModel.filterOnSaleClicked() }

        refresh_layout.setOnRefreshListener { viewModel.refresh() }

        requested_discover.setOnClickListener { navigator.navigateToDiscover.go() }
    }

    override fun showFilters(active: Boolean) {
        container_filters_requested.isVisible = active
        container_filters_requested.setExpanded(active)
    }

    override fun showAllTickets(active: Boolean) {
        filter_all.isActivated = active
    }

    override fun showYoopListTickets(active: Boolean) {
        filter_list.isActivated = active
    }

    override fun showOnSaleTickets(active: Boolean) {
        filter_on_sale.isActivated = active
    }

    override fun showRequestedItems(items: List<RequestedItem>) {
        adapter.submitList(items)
    }

    override fun showEmptyEvents(active: Boolean) {
        container_empty_requested?.isVisible = active
        requested_list?.isVisible = !active
    }

    override fun showLoadingIndicator(active: Boolean) {
        adapter.isLoading = active
    }

    override fun showRefreshingIndicator(active: Boolean) {
        refresh_layout.isRefreshing = active
    }

    companion object {
        fun newInstance() = RequestedFragment()
    }
}