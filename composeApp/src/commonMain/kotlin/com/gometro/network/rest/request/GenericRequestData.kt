package com.gometro.network.rest.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
open class GenericRequestData {

    @SerialName("url")
    var url: String? = null
        get() = if (field != null) {
            field
        } else if (subUrl != null && baseUrl != null) {
            "$baseUrl/$subUrl"
        } else if (baseUrl != null) {
            baseUrl
        } else {
            null
        }

    @SerialName("baseUrl")
    var baseUrl: String? = null

    @SerialName("subUrl")
    var subUrl: String? = null

    @SerialName("httpMethod")
    var httpMethod: String? = null

    @SerialName("headers")
    var headers: HashMap<String, String> = HashMap()

    @SerialName("queryParams")
    var queryParams: MutableMap<String, String> = HashMap()

    @SerialName("pathParams")
    var pathParams: HashMap<String, String> = HashMap()

    @SerialName("singleBodyMediaType")
    var mediaType: String? = null

    @SerialName("bodyJSON")
    var bodyJSON: String? = null

    @SerialName("extraInfo")
    var extraInfo: String? = null

    @SerialName("priority")
    var priority: Int = 0

    @SerialName("content-type")
    var contentType: String? = null

    @SerialName("connect-timeout")
    var connectTimeout: Int? = null

    @SerialName("read-timeout")
    var readTimeout: Int? = null

    @SerialName("write-timeout")
    var writeTimeout: Int? = null

    @SerialName("retry-count")
    var retryCount: Int = 0

    @SerialName("retry-strategy")
    var retryStrategy: String? = RetryStrategyType.LINEAR_BACKOFF.name

    init {
        mediaType = APPLICATION_JSON
        httpMethod = HttpRequestType.GET.httpRequestType
    }

    // *********************************************************************
    // Transformation methods
    // *********************************************************************

    fun getPathTransformedFullUrl(pBaseUrl: String?): String {
        var baseUrl = pBaseUrl

        if (!this.baseUrl.isNullOrEmpty()) {
            baseUrl = this.baseUrl
        }

        return if (!this.url.isNullOrEmpty()) {
            replacePathParams(url, pathParams)!!
        } else {
            replacePathParams("$baseUrl/$subUrl", pathParams)!!
        }
    }

    override fun toString(): String {
        return "$url $baseUrl $subUrl $httpMethod $headers $queryParams $pathParams $bodyJSON $mediaType" +
                "$extraInfo $priority $contentType"
    }

    companion object {
        private const val APPLICATION_JSON = "application/json"

        fun isValidRequest(request: GenericRequestData): Boolean {
            // Validate Path Params
            if (!validateMap(request.pathParams)) {
                return false
            }

            // Validate Query Params
            if (!validateMap(request.queryParams)) {
                return false
            }

            val httpRequestType = HttpRequestType.from(request.httpMethod)
            if (httpRequestType == HttpRequestType.GET) {
                /* For GET request nothing is compulsory except a valid URL */
                return isValidUrl()
            }

            if (httpRequestType == HttpRequestType.DELETE) {
                /* For DELETE request nothing is compulsory except a valid URL */
                return isValidUrl()
            }

            if (httpRequestType == HttpRequestType.POST || httpRequestType == HttpRequestType.PUT) {
                if (request is MultipartRequestData) {
                    return request.isValidRequest()
                }

                if (request.mediaType == null || request.mediaType.isNullOrEmpty()) {
                    return false
                }

                return !(request.mediaType != null && !isSupportedMediaType(request.mediaType))
            }

            return false
        }

        private fun validateMap(map: Map<String, String>?): Boolean {
            map?.entries?.forEach { entry ->
                apply {
                    @Suppress("SENSELESS_COMPARISON")
                    // If its called from Kotlin, then this check is not required. But this map is also getting set from Java,
                    // where values can be null
                    if (entry.key == null || entry.value == null) return false
                }
            }

            return true
        }

        // *********************************************************************
        // Utility methods
        // *********************************************************************

        private fun isValidUrl(): Boolean {
            return true
        }

        private fun replacePathParams(url: String?, pathParams: Map<String, String>?): String? {
            if (url.isNullOrEmpty()) {
                return url
            }

            if (pathParams == null) {
                return url
            }

            var transformedUrl: String = url

            for ((key, value) in pathParams) {
                transformedUrl = transformedUrl.replace("{$key}", value)
            }

            return transformedUrl
        }

        private fun isSupportedMediaType(type: String?): Boolean {
            val validMediaTypes = listOf("application/json")
            return validMediaTypes.contains(type)
        }
    }
}
