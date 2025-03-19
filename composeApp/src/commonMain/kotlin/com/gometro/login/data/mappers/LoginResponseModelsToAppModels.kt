package com.gometro.login.data.mappers

import com.gometro.userprofile.data.models.Gender
import com.gometro.userprofile.data.models.UserProfileAppModel
import com.gometro.login.data.exceptions.InvalidRefreshAuthTokensException
import com.gometro.login.data.exceptions.ProfileAndTokensExceptions
import com.gometro.login.data.models.app.PostLoginProfileAndTokensAppModel
import com.gometro.login.data.models.app.RefreshTokenResponseAppModel
import com.gometro.login.data.models.response.PostLoginAuthTokensResponseModel
import com.gometro.login.data.models.response.RefreshTokensResponseApiModel
import com.gometro.login.data.models.response.UserProfileResponseModel

@Throws(ProfileAndTokensExceptions::class)
fun PostLoginAuthTokensResponseModel.toPostLoginProfileAndTokensAppModel(): PostLoginProfileAndTokensAppModel {
    if (accessToken.isNullOrEmpty()) {
        throw ProfileAndTokensExceptions.InvalidAccessToken(accessToken, "null or empty access token received")
    }
    if (refreshToken.isNullOrEmpty()) {
        throw ProfileAndTokensExceptions.InvalidRefreshToken(refreshToken, "null or empty refresh token received")
    }

    val userProfile = userProfile?.toUserProfileAppModel()
        ?: throw ProfileAndTokensExceptions.InvalidProfileDetails(userProfile, "null userProfile received")

    return PostLoginProfileAndTokensAppModel(
        accessToken = accessToken,
        refreshToken = refreshToken,
        userProfile = userProfile
    )
}

@Throws(ProfileAndTokensExceptions.InvalidProfileDetails::class)
fun UserProfileResponseModel.toUserProfileAppModel(): UserProfileAppModel {
    if (mobileNumber == null || countryCode == null || userId == null) {
        throw ProfileAndTokensExceptions.InvalidProfileDetails(
            profileReceived = this,
            msg = "mandatory profile fields are null"
        )
    }

    return UserProfileAppModel(
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        profilePhoto = profilePhoto ?: "",
        gender = Gender.fromString(gender),
        mobileNumber = mobileNumber,
        dobInMillis = dateOfBirth,
        userId = userId
    )
}

@Throws(InvalidRefreshAuthTokensException::class)
fun RefreshTokensResponseApiModel.toRefreshTokenResponseAppModel(): RefreshTokenResponseAppModel {
    if (this.refreshToken.isNullOrEmpty() || this.accessToken.isNullOrEmpty()) {
        throw InvalidRefreshAuthTokensException(
            refreshTokensResponseApiModel = this,
            "refresh token / access token is either null or empty"
        )
    }

    return RefreshTokenResponseAppModel(
        accessToken = accessToken,
        refreshToken = refreshToken
    )
}
