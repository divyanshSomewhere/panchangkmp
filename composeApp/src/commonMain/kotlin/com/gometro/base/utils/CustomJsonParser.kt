package com.gometro.base.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive


object CustomJsonParser {
    @OptIn(ExperimentalSerializationApi::class)
    val Json = Json {
        // Setting this true means if json contains any unknown keys, it will not be treated as an
        // error and will simply ignore those keys
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        // Setting this false means if variable is nullable, and any key is not present in json
        // it will set the value for that variable to null
        explicitNulls = false
    }

    val JsonElement.jsonPrimitiveSafe: JsonPrimitive?
        get() {
            return try {
                this.jsonPrimitive
            } catch (e: Exception) {
                null
            }
        }

    /**
     * Creates Json instance with custom configuration. Basic fields are automatically set same as ChaloJson.Json
     * but can be edited if needed by specifying in block
     */
    @OptIn(ExperimentalSerializationApi::class)
    fun customJson(block: JsonBuilder.() -> Unit): Json {
        return Json {
            // Setting this true means if json contains any unknown keys, it will not be treated as an
            // error and will simply ignore those keys
            ignoreUnknownKeys = true
            isLenient = true
            coerceInputValues = true
            // Setting this false means if variable is nullable, and any key is not present in json
            // it will set the value for that variable to null
            explicitNulls = false

            block()
        }
    }

    /**
     * Decodes and deserializes the given JSON string to the value of type T using deserializer
     * retrieved from the reified type parameter or null if any exception
     */
    inline fun <reified T> Json.decodeFromStringSafely(string: String): T? {
        return try {
            Json.decodeFromString<T>(string)
        } catch (e: Exception) {
            null
        }
    }

    inline fun <reified T> Json.encodeToStringSafely(any: T): String? {
        return try {
            Json.encodeToString(any)
        } catch (e: Exception) {
            null
        }
    }
}
