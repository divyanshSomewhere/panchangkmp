package com.gometro.base.utils

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.SystemClock
import android.provider.Settings
import com.gometro.base.featurecontracts.SystemHelper

class SystemHelperAndroidImpl(
    private val context: Context
): SystemHelper {

    override fun currentTimeInMillis(): Long {
        return System.currentTimeMillis()
    }

    override fun elapsedRealTime(): Long {
        return SystemClock.elapsedRealtime()
    }

    override fun openApplicationSettings() {
        val lSettingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val lUri = Uri.fromParts("package", context.packageName, null)
        lSettingsIntent.data = lUri
        lSettingsIntent.flags = FLAG_ACTIVITY_NEW_TASK
        context.startActivity(lSettingsIntent)
    }
}