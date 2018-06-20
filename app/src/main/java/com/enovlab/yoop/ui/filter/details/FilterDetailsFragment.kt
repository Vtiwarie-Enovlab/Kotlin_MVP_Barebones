package com.enovlab.yoop.ui.filter.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.enovlab.yoop.R
import com.enovlab.yoop.ui.filter.FilterFragment
import com.enovlab.yoop.utils.ext.progressChangedListener
import kotlinx.android.synthetic.main.fragment_filter_details.*

/**
 * Created by mtosk on 3/14/2018.
 */
class FilterDetailsFragment : FilterFragment<FilterDetailsView, FilterDetailsViewModel>(), FilterDetailsView {
    override val vmClass = FilterDetailsViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_filter_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        all_locations.setOnClickListener { viewModel.allLocationsSelected() }
        spec_location.setOnClickListener { navigator.navigateToSearch.go() }
        radius_bar.progressChangedListener { viewModel.searchRadiusChanged(it) }

        all_tickets.setOnClickListener { viewModel.allTicketsSelected() }
        first_access_tickets.setOnClickListener { viewModel.firstAccessTicketsSelected() }
        on_sale_tickets.setOnClickListener { viewModel.onSaleTicketsSelected() }

        done.setOnClickListener { navigator.navigateBack.go(true) }
    }

    override fun showLocationName(name: String?) {
        spec_location.text = name ?: getString(R.string.filter_specific_location)
    }

    override fun showAllLocations(active: Boolean) {
        all_locations.isActivated = active
    }

    override fun showSpecificLocation(active: Boolean) {
        spec_location.isActivated = active
        container_radius_bar.isVisible = active
        radius_bar.isVisible = active
    }

    override fun showAllTickets(active: Boolean) {
        all_tickets.isActivated = active
    }

    override fun showFirstAccessTickets(active: Boolean) {
        first_access_tickets.isActivated = active
    }

    override fun showOnSaleTickets(active: Boolean) {
        on_sale_tickets.isActivated = active
    }

    override fun showRadius(radius: Int) {
        radius_bar.setProgress(radius.toFloat())
    }

    companion object {
        fun newInstance() = FilterDetailsFragment()
    }
}