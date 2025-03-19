package com.gometro.login.ui

import co.touchlab.skie.configuration.annotations.EnumInterop

@EnumInterop.Enabled
enum class LoginLoadingPurpose {
    REQUESTING_OTP,
    VERIFYING_LOGIN,
    GENERAL
}
