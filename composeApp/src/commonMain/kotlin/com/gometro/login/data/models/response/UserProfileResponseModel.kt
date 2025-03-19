package com.gometro.login.data.models.response

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponseModel(
    val firstName: String?,
    val lastName: String?,
    val profilePhoto: String?,
    val gender: String?,
    val mobileNumber: String?,
    val countryCode: String?,
    val dateOfBirth: Long?,
    val emailId: String?,
    val userId: String?
)
