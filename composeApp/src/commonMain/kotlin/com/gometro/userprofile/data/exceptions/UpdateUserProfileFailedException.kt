package com.gometro.userprofile.data.exceptions

import com.gometro.network.model.GenericApiCallErrorResponse


class UpdateUserProfileFailedException(
    val genericChaloErrorResponse: GenericApiCallErrorResponse?,
    msg: String?
) : Exception(msg)
