package com.gometro.userprofile.data.remote

import com.gometro.login.data.models.response.UserProfileResponseModel
import com.gometro.userprofile.data.models.request.UpdateUserProfileRequestApiModel

interface UserProfileRemoteDataSource {

    suspend fun updateUserProfileOnServer(requestApiModel: UpdateUserProfileRequestApiModel): UserProfileResponseModel
}
