package com.enovlab.yoop.utils

import android.content.Context
import android.media.AudioManager
import android.os.Build

class VolumeObserver(context: Context) {

    var listener: ((Boolean) -> Unit)? = null

    private val audioManager: AudioManager
    private var volume: Int = 0

    init {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
    }

    fun updateVolume() {
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

        val delta = volume - currentVolume

        if (delta > 0) {
            volume = currentVolume
        } else if (delta < 0) {
            volume = currentVolume
        }

        listener?.invoke(volume > 0)
    }

    @Suppress("DEPRECATION")
    fun updateMuteSettings() {
        if (volume > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0)
            } else {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0)
            } else {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            }
        }

        updateVolume()
    }
}