package com.enovlab.yoop.ui.filter.search

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enovlab.yoop.R
import com.enovlab.yoop.data.entity.City
import com.enovlab.yoop.ui.filter.FilterFragment
import com.enovlab.yoop.ui.filter.search.adapter.SearchCityAdapter
import com.enovlab.yoop.ui.filter.search.adapter.SearchDecoration
import com.enovlab.yoop.utils.ext.hideKeyboard
import com.enovlab.yoop.utils.ext.requiresLocationPermission
import com.enovlab.yoop.utils.ext.showKeyboard
import com.enovlab.yoop.utils.ext.textChangeListener
import com.google.android.gms.common.api.ResolvableApiException
import kotlinx.android.synthetic.main.fragment_search_city.*

/**
 * Created by mtosk on 3/13/2018.
 */
class SearchCityFragment : FilterFragment<SearchCityView, SearchCityViewModel>(), SearchCityView {
    override val vmClass = SearchCityViewModel::class.java

    private val adapter: SearchCityAdapter by lazy { SearchCityAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_city, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clear.setOnClickListener {
            search.text = null
        }
        back.setOnClickListener {
            navigator.navigateBack.go(false)
        }

        adapter.listener = viewModel::citySelected
        search_list.adapter = adapter
        search_list.addItemDecoration(SearchDecoration(ContextCompat.getDrawable(context!!, R.drawable.decoration_divider_search)!!))

        search.textChangeListener(viewModel::searchCities)
        search.setOnFocusChangeListener({v, hf -> if(!hf) search.hideKeyboard()})
        search.requestFocus()
        search.showKeyboard()

        current_location.setOnClickListener {
            when {
                context!!.requiresLocationPermission() -> {
                    requestPermissions(arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION), RC_LOCATION_PERMISSION)
                }
                else -> viewModel.requestLocationSettings(context!!)
            }
        }
    }

    override fun showCities(cities: List<City>) {
        adapter.submitList(cities)
    }

    override fun showNoResults() {
        adapter.clear()
    }

    override fun showLoadingIndicator(active: Boolean) {
        adapter.isLoading = active
    }

    override fun showActionIndicator(active: Boolean) {

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == RC_LOCATION_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED } )) {
                viewModel.requestLocationSettings(context!!)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_LOCATION_RESOLUTION && resultCode == Activity.RESULT_OK) {
            showLocationSettingsSuccess()
        }
    }

    override fun showLocationsSettingsError(error: ResolvableApiException) {
        error.startResolutionForResult(activity!!, RC_LOCATION_RESOLUTION)
    }

    override fun showLocationSettingsSuccess() {
        viewModel.startLocationUpdates()
    }

    override fun showNoCityFound() {
        showSnackbar(search, getString(R.string.search_no_city_found))
    }

    override fun showFilterSaved() {
        navigator.navigateBack.go(false)
    }

    companion object {
        fun newInstance() = SearchCityFragment()

        private const val RC_LOCATION_PERMISSION = 732
        private const val RC_LOCATION_RESOLUTION = 733
    }
}