package com.gometro.network.rest.generic

enum class ErrorType {
    TYPE_NONE,
    TYPE_UNAUTHORIZED,
    TYPE_CLIENT_ERROR,
    TYPE_NETWORK_ERROR,
    TYPE_SERVER_ERROR,
    TYPE_NO_INTERNET,
    TYPE_REQUEST_CANCELLED,
    TYPE_COMMON_HEADER,
    TYPE_TIMEOUT,
    TYPE_UNKNOWN,
    TYPE_NO_UPDATE_IN_DATA;

    companion object {
        fun getErrorTypeFromHttpStatusCode(statusCode: Int): ErrorType {
            return when (statusCode) {
                401 -> TYPE_UNAUTHORIZED
                in 400 until 500 -> TYPE_CLIENT_ERROR
                in 500 until 600 -> TYPE_SERVER_ERROR
                else -> TYPE_UNKNOWN
            }
        }
    }
}

fun ErrorType.code(): Int {
    return when(this) {
        ErrorType.TYPE_NONE -> 0
        ErrorType.TYPE_UNAUTHORIZED -> 1000
        ErrorType.TYPE_CLIENT_ERROR -> 2000
        ErrorType.TYPE_NETWORK_ERROR -> 3000
        ErrorType.TYPE_SERVER_ERROR -> 4000
        ErrorType.TYPE_NO_INTERNET -> 5000
        ErrorType.TYPE_REQUEST_CANCELLED -> 6000
        ErrorType.TYPE_COMMON_HEADER -> 7000
        ErrorType.TYPE_TIMEOUT -> 8000
        ErrorType.TYPE_UNKNOWN -> 9000
        ErrorType.TYPE_NO_UPDATE_IN_DATA -> 10000
    }
}
