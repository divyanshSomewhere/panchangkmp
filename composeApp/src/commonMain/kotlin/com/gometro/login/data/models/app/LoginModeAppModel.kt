package com.gometro.login.data.models.app

sealed class LoginModeAppModel {

    data class TruecallerLoginModel(
        val payload: String,
        val signature: String,
        val signatureAlgorithm: String,
        val uid: String,
        val phoneNumber: String,
        val countryCode: String,
        val firstName: String,
        val lastName: String,
    ) : LoginModeAppModel()

    data class PhoneAuthLoginModel(
        val phoneNumber: String,
        val countryCode: String,
        val otp: String,
        val refNo: String,
        val refreshToken: Boolean = true
    ) : LoginModeAppModel()
}
