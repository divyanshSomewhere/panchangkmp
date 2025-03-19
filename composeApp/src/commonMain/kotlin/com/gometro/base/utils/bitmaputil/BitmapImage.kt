package com.gometro.base.utils.bitmaputil

import androidx.compose.ui.graphics.ImageBitmap

expect class BitmapImage {
    val byteArray: ByteArray
}

expect fun ByteArray.toImageBitmap(): ImageBitmap