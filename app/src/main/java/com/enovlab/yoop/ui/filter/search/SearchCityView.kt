package com.enovlab.yoop.ui.filter.search

import com.enovlab.yoop.data.entity.City
import com.enovlab.yoop.ui.base.state.StateView
import com.google.android.gms.common.api.ResolvableApiException

/**
 * Created by mtosk on 3/13/2018.
 */
interface SearchCityView : StateView {
    fun showCities(cities: List<City>)
    fun showNoResults()
    fun showLocationsSettingsError(error: ResolvableApiException)
    fun showLocationSettingsSuccess()
    fun showNoCityFound()
    fun showFilterSaved()
}