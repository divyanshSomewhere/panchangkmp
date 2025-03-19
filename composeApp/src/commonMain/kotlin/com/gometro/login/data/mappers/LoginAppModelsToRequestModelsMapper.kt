package com.gometro.login.data.mappers

import app.chalo.login.data.models.request.LoginApiRequestModel
import app.chalo.login.utils.LoginAnalyticsConstants
import com.gometro.login.data.models.app.LoginModeAppModel
import com.gometro.login.constants.LoginGeneralConstants


fun LoginModeAppModel.toLoginApiRequestModel(
    deviceId: String,
    shouldOmitPlusSignFromCountryCode: Boolean = true
): LoginApiRequestModel {
    return when (this) {
        is LoginModeAppModel.PhoneAuthLoginModel -> {
            val updatedCountryCode = LoginGeneralConstants.COUNTRY_CODE_IND
            LoginApiRequestModel.PhoneAuthLoginApiRequestModel(
                deviceId = deviceId,
                mobileNumber = phoneNumber,
                countryCode = updatedCountryCode,
                otp = otp,
                refNo = refNo
            )
        }
        is LoginModeAppModel.TruecallerLoginModel -> {
            LoginAnalyticsConstants
            val updatedCountryCode = LoginGeneralConstants.COUNTRY_CODE_IND
            LoginApiRequestModel.TruecallerLoginApiRequestModel(
                deviceId = deviceId,
                payload = payload,
                signature = signature,
                signatureAlgorithm = signatureAlgorithm,
                uid = uid,
                mobileNumber = phoneNumber,
                countryCode = updatedCountryCode,
                firstName = firstName,
                lastName = lastName
            )
        }
    }
}
