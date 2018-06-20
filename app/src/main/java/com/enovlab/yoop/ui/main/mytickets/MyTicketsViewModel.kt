package com.enovlab.yoop.ui.main.mytickets

import com.enovlab.yoop.data.repository.NotificationsRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.utils.ext.plusAssign
import timber.log.Timber
import javax.inject.Inject
import com.google.android.exoplayer2.upstream.cache.CacheUtil.getKey



class MyTicketsViewModel
@Inject constructor(private val repository: NotificationsRepository) : StateViewModel<MyTicketsView>() {

    override fun start() {
        disposables += repository.observeUnreadNotifications().subscribe({
            view?.showNotificationsCountActive(it > 0)
            view?.showNotificationsCount(formatCount(it))
        }, { error ->
            Timber.e(error)
        })
    }

    private fun formatCount(value: Int): String {
        if (value < 1000) return value.toString() //deal with easy case

        val divideBy = 1000
        val suffix = "k"

        val truncated = value / (divideBy / 10) //the number part of the output times 10
        val hasDecimal = truncated < 100 && truncated / 10.0 != (truncated / 10).toDouble()
        return when {
            hasDecimal -> "${(truncated / 10.0)}$suffix"
            else -> "${(truncated / 10)}$suffix"
        }
    }
}