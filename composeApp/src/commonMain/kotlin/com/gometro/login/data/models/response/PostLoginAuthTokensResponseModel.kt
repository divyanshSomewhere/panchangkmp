package com.gometro.login.data.models.response

import kotlinx.serialization.Serializable

@Serializable
data class PostLoginAuthTokensResponseModel(
    val accessToken: String?,
    val refreshToken: String?,
    val status: Boolean?,
    val userProfile: UserProfileResponseModel?
)
