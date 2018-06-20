package com.enovlab.yoop.ui.transaction

import com.enovlab.yoop.ble.UserScanner
import com.enovlab.yoop.data.repository.EventsRepository
import com.enovlab.yoop.ui.base.state.StateViewModel
import com.enovlab.yoop.utils.ext.plusAssign
import timber.log.Timber
import javax.inject.Inject

class TransactionViewModel
@Inject constructor(private val repository: EventsRepository,
                    private val userScanner: UserScanner) : StateViewModel<TransactionView>() {

    internal lateinit var id: String
    internal var type: String? = null

    override fun start() {
        disposables += repository.observeEvent(id).subscribe({ event ->
            view?.showBackgroundImage(event.defaultMedia?.url)
            userScanner.event = event
        }, {
            Timber.e(it, "Error loading event from local db.")
        })
        observe(userScanner.state, {view?.showScanStateUpdated(it)})
        startScanner()
    }

    override fun stop() {
        super.stop()
        stopScanner()
    }

    internal fun settingsRequested() {
        stopScanner()
    }

    internal fun settingsResult() {
        startScanner()
    }

    private fun startScanner() {
        userScanner.start()
    }

    private fun stopScanner() {
        userScanner.stop()
    }

    internal fun saveNavigationPreference(navigation: String) {
        preferences.navigation = navigation
    }
}