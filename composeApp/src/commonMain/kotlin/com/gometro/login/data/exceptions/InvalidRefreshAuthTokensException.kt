package com.gometro.login.data.exceptions

import com.gometro.login.data.models.response.RefreshTokensResponseApiModel

class InvalidRefreshAuthTokensException(
    val refreshTokensResponseApiModel: RefreshTokensResponseApiModel,
    msg: String?
) : Exception(msg)
