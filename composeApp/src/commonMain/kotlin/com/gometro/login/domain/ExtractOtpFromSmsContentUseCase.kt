package com.gometro.login.domain

import com.gometro.login.constants.LoginGeneralConstants


class ExtractOtpFromSmsContentUseCase() {
    operator fun invoke(
        smsContent: String?,
        otpLength: Int = LoginGeneralConstants.DEFAULT_OTP_LENGTH
    ): String? {
        if (smsContent == null) return null

        val matcher = "\\d{$otpLength}".toRegex()

        val possibleOTPs = matcher.findAll(smsContent).toList().map { it.value }

        // ideally there will be only one item in this list, but if many, return first
        return possibleOTPs.firstOrNull()
    }

}