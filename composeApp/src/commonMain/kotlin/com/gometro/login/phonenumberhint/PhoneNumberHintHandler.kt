package com.gometro.login.phonenumberhint

import kotlinx.coroutines.flow.Flow

interface PhoneNumberHintHandler {

    val hintResultState: Flow<PhoneNumberHintResult>

    fun requestPhoneNumberHint()

}

sealed class PhoneNumberHintResult {
    data class Success(val phoneNumber: String) : PhoneNumberHintResult()
    data class NoValidPhoneNumber(val error: String?) : PhoneNumberHintResult()
    data class UnknownException(val exception: Exception) : PhoneNumberHintResult()
}
