package com.gometro.userprofile.data.local

import com.gometro.base.userprofile.UserProfileAndAuthStoreManager
import com.gometro.userprofile.data.models.UserProfileDatastoreModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class UserProfileLocalDataSourceImpl(
    private val userProfileAndAuthStoreManager: UserProfileAndAuthStoreManager
) : UserProfileLocalDataSource {

    override suspend fun updateUserProfileLocally(userProfileDatastoreModel: UserProfileDatastoreModel) {
        userProfileAndAuthStoreManager.storeUserProfileDetails(userProfileDatastoreModel)
    }

    override suspend fun getUserProfileDetails(): UserProfileDatastoreModel? {
        return userProfileAndAuthStoreManager.getUserProfileDetails().firstOrNull()
    }

    override fun getUserProfileDetailsAsFlow(): Flow<UserProfileDatastoreModel?> {
        return userProfileAndAuthStoreManager.getUserProfileDetails()
    }

    override suspend fun clearStoredUserProfileAndAuthTokensDetails() {
        userProfileAndAuthStoreManager.apply {
            clearStoredUserDetails()
            clearStoredAuthTokensDetails()
        }
    }

    override fun isUserLoggedIn(): Flow<Boolean> {
        return userProfileAndAuthStoreManager.getUserProfileDetails()
            .map { it?.userId != null }
    }
}
