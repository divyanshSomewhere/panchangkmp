package com.gometro.userprofile.data.models

import com.gometro.base.featurecontracts.TimeUtilsContract


data class UserProfileAppModel(
    val firstName: String,
    val lastName: String,
    val profilePhoto: String,
    val gender: Gender,
    val mobileNumber: String,
    val dobInMillis: Long?,
    val userId: String
) {
    fun getFullName(): String {
        return when {
            lastName.isEmpty() -> {
                firstName.ifEmpty { "" }
            }
            firstName.isEmpty() -> {
                lastName.ifEmpty { "" }
            }
            else -> "$firstName $lastName"
        }
    }

    fun getDateOfBirthString(
        timeUtilsContract: TimeUtilsContract
    ): String? {
        return dobInMillis?.let { timeUtilsContract.getDateFromEpochTime(dobInMillis) }
    }
}

fun UserProfileAppModel.toUserProfileLocalInfoModel(): UserProfileDatastoreModel {
    return UserProfileDatastoreModel(
        userId = userId,
        firstName = firstName,
        lastName = lastName,
        gender = gender.name,
        mobileNumber = mobileNumber,
        profilePhoto = profilePhoto,
        dobInMillis = dobInMillis
    )
}

