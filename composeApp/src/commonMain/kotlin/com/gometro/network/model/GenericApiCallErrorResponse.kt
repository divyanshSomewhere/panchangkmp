package com.gometro.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GenericApiCallErrorResponse(
    @SerialName("code")
    val errorCode: Int = -1,
    @SerialName("message")
    val message: String? = null,
    @SerialName("status")
    val status: String? = null,
    @SerialName("details")
    val errorDetails: List<ErrorDetails>? = null
)

@Serializable
data class ErrorDetails(
    @SerialName("type")
    val type: String? = null,
    @SerialName("reason")
    val reason: String? = null,
    @SerialName("metadata")
    val metaData: Map<String, String>? = null
)

@Serializable
data class ApiCallError(
    @SerialName("error")
    val genericApiCallErrorResponse: GenericApiCallErrorResponse? = null
)