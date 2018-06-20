package com.enovlab.yoop.ui.filter.details

import com.enovlab.yoop.data.entity.FilterOptions
import com.enovlab.yoop.data.entity.FilterOptions.SaleState
import com.enovlab.yoop.data.repository.FilterRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.utils.ext.plusAssign
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by mtosk on 3/14/2018.
 */
class FilterDetailsViewModel
@Inject constructor(private val repository: FilterRepository) : StateViewModel<FilterDetailsView>() {

    private var filterOptions = FilterOptions.empty()

    override fun start() {
        disposables += repository.filter().subscribe({
            filterOptions = it

            view?.showRadius(filterOptions.searchRadius)
            showFilterLocations()
            showFilterTickets()
        }, { error ->
            view?.showRadius(filterOptions.searchRadius)
            showFilterLocations()
            showFilterTickets()

            Timber.e(error)
        })
    }

    internal fun allLocationsSelected() {
        filterOptions.apply {
            locationId = null
            locationName = null
            locationLatitude = null
            locationLongitude = null
            searchRadius = FilterOptions.DEFAULT_RADIUS
        }
        showFilterLocations()
        saveFilter()
    }

    internal fun allTicketsSelected() {
        filterOptions.saleState = SaleState.ALL
        showFilterTickets()
        saveFilter()
    }

    internal fun firstAccessTicketsSelected() {
        filterOptions.saleState = SaleState.FIRST_ACCESS
        showFilterTickets()
        saveFilter()
    }

    internal fun onSaleTicketsSelected() {
        filterOptions.saleState = SaleState.ON_SALE
        showFilterTickets()
        saveFilter()
    }

    internal fun searchRadiusChanged(radius: Int) {
        filterOptions.searchRadius = radius
        saveFilter()
    }

    private fun showFilterLocations() {
        view?.showLocationName(filterOptions.locationName)
        view?.showAllLocations(filterOptions.locationName == null)
        view?.showSpecificLocation(filterOptions.locationName != null)
    }

    private fun showFilterTickets() {
        view?.showAllTickets(filterOptions.saleState == SaleState.ALL)
        view?.showFirstAccessTickets(filterOptions.saleState == SaleState.FIRST_ACCESS)
        view?.showOnSaleTickets(filterOptions.saleState == SaleState.ON_SALE)
    }

    private fun saveFilter() {
        disposables += repository.saveFilter(filterOptions).subscribe({}, {
            Timber.e(it, "Error saving filter to local db.")
        })
    }
}