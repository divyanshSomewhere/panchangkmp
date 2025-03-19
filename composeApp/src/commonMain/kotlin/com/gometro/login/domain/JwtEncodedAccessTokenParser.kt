package com.gometro.login.domain

import com.gometro.base.utils.CustomJsonParser
import com.gometro.base.utils.CustomJsonParser.jsonPrimitiveSafe
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import okio.internal.commonToUtf8String
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
class JwtEncodedAccessTokenParser {

    @Throws(AccessTokenParseFailedException::class)
    operator fun invoke(accessToken: String): ParsedTokenResult {
        try {
            val split = accessToken.split(".")
            val decodedAccessToken = getJson(split[1])
//            val accessTokenBody = JSONObject(decodedAccessToken)
            val accessTokenBody = CustomJsonParser.Json.parseToJsonElement(decodedAccessToken).jsonObject
//            val expiryTimeString = accessTokenBody.getString(KEY_TOKEN_EXPIRY)
            val expiryTimeString = accessTokenBody[KEY_TOKEN_EXPIRY]?.jsonPrimitiveSafe?.contentOrNull ?: ""
//            val issuedTimeString = accessTokenBody.getString(KEY_TOKEN_ISSUED_AT)
            val issuedTimeString = accessTokenBody[KEY_TOKEN_ISSUED_AT]?.jsonPrimitiveSafe?.contentOrNull ?: ""

            return ParsedTokenResult(
                expiryTime = expiryTimeString.toLongOrNull(),
                issuedAt = issuedTimeString.toLongOrNull()
            )
        } catch (e: Exception) {
            throw AccessTokenParseFailedException(e.message)
        }
    }

    private fun getJson(encodedString: String): String {
        return Base64.UrlSafe
            .withPadding(Base64.PaddingOption.PRESENT_OPTIONAL)
            .decode(source = encodedString).commonToUtf8String()
    }

    companion object {
        private const val KEY_TOKEN_EXPIRY = "exp"
        private const val KEY_TOKEN_ISSUED_AT = "iat"
    }
}

class AccessTokenParseFailedException(msg: String?) : Exception(msg)

data class ParsedTokenResult(
    val expiryTime: Long?,
    val issuedAt: Long?
)
