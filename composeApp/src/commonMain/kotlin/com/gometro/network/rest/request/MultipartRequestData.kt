package com.gometro.network.rest.request

import io.ktor.client.request.forms.FormPart
import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpHeaders
import io.ktor.http.escapeIfNeeded
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class MultipartRequestData : GenericRequestData() {

    @SerialName("file-part")
    var fileData: MultipartRequestFormDataType? = null

    @SerialName("additional-info-part")
    var additionalInfoData: MultipartRequestFormDataType? = null

    fun isValidRequest(): Boolean {
        return fileData != null || additionalInfoData != null
    }

}

@Serializable
sealed class MultipartRequestFormDataType(
    open val key: String,
    open val fileName: String?
) {
    data class StringData(
        override val key: String,
        override val fileName: String? = null,
        val data: String
    ) : MultipartRequestFormDataType(key, fileName)
    data class NumberData(
        override val key: String,
        override val fileName: String? = null,
        val data: Number
    ) : MultipartRequestFormDataType(key, fileName)
    class ByteArrayData(
        override val key: String,
        override val fileName: String? = null,
        val data: ByteArray
    ) : MultipartRequestFormDataType(key, fileName)
}

internal fun MultipartRequestFormDataType.toFormPart(): FormPart<out Any> {
    val headers = this.fileName?.let { fileName ->
        HeadersBuilder().apply {
            this[HttpHeaders.ContentDisposition] = "filename=${fileName.escapeIfNeeded()}"
        }.build()
    } ?: Headers.Empty

    return when(this) {
        is MultipartRequestFormDataType.ByteArrayData -> FormPart<ByteArray>(this.key, this.data, headers)
        is MultipartRequestFormDataType.NumberData -> FormPart<Number>(this.key, this.data, headers)
        is MultipartRequestFormDataType.StringData -> FormPart<String>(this.key, this.data, headers)
    }
}
