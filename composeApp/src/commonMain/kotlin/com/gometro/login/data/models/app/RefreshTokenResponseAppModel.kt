package com.gometro.login.data.models.app

data class RefreshTokenResponseAppModel(
    val accessToken: String,
    val refreshToken: String
)
