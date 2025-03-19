package com.gometro.userprofile.data.models.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserProfileRequestApiModel(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val profilePhoto: String?,
    val gender: String,
    val dateOfBirth: Long?,
    val emailId: String?
)
