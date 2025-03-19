package com.gometro.login.ui.otp

/*
import app.chalo.analytics.contract.AnalyticsContract
import app.chalo.compose.theme.ChaloThemeHelper
import app.chalo.compose.theme.UIShape
import app.chalo.constants.Source
import app.chalo.uistate.base.BorderUISpec
import app.chalo.uistate.base.ChaloColorToken
import app.chalo.uistate.base.UiEventHandler
import app.chalo.uistate.button.ButtonUIState
import app.chalo.uistate.button.ButtonUIStateFactory
import app.chalo.uistate.dialog.DialogUIStateFactory
import app.chalo.uistate.dialog.LoadingDialogUIState
import app.chalo.uistate.text.ChaloTextUIState
import app.chalo.uistate.text.UITextAlign
import app.chalo.uistate.text.UITextStyle
import app.chalo.uistate.toolbar.ToolbarUIState
import app.chalo.uistate.toolbar.ToolbarUIStateFactory
import app.chalo.extensions.ifNullOrEmptyThen
import app.chalo.login.data.models.app.LoginModeAppModel
import app.chalo.login.domain.ExtractOtpFromSmsContentUseCase
import app.chalo.login.domain.LoginVerificationResult
import app.chalo.login.domain.SendOtpForLoginErrorReason
import app.chalo.login.domain.SendOtpForLoginUseCase
import app.chalo.login.domain.SyncAndUpdateAnalyticsPropertiesAfterLoginUseCase
import app.chalo.login.domain.VerifyLoginSuccessOnServerAndHandleTokensUseCase
import app.chalo.login.domain.getCountdownTimerFlow
import app.chalo.login.smsautoread.SMSStatus
import app.chalo.login.smsautoread.SmsAutoReader
import app.chalo.login.ui.LoginLoadingPurpose
import app.chalo.login.utils.LoginAnalyticsConstants
import app.chalo.login.utils.LoginAnalyticsConstants.ERROR_REASON_INVALID_OTP
import app.chalo.login.utils.LoginAnalyticsConstants.EVENT_LOGIN_FAILED
import app.chalo.login.utils.LoginAnalyticsConstants.EVENT_LOGIN_SUCCESS
import app.chalo.login.utils.LoginAnalyticsConstants.EVENT_OTP_ENTERED
import app.chalo.login.utils.LoginAnalyticsConstants.EVENT_OTP_RESEND_REQUEST_ERROR
import app.chalo.login.utils.LoginAnalyticsConstants.EVENT_OTP_RESENT
import app.chalo.login.utils.LoginAnalyticsConstants.EVENT_RESEND_OTP_CLICKED
import app.chalo.login.utils.LoginAnalyticsConstants.SOURCE_LOGIN_OTP_SCREEN
import app.chalo.mvibase.simple.ChaloBaseStateMviViewModel
import app.chalo.providers.cityprovider.CityProvider
import app.chalo.providers.stringprovider.StringEnum
import app.chalo.providers.stringprovider.StringProvider
import app.chalo.scenes.ChaloNavOptions
import app.chalo.scenes.ChaloNavigationManager
import app.chalo.scenes.ChaloNavigationRequest
import app.chalo.scenes.ChaloScenes
import app.chalo.scenes.PopUpToConfig
import app.chalo.scenes.args.CitySelectionArgs
import app.chalo.scenes.args.HomeArgs
import app.chalo.scenes.args.LoginOtpSceneArgs
import app.chalo.uistate.text.UIFontWeight
import app.chalo.uistate.textField.TextFieldEventHandlerHelper
import app.chalo.uistate.textField.TextFieldUIState
import app.chalo.uistate.textField.UIImeAction
import app.chalo.uistate.textField.UIImeActionHandler
import app.chalo.uistate.textField.UIKeyboardOption
import app.chalo.uistate.textField.UIKeyboardType
import app.chalo.uistate.textField.UITextFieldColors
import app.chalo.utils.ChaloUseCaseResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.viewModelScope


class LoginOtpEnterViewModel(
    args: LoginOtpSceneArgs,
    private val sendOtpForLoginUseCase: SendOtpForLoginUseCase,
    private val verifyLoginSuccessOnServerAndHandleTokensUseCase: VerifyLoginSuccessOnServerAndHandleTokensUseCase,
    private val stringProvider: StringProvider,
    private val analyticsContract: AnalyticsContract,
    private val syncAndUpdateAnalyticsPropertiesAfterLoginUseCase: SyncAndUpdateAnalyticsPropertiesAfterLoginUseCase,
    private val extractOtpFromSmsContentUseCase: ExtractOtpFromSmsContentUseCase,
    private val cityProvider: CityProvider,
    private val chaloNavigationManager: ChaloNavigationManager,
    private val smsAutoReader: SmsAutoReader
) : ChaloBaseStateMviViewModel<LoginOtpEnterIntent, LoginOtpEnterDataState, LoginOtpEnterUIState, LoginOtpEnterSideEffect>() {

    private var resendOtpTimerJob: Job? = null
    private var smsAutoReadStatusJob: Job? = null

    override fun initialDataState(): LoginOtpEnterDataState {
        return LoginOtpEnterDataState()
    }

    init {
        processIntent(
            LoginOtpEnterIntent.InitializationIntent(
                refNo = args.refNo,
                phoneNumber = args.phoneNumber,
                countryCode = args.countryCallingCode,
                source = args.source.sourceName
            )
        )
    }

    override fun processIntent(intent: LoginOtpEnterIntent) {
        super.processIntent(intent)
        when (intent) {
            is LoginOtpEnterIntent.InitializationIntent -> handleInitializationIntent(intent)
            is LoginOtpEnterIntent.OnOtpEnteredIntent -> handleOnOtpEnteredIntent(intent)
            is LoginOtpEnterIntent.OnResendOtpClickedIntent -> handleOnResendOtpClickedIntent()
            is LoginOtpEnterIntent.OnVerifyClickedIntent -> handleOnVerifyClickedIntent()
            is LoginOtpEnterIntent.OnResendOtpEnableCountdownUpdate -> handleOnResendOtpEnableCountdownUpdateIntent(intent)
            is LoginOtpEnterIntent.OnOtpSMSReadSuccessfully -> handleOtpSMSReadSuccessfully(intent.msg)
            LoginOtpEnterIntent.OnActiveIntent -> handleOnActiveIntent()
            LoginOtpEnterIntent.OnInactiveIntent -> { smsAutoReadStatusJob?.cancel() }
        }
    }

    private fun handleOtpSMSReadSuccessfully(msg: String) {
        val extractedOtp = extractOtpFromSmsContentUseCase.invoke(
            smsContent = msg,
            otpLength = dataState.value.otpLength
        )

        extractedOtp?.let { otp ->
            if (otp.isNotBlank()) {
                updateState {
                    it.copy(
                        otpEntered = otp,
                        isVerifyBtnEnabled = otp.length == dataState.value.otpLength
                    )
                }

                handleOnVerifyClickedIntent()
            }
        }
    }

    private fun raiseAnalyticsEvent(
        mEventName: String? = null,
        additionalParamsToAdd: Map<String, String>? = null
    ) {
        mEventName?.let { eventName ->
            val paramsMap = mutableMapOf<String, String>().apply {
                dataState.value.addToAnalyticsParamsMap(this)
                additionalParamsToAdd?.let { this.putAll(it) }
            }

            analyticsContract.raiseAnalyticsEvent(
                name = eventName,
                source = SOURCE_LOGIN_OTP_SCREEN,
                eventProperties = paramsMap
            )
        }
    }

    private fun handleInitializationIntent(intent: LoginOtpEnterIntent.InitializationIntent) {
        if (initialIntentHandled) return
        initialIntentHandled = true

        startResendOtpEnableTimer()
        smsAutoReader.startSession()

        updateState {
            it.copy(
                refNo = intent.refNo,
                phoneNumber = intent.phoneNumber,
                countryCode = intent.countryCode,
                source = intent.source
            )
        }
        emitSideEffect(LoginOtpEnterSideEffect.BringFocusOnOtpField)
    }

    private fun handleOnOtpEnteredIntent(intent: LoginOtpEnterIntent.OnOtpEnteredIntent) {
        updateState {
            it.copy(
                otpEntered = intent.otpEntered,
                isVerifyBtnEnabled = intent.otpEntered.length == dataState.value.otpLength
            )
        }
    }

    private fun handleOnResendOtpClickedIntent() {
        val phoneNumber = dataState.value.phoneNumber
        val countryCode = dataState.value.countryCode
        val refNo = dataState.value.refNo

        viewModelScope.launch {
            if (phoneNumber != null && countryCode != null) {
                raiseAnalyticsEvent(EVENT_RESEND_OTP_CLICKED)
                updateState {
                    it.copy(
                        isLoading = true,
                        loadingPurpose = LoginLoadingPurpose.REQUESTING_OTP
                    )
                }

                when (val result = sendOtpForLoginUseCase.invoke(phoneNumber, countryCode, refNo)) {
                    is ChaloUseCaseResult.Failure -> {
                        when (result.error.reason) {
                            SendOtpForLoginErrorReason.SERVER_ERROR,
                            SendOtpForLoginErrorReason.UNKNOWN_ERROR -> {
                                val errorMsg = result.error.msg ifNullOrEmptyThen run {
                                    stringProvider.getString(
                                        StringEnum.GENERIC_ERROR_WITH_CODE,
                                        result.error.reason.getErrorCodeForReason()
                                    )
                                }
                                raiseOnOtpResendErrorAnalyticsEvent(errorMsg)
                                emitSideEffect(LoginOtpEnterSideEffect.ShowToast(errorMsg))
                            }

                            SendOtpForLoginErrorReason.INVALID_REF_NO,
                            SendOtpForLoginErrorReason.OTP_STATUS_FALSE,
                            SendOtpForLoginErrorReason.PARSE_EXCEPTION -> {
                                val errorMsg = stringProvider.getString(
                                    StringEnum.GENERIC_ERROR_WITH_CODE,
                                    result.error.reason.getErrorCodeForReason()
                                )
                                raiseOnOtpResendErrorAnalyticsEvent(errorMsg)
                                emitSideEffect(LoginOtpEnterSideEffect.ShowToast(errorMsg))
                            }

                            SendOtpForLoginErrorReason.PREVIOUS_OTP_EXPIRED -> {
                                val errorMsg = result.error.msg ifNullOrEmptyThen run {
                                    stringProvider.getString(
                                        StringEnum.GENERIC_ERROR_WITH_CODE,
                                        result.error.reason.getErrorCodeForReason()
                                    )
                                }
                                raiseOnOtpResendErrorAnalyticsEvent(errorMsg)
                                emitSideEffect(LoginOtpEnterSideEffect.ShowToast(errorMsg))
                                chaloNavigationManager.postNavigationRequest(
                                    navRequest = ChaloNavigationRequest.GoBack()
                                )
                            }
                        }
                    }

                    is ChaloUseCaseResult.Success -> {
                        startResendOtpEnableTimer()
                        raiseAnalyticsEvent(EVENT_OTP_RESENT)
                        updateState {
                            it.copy(
                                refNo = result.data,
                                isResendBtnEnabled = false
                            )
                        }
                        smsAutoReader.startSession()
                        emitSideEffect(LoginOtpEnterSideEffect.BringFocusOnOtpField)
                    }
                }
                updateState { it.copy(isLoading = false) }
            } else {
                val errorMsg = stringProvider.getString(StringEnum.SOMETHING_WENT_WRONG)
                updateState { it.copy(isLoading = false) }
                raiseOnOtpResendErrorAnalyticsEvent(errorMsg)
                emitSideEffect(LoginOtpEnterSideEffect.ShowToast(errorMsg))
            }
        }
    }

    private fun raiseOnOtpResendErrorAnalyticsEvent(errorMsg: String) {
        raiseAnalyticsEvent(
            EVENT_OTP_RESEND_REQUEST_ERROR,
            mapOf(LoginAnalyticsConstants.ATTR_LOGIN_OTP_REQUEST_ERROR to errorMsg)
        )
    }

    private fun handleOnVerifyClickedIntent() {
        val phoneNumber = dataState.value.phoneNumber
        val countryCode = dataState.value.countryCode
        val refNo = dataState.value.refNo

        viewModelScope.launch {
            if (phoneNumber != null && countryCode != null && refNo != null) {
                raiseAnalyticsEvent(EVENT_OTP_ENTERED)
                updateState {
                    it.copy(
                        isLoading = true,
                        loadingPurpose = LoginLoadingPurpose.VERIFYING_LOGIN
                    )
                }
                val phoneLoginModeAppModel = LoginModeAppModel.PhoneAuthLoginModel(
                    phoneNumber = phoneNumber,
                    countryCode = countryCode,
                    otp = dataState.value.otpEntered,
                    refNo = refNo
                )

                when (val result = verifyLoginSuccessOnServerAndHandleTokensUseCase.invoke(phoneLoginModeAppModel)) {
                    LoginVerificationResult.LoginVerified -> {
                        syncAndUpdateAnalyticsPropertiesAfterLoginUseCase.invoke()
                        raiseAnalyticsEvent(
                            EVENT_LOGIN_SUCCESS,
                            mapOf(LoginAnalyticsConstants.ATTR_LOGIN_METHOD to "otp")
                        )
                        updateState {
                            it.copy(
                                isLoading = false
                            )
                        }
                        handlePostLoginStep()
                    }

                    LoginVerificationResult.ServerError.InvalidOtpEntered -> {
                        raiseOnVerifyClickedErrorAnalyticsEvent(
                            errorMsg = ERROR_REASON_INVALID_OTP,
                            shouldShowInvalidOtpError = true
                        )
                        updateState {
                            it.copy(
                                isLoading = false,
                                shouldShowInvalidOtpError = true
                            )
                        }
                    }

                    is LoginVerificationResult.ServerError.UnknownError -> {
                        val errorMsg = result.errorMsg ifNullOrEmptyThen run {
                            stringProvider.getString(
                                StringEnum.GENERIC_ERROR_WITH_CODE,
                                result.getErrorCodes()
                            )
                        }
                        raiseOnVerifyClickedErrorAnalyticsEvent(errorMsg = errorMsg)
                        updateState {
                            it.copy(isLoading = false)
                        }
                        emitSideEffect(LoginOtpEnterSideEffect.ShowToast(errorMsg))
                    }

                    is LoginVerificationResult.LocalError -> {
                        val errorMsg = result.errorMsg ifNullOrEmptyThen run {
                            stringProvider.getString(
                                StringEnum.GENERIC_ERROR_WITH_CODE,
                                result.getErrorCodes()
                            )
                        }
                        raiseOnVerifyClickedErrorAnalyticsEvent(errorMsg = errorMsg)
                        updateState {
                            it.copy(isLoading = false)
                        }
                        emitSideEffect(LoginOtpEnterSideEffect.ShowToast(errorMsg))
                    }

                    LoginVerificationResult.ServerError.InvalidTokensReceived,
                    LoginVerificationResult.ServerError.InvalidProfileReceived,
                    LoginVerificationResult.ServerError.ParseError,
                    LoginVerificationResult.TokenProcessingError -> {
                        val errorMsg = stringProvider.getString(
                            StringEnum.GENERIC_ERROR_WITH_CODE,
                            result.getErrorCodes()
                        )
                        raiseOnVerifyClickedErrorAnalyticsEvent(errorMsg = errorMsg)
                        updateState {
                            it.copy(isLoading = false)
                        }
                        emitSideEffect(LoginOtpEnterSideEffect.ShowToast(errorMsg))
                    }
                }
            } else {
                val errorMsg = stringProvider.getString(StringEnum.SOMETHING_WENT_WRONG)
                raiseOnVerifyClickedErrorAnalyticsEvent(errorMsg = errorMsg)
                updateState {
                    it.copy(isLoading = false)
                }
                emitSideEffect(LoginOtpEnterSideEffect.ShowToast(errorMsg))
            }
        }
    }

    private fun handlePostLoginStep() {
        if (cityProvider.currentCity.value == null) {
//            TODO :: Ayansh : Check the popUpToConfig arg once.
            chaloNavigationManager.postNavigationRequest(
                navRequest = ChaloNavigationRequest.Navigate(
                    args = CitySelectionArgs(
                        source = Source.LOGIN_OTP_SCREEN
                    ),
                    navOptions = ChaloNavOptions(
                        launchSingleTop = true,
                        popUpToConfig = PopUpToConfig.Scene(
                            scene = ChaloScenes.LoginOptions,
                            inclusive = true
                        )
                    )
                )
            )
            return
        }
        else {
            chaloNavigationManager.postNavigationRequest(
                navRequest = ChaloNavigationRequest.Navigate(
                    args = HomeArgs(),
                    navOptions = ChaloNavOptions(
                        launchSingleTop = true,
                        popUpToConfig = PopUpToConfig.ClearAll()
                    )
                )
            )
        }
    }

    private fun raiseOnVerifyClickedErrorAnalyticsEvent(errorMsg: String, shouldShowInvalidOtpError: Boolean = false) {
        raiseAnalyticsEvent(
            EVENT_LOGIN_FAILED,
            mapOf(
                LoginAnalyticsConstants.ATTR_LOGIN_METHOD to "otp",
                LoginAnalyticsConstants.ATTR_LOGIN_FAILED_REASON to errorMsg,
                LoginAnalyticsConstants.ATTR_IS_INVALID_OTP_ENTERED to shouldShowInvalidOtpError.toString()
            )
        )
    }

    private fun handleOnResendOtpEnableCountdownUpdateIntent(intent: LoginOtpEnterIntent.OnResendOtpEnableCountdownUpdate) {
        updateState {
            it.copy(
                remainingTimeInMillisForEnablingResend = intent.remainingTimeInMillisForEnablingResend,
                isResendBtnEnabled = intent.remainingTimeInMillisForEnablingResend <= 0
            )
        }
    }

    private fun handleOnActiveIntent() {
        smsAutoReadStatusJob?.cancel()
        smsAutoReadStatusJob = viewModelScope.launch {
            smsAutoReader.receiveSmsStatus()
                .collect { smsStatus ->
                    when(smsStatus) {
                        is SMSStatus.Success -> {
                            processIntent(LoginOtpEnterIntent.OnOtpSMSReadSuccessfully(smsStatus.msg))
                        }
                        SMSStatus.Timeout -> {
                            smsAutoReader.startSession()
                        }
                        SMSStatus.Unknown -> {}
                    }
                }
        }
    }

    private fun startResendOtpEnableTimer() {
        resendOtpTimerJob?.cancel()
        resendOtpTimerJob = viewModelScope.launch {
            getCountdownTimerFlow(RESEND_OTP_OPTION_TIME_IN_MILLIS)
                .cancellable()
                .collect { remainingTimeInMillis ->
                    processIntent(
                        LoginOtpEnterIntent.OnResendOtpEnableCountdownUpdate(
                            remainingTimeInMillisForEnablingResend = remainingTimeInMillis
                        )
                    )
                }
        }
    }

    override suspend fun convertToUiState(dataState: LoginOtpEnterDataState): LoginOtpEnterUIState {
        return LoginOtpEnterUIState(
            toolbar = ToolbarUIState(
                title = ToolbarUIStateFactory.defaultToolbarStyleTitle(
                    title = ""
                ),
                elevation = ChaloThemeHelper.dimens.noPadding
            ),
            loadingDialog = loadingDialogUIState(dataState.isLoading, dataState.loadingPurpose),
            headingText = headingTextUIState(dataState.phoneNumber),
            verifyBtn = verifyBtnUIState(dataState.isVerifyBtnEnabled),
            resendText = resendTextUIState(
                isResendEnabled = dataState.isResendBtnEnabled,
                remainingTimeInMillisForEnablingResend = dataState.remainingTimeInMillisForEnablingResend
            ),
            otpEnterField = otpEnterFieldUIState(
                otpEntered = dataState.otpEntered,
                otpLength = dataState.otpLength
            ),
            invalidOtpErrorText = if (dataState.shouldShowInvalidOtpError) { otpErrorUIState() } else { null }
        )
    }

    private fun otpEnterFieldUIState(
        otpEntered: String,
        otpLength: Int
    ): LoginOtpEnterTextFieldUIState {
        return LoginOtpEnterTextFieldUIState(
            textField = TextFieldUIState(
                value = otpEntered,
                eventHandler = TextFieldEventHandlerHelper.otp(otpLength) {
                    processIntent(LoginOtpEnterIntent.OnOtpEnteredIntent(it))
                },
                maxLength = otpLength,
                textStyle = UITextStyle.DisplaySmall,
                keyboardOption = UIKeyboardOption(
                    keyboardType = UIKeyboardType.Number,
                    imeAction = UIImeActionHandler(
                        action = UIImeAction.Done,
                        onAction = { emitSideEffect(LoginOtpEnterSideEffect.HideKeyboard) }
                    )
                ),
                colors = UITextFieldColors(
                    focusedIndicatorColor = ChaloColorToken.Transparent,
                    errorIndicatorColor = ChaloColorToken.Transparent,
                    disabledIndicatorColor = ChaloColorToken.Transparent,
                    unfocusedIndicatorColor = ChaloColorToken.Transparent,
                )
            ),
            decorationIndividualFields = (0 until otpLength).map {  index ->
                val otpChar = when {
                    index < otpEntered.length -> otpEntered[index].toString()
                    else -> ""
                }

                val isFocused = otpEntered.length == index
                val borderColor = if (isFocused) {
                    ChaloColorToken.Orange2
                } else {
                    ChaloColorToken.ShadyGray
                }

                if (isFocused && otpChar.isEmpty()) {
                    LoginOtpEnterIndividualBoxUIState.BlinkingCursor(
                        _borderUISpec = BorderUISpec(
                            width = ChaloThemeHelper.dimens.small0,
                            color = borderColor,
                            shape = UIShape.MediumRoundedCorner
                        )
                    )
                } else {
                    LoginOtpEnterIndividualBoxUIState.Text(
                        text = ChaloTextUIState(
                            string = otpChar,
                            style = UITextStyle.DisplaySmall,
                            textAlign = UITextAlign.Center
                        ),
                        _borderUISpec = BorderUISpec(
                            width = ChaloThemeHelper.dimens.small0,
                            color = borderColor,
                            shape = UIShape.MediumRoundedCorner
                        )
                    )
                }
            }
        )
    }

    private suspend fun resendTextUIState(
        isResendEnabled: Boolean,
        remainingTimeInMillisForEnablingResend: Long
    ): LoginOtpResendTextUIState {
        val didntReceiveOtpMsg = if (isResendEnabled) {
            stringProvider.getString(StringEnum.DIDNT_RECEIVE_OTP)
        } else {
            stringProvider.getString(
                StringEnum.DIDNT_RECEIVE_OTP_RETRY_IN_X,
                convertMillisToMinutesAndSeconds(remainingTimeInMillisForEnablingResend)
            )
        }

        return LoginOtpResendTextUIState(
            descriptionText = ChaloTextUIState(
                string = didntReceiveOtpMsg,
                style = UITextStyle.BodyMedium,
                chaloColorToken = ChaloColorToken.Black_60,
                textAlign = UITextAlign.Center
            ),
            resendClickableText = if (isResendEnabled) {
                ChaloTextUIState(
                    string = stringProvider.getString(StringEnum.RESEND_SMS),
                    style = UITextStyle.BodyMedium,
                    chaloColorToken = ChaloColorToken.OrangePrimary,
                    textAlign = UITextAlign.Center,
                    eventHandler = UiEventHandler.fromLambda {
                        processIntent(LoginOtpEnterIntent.OnResendOtpClickedIntent)
                    }
                )
            } else {
                null
            }
        )
    }

    private suspend fun loadingDialogUIState(
        isLoading: Boolean,
        purpose: LoginLoadingPurpose
    ): LoadingDialogUIState? {
        return if (isLoading) {
            DialogUIStateFactory.chaloLoadingDialog(
                text = getLoadingDialogText(purpose)
            )
        } else {
            null
        }
    }

    private suspend fun otpErrorUIState(): ChaloTextUIState {
        return ChaloTextUIState(
            string = stringProvider.getString(StringEnum.OTP_VERIFICATION_FAILED),
            style = UITextStyle.BodyMedium,
            chaloColorToken = ChaloColorToken.ErrorColor
        )
    }

    private suspend fun headingTextUIState(
        phoneNumber: String?
    ): ChaloTextUIState {
        val title =  if (phoneNumber.isNullOrEmpty()) {
            stringProvider.getString(StringEnum.ENTER_6_DIGIT_VERIFICATION_CODE)
        } else {
            stringProvider.getString(
                StringEnum.WE_HAVE_SENT_AN_OTP,
                phoneNumber
            )
        }

        return ChaloTextUIState(
            string = title,
            style = UITextStyle.DisplayMedium,
            fontWeight = UIFontWeight.Bold,
            fontSize = 24
        )
    }

    private suspend fun verifyBtnUIState(
        isEnabled: Boolean
    ): ButtonUIState {
        return ButtonUIStateFactory.chaloOrangeButton(
            text = stringProvider.getString(StringEnum.PROCEED),
            onClick = {
                emitSideEffect(LoginOtpEnterSideEffect.ClearFocus)
                processIntent(LoginOtpEnterIntent.OnVerifyClickedIntent)
            },
            enabled = isEnabled
        )
    }

    private fun convertMillisToMinutesAndSeconds(timeInMillis: Long): String {
        val seconds = (timeInMillis / 1000) % 60
        val minutes = (timeInMillis / (1000 * 60)) % 60

        val minutesString = if (minutes < 10) "0$minutes" else minutes.toString()
        val secondsString = if (seconds < 10) "0$seconds" else seconds.toString()
        return "$minutesString:$secondsString"
    }


    private suspend fun getLoadingDialogText(loadingPurpose: LoginLoadingPurpose): String {
        return when (loadingPurpose) {
            LoginLoadingPurpose.REQUESTING_OTP -> stringProvider.getString(StringEnum.VERIFYING_PHONE_NUMBER)
            LoginLoadingPurpose.VERIFYING_LOGIN -> stringProvider.getString(StringEnum.VERIFYING_OTP)
            LoginLoadingPurpose.GENERAL -> stringProvider.getString(StringEnum.LOADING_THREE_DOTS)
        }
    }

    companion object {
        const val RESEND_OTP_OPTION_TIME_IN_MILLIS = 30_000L
    }
}


 */