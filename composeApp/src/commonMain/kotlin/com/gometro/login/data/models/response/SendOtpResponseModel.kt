package com.gometro.login.data.models.response

import kotlinx.serialization.Serializable

@Serializable
data class SendOtpResponseModel(
    val status: Boolean? = null,
    val refNo: String? = null
)
