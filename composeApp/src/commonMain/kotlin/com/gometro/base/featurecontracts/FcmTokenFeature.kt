package com.gometro.base.featurecontracts

interface FcmTokenFeature {

    fun getToken(): String?

    fun sendFcmTokenIfNotSetAlready()

    // TODO::KSHITIJ - come back for this
    companion object Dummy : FcmTokenFeature {
        override fun getToken(): String? {
            return ""
        }

        override fun sendFcmTokenIfNotSetAlready() {}
    }
}
