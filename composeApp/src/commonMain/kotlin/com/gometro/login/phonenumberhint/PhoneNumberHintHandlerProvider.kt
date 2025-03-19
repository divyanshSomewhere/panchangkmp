package com.gometro.login.phonenumberhint

interface PhoneNumberHintHandlerProvider {

    val phoneNumberHintHandler: PhoneNumberHintHandler?

}

interface PhoneNumberHintHandlerSetter : PhoneNumberHintHandlerProvider {
    fun setPhoneNumberHintHandler(handler: PhoneNumberHintHandler)
    fun removePhoneNumberHintHandler()
}

class PhoneNumberHintHandlerProviderImpl : PhoneNumberHintHandlerSetter {

    private var _phoneNumberHintHandler: PhoneNumberHintHandler? = null
    override val phoneNumberHintHandler: PhoneNumberHintHandler? get() = _phoneNumberHintHandler

    override fun setPhoneNumberHintHandler(handler: PhoneNumberHintHandler) {
        _phoneNumberHintHandler = handler
    }

    override fun removePhoneNumberHintHandler() {
        _phoneNumberHintHandler = null
    }
}