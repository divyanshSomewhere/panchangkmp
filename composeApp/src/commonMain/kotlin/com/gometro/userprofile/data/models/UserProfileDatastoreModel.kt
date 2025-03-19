package com.gometro.userprofile.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDatastoreModel(
    val userId: String,
    val firstName: String?,
    val lastName: String?,
    val gender: String?,
    val mobileNumber: String,
    val profilePhoto: String?,
    val dobInMillis: Long?
)

fun UserProfileDatastoreModel.toUserProfileAppModel(): UserProfileAppModel {
    return UserProfileAppModel(
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        profilePhoto = profilePhoto ?: "",
        gender = Gender.fromString(gender),
        mobileNumber = mobileNumber,
        dobInMillis = dobInMillis,
        userId = userId
    )
}