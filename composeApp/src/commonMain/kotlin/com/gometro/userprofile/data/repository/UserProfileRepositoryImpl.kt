package com.gometro.userprofile.data.repository

import com.gometro.base.featurecontracts.TimeUtilsContract
import com.gometro.login.data.exceptions.ProfileAndTokensExceptions
import com.gometro.login.data.mappers.toUserProfileAppModel
import com.gometro.network.exception.ApiCallLocalNetworkException
import com.gometro.userprofile.data.exceptions.UpdateUserProfileFailedException
import com.gometro.userprofile.data.local.UserProfileLocalDataSource
import com.gometro.userprofile.data.models.UserProfileAppModel
import com.gometro.userprofile.data.models.request.UpdateUserProfileRequestApiModel
import com.gometro.userprofile.data.models.toUserProfileAppModel
import com.gometro.userprofile.data.models.toUserProfileLocalInfoModel
import com.gometro.userprofile.data.remote.UserProfileRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.cancellation.CancellationException

class UserProfileRepositoryImpl(
    private val remoteDataSource: UserProfileRemoteDataSource,
    private val localDataSource: UserProfileLocalDataSource,
    private val timeUtilsContract: TimeUtilsContract
) : UserProfileRepository {

    @Throws(
        UpdateUserProfileFailedException::class,
        ProfileAndTokensExceptions.InvalidProfileDetails::class,
        ApiCallLocalNetworkException::class,
        CancellationException::class
    )
    override suspend fun updateUserProfileOnServer(
        userId: String,
        firstName: String,
        lastName: String,
        profilePhoto: String?,
        gender: String,
        dateOfBirth: Long?,
        emailId: String?
    ): UserProfileAppModel {
        val requestApiModel = UpdateUserProfileRequestApiModel(
            userId = userId,
            firstName = firstName,
            lastName = lastName,
            profilePhoto = profilePhoto,
            gender = gender,
            dateOfBirth = dateOfBirth,
            emailId = emailId
        )

        return remoteDataSource.updateUserProfileOnServer(requestApiModel)
            .toUserProfileAppModel()
    }

    override suspend fun updateUserProfileLocally(userProfileAppModel: UserProfileAppModel) {
        localDataSource.updateUserProfileLocally(userProfileAppModel.toUserProfileLocalInfoModel())
    }

    override suspend fun getUserProfileDetails(): UserProfileAppModel? {
        return localDataSource.getUserProfileDetails()?.toUserProfileAppModel()
    }

    override suspend fun clearStoredUserProfileAndAuthTokensDetails() {
        localDataSource.clearStoredUserProfileAndAuthTokensDetails()
    }

    override fun isUserLoggedIn(): Flow<Boolean> {
        return localDataSource.isUserLoggedIn()
    }
}
