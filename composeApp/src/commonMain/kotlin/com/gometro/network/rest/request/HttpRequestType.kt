package com.gometro.network.rest.request

enum class HttpRequestType(val httpRequestType: String) {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    UNKNOWN("UNKNOWN");

    companion object {
        fun from(value: String?): HttpRequestType {
            if (value == null) {
                return UNKNOWN
            }
            for (type in values()) {
                if (type.httpRequestType == value) {
                    return type
                }
            }
            return UNKNOWN
        }
    }
}
