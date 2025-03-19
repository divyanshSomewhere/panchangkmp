package com.gometro.base.utils.bitmaputil

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.posix.memcpy

actual class BitmapImage(
    val uiImage: UIImage,
    actual val byteArray: ByteArray
)

@OptIn(ExperimentalForeignApi::class)
suspend fun UIImage.toByteArray(): ByteArray {
    return withContext(Dispatchers.Default) {
        val jpegData = UIImageJPEGRepresentation(this@toByteArray, 50.0) ?: return@withContext ByteArray(0)
        ByteArray(jpegData.length.toInt()).apply {
            usePinned {
                memcpy(it.addressOf(0), jpegData.bytes, jpegData.length)
            }
        }
    }
}

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    return Image.makeFromEncoded(this).toComposeImageBitmap()
}