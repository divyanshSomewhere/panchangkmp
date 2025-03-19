package com.gometro.login.constants

object LoginFeatureErrorCodes {
    const val LOGIN_OPTION_SEND_OTP_SERVER_ERROR = 100
    const val LOGIN_OPTION_SEND_OTP_INVALID_REF_NO = 101
    const val LOGIN_OPTION_SEND_OTP_OTP_STATUS_FALSE = 102
    const val LOGIN_OPTION_SEND_OTP_PARSE_EXCEPTION = 103
    const val LOGIN_OPTION_SEND_OTP_UNKNOWN_ERROR = 104
    const val LOGIN_OPTION_SEND_OTP_PREVIOUS_OTP_EXPIRED = 112

    const val LOGIN_VERIFY_UNKNOWN_ERROR = 105
    const val LOGIN_VERIFY_INVALID_TOKENS_RECEIVED = 106
    const val LOGIN_VERIFY_TOKEN_PROCESSING_ERROR = 107
    const val LOGIN_VERIFY_LOCAL_ERROR = 108
    const val LOGIN_VERIFY_INVALID_OTP = 109
    const val LOGIN_VERIFY_INVALID_PROFILE = 110
    const val LOGIN_VERIFY_PARSE_ERROR = 111
}