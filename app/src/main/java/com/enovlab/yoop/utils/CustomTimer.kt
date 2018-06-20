package com.enovlab.yoop.utils

import android.os.CountDownTimer
import java.util.*

class CustomTimer(endDate: Date, countDownInterval: Long = 1000L)
    : CountDownTimer(endDate.time - System.currentTimeMillis(), countDownInterval) {

    var tickListener: ((TimeInterval) -> Unit)? = null
    var finishListener: (() -> Unit)? = null

    override fun onTick(millisUntilFinished: Long) {
        tickListener?.invoke(timeInterval(millisUntilFinished))
    }

    override fun onFinish() {
        finishListener?.invoke()
    }

    fun timeInterval(millisTillEnd: Long): TimeInterval {
        val days = (millisTillEnd / 1000 / 86400).toInt()
        val hours = ((millisTillEnd / 1000 - days * 86400) / 3600).toInt()
        val minutes = ((millisTillEnd / 1000 - (days * 86400).toLong() - (hours * 3600).toLong()) / 60).toInt()
        val seconds = (millisTillEnd / 1000 % 60).toInt()

        return TimeInterval(days, hours, minutes, seconds)
    }

    data class TimeInterval(val days: Int, val hours: Int, val minutes: Int, val seconds: Int)
}
