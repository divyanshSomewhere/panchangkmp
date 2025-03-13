package com.gometro.logger

interface GometroLogger {

    fun debug(tag: String, message: String)

    fun debug(tag: String, message: String, throwable: Throwable?)

    fun error(tag: String, message: String)

    fun error(tag: String, message: String, throwable: Throwable?)

    fun warn(tag: String, throwable: Throwable)

    fun warn(tag: String, message: String, throwable: Throwable?)

    fun info(tag: String, message: String)

    fun info(tag: String, message: String, throwable: Throwable?)
}