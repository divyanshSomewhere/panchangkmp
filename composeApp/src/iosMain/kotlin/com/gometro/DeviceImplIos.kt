package com.gometro

import com.gometro.base.featurecontracts.Device
import platform.UIKit.UIDevice

class DeviceImplIos: Device {
    override fun getDeviceId(): String {
        return UIDevice.currentDevice.identifierForVendor?.UUIDString ?: ""
    }
}