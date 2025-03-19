package com.gometro.userprofile.data.remote

import com.gometro.login.data.models.response.UserProfileResponseModel
import com.gometro.network.NetworkManager
import com.gometro.network.mapper.GenericNetworkExceptionMapper
import com.gometro.network.rest.request.HttpRequestType
import com.gometro.userprofile.data.exceptions.UpdateUserProfileFailedException
import com.gometro.userprofile.data.models.request.UpdateUserProfileRequestApiModel


class UserProfileRemoteDataSourceImpl(
    private val networkManager: NetworkManager,
    private val genericNetworkExceptionMapper: GenericNetworkExceptionMapper
) : UserProfileRemoteDataSource {

    override suspend fun updateUserProfileOnServer(requestApiModel: UpdateUserProfileRequestApiModel): UserProfileResponseModel {
        val networkResponse = networkManager
            .getStandardNetworkRequestBuilder()
            .subUrl(UPDATE_USER_PROFILE_SUB_URL)
            .httpMethod(HttpRequestType.POST)
            .body(requestApiModel)
            .addSecureApiHeaders()
            .build()
            .processSync()

        return if (networkResponse.isSuccess) {
            networkResponse.getSuccessResponseOrThrowParseException()
        } else {
            throw genericNetworkExceptionMapper.invoke(networkResponse) {
                UpdateUserProfileFailedException(it, it?.message)
            }
        }
    }

    companion object {
        private const val UPDATE_USER_PROFILE_SUB_URL = "chaukidar/v1/app/user/update-profile"
    }
}
