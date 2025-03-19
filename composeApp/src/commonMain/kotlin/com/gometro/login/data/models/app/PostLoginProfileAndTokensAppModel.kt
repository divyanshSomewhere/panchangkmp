package com.gometro.login.data.models.app

import com.gometro.userprofile.data.models.UserProfileAppModel


data class PostLoginProfileAndTokensAppModel(
    val accessToken: String,
    val refreshToken: String,
    val userProfile: UserProfileAppModel
)
