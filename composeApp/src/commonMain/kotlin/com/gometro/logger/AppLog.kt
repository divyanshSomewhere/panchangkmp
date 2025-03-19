package com.gometro.logger

object AppLog {

    private var logger: AppLogger? = null
    private var isDebugBuild: Boolean = false

    fun register(logger: AppLogger, isDebugBuild: Boolean) {
        this.logger = logger
        this.isDebugBuild = isDebugBuild
    }

    fun debug(tag: String, message: String) {
        if (isDebugBuild) {
            logger?.debug(tag, message)
        }
    }

    fun debug(tag: String, message: String, throwable: Throwable?) {
        if (isDebugBuild) {
            logger?.debug(tag, message, throwable)
        }
    }

    fun error(tag: String, message: String) {
        if (isDebugBuild) {
            logger?.error(tag, message)
        }
    }

    fun error(tag: String, message: String, throwable: Throwable?) {
        if (isDebugBuild) {
            logger?.error(tag, message, throwable)
        }
    }

    fun warn(tag: String, throwable: Throwable) {
        if (isDebugBuild) {
            logger?.warn(tag, throwable)
        }
    }

    fun warn(tag: String, message: String, throwable: Throwable?) {
        if (isDebugBuild) {
            logger?.warn(tag, message, throwable)
        }
    }

    fun info(tag: String, message: String) {
        if (isDebugBuild) {
            logger?.info(tag, message)
        }
    }

    fun info(tag: String, message: String, throwable: Throwable?) {
        if (isDebugBuild) {
            logger?.info(tag, message, throwable)
        }
    }
}