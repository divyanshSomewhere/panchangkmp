package com.gometro.network.exception

import io.ktor.utils.io.errors.IOException

internal sealed class BaseNetworkException(msg: String?, cause: Throwable?) : IOException(msg ?: "", cause)  {
    class InvalidAccessTokenUsedException(msg: String?) : BaseNetworkException(msg, null)

    class NetworkConnectionFailedException(cause: Throwable?) : BaseNetworkException(cause?.message, cause)

    class TimeoutException(msg: String?) : BaseNetworkException(msg, null)
}