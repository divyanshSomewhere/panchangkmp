package com.gometro.login.data

import com.gometro.base.utils.CustomJsonParser
import com.gometro.base.utils.CustomJsonParser.jsonPrimitiveSafe
import com.gometro.login.data.exceptions.RefreshAuthTokensFailedException
import com.gometro.login.data.exceptions.SendOtpFailedException
import com.gometro.login.data.exceptions.TruecallerUidFetchFailedException
import com.gometro.network.model.GenericApiCallErrorResponse
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject

/**
 * Custom remote exception and error handler for login apis.
 * Some old apis were not in the structure in which we generally get error responses
 * so handling them here.
 */
class LoginRemoteExceptionAndErrorHandler {

    fun createGenericChaloErrorResponseFromJsonString(
        jsonString: String?,
        apiCallType: LoginApiCallsType
    ): GenericApiCallErrorResponse {
        return when (apiCallType) {
            LoginApiCallsType.GENERATE_UID,
            LoginApiCallsType.SEND_OTP,
            LoginApiCallsType.REFRESH_TOKENS -> {
                jsonString?.let {
                    try {
                        val errorBody = CustomJsonParser.Json.parseToJsonElement(jsonString).jsonObject
                        val errorCode = errorBody["statusCode"]?.jsonPrimitiveSafe?.intOrNull ?: -1
                        val message = errorBody["message"]?.jsonPrimitiveSafe?.contentOrNull ?: "Something went wrong"
                        GenericApiCallErrorResponse(errorCode, message, null, null)
                    } catch (e: Exception) {
                        null
                    }
                } ?: GenericApiCallErrorResponse(-1, "", null, null)
            }
        }
    }

    fun createLoginCallsException(
        genericChaloErrorResponse: GenericApiCallErrorResponse?,
        apiCallType: LoginApiCallsType
    ): Exception {
        return when (apiCallType) {
            LoginApiCallsType.GENERATE_UID -> {
                TruecallerUidFetchFailedException(genericChaloErrorResponse, genericChaloErrorResponse?.message)
            }
            LoginApiCallsType.SEND_OTP -> {
                SendOtpFailedException(genericChaloErrorResponse, genericChaloErrorResponse?.message)
            }
            LoginApiCallsType.REFRESH_TOKENS -> {
                RefreshAuthTokensFailedException(genericChaloErrorResponse, genericChaloErrorResponse?.message)
            }
        }
    }
}

enum class LoginApiCallsType {
    GENERATE_UID,
    SEND_OTP,
    REFRESH_TOKENS
}
