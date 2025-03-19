package com.gometro.userprofile.data.local

import com.gometro.userprofile.data.models.UserProfileDatastoreModel
import kotlinx.coroutines.flow.Flow

interface UserProfileLocalDataSource {

    suspend fun updateUserProfileLocally(
        userProfileDatastoreModel: UserProfileDatastoreModel
    )

    suspend fun getUserProfileDetails(): UserProfileDatastoreModel?

    fun getUserProfileDetailsAsFlow(): Flow<UserProfileDatastoreModel?>

    suspend fun clearStoredUserProfileAndAuthTokensDetails()

    fun isUserLoggedIn(): Flow<Boolean>

}
