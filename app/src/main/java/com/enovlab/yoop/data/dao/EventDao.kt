package com.enovlab.yoop.data.dao

import android.arch.persistence.room.*
import com.enovlab.yoop.data.entity.event.*
import com.enovlab.yoop.data.query.EventQuery
import com.enovlab.yoop.data.query.EventTicketsQuery
import io.reactivex.Flowable
import io.reactivex.Single
import java.util.*

/**
 * Created by Max Toskhoparan on 2/2/2018.
 */

@Dao
abstract class EventDao {

    @Transaction
    @Query("SELECT * FROM events WHERE discoverable = 1 ORDER BY date ASC")
    abstract fun getDiscoverEvents(): Flowable<List<EventQuery>>

    @Transaction
    @Query("SELECT * FROM events WHERE discoverable = 1 AND first_access_end_date is NOT NULL AND first_access_end_date >= :date ORDER BY date ASC")
    abstract fun getDiscoverFirstAccessEvents(date: Long = Date().time): Flowable<List<EventQuery>>

    @Transaction
    @Query("SELECT * FROM events WHERE discoverable = 1 AND on_sale_end_date is NOT NULL AND on_sale_end_date >= :date ORDER BY date ASC")
    abstract fun getDiscoverOnSaleEvents(date: Long = Date().time): Flowable<List<EventQuery>>

    @Transaction
    @Query("SELECT * FROM events WHERE user_activity = 1 ORDER BY date ASC")
    abstract fun getUserEvents(): Flowable<List<EventQuery>>

    @Transaction
    @Query("SELECT * FROM events WHERE id = :id")
    abstract fun getEvent(id: String): Flowable<EventQuery>

    @Transaction
    @Query("SELECT * FROM events WHERE id = :id")
    abstract fun getEventTickets(id: String): Flowable<EventTicketsQuery>

    @Query("DELETE FROM events WHERE discoverable = 1 AND (user_activity IS NULL OR user_activity = 0)")
    abstract fun deleteDiscoverEvents()

    @Query("SELECT * FROM performers INNER JOIN event_performers ON performers.id = event_performers.performer_id WHERE event_performers.event_id = :eventId")
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    abstract fun getPerformers(eventId: String): List<Performer>

    @Query("SELECT * FROM token_info WHERE id = :id")
    abstract fun getTokenInfo(id: String): Single<TokenInfo>

    @Transaction
    open fun saveDiscoverEvents(events: List<Event>) {
        syncEvents(events, SyncType.DISCOVERABLE)
    }

    @Transaction
    open fun saveUserEvents(events: List<Event>) {
        syncEvents(events, SyncType.USER_ACTIVITY)
    }

    @Transaction
    open fun saveEvent(event: Event) {
        syncEvent(event)
    }

    @Transaction
    open fun deleteOffer(id: String) {
        val offerGroup = getOfferGroup(id)
        offerGroup.offer = null
        saveOfferGroupInternal(offerGroup)
    }

    @Transaction
    open fun deleteUserEvents() {
        deleteUserEventsPerformersInternal(getUserEventsPerformersInternal())
        deleteUserEventsInternal()
    }

    @Query("SELECT * FROM offer_groups WHERE id = :id")
    protected abstract fun getOfferGroup(id: String): OfferGroup

    @Query("SELECT * FROM events WHERE id IN(:ids) AND discoverable = 1")
    protected abstract fun getRelevantDiscoverEvents(ids: List<String>): List<Event>

    @Query("SELECT * FROM events WHERE id IN(:ids) AND user_activity = 1")
    protected abstract fun getRelevantUserEvents(ids: List<String>): List<Event>

    @Query("SELECT * FROM events WHERE id = :id")
    protected abstract fun getEventSync(id: String): Event?

    @Query("DELETE FROM events WHERE id NOT IN(:ids) AND discoverable = 1 AND (user_activity IS NULL OR user_activity = 0)")
    protected abstract fun deleteIrrelevantDiscoverEvents(ids: List<String>)

    @Query("DELETE FROM events WHERE id NOT IN(:ids) AND user_activity = 1 AND (discoverable IS NULL OR discoverable = 0)")
    protected abstract fun deleteIrrelevantUserEvents(ids: List<String>)

