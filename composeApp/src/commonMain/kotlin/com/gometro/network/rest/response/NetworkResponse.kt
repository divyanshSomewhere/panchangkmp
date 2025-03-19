package com.gometro.network.rest.response

import com.gometro.base.utils.CustomJsonParser
import com.gometro.logger.CrashlyticsLogger
import com.gometro.network.exception.NetworkErrorResponseParseException
import com.gometro.network.exception.NetworkSuccessResponseParseException
import com.gometro.network.rest.generic.ErrorType
import kotlinx.serialization.json.Json


/**
 * Response for Generic Network Request
 */
data class NetworkResponse(
    val isSuccess: Boolean,
    val errorType: ErrorType,
    val response: String?,
    val headers: Map<String, List<String>>?,
    val httpResponseCode: Int,
    val crashlyticsLogger: CrashlyticsLogger
) {

    companion object {
        fun errorFormat(msg: String?, className: String?, response: String?): String {
            return "$msg Name : $className response : $response"
        }
    }

    /**
     *
     * This method is used to parse the response json to the given class using kotlinx serialization
     * @see KotlinJson is kotlinx serialization json instance which is used to serialize/deserialize data classes
     */
    @Throws(NetworkSuccessResponseParseException::class)
    inline fun <reified T_SUCCESS> getSuccessResponseOrThrowParseException(json: Json = CustomJsonParser.Json): T_SUCCESS {
        var responseObject: T_SUCCESS? = null
        // responseObject will stay null in 2 cases,
        // 1- some exception occurred in gson
        // 2- no exception occurred, but response json was null or empty
        // hence this default msg for parseException
        var parseException =
            NetworkSuccessResponseParseException("Response json is either null or empty")
        try {
            response?.let {
                responseObject = json.decodeFromString(it)
            }
        } catch (e: Exception) {
            parseException = NetworkSuccessResponseParseException(
                errorFormat(
                    e.message,
                    T_SUCCESS::class.simpleName,
                    response
                )
            )
            crashlyticsLogger.reportHandledException(parseException)
        }

        return responseObject ?: throw parseException
    }

    inline fun <reified T_Error> getErrorResponse(): T_Error? {
        var responseObject: T_Error? = null
        try {
            responseObject = response?.let { CustomJsonParser.Json.decodeFromString(it) }
        } catch (e: Exception) {
            crashlyticsLogger.reportHandledException(
                NetworkErrorResponseParseException(
                    errorFormat(
                        e.message,
                        T_Error::class.simpleName,
                        response
                    )
                )
            )
        }

        return responseObject
    }

    fun getResponseHeaders(header: String): List<String>? {
        return headers?.get(header)
    }
}
