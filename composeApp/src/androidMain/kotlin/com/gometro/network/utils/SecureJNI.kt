package com.gometro.network.utils

object SecureJNI {
    init {
        System.loadLibrary("secure-keys-android")
    }

    external fun getPKP1c(): String

    external fun getPKP2c(): String

    external fun getPKP3c(): String

    external fun getPKP1z(): String

    external fun getc(): String

    external fun getz(): String

    external fun getVaultKey(): String
}