    @Query("DELETE FROM event_performers WHERE event_id NOT IN(:ids)")
    protected abstract fun deleteIrrelevantEventPerformers(ids: List<String>)

    @Query("DELETE FROM events WHERE user_activity = 1")
    protected abstract fun deleteUserEventsInternal()

    @Query("SELECT * FROM event_performers INNER JOIN events ON events.id = event_performers.event_id WHERE events.user_activity = 1")
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    protected abstract fun getUserEventsPerformersInternal(): List<EventPerformer>

    @Delete
    protected abstract fun deleteUserEventsPerformersInternal(eventPerformers: List<EventPerformer>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun saveEventInternal(event: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun saveEventsInternal(events: List<Event>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun saveMarketplaceInfoInternal(marketplaceInfo: MarketplaceInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun saveOfferGroupInternal(offerGroup: OfferGroup)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun saveTokenInfoInternal(tokenInfo: TokenInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun saveMediaInternal(media: EventMedia)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun savePerformerInternal(performer: Performer)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun saveEventPerformerInternal(eventPerformer: EventPerformer)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract fun saveTimelineInternal(timeline: Timeline)

    private fun syncEvents(events: List<Event>, type: SyncType) {
        val ids = events.map { it.id }

        deleteIrrelevantEventPerformers(ids)
        when (type) {
            SyncType.DISCOVERABLE -> deleteIrrelevantDiscoverEvents(ids)
            SyncType.USER_ACTIVITY -> deleteIrrelevantUserEvents(ids)
        }

        val relevant = when (type) {
            SyncType.DISCOVERABLE -> getRelevantDiscoverEvents(ids)
            SyncType.USER_ACTIVITY -> getRelevantUserEvents(ids)
        }

        val updateDate = Date()
        events.forEach { event ->

            val cached = relevant.find { it.id == event.id }

            when (type) {
                SyncType.DISCOVERABLE -> {
                    event.discoverable = true
                    if (cached != null) event.userActivity = cached.userActivity
                }
                SyncType.USER_ACTIVITY -> {
                    event.userActivity = true
                    if (cached != null) event.discoverable = cached.discoverable
                }
            }

            if (cached?.firstAccessEndDate != null)
                event.firstAccessEndDate = cached.firstAccessEndDate
            if (cached?.onSaleEndDate != null)
                event.onSaleEndDate = cached.onSaleEndDate

            event.updateDate = updateDate
            saveEventAndRelations(event)
        }
    }

    private fun syncEvent(event: Event) {
        val cached = getEventSync(event.id)
        event.discoverable = cached?.discoverable
        event.userActivity = cached?.userActivity
        event.firstAccessEndDate = cached?.firstAccessEndDate
        event.onSaleEndDate = cached?.onSaleEndDate
        event.nextMarketplace = cached?.nextMarketplace

        event.updateDate = Date()
        saveEventAndRelations(event)
    }

    private fun saveEventAndRelations(event: Event) {
        saveEventInternal(event)

        event.marketplaceInfo?.forEach { marketplaceInfo ->
            marketplaceInfo.eventId = event.id
            saveMarketplaceInfoInternal(marketplaceInfo)

            marketplaceInfo.offerGroups?.forEach { offerGroup ->
                offerGroup.marketplaceId = marketplaceInfo.id
                saveOfferGroupInternal(offerGroup)
            }
        }

        saveTokenInfo(event.tokenInfo, event.id, false)
        saveTokenInfo(event.assigneeTokenInfo, event.id, true)

        event.media?.forEach { media ->
            media.eventId = event.id
            saveMediaInternal(media)
        }

        event.performers?.forEach { performer ->
            savePerformerInternal(performer)
            saveEventPerformerInternal(EventPerformer(event.id, performer.id))
        }

        event.timelines?.forEach { timeline ->
            timeline.eventId = event.id
            saveTimelineInternal(timeline)
        }
    }

    private fun saveTokenInfo(tokenInfo: List<TokenInfo>?, eventId: String, isAssignee: Boolean = false) {
        tokenInfo?.forEach { token ->
            token.eventId = eventId
            token.isAssignee = isAssignee
            saveTokenInfoInternal(token)
        }
    }

    private enum class SyncType {
        DISCOVERABLE, USER_ACTIVITY
    }
}