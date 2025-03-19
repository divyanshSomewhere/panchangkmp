package app.chalo.login.data.models.request

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("AuthType")
sealed class LoginApiRequestModel {
    @Serializable
    @SerialName("TrueCallerAuth")
    data class TruecallerLoginApiRequestModel(
        val deviceId: String,
        val payload: String,
        val signature: String,
        val signatureAlgorithm: String,
        val uid: String,
        val mobileNumber: String,
        val countryCode: String,
        val firstName: String,
        val lastName: String
    ) : LoginApiRequestModel()

    @Serializable
    @SerialName("SMS")
    data class PhoneAuthLoginApiRequestModel(
        val deviceId: String,
        val mobileNumber: String,
        val countryCode: String,
        val otp: String,
        val refNo: String
    ) : LoginApiRequestModel()
}
