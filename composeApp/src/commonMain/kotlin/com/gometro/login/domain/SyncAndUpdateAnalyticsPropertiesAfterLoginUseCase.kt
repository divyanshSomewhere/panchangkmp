package com.gometro.login.domain

import com.gometro.base.featurecontracts.AnalyticsContract
import com.gometro.base.featurecontracts.FcmTokenFeature
import com.gometro.base.featurecontracts.TimeUtilsContract
import com.gometro.userprofile.domain.UserProfileDetailsProvider


class SyncAndUpdateAnalyticsPropertiesAfterLoginUseCase(
    private val fcmTokenFeature: FcmTokenFeature,
    private val analyticsContract: AnalyticsContract,
    private val userProfileDetailsProvider: UserProfileDetailsProvider,
    private val timeUtilsContract: TimeUtilsContract
) {

    operator fun invoke() {
        fcmTokenFeature.sendFcmTokenIfNotSetAlready()
        updatePersonProperties()
    }

    private fun updatePersonProperties() {
        val userProfile = userProfileDetailsProvider.getUserProfileDetails() ?: return
        val analyticsProperties = mapOf<String, String>(
            "phone" to (userProfile.mobileNumber),
            "dob" to (userProfile.dobInMillis?.let { timeUtilsContract.getDateFromEpochTime(it) } ?: ""),
            "gender" to (userProfile.gender.name),
            "firstName" to (userProfile.firstName),
            "lastName" to (userProfile.lastName)
        )
        analyticsContract.apply {
            addToPeopleProperties(analyticsProperties)
            addToSuperProperties(analyticsProperties)
            val username = "${userProfile.firstName} ${userProfile.lastName}"
        }
    }
}
