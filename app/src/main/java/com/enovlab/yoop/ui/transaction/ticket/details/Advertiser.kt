package com.example.android.bluetoothadvertisements

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.AdvertiseSettings.ADVERTISE_TX_POWER_LOW
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Intent
import android.os.Handler
import android.util.Log
import com.enovlab.yoop.ui.transaction.ticket.details.ScanData
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.TimeUnit
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import kotlin.experimental.and


/**
 * Manages BLE Advertising independent of the main app.
 * If the app goes off screen (or gets killed completely) advertising can continue because this
 * Service is maintaining the necessary Callback in memory.
 */
class Advertiser @Inject constructor(val mBluetoothAdapter: BluetoothAdapter) {

    private var mBluetoothLeAdvertiser: BluetoothLeAdvertiser? = null

    private var mAdvertiseCallback: AdvertiseCallback? = null

    private var mHandler: Handler? = null

    private var timeoutRunnable: Runnable? = null

    /**
     * Length of time to allow advertising before automatically shutting off. (10 minutes)
     */
    private val TIMEOUT = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MINUTES)

    init {
        running = true
//        initialize()
//        startAdvertising()
//        setTimeout()
    }

    fun destroy() {
        /**
         * Note that onDestroy is not guaranteed to be called quickly or at all. Services exist at
         * the whim of the system, and onDestroy can be delayed or skipped entirely if memory need
         * is critical.
         */
        running = false
        stopAdvertising()
        mHandler!!.removeCallbacks(timeoutRunnable)
    }

    /**
     * Get references to system Bluetooth objects if we don't have them already.
     */
    private fun initialize() {
        mBluetoothLeAdvertiser = mBluetoothAdapter.bluetoothLeAdvertiser
        mBluetoothAdapter.name = "Ha" //8 characters works, 9+ fails
    }

    /**
     * Starts a delayed Runnable that will cause the BLE Advertising to timeout and stop after a
     * set amount of time.
     */
    private fun setTimeout() {
        mHandler = Handler()
        timeoutRunnable = Runnable {
            Log.d(TAG, "AdvertiserService has reached timeout of $TIMEOUT milliseconds, stopping advertising.")
            sendFailureIntent(ADVERTISING_TIMED_OUT)
        }
        mHandler!!.postDelayed(timeoutRunnable, TIMEOUT)
    }

    /**
     * Starts BLE Advertising.
     */
    fun startAdvertising(data: ScanData) {
        if (mAdvertiseCallback == null) {
            val settings = buildAdvertiseSettings()
            val data = buildAdvertiseData(data)
            mAdvertiseCallback = SampleAdvertiseCallback()

            if (mBluetoothLeAdvertiser != null) {
                mBluetoothLeAdvertiser!!.startAdvertising(settings, data,
                    mAdvertiseCallback)

                if (mBluetoothAdapter != null) {
                    val address = mBluetoothAdapter.address
                    Log.d(TAG, "Mac Address: $address")
                }
            }
        }
    }

    /**
     * Stops BLE Advertising.
     */
    private fun stopAdvertising() {
        Log.d(TAG, "Service: Stopping Advertising")
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser!!.stopAdvertising(mAdvertiseCallback)
            mAdvertiseCallback = null
        }
    }

    private fun buildAdvertiseData(scanData: ScanData): AdvertiseData {

        val mBuilder = AdvertiseData.Builder()
        val mManufacturerData = ByteBuffer.allocate(24)

        val eventKey = scanData.eventKey
        if(eventKey != null) {
            val uuid = getBytesFromUUID(UUID.fromString(buildUserUUID(scanData.uuid ?: "", calculateRotatingID(eventKey))))

            mManufacturerData.put(0, 0x02.toByte()) // Beacon Identifier
            mManufacturerData.put(1, 0x15.toByte()) // Beacon Identifier

            mManufacturerData.put(UUID_OFFSET, 0x15.toByte()) // Beacon Identifier
            for (i in UUID_OFFSET..17) {
                mManufacturerData.put(i, uuid[i - UUID_OFFSET]) // adding the UUID
            }

            mManufacturerData.put(18, 0x00.toByte()) // first byte of Major
            mManufacturerData.put(19, 0x00.toByte()) // second byte of Major
            mManufacturerData.put(20, 0x00.toByte()) // first minor
            mManufacturerData.put(21, 0x00.toByte()) // second minor
            mManufacturerData.put(22, 0xB5.toByte()) // txPower

            mBuilder.addManufacturerData(0x004C, mManufacturerData.array()) // using google's company ID
        }

        return mBuilder.build()
    }

    private fun buildUserUUID(uuid: String, rotatingID: String): String {
        return uuid + rotatingID
    }

    //TODO change encryption algorithm here
    private fun calculateRotatingID(keyString: String): String {
        val mac = Mac.getInstance("HmacSHA512")
        val keyStringByteArray = keyString.toByteArray(StandardCharsets.US_ASCII)
        val algortyhmKey = SecretKeySpec(keyStringByteArray, "RAW")
        mac.init(algortyhmKey)
        val date = Date()
        val timeStepMillis = (30 * 1000).toLong()//30 seconds interval
        val counter = date.time / timeStepMillis

        val counterLongBuffer = ByteBuffer.allocate(8)//Long is 8 byte
        counterLongBuffer.putLong(0, counter)
        val hmacCounterHash = mac.doFinal(counterLongBuffer.array())

        val offset = hmacCounterHash[hmacCounterHash.size - 1] and 0x0f//last half byte of hmac is the offset
        val randomIntegerBuffer = ByteBuffer.allocate(4)//Integer is 4 byte
        for (i in 0..3) {
            randomIntegerBuffer.put(i, hmacCounterHash[i + offset])
        }
        val hotp = randomIntegerBuffer.getInt(0) and 0x7fffffff//I guess is to get rid of the sign bit

        val modDivisor = 100000000 //8 digit code but if we transmit a4 byte integer this is not necessary
        val trimmedHotp = hotp % modDivisor

        return trimmedHotp.toString()
    }

    /**
     * Returns an AdvertiseSettings object set to use low power (to help preserve battery life)
     * and disable the built-in timeout since this code uses its own timeout runnable.
     */
    private fun buildAdvertiseSettings(): AdvertiseSettings {
        val settingsBuilder = AdvertiseSettings.Builder()
        settingsBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
        settingsBuilder.setTxPowerLevel(ADVERTISE_TX_POWER_LOW)
        settingsBuilder.setTimeout(0)
        settingsBuilder.setConnectable(true)
        return settingsBuilder.build()
    }

    /**
     * Custom callback after Advertising succeeds or fails to start. Broadcasts the error code
     * in an Intent to be picked up by AdvertiserFragment and stops this Service.
     */
    private inner class SampleAdvertiseCallback : AdvertiseCallback() {

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)

            Log.d(TAG, "Advertising failed")
            sendFailureIntent(errorCode)
        }

        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            super.onStartSuccess(settingsInEffect)
            Log.d(TAG, "Advertising successfully started")
        }
    }

    /**
     * Builds and sends a broadcast intent indicating Advertising has failed. Includes the error
     * code as an extra. This is intended to be picked up by the `AdvertiserFragment`.
     */
    private fun sendFailureIntent(errorCode: Int) {
        val failureIntent = Intent()
        failureIntent.action = ADVERTISING_FAILED
        failureIntent.putExtra(ADVERTISING_FAILED_EXTRA_CODE, errorCode)
    }

    companion object {

        internal const val UUID_OFFSET = 2
        private val TAG = Advertiser.javaClass::class.simpleName

        /**
         * A global variable to let AdvertiserFragment check if the Service is running without needing
         * to start or bind to it.
         * This is the best practice method as defined here:
         * https://groups.google.com/forum/#!topic/android-developers/jEvXMWgbgzE
         */
        var running = false

        val ADVERTISING_FAILED = "com.example.android.bluetoothadvertisements.advertising_failed"

        val ADVERTISING_FAILED_EXTRA_CODE = "failureCode"

        val ADVERTISING_TIMED_OUT = 6

        fun getBytesFromUUID(uuid: UUID): ByteArray {
            val bb = ByteBuffer.wrap(ByteArray(16))
            bb.putLong(uuid.mostSignificantBits)
            bb.putLong(uuid.leastSignificantBits)

            return bb.array()
        }
    }
}