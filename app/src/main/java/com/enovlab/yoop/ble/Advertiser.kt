package com.enovlab.yoop.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import kotlin.experimental.and

class Advertiser
@Inject constructor(private val bluetoothAdapter: BluetoothAdapter) {

    private val bluetoothAdvertiser: BluetoothLeAdvertiser?
        get() = bluetoothAdapter.bluetoothLeAdvertiser

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            Timber.d("Advertise started.")
        }

        override fun onStartFailure(errorCode: Int) {
            Timber.d("Advertise start failed: $errorCode.")
        }
    }

    fun startAdvertise(uuid: String, eventKey: String?) {
        bluetoothAdvertiser?.startAdvertising(buildSettings(), buildAdvertiseData(uuid, eventKey), advertiseCallback)
    }

    fun stopAdvertise() {
        bluetoothAdvertiser?.stopAdvertising(advertiseCallback)
    }

    private fun buildSettings(): AdvertiseSettings {
        return AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
            .setTimeout(0)
            .setConnectable(true)
            .build()
    }

    private fun buildAdvertiseData(uuid: String, eventKey: String?): AdvertiseData {
        val manufacturerData = ByteBuffer.allocate(24)

        manufacturerData.put(0, 0x02.toByte()) // Beacon Identifier
        manufacturerData.put(1, 0x15.toByte()) // Beacon Identifier

        val uuidBytes = bytesFromUUID(UUID.fromString(convertToUUIDString(buildEncryptedUserUUID(uuid, eventKey))))
        manufacturerData.put(UUID_OFFSET, 0x15.toByte()) // Beacon Identifier
        for (i in UUID_OFFSET..17) {
            manufacturerData.put(i, uuidBytes[i - UUID_OFFSET]) // adding the UUID
        }

        manufacturerData.put(18, 0x00.toByte()) // first byte of Major
        manufacturerData.put(19, 0x09.toByte()) // second byte of Major
        manufacturerData.put(20, 0x00.toByte()) // first minor
        manufacturerData.put(21, 0x06.toByte()) // second minor
        manufacturerData.put(22, 0xB5.toByte()) // txPower

        //used to be 224
        return AdvertiseData.Builder()
            .addManufacturerData(0x004C, manufacturerData.array()) // using google's company ID
            .build()
    }

    private fun bytesFromUUID(uuid: UUID): ByteArray {
        val bb = ByteBuffer.wrap(ByteArray(16))
        bb.putLong(uuid.mostSignificantBits)
        bb.putLong(uuid.leastSignificantBits)
        return bb.array()
    }

    private fun convertToUUIDString(uuid: String): String {
        if (uuid.contains("-")) {
            return uuid
        } else {
            return "${uuid.substring(0, 8)}-${uuid.substring(8, 12)}-${uuid.substring(12, 16)}-${uuid.substring(16, 20)}-${uuid.substring(20)}"
        }
    }

    private fun buildEncryptedUserUUID(uuid: String, eventKey: String?): String {
        return uuid + calculateRotatingID(eventKey)
    }

    //TODO change encryption algorithm here
    private fun calculateRotatingID(keyString: String?): String {
        val mac = Mac.getInstance("HmacSHA512")
        val keyStringByteArray = keyString?.toByteArray(StandardCharsets.US_ASCII)
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

    companion object {
        private const val UUID_OFFSET = 2
    }
}