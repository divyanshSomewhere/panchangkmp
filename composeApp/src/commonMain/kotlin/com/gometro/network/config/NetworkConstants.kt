package com.gometro.network.config

object NetworkConstants {


    object Analytics {
        // Category
        const val CATEGORY_NETWORK_CALL = "NETWORK_CALL"
        const val INTERCEPTOR = "interceptor"

        // Events

        const val ACTION_NETWORK_INTERCEPTOR_TIME = "NETWORK_INTERCEPTOR_TIME"

        // Attributes
        const val CALL_LATENCY = "callLatency"
        const val URL = "url"
        const val INTERCEPTOR_LATENCY = "interceptorLatency"
    }

    object Header {
        const val KEY_CONTENT_TYPE = "Content-Type"
        const val KEY_ACCEPT = "accept"
        const val CONTENT_TYPE_JSON = "application/json"

        const val KEY_CONNECTION_TIMEOUT = "connection_timeout"
        const val KEY_READ_TIMEOUT = "read_timeout"
        const val KEY_WRITE_TIMEOUT = "write_timeout"

        const val SOURCE = "source"
        const val DEVICE_ID = "deviceId"
        const val APP_VERSION = "appVer"

        const val SECURE_API_HEADERS = "secureApiHeaders"
        const val USER_ID = "userId"
        const val AUTH_TYPE = "authType"
        const val ACCESS_TOKEN = "accessToken"
        const val X_TYPE = "x-type"
    }

    object Config {
        const val MAX_KEEP_ALIVE: Long = 60
        const val CONNECTION_TIMEOUT_IN_MILLIS = 15 * 1000
        const val OK_HTTP_READ_TIMEOUT_IN_MILLIS = 15 * 1000
        const val OK_HTTP_WRITE_TIMEOUT_IN_MILLIS = 15 * 1000

        const val MAX_IDLE_CONNECTIONS_FOR_HIGH_PRIORITY_CALLS = 1
        const val MAX_IDLE_CONNECTIONS_FOR_NORMAL_PRIORITY_CALL = 2
        const val MAX_IDLE_CONNECTIONS_FOR_LOW_PRIORITY_CALLS = 1

        const val AUTHENTICATION_RETRY_LIMIT = 4
    }

    object General {
        const val AUTH_TYPE_ACCESS_TOKEN = "ACCESS_TOKEN"
    }
}