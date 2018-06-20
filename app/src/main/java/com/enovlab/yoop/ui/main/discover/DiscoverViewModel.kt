package com.enovlab.yoop.ui.main.discover

import com.enovlab.yoop.data.entity.FilterOptions
import com.enovlab.yoop.data.entity.FilterOptions.SaleState
import com.enovlab.yoop.data.entity.enums.MarketplaceType
import com.enovlab.yoop.data.entity.enums.OfferStatus
import com.enovlab.yoop.data.entity.event.Event
import com.enovlab.yoop.data.repository.EventsRepository
import com.enovlab.yoop.data.repository.FilterRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.ui.main.discover.adapter.DiscoverItem
import com.enovlab.yoop.utils.ext.plusAssign
import com.enovlab.yoop.utils.ext.toCompletable
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Created by mtosk on 3/8/2018.
 */
class DiscoverViewModel
@Inject constructor(private val repository: EventsRepository,
                    private val filterRepository: FilterRepository) : StateViewModel<DiscoverView>() {

    override fun start() {
        loadFilterAndEvents()
    }

    internal fun refresh() {
        loadFilterAndEvents(true)
    }

    internal fun clearFilter() {
        disposables += filterRepository.saveFilter(FilterOptions.empty()).subscribe({
            loadFilterAndEvents()
        }, { error ->
            loadFilteredEvents()
            Timber.e(error)
        })
    }

    private fun loadFilterAndEvents(refresh: Boolean = false) {
        disposables += filterRepository.filter().subscribe({ filter ->
            when {
                refresh -> refreshFilteredEvents(filter)
                else -> loadFilteredEvents(filter)
            }
            updateFilter(filter)
        }, { error ->
            when {
                refresh -> refreshFilteredEvents()
                else -> loadFilteredEvents()
            }
            updateFilter()
            Timber.e(error)
        })
    }

    private fun updateFilter(filter: FilterOptions? = null) {
        view?.showFilterActive(filter != null &&
                (filter.locationName != null || filter.saleState != SaleState.ALL))
        when {
            filter?.locationName == null -> view?.showAllEvents()
            else -> view?.showCityName(filter.locationName!!)
        }
        when (filter?.saleState) {
            SaleState.FIRST_ACCESS -> {
                view?.showFirstAccessTickets()
                view?.showSaleState(true)
            }
            SaleState.ON_SALE -> {
                view?.showOnSaleTickets()
                view?.showSaleState(true)
            }
            SaleState.ALL, null -> view?.showSaleState(false)
        }
    }

    private fun loadFilteredEvents(filter: FilterOptions? = null) {
        observeLocalEvents(filter)
        load { repository.loadDiscoverEvents(filter).toCompletable() }
    }

    private fun refreshFilteredEvents(filter: FilterOptions? = null) {
        observeLocalEvents(filter)
        refresh { repository.loadDiscoverEvents(filter).toCompletable() }
    }

    private fun observeLocalEvents(filter: FilterOptions? = null) {
        singleSubscription?.dispose()
        singleSubscription = repository.observeDiscoverEvents(filter)
            .observeOn(schedulers.disk)
            .map(::mapToDiscoverItems)
            .observeOn(schedulers.main)
            .subscribe({ items ->
                when {
                    items.isNotEmpty() -> {
                        view?.showEvents(items)
                        view?.showEmptyEvents(false)
                    }
                    else -> view?.showEmptyEvents(filter != null && !filter.isEmpty())
                }
            }, { error ->
                Timber.e(error)
            })
    }

    private fun mapToDiscoverItems(events: List<Event>): List<DiscoverItem> {
        val items = mutableListOf<DiscoverItem>()

        val currentDate = Date()

        events.forEach {
            val pills = mutableListOf<DiscoverItem.Pill>()

            val hasAssignedTokens = (it.tokenInfo != null && it.tokenInfo!!.isNotEmpty())
                || (it.assigneeTokenInfo != null && it.assigneeTokenInfo!!.isNotEmpty())

            if (hasAssignedTokens) {
                pills.add(DiscoverItem.Pill.GoingPill(it.userInfo?.photo))
            }

            var hasActivePills = false

            if (it.marketplaceInfo != null && it.marketplaceInfo!!.isNotEmpty()) {
                it.marketplaceInfo?.forEach { marketplace ->
                    if (currentDate <= marketplace.endDate) {
                        val offerGroups = marketplace.offerGroups?.filter {
                            it.offer != null && it.offer!!.offerStatus != OfferStatus.WON_TOKEN_ASSIGNED
                        }

                        if (offerGroups != null && offerGroups.isNotEmpty()) {
                            when (marketplace.type) {
                                MarketplaceType.DRAW -> {
                                    pills.add(DiscoverItem.Pill.ActiveListPill(it.userInfo?.photo))
                                }
                                MarketplaceType.AUCTION -> {
                                    val offer = offerGroups.sortedBy { it.offer!!.chance }.last().offer!! // worst offer
                                    pills.add(DiscoverItem.Pill.ActiveOnSalePill(offer.chance, it.userInfo?.photo))
                                }
                                else -> {
                                }
                            }
                            hasActivePills = true
                        }
                    }
                }
            }

            if (!hasActivePills) {
                when {
                    it.firstAccessEndDate != null && currentDate <= it.firstAccessEndDate -> {
                        pills.add(DiscoverItem.Pill.NormalListPill(it.minQualifyingPrice,
                            currencySign(it.currency!!), it.maxOfferGroupCountInActiveMarketplace ?: 0 > 1))
                    }
                    it.onSaleEndDate != null && currentDate <= it.onSaleEndDate -> {
                        pills.add(DiscoverItem.Pill.NormalOnSalePill(it.minQualifyingPrice, currencySign(it.currency!!)))
                    }
                }
            }

            if (!hasActivePills && it.earliestMarketplaceStartDateTime != null
                && currentDate <= it.earliestMarketplaceStartDateTime) {

                pills.add(DiscoverItem.Pill.NewPill)
            }

            items.add(DiscoverItem(it, pills))
        }

        return items
    }

    private fun currencySign(code: String): String {
        return Currency.getInstance(code).getSymbol(Locale.getDefault())
    }
}