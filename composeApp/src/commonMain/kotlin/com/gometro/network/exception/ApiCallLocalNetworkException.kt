package com.gometro.network.exception

import com.gometro.network.model.GenericApiCallErrorResponse

sealed class ApiCallLocalNetworkException(msg: String) : Exception(msg) {

    // ErrorType.TYPE_NO_INTERNET
    class NoInternetException(
        val msg: String
    ) : ApiCallLocalNetworkException(msg)

    // ErrorType.TYPE_UNKNOWN
    class UnexpectedException(
        val genericChaloErrorResponse: GenericApiCallErrorResponse?,
        val msg: String
    ) : ApiCallLocalNetworkException(msg)

    // ErrorType.TYPE_SERVER_ERROR
    class ServerErrorException(
        val genericChaloErrorResponse: GenericApiCallErrorResponse?,
        val msg: String
    ) : ApiCallLocalNetworkException(msg)

    // ErrorType.TYPE_REQUEST_CANCELLED
    class RequestCancelledException(
        val msg: String
    ) : ApiCallLocalNetworkException(msg)

    // ErrorType.TYPE_UNAUTHORIZED
    // This error is thrown when a secured api call fails with 401 AND refresh token api also fails
    // with response code != 401
    // Reason - whenever any secured api returns 401, [TokenRefreshAuthenticator] tries to refresh
    // the tokens and retry the secured api call with refreshed token. If refresh token api fails
    // because of any error not equal to 401, we do not retry the secured api call and it should
    // be handled by the feature usecase itself, if retry attempts limit is crossed in authenticator, we logout
    // the user and take them to login screen
    class SecureCallUnauthorizedAndRefreshTokenServerUnreachableException(
        val genericChaloErrorResponse: GenericApiCallErrorResponse?,
        val msg: String
    ) : ApiCallLocalNetworkException(msg)

    class NoUpdateInFetchedDataBasedOnEtagVersion(
        val genericChaloErrorResponse: GenericApiCallErrorResponse?,
        val msg: String
    ) : ApiCallLocalNetworkException(msg)
}
