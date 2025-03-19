package com.gometro.login.manager

import com.gometro.base.featurecontracts.CoroutineContextProvider
import com.gometro.userprofile.data.local.UserProfileLocalDataSource
import com.gometro.userprofile.data.models.UserProfileAppModel
import com.gometro.userprofile.data.models.toUserProfileAppModel
import com.gometro.userprofile.domain.UserProfileDetailsProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking

class UserProfileDetailsProviderImpl(
    private val userProfileLocalDataSource: UserProfileLocalDataSource,
    private val coroutineContextProvider: CoroutineContextProvider
) : UserProfileDetailsProvider {

    private val scope = CoroutineScope(coroutineContextProvider.io)

    override val isLoggedIn: StateFlow<Boolean> =
        userProfileLocalDataSource.isUserLoggedIn()
            .stateIn(scope, SharingStarted.Eagerly, false)

    override suspend fun getUserIdAsync(): String? {
        return userProfileLocalDataSource.getUserProfileDetails()?.userId
    }

    override suspend fun getUserProfileDetailsAsync(): UserProfileAppModel? {
        return userProfileLocalDataSource.getUserProfileDetails()?.toUserProfileAppModel()
    }

    override fun getUserProfileDetailsAsFlow(): Flow<UserProfileAppModel?> {
        return userProfileLocalDataSource.getUserProfileDetailsAsFlow().map { it?.toUserProfileAppModel() }
    }

    override fun getUserId(): String? {
        return runBlocking {
            getUserIdAsync()
        }
    }

    override fun getUserProfileDetails(): UserProfileAppModel? {
        return runBlocking {
            getUserProfileDetailsAsync()
        }
    }
}
