package com.enovlab.yoop.ui.main.discover

import android.os.Bundle
import android.support.v7.widget.SimpleItemAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.main.discover.adapter.DiscoverItem
import com.enovlab.yoop.ui.base.ViewModelOwner
import com.enovlab.yoop.ui.main.MainFragment
import com.enovlab.yoop.ui.main.discover.adapter.DiscoverAdapter
import com.enovlab.yoop.ui.main.discover.adapter.DiscoverItemDecoration
import com.enovlab.yoop.ui.widget.ScrollLinearLayoutManager
import com.enovlab.yoop.utils.ext.dragScrollListener
import kotlinx.android.synthetic.main.fragment_discover.*

/**
 * Created by mtosk on 3/8/2018.
 */
class DiscoverFragment : MainFragment<DiscoverView, DiscoverViewModel>(), DiscoverView {
    override val viewModelOwner = ViewModelOwner.FRAGMENT
    override val vmClass = DiscoverViewModel::class.java

    private val adapter: DiscoverAdapter by lazy { DiscoverAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_discover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter.listener = { navigator.navigateToEventLanding.go(it.event.id) }

        discover_list.layoutManager = ScrollLinearLayoutManager(context!!)
        discover_list.adapter = adapter
        discover_list.addItemDecoration(DiscoverItemDecoration(resources.getDimensionPixelSize(R.dimen.padding_extra_small)))
        discover_list.dragScrollListener(::hideSnackbar)
        (discover_list.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        refresh_layout.setOnRefreshListener { viewModel.refresh() }

        search_box.setOnClickListener { navigator.navigateToSearchEvents.go() }
        filter.setOnClickListener { navigator.navigateToFilter.go() }

        clear_filters.setOnClickListener { viewModel.clearFilter() }
    }

    override fun showEvents(events: List<DiscoverItem>) {
        adapter.submitList(events)
    }

    override fun showEmptyEvents(active: Boolean) {
        container_empty?.isVisible = active
        discover_list?.isVisible = !active
    }

    override fun showFilterActive(active: Boolean) {
        filter.isActivated = active
    }

    override fun showAllEvents() {
        headline.setText(R.string.discover_filter_all_events)
    }

    override fun showCityName(locationName: String) {
        headline.text = locationName
    }

    override fun showSaleState(active: Boolean) {
        filter_title.isVisible = active
    }

    override fun showFirstAccessTickets() {
        filter_title.setText(R.string.filter_first_access_tickets)
    }

    override fun showOnSaleTickets() {
        filter_title.setText(R.string.filter_on_sale_tickets)
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

    companion object {
        fun newInstance() = DiscoverFragment()
    }
}