package com.gometro.login.data.local

import com.gometro.userprofile.data.models.UserProfileDatastoreModel

interface LoginLocalDataSource {

    suspend fun storeAuthTokens(
        accessToken: String,
        refreshToken: String,
        expiryTime: Long?,
        delta: Long?
    )

    suspend fun storeUserProfileDetails(
        userProfileLocalInfoModel: UserProfileDatastoreModel
    )

    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?

    suspend fun getUserId(): String?

    suspend fun getAccessTokenExpiryTime(): Long

    suspend fun getAccessTokenDelta(): Long

    suspend fun setIsUserLoginFirstTime(isUserLoginFirstTime: Boolean)
    suspend fun isUserLoginFirstTime(): Boolean
}
