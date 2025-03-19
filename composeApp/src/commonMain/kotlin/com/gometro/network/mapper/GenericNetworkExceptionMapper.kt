package com.gometro.network.mapper

import com.gometro.base.utils.CustomJsonParser
import com.gometro.base.utils.CustomJsonParser.jsonPrimitiveSafe
import com.gometro.network.exception.ApiCallLocalNetworkException
import com.gometro.network.model.GenericApiCallErrorResponse
import com.gometro.network.model.ApiCallError
import com.gometro.network.rest.generic.ErrorType
import com.gometro.network.rest.response.NetworkResponse
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject

class GenericNetworkExceptionMapper  {

    operator fun invoke(
        networkResponse: NetworkResponse,
        unknownErrorToGenericChaloErrorResponse: ((String?) -> GenericApiCallErrorResponse)? = null,
        createException: (GenericApiCallErrorResponse?) -> Exception
    ): Exception {
        val genericChaloErrorResponse = networkResponse
            .getErrorResponse<ApiCallError>()?.genericApiCallErrorResponse
            ?: unknownErrorToGenericChaloErrorResponse?.invoke(networkResponse.response)
            ?: convertUnknownErrorToGenericChaloErrorResponse(networkResponse.response)

        val localException = when (networkResponse.errorType) {
            ErrorType.TYPE_SERVER_ERROR -> {
                ApiCallLocalNetworkException.ServerErrorException(
                    genericChaloErrorResponse,
                    "Some server error happened. errorType: ${networkResponse.errorType} and responseCode: ${networkResponse.httpResponseCode}"
                )
            }
            ErrorType.TYPE_NO_INTERNET -> {
                ApiCallLocalNetworkException.NoInternetException(
                    "No internet. errorType: ${networkResponse.errorType}"
                )
            }
            ErrorType.TYPE_REQUEST_CANCELLED -> {
                ApiCallLocalNetworkException.RequestCancelledException(
                    "Request cancelled. errorType: ${networkResponse.errorType}"
                )
            }
            ErrorType.TYPE_UNKNOWN -> {
                ApiCallLocalNetworkException.UnexpectedException(
                    genericChaloErrorResponse,
                    "Something unexpected happened. errorType: ${networkResponse.errorType}"
                )
            }
            ErrorType.TYPE_UNAUTHORIZED -> {
                ApiCallLocalNetworkException.SecureCallUnauthorizedAndRefreshTokenServerUnreachableException(
                    genericChaloErrorResponse,
                    "Unauthorized access, please try again later. errorType: ${networkResponse.errorType}"
                )
            }
            ErrorType.TYPE_NO_UPDATE_IN_DATA -> {
                ApiCallLocalNetworkException.NoUpdateInFetchedDataBasedOnEtagVersion(
                    genericChaloErrorResponse,
                    "No update in requested data based on etag. errorType: ${networkResponse.errorType}"
                )
            }
            else -> null
        }
        return localException ?: createException.invoke(genericChaloErrorResponse)
    }

    private fun convertUnknownErrorToGenericChaloErrorResponse(
        responseString: String?,
    ): GenericApiCallErrorResponse {
        return try {
            val errorObject = CustomJsonParser.Json.parseToJsonElement(responseString ?: "").jsonObject
            val errorType = errorObject["errorType"]?.jsonPrimitiveSafe?.contentOrNull ?: "UNKNOWN"
            val errorMessage = errorObject["errorMessage"]?.jsonPrimitiveSafe?.contentOrNull ?: ""

            val errorCode = if (errorType == "AUTHENTICATION_ERROR") {
                401
            } else {
                -1
            }
            GenericApiCallErrorResponse(errorCode, errorMessage, null, null)
        } catch (exception: Exception) {
            GenericApiCallErrorResponse(-1, "", null, null)
        }
    }
}