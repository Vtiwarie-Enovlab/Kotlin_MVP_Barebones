package com.enovlab.yoop.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import com.enovlab.yoop.utils.ext.hex
import com.enovlab.yoop.utils.ext.hexString
import com.enovlab.yoop.utils.ext.send
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class Scanner
@Inject constructor(private val bluetoothAdapter: BluetoothAdapter) {

    private val bluetoothScanner: BluetoothLeScanner?
        get() = bluetoothAdapter.bluetoothLeScanner

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, scannerResult: ScanResult) {
            val result = parseResult(scannerResult)
            if (result != null) {
                callback.send(listOf(result))
            }
        }

        override fun onBatchScanResults(scannerResults: MutableList<ScanResult>) {
            val results = parseResults(scannerResults)
            if (results.isNotEmpty()) {
                callback.send(results)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Timber.e("Scan failed: $errorCode")
        }
    }

    val callback = BehaviorSubject.create<List<Result>>()

    fun startScan() {
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        val filters = mutableListOf<ScanFilter>()

        bluetoothScanner?.startScan(filters, settings, scanCallback)
    }

    fun stopScan() {
        bluetoothScanner?.stopScan(scanCallback)
    }

    private fun parseResults(scanResults: List<ScanResult>): List<Result> {
        val results = mutableListOf<Result>()
        scanResults.forEach {
            parseResult(it)?.run { results.add(this) }
        }
        return results
    }

    private fun parseResult(scanResult: ScanResult): Result? {
        val scanRecord = scanResult.scanRecord.bytes

        val records = mutableListOf<ByteArray>()

        var index = 0
        while (index < scanRecord.size) {
            val length = scanRecord[index++].toInt()
            //Done once we run out of records
            if (length == 0) {
                break
            }

            val type = scanRecord[index].toInt()
            //Done if our scanRecord isn't a valid type
            if (type == 0) {
                break
            }

            records.add(Arrays.copyOfRange(scanRecord, index + 1, index + length))

            //Advance
            index += length
        }

        if (records.size > 1) {
            val record = records[1]
            if (record.size >= 24) {
                val uuid = Arrays.copyOfRange(record, 4, 20).hexString()
                val userId = Arrays.copyOfRange(record, 12, 20).hexString()
                val prefix = Arrays.copyOfRange(record, 4, 8).hexString()
                val code = Arrays.copyOfRange(record, 20, 24).hex()

                return Result(uuid, userId, prefix, code, scanResult.rssi)
            }
        }

        return null
    }

    data class Result(val uuid: String,
                      val userId: String,
                      val prefix: String,
                      val code: Long,
                      val signalStrength: Int)
}