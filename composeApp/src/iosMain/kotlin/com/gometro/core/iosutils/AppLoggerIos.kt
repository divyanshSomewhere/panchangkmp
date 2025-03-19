package com.gometro.core.iosutils

import com.gometro.logger.AppLogger
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ptr
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import platform.darwin.OS_LOG_DEFAULT
import platform.darwin.OS_LOG_TYPE_DEBUG
import platform.darwin.OS_LOG_TYPE_ERROR
import platform.darwin.OS_LOG_TYPE_FAULT
import platform.darwin.OS_LOG_TYPE_INFO
import platform.darwin.__dso_handle
import platform.darwin._os_log_internal

@OptIn(ExperimentalForeignApi::class)
class AppLoggerIos: AppLogger {

    override fun debug(tag: String, message: String) {
        _os_log_internal(__dso_handle.ptr, OS_LOG_DEFAULT, OS_LOG_TYPE_DEBUG, "[DEBUG - ${currentTimeInMillis()}] $tag $message")
    }

    override fun debug(tag: String, message: String, throwable: Throwable?) {
        var fullMessage = message
        if (throwable != null) {
            fullMessage += "\n${throwable.stackTraceToString()}"
        }
        _os_log_internal(__dso_handle.ptr, OS_LOG_DEFAULT, OS_LOG_TYPE_DEBUG, "[DEBUG - ${currentTimeInMillis()}] $tag $fullMessage")
    }

    override fun error(tag: String, message: String) {
        _os_log_internal(__dso_handle.ptr, OS_LOG_DEFAULT, OS_LOG_TYPE_FAULT, "[ERROR - ${currentTimeInMillis()}] $tag $message")
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        var fullMessage = message
        if (throwable != null) {
            fullMessage += "\n${throwable.stackTraceToString()}"
        }
        _os_log_internal(__dso_handle.ptr, OS_LOG_DEFAULT, OS_LOG_TYPE_FAULT, "[ERROR - ${currentTimeInMillis()}] $tag $fullMessage")
    }

    override fun warn(tag: String, throwable: Throwable) {
        _os_log_internal(__dso_handle.ptr, OS_LOG_DEFAULT, OS_LOG_TYPE_ERROR, "[WARN - ${currentTimeInMillis()}] $tag ${throwable.stackTraceToString()}")
    }

    override fun warn(tag: String, message: String, throwable: Throwable?) {
        var fullMessage = message
        if (throwable != null) {
            fullMessage += "\n${throwable.stackTraceToString()}"
        }
        _os_log_internal(__dso_handle.ptr, OS_LOG_DEFAULT, OS_LOG_TYPE_ERROR, "[WARN - ${currentTimeInMillis()}] $tag $fullMessage")
    }

    override fun info(tag: String, message: String) {
        _os_log_internal(__dso_handle.ptr, OS_LOG_DEFAULT, OS_LOG_TYPE_INFO, "[INFO - ${currentTimeInMillis()}] $tag $message")
    }

    override fun info(tag: String, message: String, throwable: Throwable?) {
        var fullMessage = message
        if (throwable != null) {
            fullMessage += "\n${throwable.stackTraceToString()}"
        }
        _os_log_internal(__dso_handle.ptr, OS_LOG_DEFAULT, OS_LOG_TYPE_INFO, "[INFO - ${currentTimeInMillis()}] $tag $fullMessage")
    }

    private fun currentTimeInMillis(): Long {
        return NSDate().timeIntervalSince1970.toLong() * 1000
    }

}