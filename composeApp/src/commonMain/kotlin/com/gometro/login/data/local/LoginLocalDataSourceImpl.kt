package com.gometro.login.data.local

import com.gometro.base.userprofile.UserProfileAndAuthStoreManager
import com.gometro.userprofile.data.models.UserProfileDatastoreModel
import kotlinx.coroutines.flow.firstOrNull

class LoginLocalDataSourceImpl(
    private val userProfileAndAuthStoreManager: UserProfileAndAuthStoreManager
) : LoginLocalDataSource {

    override suspend fun storeAuthTokens(
        accessToken: String,
        refreshToken: String,
        expiryTime: Long?,
        delta: Long?
    ) {
        userProfileAndAuthStoreManager.storeAuthTokens(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiryTime = expiryTime ?: -1,
            delta = delta ?: -1
        )
    }

    override suspend fun storeUserProfileDetails(userProfileDatastoreModel: UserProfileDatastoreModel) {
        userProfileAndAuthStoreManager.storeUserProfileDetails(userProfileDatastoreModel)
    }

    override suspend fun getAccessToken(): String? {
        return userProfileAndAuthStoreManager.getAccessToken()
    }

    override suspend fun getRefreshToken(): String? {
        return userProfileAndAuthStoreManager.getRefreshToken()
    }

    override suspend fun getUserId(): String? {
        return userProfileAndAuthStoreManager.getUserProfileDetails().firstOrNull()?.userId
    }

    override suspend fun getAccessTokenExpiryTime(): Long {
        return userProfileAndAuthStoreManager.getAccessTokenExpiryTime()
    }

    override suspend fun getAccessTokenDelta(): Long {
        return userProfileAndAuthStoreManager.getDeltaTime()
    }

    override suspend fun setIsUserLoginFirstTime(isUserLoginFirstTime: Boolean) {
        userProfileAndAuthStoreManager.setIsUserLoginFirstTime(isUserLoginFirstTime)
    }

    override suspend fun isUserLoginFirstTime(): Boolean {
        return userProfileAndAuthStoreManager.isUserLoginFirstTime()
    }
}
