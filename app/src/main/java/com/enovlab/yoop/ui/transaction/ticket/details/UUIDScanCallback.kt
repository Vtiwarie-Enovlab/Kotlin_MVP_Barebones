package com.enovlab.yoop.ui.transaction.ticket.details

import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import timber.log.Timber
import java.util.*

class UUIDScanCallback constructor(val listener: (scanData: ScanData) -> Unit) : ScanCallback() {

    override fun onScanResult(callbackType: Int, result: ScanResult) {
        super.onScanResult(callbackType, result)

        val data = getScanRecord(result)

        listener.invoke(data)

        Timber.d("ScanData: ${data}")
    }

    override fun onScanFailed(errorCode: Int) {
        super.onScanFailed(errorCode)
        Timber.e("[ERROR] - Scan failed with error code: ${errorCode}")
    }
}

internal fun getScanRecord(result: ScanResult): ScanData {
    val mScanRecord = result.scanRecord!!.bytes
    var uuid: String?
    var newMajor: Long?
    var newMinor: Long?

    var newScanData = ScanData()

    val records = AdRecord.parseScanRecord(mScanRecord)
    if (records != null && records.size > 1) {
        val record = records[1]
        val data = record.data
        if (data.size > 24) {
            //TODO change ranges
            val uuidBytes = Arrays.copyOfRange(data, 4, 20)
            val majorBytes = Arrays.copyOfRange(data, 20, 22)
            val minorBytes = Arrays.copyOfRange(data, 22, 24)

            uuid = AdRecord.getHexString(uuidBytes)
            newMajor = AdRecord.getHex(majorBytes)
            newMinor = AdRecord.getHex(minorBytes)

            newScanData = ScanData(uuid, newMajor, newMinor)
        }
    }

    return newScanData
}

class ScanData(var uuid: String? = null,
               var major: Long? = null,
               var minor: Long? = null,
               var eventKey: String? = null)
