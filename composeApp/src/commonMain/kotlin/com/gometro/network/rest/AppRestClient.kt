package com.gometro.network.rest

import com.gometro.network.rest.request.MultipartRequestFormDataType
import com.gometro.network.rest.request.toFormPart
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AppRestClient(
    private val httpClient: HttpClient
) {

    suspend fun makeGetRequest(
        url: String,
        headerMap: Map<String, String>,
        queryMap: Map<String, String>
    ): HttpResponse {
        return httpClient.get(urlString = url) {
            addHeaders(headerMap)
            addQueryParameters(queryMap)
        }
    }

    suspend fun makePostRequest(
        url: String,
        headerMap: Map<String, String>,
        queryMap: Map<String, String>,
        body: Any?
    ): HttpResponse {
        return httpClient.post(urlString = url) {
            addHeaders(headerMap)
            addQueryParameters(queryMap)
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    suspend fun makeMultipartPostRequest(
        url: String,
        headerMap: Map<String, String>,
        fileItem: MultipartRequestFormDataType?,
        additionalInfo: MultipartRequestFormDataType?
    ): HttpResponse {
        return httpClient.submitFormWithBinaryData(
            url = url,
            formData = formData {
                fileItem?.let { this.append(fileItem.toFormPart()) }
                additionalInfo?.let { this.append(additionalInfo.toFormPart()) }
            }
        ) {
            addHeaders(headerMap)
        }
    }

    suspend fun makeDeleteRequest(
        url: String,
        headerMap: Map<String, String>,
        queryMap: Map<String, String>
    ): HttpResponse {
        return httpClient.delete(urlString = url) {
            addHeaders(headerMap)
            addQueryParameters(queryMap)
        }
    }

    suspend fun makePutRequest(
        url: String,
        headerMap: Map<String, String>,
        queryMap: Map<String, String>,
        body: Any?
    ): HttpResponse {
        return httpClient.put(urlString = url) {
            addHeaders(headerMap)
            addQueryParameters(queryMap)
            this.contentType(ContentType.Application.Json)
            this.setBody(body)
        }
    }

    private fun HttpRequestBuilder.addHeaders(headerMap: Map<String, String>) {
        this.headers { headerMap.forEach { this.append(it.key, it.value) } }
    }

    private fun HttpRequestBuilder.addQueryParameters(queryMap: Map<String, String>) {
        this.url { queryMap.forEach { this.parameters.append(it.key, it.value) } }
    }
}