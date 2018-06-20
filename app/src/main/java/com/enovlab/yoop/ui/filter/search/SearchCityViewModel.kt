package com.enovlab.yoop.ui.filter.search

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentSender
import android.location.Location
import com.enovlab.yoop.data.entity.City
import com.enovlab.yoop.data.entity.FilterOptions
import com.enovlab.yoop.data.repository.FilterRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.utils.ext.plusAssign
import com.enovlab.yoop.utils.ext.toCompletable
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by mtosk on 3/13/2018.
 */
class SearchCityViewModel
@Inject constructor(private val repository: FilterRepository,
                    context: Context) : StateViewModel<SearchCityView>() {

    private val locationClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationRequest: LocationRequest by lazy {
        LocationRequest().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }
    private val locationCallback: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                if (result != null && result.locations.isNotEmpty()) {
                    searchCity(result.locations.first())
                }
            }
        }
    }

    override fun start() {
        load {
            repository.refreshCities().toCompletable()
        }
    }

    override fun stop() {
        super.stop()
        removeLocationUpdates()
    }

    internal fun searchCities(query: String) {
        when {
            query.length >= MIN_VALID_CHARS -> {
                disposables += repository.searchCities(query).subscribe({
                    when {
                        it.isNotEmpty() -> view?.showCities(it)
                        else -> view?.showNoResults()
                    }
                }, {
                    view?.showNoResults()
                    Timber.e(it, "Error loading searchCities from local db.")
                })
            }
            else -> view?.showNoResults()
        }
    }

    internal fun requestLocationSettings(context: Context) {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            view?.showLocationSettingsSuccess()
        }

        task.addOnFailureListener { error ->
            if (error is ResolvableApiException){
                try {
                    view?.showLocationsSettingsError(error)
                } catch (e: IntentSender.SendIntentException) {
                    Timber.e(e)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    internal fun startLocationUpdates() {
        locationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    internal fun searchCity(location: Location) {
        action {
            repository.searchCity("${location.latitude},${location.longitude}")
                .doOnNext {
                    if (it.results != null && it.results.isNotEmpty()) {
                        val result = it.results.first()
                        val adminArea = result.addressComponents.filter { it.types.contains(TYPE_ADMIN_AREA) }
                        val locality = result.addressComponents.filter { it.types.contains(TYPE_LOCALITY) }

                        var name = ""

                        when {
                            adminArea.isNotEmpty() && locality.isNotEmpty() -> {
                                val localityName = locality.first().longName
                                val stateName = adminArea.first().shortName
                                name = "$localityName, $stateName"
                            }
                            adminArea.isEmpty() && locality.isNotEmpty() -> {
                                name = locality.first().longName
                            }
                            adminArea.isNotEmpty() && locality.isEmpty() -> {
                                val state = adminArea.first()
                                name = "${state.longName}, ${state.shortName}"
                            }
                            else -> view?.showNoCityFound()
                        }

                        if (name.isNotEmpty())
                            saveFilter(name, latitude = location.latitude, longitude = location.longitude)

                    } else {
                        view?.showNoCityFound()
                    }
                    removeLocationUpdates()
                }.doOnError {
                    removeLocationUpdates()
                }
                .toCompletable()
        }
    }

    internal fun citySelected(city: City) {
        saveFilter("${city.name!!}, ${city.regionAbbreviation!!}", city.id)
    }

    private fun saveFilter(name: String, id: String? = null, latitude: Double? = null, longitude: Double? = null) {
        disposables += repository.filter().subscribe({ filter ->
            filter.apply {
                locationName = name
                locationId = id
                locationLatitude = latitude
                locationLongitude = longitude
                searchRadius = FilterOptions.DEFAULT_RADIUS
            }
            saveFilter(filter)
        }, { error ->
            val filter = FilterOptions.empty().apply {
                locationName = name
                locationId = id
                locationLatitude = latitude
                locationLongitude = longitude
            }
            saveFilter(filter)
            Timber.e(error)
        })
    }

    private fun saveFilter(filterOptions: FilterOptions) {
        disposables += repository.saveFilter(filterOptions).subscribe({
            view?.showFilterSaved()
        }, {
            Timber.e(it, "Error saving filter to local db.")
        })
    }

    private fun removeLocationUpdates() {
        locationClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        private const val MIN_VALID_CHARS = 2
        private const val TYPE_ADMIN_AREA = "administrative_area_level_1"
        private const val TYPE_LOCALITY = "locality"
    }
}