package com.gometro

import android.content.Context
import android.provider.Settings
import com.gometro.base.featurecontracts.Device

class DeviceImplAndroid (
    private val context: Context
): Device {
    override fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    }
}