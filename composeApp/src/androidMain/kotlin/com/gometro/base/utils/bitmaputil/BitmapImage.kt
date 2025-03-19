package com.gometro.base.utils.bitmaputil

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

actual class BitmapImage(
    val bitmap: Bitmap,
    actual val byteArray: ByteArray
) {
    companion object {
        suspend fun fromUriContent(uriContent: Uri, context: Context): BitmapImage? {
            val bitmap = try {
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uriContent))
                    }
                    else -> MediaStore.Images.Media.getBitmap(context.contentResolver, uriContent)
                }
            } catch (e: Exception) {
                null
            }

            return bitmap?.let {
                BitmapImage(
                    bitmap = bitmap,
                    byteArray = bitmap.toByteArray()
                )
            }
        }
    }

}

suspend fun Bitmap.toByteArray(): ByteArray {
    return withContext(Dispatchers.Default) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        this@toByteArray.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        byteArrayOutputStream.toByteArray()
    }
}

actual fun ByteArray.toImageBitmap(): ImageBitmap {
    return BitmapFactory.decodeByteArray(this, 0, size).asImageBitmap()
}