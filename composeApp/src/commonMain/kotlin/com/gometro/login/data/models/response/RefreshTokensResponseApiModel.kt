package com.gometro.login.data.models.response

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokensResponseApiModel(
    val accessToken: String?,
    val refreshToken: String?
)
