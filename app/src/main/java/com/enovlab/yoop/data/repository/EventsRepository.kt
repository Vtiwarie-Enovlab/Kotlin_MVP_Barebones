package com.enovlab.yoop.data.repository

import com.enovlab.yoop.api.YoopService
import com.enovlab.yoop.api.request.FilterRequest
import com.enovlab.yoop.api.request.LocationFilterRequest
import com.enovlab.yoop.api.response.EventSearch
import com.enovlab.yoop.data.dao.EventDao
import com.enovlab.yoop.data.entity.FilterOptions
import com.enovlab.yoop.data.entity.FilterOptions.SaleState
import com.enovlab.yoop.data.entity.event.Event
import com.enovlab.yoop.data.ext.mapToUserInfo
import com.enovlab.yoop.data.manager.AppPreferences
import com.enovlab.yoop.data.manager.KeyStoreManager
import com.enovlab.yoop.data.query.EventQuery
import com.enovlab.yoop.data.query.EventTicketsQuery
import com.enovlab.yoop.utils.Flowables
import com.enovlab.yoop.utils.RxSchedulers
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Max Toskhoparan on 11/27/2017.
 */

class EventsRepository
@Inject constructor(private val yoopService: YoopService,
                    private val eventDao: EventDao,
                    private val preferences: AppPreferences,
                    private val schedulers: RxSchedulers,
                    private val keyStoreManager: KeyStoreManager) {

    /* Local database observables */

    fun observeDiscoverEvents(filter: FilterOptions?): Flowable<List<Event>> {
        return when (filter?.saleState) {
            SaleState.FIRST_ACCESS -> eventDao.getDiscoverFirstAccessEvents()
            SaleState.ON_SALE -> eventDao.getDiscoverOnSaleEvents()
            SaleState.ALL, null -> eventDao.getDiscoverEvents()
        }
            .subscribeOn(schedulers.disk)
            .observeOn(schedulers.disk)
            .map(::mapPerformers)
            .map { it.map(EventQuery::toEvent) }
            .observeOn(schedulers.main)
            .distinctUntilChanged()
    }

    fun observeUserEvents(): Flowable<List<Event>> {
        return eventDao.getUserEvents()
            .subscribeOn(schedulers.disk)
            .observeOn(schedulers.disk)
            .map(::mapPerformers)
            .map { it.map(EventQuery::toEvent) }
            .observeOn(schedulers.main)
            .distinctUntilChanged()
    }

    fun observeEvent(id: String): Flowable<Event> {
        return eventDao.getEvent(id)
            .subscribeOn(schedulers.disk)
            .observeOn(schedulers.disk)
            .map(::mapPerformer)
            .map(EventQuery::toEvent)
            .observeOn(schedulers.main)
            .distinctUntilChanged()
    }

    fun observeEventTickets(id: String): Flowable<Event> {
        return eventDao.getEventTickets(id)
            .subscribeOn(schedulers.disk)
            .observeOn(schedulers.disk)
            .map(EventTicketsQuery::toEvent)
            .map(::decryptEventKey)
            .observeOn(schedulers.main)
            .distinctUntilChanged()
    }

    /* API's refreshable */

    fun loadDiscoverEvents(filter: FilterOptions?): Flowable<List<Event>> {
        val filterRequest = filterRequest(filter)
        val discoverEventsSource = when {
            filterRequest != null -> yoopService.getEvents(filterRequest)
            else -> yoopService.getEvents()
        }

        val authorized = preferences.authToken != null
        return when {
            authorized -> Flowables.zip(discoverEventsSource, loadUserEventsInternal(), ::mergeEvents)
            else -> discoverEventsSource
        }
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.disk)
            .doOnNext {
                encryptEventKeys(it)
                eventDao.saveDiscoverEvents(it)
            }
    }

    fun loadEvent(id: String): Single<Event> {
        val authorized = preferences.authToken != null
        return when {
            authorized -> loadUserEventInternal(id)
            else -> yoopService.getEvent(id)
        }
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.disk)
            .doOnSuccess {
                encryptEventKey(it)
                eventDao.saveEvent(it)
            }
    }

    fun loadUserEvents(): Flowable<List<Event>> {
        return loadUserEventsInternal()
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.disk)
            .doOnNext {
                encryptEventKeys(it)
                eventDao.saveUserEvents(it)
            }
    }

    fun loadUserEvent(id: String): Single<Event> {
        return loadUserEventInternal(id)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.disk)
            .doOnSuccess {
                encryptEventKey(it)
                eventDao.saveEvent(it)
            }
    }

    fun searchEvents(query: String): Flowable<List<EventSearch>> {
        return yoopService.searchEvents(query)
            .subscribeOn(schedulers.network)
            .observeOn(schedulers.main)
    }

    private fun loadUserEventsInternal(): Flowable<List<Event>> {
        return yoopService.getUserEventsV2()
            .flatMap { userEvent ->
                Flowable.fromIterable(userEvent.events).map {
                    it.userInfo = userEvent.user.mapToUserInfo()
                    it
                }
            }.toList().toFlowable()
    }

    private fun loadUserEventInternal(id: String): Single<Event> {
        return yoopService.getUserEventV2(id)
            .map {
                it.event.userInfo = it.user.mapToUserInfo()
                it.event
            }
    }

    /* Mappers */

    private fun mergeEvents(events: List<Event>, userEvents: List<Event>): List<Event> {
        userEvents.forEach { userEvent ->
            val event = events.find { it.id == userEvent.id }
            event?.let {
                it.tokenInfo = userEvent.tokenInfo
                it.assigneeTokenInfo = userEvent.assigneeTokenInfo
                it.marketplaceInfo = userEvent.marketplaceInfo
                it.nextMarketplace = userEvent.nextMarketplace
                it.userInfo = userEvent.userInfo
                it.userActivity = true
            }
        }
        return events
    }

    private fun filterRequest(filter: FilterOptions?): FilterRequest? {
        return when {
            filter?.locationId != null || !(filter?.locationLatitude == null || filter.locationLongitude == null) -> {
                FilterRequest(LocationFilterRequest(filter.locationId,
                    filter.locationLatitude, filter.locationLongitude, filter.radiusInMiles()))
            }
            else -> null
        }
    }

    private fun mapPerformers(initial: List<EventQuery>): List<EventQuery> {
        initial.forEach { mapPerformer(it) }
        return initial
    }

    private fun mapPerformer(initial: EventQuery): EventQuery {
        initial.event.performers = eventDao.getPerformers(initial.event.id)
        return initial
    }

    private fun encryptEventKeys(events: List<Event>) {
        events.forEach(::encryptEventKey)
    }

    private fun encryptEventKey(event: Event) {
        if (event.userEventKey != null) {
            event.userEventKey = keyStoreManager.encryptKey(event.userEventKey!!)
        }
    }

    private fun decryptEventKey(event: Event): Event {
        if (event.userEventKey != null) {
            event.userEventKey = keyStoreManager.decryptKey(event.userEventKey!!)
        }
        return event
    }

}
