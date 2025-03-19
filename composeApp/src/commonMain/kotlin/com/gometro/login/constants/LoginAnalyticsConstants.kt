package app.chalo.login.utils

object LoginAnalyticsConstants {

    // Login options
    const val EVENT_ENTER_NUMBER_SCREEN_OPENED = "login screen displayed"
    const val EVENT_TRUECALLER_UID_FETCH_TRIED = "login truecaller uid fetch try"
    const val EVENT_TRUECALLER_UID_FETCH_FAILED = "login truecaller uid fetch failed"
    const val EVENT_TRUECALLER_UID_FETCH_SUCCESS = "login truecaller uid fetch success"
    const val EVENT_CONTINUE_WITH_TRUECALLER = "continue with truecaller clicked"
    const val EVENT_LOGIN_CONTINUE_BTN_CLICKED = "login continue btn clicked"
    const val EVENT_LOGIN_SKIP_BTN_CLICKED = "login skip btn clicked"
    const val EVENT_OTP_SENT = "otp sent"
    const val EVENT_LOGIN_FAILED = "login failed"
    const val EVENT_LOGIN_OTP_REQUEST_FAILED = "login otp request failed"
    const val EVENT_TRUECALLER_BOTTOM_SHEET_RENDERED = "truecaller bottomsheet rendered for login"
    const val EVENT_LOGIN_SUCCESS = "login successful"
    const val EVENT_TRUECALLER_LOGIN_BOTTOM_SHEET_ERROR = "truecaller error callback"

    // Enter otp
    const val EVENT_ENTER_OTP_SCREEN_OPENED = "login otp screen displayed"
    const val EVENT_OTP_ENTERED = "otp entered"
    const val EVENT_RESEND_OTP_CLICKED = "resend otp button clicked"
    const val EVENT_OTP_RESEND_REQUEST_ERROR = "resend otp request failed"
    const val EVENT_OTP_RESENT = "otp resent"

    const val ATTR_COUNTRY_CALLING_CODE = "countryCallingCode"
    const val ATTR_IS_TRUECALLER_INSTALLED = "isTrueCallerInstalled"
    const val ATTR_LOGIN_OTP_REQUEST_ERROR = "otpRequestErrorMsg"
    const val ATTR_LOGIN_METHOD = "method"
    const val ATTR_LOGIN_FAILED_REASON = "reason"
    const val ATTR_IS_INVALID_OTP_ENTERED = "isInvalidOtpEntered"
    const val ATTR_SOURCE = "src"

    const val ERROR_REASON_INVALID_OTP = "Invalid otp entered"

    const val SOURCE_LOGIN_OPTIONS_SCREEN = "loginOptionsScreen"
    const val SOURCE_LOGIN_OTP_SCREEN = "loginOtpEnterScreen"
}
