package com.gometro.userprofile.data.repository

import com.gometro.login.data.exceptions.ProfileAndTokensExceptions
import com.gometro.network.exception.ApiCallLocalNetworkException
import com.gometro.userprofile.data.exceptions.UpdateUserProfileFailedException
import com.gometro.userprofile.data.models.UserProfileAppModel
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.cancellation.CancellationException

interface UserProfileRepository {

    @Throws(
        UpdateUserProfileFailedException::class,
        ProfileAndTokensExceptions.InvalidProfileDetails::class,
        ApiCallLocalNetworkException::class,
        CancellationException::class,
    )
    suspend fun updateUserProfileOnServer(
        userId: String,
        firstName: String,
        lastName: String,
        profilePhoto: String?,
        gender: String,
        dateOfBirth: Long?,
        emailId: String?
    ): UserProfileAppModel

    suspend fun updateUserProfileLocally(userProfileAppModel: UserProfileAppModel)

    suspend fun getUserProfileDetails(): UserProfileAppModel?

    suspend fun clearStoredUserProfileAndAuthTokensDetails()

    fun isUserLoggedIn(): Flow<Boolean>
}
