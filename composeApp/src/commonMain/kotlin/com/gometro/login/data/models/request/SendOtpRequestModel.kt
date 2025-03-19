package com.gometro.login.data.models.request

import kotlinx.serialization.Serializable

/**
 * @param refNo pass previous refNo if requesting to resend otp else pass null
 */
@Serializable
data class SendOtpRequestModel(
    val mobileNumber: String,
    val countryCode: String,
    val templateId: String,
    val refNo: String? = null
)
