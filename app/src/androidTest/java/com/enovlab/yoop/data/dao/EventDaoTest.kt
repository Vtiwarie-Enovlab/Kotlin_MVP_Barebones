package com.enovlab.yoop.data.dao

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.enovlab.yoop.TestUtils
import com.enovlab.yoop.data.YoopDatabase
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Max Toskhoparan on 2/22/2018.
 */

@RunWith(AndroidJUnit4::class)
class EventDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: YoopDatabase

    @Before
    fun createDatabase() {
        database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), YoopDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun getDiscoverEventsWhenNoEventsSaved() {
        database.eventDao().getDiscoverEvents()
            .test()
            .assertValue {
                it.isEmpty()
            }
    }

    @Test
    fun saveAndGetDiscoverEvents() {
        val events = TestUtils.createEvents(12, discoverable = true)

        database.eventDao().saveDiscoverEvents(events)

        database.eventDao().getDiscoverEvents()
            .test()
            .assertValue { list ->
                list.size == events.size && list.all { query ->
                    query.event.discoverable == true && query.event.userActivity == null
                }
            }
    }

    @Test
    fun saveAndGetDiscoverEvents_verifyMarketplaceInfo() {
        val marketplaceInfoSize = 3
        val events = TestUtils.createEvents(8, discoverable = true)
        events.forEachIndexed { i, event -> event.marketplaceInfo = TestUtils.createMarketplaceInfo(marketplaceInfoSize, i) }

        database.eventDao().saveDiscoverEvents(events)

        database.eventDao().getDiscoverEvents()
            .test()
            .assertValue { list ->
                list.size == events.size && list.all { query ->
                    query.event.discoverable == true && query.event.userActivity == null
                        && query.marketplaceInfo?.size == marketplaceInfoSize
                        && query.marketplaceInfo?.all { info -> info.marketplaceInfo?.eventId == query.event.id } == true
                }
            }
    }

    @Test
    fun saveAndGetDiscoverEvents_verifyTokenInfo() {
        val tokenInfoSize = 2
        val events = TestUtils.createEvents(8, discoverable = true)
        events.forEachIndexed { i, event -> event.tokenInfo = TestUtils.createTokenInfo(tokenInfoSize, i) }

        database.eventDao().saveDiscoverEvents(events)

        database.eventDao().getDiscoverEvents()
            .test()
            .assertValue { list ->
                list.size == events.size && list.all { query ->
                    query.event.discoverable == true && query.event.userActivity == null
                        && query.tokenInfo?.size == tokenInfoSize
                        && query.tokenInfo?.all { info -> info.eventId == query.event.id } == true
                }
            }
    }

    @Test
    fun deleteAndGetUserEvents() {
        val tokenInfoSize = 2
        val marketplaceInfoSize = 3

        val events = TestUtils.createEvents(15, userActivity = true)
        events.forEachIndexed { i, event ->
            event.marketplaceInfo = TestUtils.createMarketplaceInfo(marketplaceInfoSize, i)
            event.tokenInfo = TestUtils.createTokenInfo(tokenInfoSize, i)
        }

        database.eventDao().saveUserEvents(events)

        database.eventDao().deleteUserEvents()

        database.eventDao().getUserEvents()
            .test()
            .assertValue {
                it.isEmpty()
            }
    }

    @Test
    fun updateAndGetUserEvents() {
        val tokenInfoSize = 2
        val marketplaceInfoSize = 3

        val events = TestUtils.createEvents(15, userActivity = true)
        events.forEachIndexed { i, event ->
            event.marketplaceInfo = TestUtils.createMarketplaceInfo(marketplaceInfoSize, i)
        }

        database.eventDao().saveUserEvents(events)

        database.eventDao().getUserEvents()
            .test()
            .assertValue { list ->
                list.size == events.size && list.all { query ->
                    query.event.userActivity == true && query.event.discoverable == null
                        && query.marketplaceInfo?.size == marketplaceInfoSize
                        && query.marketplaceInfo?.all { info -> info.marketplaceInfo?.eventId == query.event.id } == true
                }
            }

        val updateEvents = TestUtils.createEvents(7, userActivity = true)
        updateEvents.forEachIndexed { i, event ->
            event.tokenInfo = TestUtils.createTokenInfo(tokenInfoSize, i)
        }

        database.eventDao().saveUserEvents(updateEvents)

        database.eventDao().getUserEvents()
            .test()
            .assertValue { list ->
                list.size == updateEvents.size && list.all { query ->
                    query.event.userActivity == true && query.event.discoverable == null
                        && query.marketplaceInfo?.isEmpty() == true
                        && query.tokenInfo?.size == tokenInfoSize
                        && query.tokenInfo?.all { info -> info.eventId == query.event.id } == true
                }
            }
    }
}