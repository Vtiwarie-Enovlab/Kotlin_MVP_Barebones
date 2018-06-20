package com.enovlab.yoop.data.repository

import com.enovlab.yoop.api.GoogleApiService
import com.enovlab.yoop.api.YoopService
import com.enovlab.yoop.api.response.maps.MapResults
import com.enovlab.yoop.data.dao.FilterDao
import com.enovlab.yoop.data.entity.City
import com.enovlab.yoop.data.entity.FilterOptions
import com.enovlab.yoop.data.manager.AppPreferences
import com.enovlab.yoop.utils.RxSchedulers
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by mtosk on 3/13/2018.
 */
class FilterRepository
@Inject constructor(private val yoopService: YoopService,
                    private val googleApiService: GoogleApiService,
                    private val filterDao: FilterDao,
                    private val preferences: AppPreferences,
                    private val schedulers: RxSchedulers) {

    fun filter(): Single<FilterOptions> {
        return filterDao.getFilterOptions()
            .subscribeOn(schedulers.disk)
            .observeOn(schedulers.main)
    }

    fun saveFilter(filterOptions: FilterOptions): Completable {
        return Completable.fromAction {
            when {
                filterOptions.isEmpty() -> filterDao.deleteFilter()
                else -> filterDao.saveFilterOptions(filterOptions)
            }
        }
            .subscribeOn(schedulers.disk)
            .observeOn(schedulers.main)
    }

    fun searchCities(query: String): Flowable<List<City>> {
        return filterDao.getCities(query)
            .subscribeOn(schedulers.disk)
            .observeOn(schedulers.main)
    }

    fun refreshCities(): Flowable<List<City>> {
        return when {
            requiresUpdate() -> yoopService.getCities()
                .subscribeOn(schedulers.network)
                .observeOn(schedulers.disk)
                .doOnNext {
                    filterDao.saveCities(it)
                    updateLastLoadDate()
                }
            else -> Flowable.empty()
        }
    }

    fun searchCity(latLng: String): Flowable<MapResults> {
        return googleApiService.geocodeAddress(latLng)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.disk)
    }

    private fun updateLastLoadDate() {
        preferences.citiesLoadDate = System.currentTimeMillis()
    }

    private fun requiresUpdate(): Boolean {
        val lastLoadDate = preferences.citiesLoadDate
        return lastLoadDate == 0L || System.currentTimeMillis() - lastLoadDate > UPDATE_INTERVAL
    }

    companion object {
        private val UPDATE_INTERVAL = TimeUnit.DAYS.toMillis(30)
    }
}