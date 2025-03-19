package com.gometro.login.data.exceptions

import com.gometro.network.model.GenericApiCallErrorResponse


class TruecallerUidFetchFailedException(
    val genericApiErrorResponse: GenericApiCallErrorResponse?,
    msg: String?
) : Exception(msg)
