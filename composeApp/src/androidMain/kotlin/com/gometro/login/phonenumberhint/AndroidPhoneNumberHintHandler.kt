package com.gometro.login.phonenumberhint

import android.app.PendingIntent
import android.content.Context
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.flow.asSharedFlow

class AndroidPhoneNumberHintHandler(
    private val context: Context,
    private val launchRequest: PhoneNumberHintLaunchRequest
) : PhoneNumberHintHandler {

    private val signInClient by lazy {
        Identity.getSignInClient(context)
    }

    private val phoneNumberHintRequest: GetPhoneNumberHintIntentRequest
        get() = GetPhoneNumberHintIntentRequest.builder().build()

    private val _hintResultState = MutableSharedFlow<PhoneNumberHintResult>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val hintResultState: Flow<PhoneNumberHintResult> = _hintResultState.asSharedFlow()

    override fun requestPhoneNumberHint() {
        signInClient.getPhoneNumberHintIntent(phoneNumberHintRequest)
            .addOnSuccessListener { result: PendingIntent ->
                try {
                    launchRequest.invoke(result.toIntentSenderRequest())
                } catch (e: Exception) {
                    _hintResultState.tryEmit(PhoneNumberHintResult.UnknownException(e))
                }
            }
            .addOnFailureListener {
                _hintResultState.tryEmit(PhoneNumberHintResult.UnknownException(it))
            }
    }

    fun onResultReceived(result: ActivityResult) {
        try {
            val phoneNumber = signInClient.getPhoneNumberFromIntent(result.data)
            _hintResultState.tryEmit(PhoneNumberHintResult.Success(phoneNumber))
        } catch (e: ApiException) {
            _hintResultState.tryEmit(PhoneNumberHintResult.NoValidPhoneNumber(e.message))
        } catch (e: Exception) {
            _hintResultState.tryEmit(PhoneNumberHintResult.UnknownException(e))
        }
    }

    private fun PendingIntent.toIntentSenderRequest(): IntentSenderRequest {
        return IntentSenderRequest.Builder(this).build()
    }

}

typealias PhoneNumberHintLaunchRequest = (request: IntentSenderRequest) -> Unit