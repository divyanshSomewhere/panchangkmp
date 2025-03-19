package com.gometro.userprofile.domain

import com.gometro.userprofile.data.models.UserProfileAppModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow


interface UserProfileDetailsProvider {

    val isLoggedIn: StateFlow<Boolean>

    suspend fun getUserIdAsync(): String?

    suspend fun getUserProfileDetailsAsync(): UserProfileAppModel?

    fun getUserProfileDetailsAsFlow(): Flow<UserProfileAppModel?>

    fun getUserId(): String?

    fun getUserProfileDetails(): UserProfileAppModel?
}
