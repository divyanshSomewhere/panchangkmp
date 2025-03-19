package com.gometro.base.utils

import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

object DataStorePathHelper {

    @OptIn(ExperimentalForeignApi::class)
    fun producePath(name: String): Path {
        val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        val pathString = requireNotNull(documentDirectory).path + "/$name"
        return pathString.toPath()
    }

}