package com.enovlab.yoop.inject

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BleModule {

    @Provides
    @Singleton
    fun provideBluetoothAdapter(context: Context): BluetoothAdapter {
        return (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }
}