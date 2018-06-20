package com.enovlab.yoop.ble

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.enovlab.yoop.data.entity.event.Event
import com.enovlab.yoop.utils.ext.requiresLocationPermission
import com.enovlab.yoop.utils.ext.send
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

class UserScanner
@Inject constructor(private val bluetoothAdapter: BluetoothAdapter,
                    private val advertiser: Advertiser,
                    private val scanner: Scanner,
                    private val context: Context) {

    val state = BehaviorSubject.create<State>()

    var event: Event? = null
        set(value) {
            field = value
            start()
        }

    private val isPermissionsRequired: Boolean
        get() = context.requiresLocationPermission()

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter.isEnabled

    private val isAdvertisementSupported: Boolean
        get() = bluetoothAdapter.isMultipleAdvertisementSupported

    private var scanCallback: Disposable? = null
    private val isStarted: Boolean
        get() = scanCallback != null


    private var bluetoothStateCallback: BroadcastReceiver? = null

    fun start() {
        if (isStarted || event == null) return

        when {
            isPermissionsRequired -> state.send(State.PermissionRequired)
            isBluetoothEnabled -> when {
                isAdvertisementSupported -> {
                    state.send(State.Working)
                    startInternal()
                }
                else -> state.send(State.AdvertiseNotSupported)
            }
            else -> state.send(State.BluetoothDisabled)
        }
    }

    fun stop() {
        if (!isStarted) return

        advertiser.stopAdvertise()
        scanner.stopScan()
        scanCallback?.dispose()
        scanCallback = null
        bluetoothStateCallback?.run { context.unregisterReceiver(this) }
        bluetoothStateCallback = null
    }

    private fun restart() {
        stop()
        start()
    }

    private fun startInternal() {
        observeScanResults()

        scanner.startScan()

        registerBluetoothChangeCallback()
    }

    private fun observeScanResults() {
        scanner.callback.observe { results ->
            val usherUUID = this.event?.usherUUID ?: ""

            //start advertising when using detects userScanner's UUID
            if (results[0].uuid?.startsWith(usherUUID, true) == true) {
                if (this.event?.userUUIDPrefix != null && this.event?.userEventKey != null) {
                    advertiser.startAdvertise(this.event?.userUUIDPrefix!!, this.event?.userEventKey!!)
                }
            }
        }
    }

    private fun registerBluetoothChangeCallback() {
        bluetoothStateCallback = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    if (state == BluetoothAdapter.STATE_OFF || state == BluetoothAdapter.STATE_TURNING_OFF) {
                        restart()
                    }
                }
            }
        }

        context.registerReceiver(bluetoothStateCallback, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    sealed class State {
        object PermissionRequired : State()
        object BluetoothDisabled : State()
        object AdvertiseNotSupported : State()
        object Working : State()
    }

    private fun <T> BehaviorSubject<T>.observe(onSuccess: (T) -> Unit) {
        scanCallback = subscribe({ onSuccess(it) }, { Timber.e(it) })
    }
}