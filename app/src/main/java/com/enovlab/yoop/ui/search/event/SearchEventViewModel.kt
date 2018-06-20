package com.enovlab.yoop.ui.search.event

import com.enovlab.yoop.data.repository.EventsRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.utils.ext.plusAssign
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by mtosk on 3/13/2018.
 */
class SearchEventViewModel
@Inject constructor(private val repository: EventsRepository) : StateViewModel<SearchEventView>() {

    internal var query: String? = null

    override fun start() {
        if (query != null && query!!.length >= MIN_VALID_CHARS)
            searchEvents(query!!)
    }

    internal fun searchEvents(query: String) {
        when {
            query.length >= MIN_VALID_CHARS -> {
                disposables += repository.searchEvents(query).subscribe({
                    when {
                        it.isNotEmpty() -> view?.showEventsSearch(it)
                        else -> view?.showNoResults()
                    }
                }, {
                    view?.showNoResults()
                    Timber.e(it, "Error searching events.")
                })
            }
            else -> view?.showNoResults()
        }
    }

    companion object {
        private const val MIN_VALID_CHARS = 3
    }
}