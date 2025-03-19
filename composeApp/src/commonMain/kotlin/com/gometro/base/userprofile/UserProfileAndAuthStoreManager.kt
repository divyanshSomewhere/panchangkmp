package com.gometro.base.userprofile

import com.gometro.userprofile.data.models.UserProfileDatastoreModel
import kotlinx.coroutines.flow.Flow

interface UserProfileAndAuthStoreManager {

    suspend fun storeUserProfileDetails(userProfileDatastoreModel: UserProfileDatastoreModel)

    fun getUserProfileDetails(): Flow<UserProfileDatastoreModel?>

    suspend fun storeAuthTokens(
        accessToken: String,
        refreshToken: String,
        expiryTime: Long,
        delta: Long
    )

    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?

    suspend fun getAccessTokenExpiryTime(): Long

    suspend fun getDeltaTime(): Long

    suspend fun clearStoredUserDetails()

    suspend fun clearStoredAuthTokensDetails()

    suspend fun setIsUserLoginFirstTime(isUserLoginFirstTime: Boolean)
    suspend fun isUserLoginFirstTime(): Boolean
}
