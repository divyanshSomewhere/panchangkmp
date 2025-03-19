package com.gometro.login.data.models.request

import kotlinx.serialization.Serializable

@Serializable
data class LogoutUserRequestApiModel(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val deviceId: String
)
