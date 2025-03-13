package com.gometro.network.utils

import android.util.Log
import com.gometro.logger.GometroLogger

class GometroLoggerAndroid: GometroLogger {
    override fun debug(tag: String, message: String) {
        Log.d(tag, message)
    }
    override fun debug(tag: String, message: String, throwable: Throwable?) {
        Log.d(tag, message, throwable)
    }

    override fun error(tag: String, message: String) {
        Log.e(tag, message)
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        Log.e(tag, message, throwable)
    }

    override fun warn(tag: String, throwable: Throwable) {
        Log.w(tag, throwable)
    }

    override fun warn(tag: String, message: String, throwable: Throwable?) {
        Log.w(tag, message, throwable)
    }

    override fun info(tag: String, message: String) {
        Log.i(tag, message)
    }

    override fun info(tag: String, message: String, throwable: Throwable?) {
        Log.i(tag, message, throwable)
    }
}