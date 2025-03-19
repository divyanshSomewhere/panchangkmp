package com.gometro.login.ui.otp

/*

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import app.chalo.compose.theme.ChaloPadding
import app.chalo.compose.theme.ChaloThemeHelper
import app.chalo.compose.theme.UIShape
import app.chalo.uistate.base.BorderUISpec
import app.chalo.uistate.base.ChaloColorToken
import app.chalo.uistate.button.ButtonUIState
import app.chalo.uistate.button.ButtonUIStateFactory
import app.chalo.uistate.dialog.LoadingDialogUIState
import app.chalo.uistate.text.ChaloTextUIState
import app.chalo.uistate.toolbar.ToolbarUIState
import app.chalo.login.ui.LoginLoadingPurpose
import app.chalo.login.ui.otp.LoginOtpEnterViewModel.Companion.RESEND_OTP_OPTION_TIME_IN_MILLIS
import app.chalo.login.utils.DEFAULT_OTP_LENGTH
import app.chalo.login.utils.LoginAnalyticsConstants.ATTR_COUNTRY_CALLING_CODE
import app.chalo.uistate.textField.TextFieldUIState
import app.chalo.uistate.textField.TextFieldUIStateFactory
import co.touchlab.skie.configuration.annotations.SealedInterop

data class LoginOtpEnterDataState(
    val otpEntered: String = "",
    val isLoading: Boolean = false,
    val loadingPurpose: LoginLoadingPurpose = LoginLoadingPurpose.GENERAL,
    val otpLength: Int = DEFAULT_OTP_LENGTH,
    val isVerifyBtnEnabled: Boolean = false,
    val shouldShowInvalidOtpError: Boolean = false,
    val remainingTimeInMillisForEnablingResend: Long = RESEND_OTP_OPTION_TIME_IN_MILLIS,
    val isResendBtnEnabled: Boolean = false,
    val refNo: String? = null,
    val phoneNumber: String? = null,
    val countryCode: String? = null,
    val source: String = ""
)

@Immutable
data class LoginOtpEnterUIState(
    val screenSpecs: LoginOtpScreenUISpecs = LoginOtpScreenUISpecs(),
    val loadingDialog: LoadingDialogUIState? = null,
    val toolbar: ToolbarUIState = ToolbarUIState(),
    val headingText: ChaloTextUIState = ChaloTextUIState(""),
    val resendText: LoginOtpResendTextUIState? = null,
    val verifyBtn: ButtonUIState = ButtonUIStateFactory.noOp(),
    val otpEnterField: LoginOtpEnterTextFieldUIState = LoginOtpEnterTextFieldUIState(),
    val invalidOtpErrorText: ChaloTextUIState? = null
)

@Immutable
data class LoginOtpResendTextUIState(
    val descriptionText: ChaloTextUIState = ChaloTextUIState(""),
    val resendClickableText: ChaloTextUIState? = null,
    val spaceBetweenDescriptionAndResendText: Dp = ChaloThemeHelper.dimens.small2,
)

@Immutable
data class LoginOtpResendTextUISpec(
    val spaceBetweenDescriptionAndResendText: Dp = ChaloThemeHelper.dimens.small2,
)

fun LoginOtpEnterDataState.addToAnalyticsParamsMap(paramsMap: MutableMap<String, String>) {
    paramsMap[ATTR_COUNTRY_CALLING_CODE] = countryCode ?: ""
}

@Immutable
data class LoginOtpEnterTextFieldUIState(
    val textField: TextFieldUIState = TextFieldUIStateFactory.noOp(),
    val decorationIndividualFields: List<LoginOtpEnterIndividualBoxUIState> = listOf(),
    val specs: LoginOtpEnterTextFieldUISpec = LoginOtpEnterTextFieldUISpec()
)

@Immutable
data class LoginOtpEnterTextFieldUISpec(
    val spaceBetweenFields: Dp = ChaloThemeHelper.dimens.small2,
)

@Immutable
@SealedInterop.Enabled
sealed class LoginOtpEnterIndividualBoxUIState(
    val borderUISpec: BorderUISpec = BorderUISpec(
        width = ChaloThemeHelper.dimens.noPadding,
        color = ChaloColorToken.StrikeOffFontColor,
        shape = UIShape.MediumRoundedCorner
    ),
    val height: Dp = ChaloThemeHelper.dimens.large6
) {

    data class Text(
        val text: ChaloTextUIState,
        private val _borderUISpec: BorderUISpec
    ) : LoginOtpEnterIndividualBoxUIState(borderUISpec = _borderUISpec)

    data class BlinkingCursor(
        val width: Dp = ChaloThemeHelper.dimens.small1,
        val color: ChaloColorToken = ChaloColorToken.OrangePrimary,
        val padding: ChaloPadding = ChaloPadding(vertical = ChaloThemeHelper.dimens.medium2),
        private val _borderUISpec: BorderUISpec
    ) : LoginOtpEnterIndividualBoxUIState(borderUISpec = _borderUISpec)

}

@Immutable
data class LoginOtpScreenUISpecs(
    val containerBackgroundColor: ChaloColorToken = ChaloColorToken.White,
    val titleAndMainContentPadding: ChaloPadding = ChaloPadding(horizontal = ChaloThemeHelper.dimens.medium3),
    val verifyBtnPadding: ChaloPadding = ChaloPadding(all = ChaloThemeHelper.dimens.medium3),
    val titleAndTextFieldSpacing: Dp = ChaloThemeHelper.dimens.medium4,
    val textFieldAndResendOtpMsgSpacing: Dp = ChaloThemeHelper.dimens.medium3
)

@SealedInterop.Enabled
sealed class LoginOtpEnterIntent {
    data class InitializationIntent(
        val refNo: String,
        val phoneNumber: String,
        val countryCode: String,
        val source: String
    ) : LoginOtpEnterIntent()
    data class OnOtpEnteredIntent(val otpEntered: String) : LoginOtpEnterIntent()
    object OnVerifyClickedIntent : LoginOtpEnterIntent()
    object OnResendOtpClickedIntent : LoginOtpEnterIntent()
    data class OnResendOtpEnableCountdownUpdate(val remainingTimeInMillisForEnablingResend: Long) : LoginOtpEnterIntent()
    data class OnOtpSMSReadSuccessfully(val msg: String) : LoginOtpEnterIntent()
    data object OnActiveIntent : LoginOtpEnterIntent()
    data object OnInactiveIntent : LoginOtpEnterIntent()
}

@SealedInterop.Enabled
sealed class LoginOtpEnterSideEffect {
    object BringFocusOnOtpField : LoginOtpEnterSideEffect()
    data class ShowToast(val toastMsg: String) : LoginOtpEnterSideEffect()
    data object ClearFocus : LoginOtpEnterSideEffect()
    data object HideKeyboard : LoginOtpEnterSideEffect()
}


 */