package com.gometro.login.ui.options

/*
class LoginOptionsViewModel(
    private val args: LoginOptionsArgs,
//    private val truecallerSetupHandlerProvider: TruecallerSetupHandlerProvider,
    private val validatePhoneNumberUseCase: ValidatePhoneNumberUseCase,
    private val sendOtpForLoginUseCase: SendOtpForLoginUseCase,
    private val verifyLoginSuccessOnServerAndHandleTokensUseCase: VerifyLoginSuccessOnServerAndHandleTokensUseCase,
    private val stringProvider: StringProvider,
    private val analyticsContract: AnalyticsContract,
    private val syncAndUpdateAnalyticsPropertiesAfterLoginUseCase: SyncAndUpdateAnalyticsPropertiesAfterLoginUseCase,
    private val fetchLoginAfterCitySelectionConfigUseCase: FetchLoginAfterCitySelectionConfigUseCase,
    private val chaloNavigationManager: ChaloNavigationManager,
    private val userProfileDetailsProvider: UserProfileDetailsProvider,
    private val phoneNumberHintHandlerProvider: PhoneNumberHintHandlerProvider,
) : ChaloBaseStateMviViewModel<LoginOptionsIntent, LoginOptionsDataState, LoginOptionsUIState, LoginOptionsSideEffect>() {

    private var isTruecallerUsable: Boolean = false

    private val supportedChaloCountryList : List<CountryPhoneNumberConfig> by lazy {
        getSupportedChaloCountryListUseCase.invoke()
    }

    override fun initialDataState(): LoginOptionsDataState {
        return LoginOptionsDataState()
    }

    init {
        processIntent(
            LoginOptionsIntent.InitializationIntent(
                source = args.source
            )
        )
    }

    override fun processIntent(intent: LoginOptionsIntent) {
        super.processIntent(intent)
        when (intent) {
            is LoginOptionsIntent.InitializationIntent -> handleInitializationIntent(intent)
            is LoginOptionsIntent.ShowTruecallerOptionIfPossible -> handleShowTruecallerOptionIfPossibleIntent()
            is LoginOptionsIntent.OnPhoneNumberEntered -> handleOnPhoneNumberEnteredIntent(
                enteredString = intent.enteredString,
                autoCallOtpApiIfValid = false
            )
            is LoginOptionsIntent.OnContinueClicked -> handleOnContinueClickedIntent()
            is LoginOptionsIntent.OnTruecallerErrorCallback -> handleOnTruecallerErrorCallbackIntent(intent)
            is LoginOptionsIntent.OnTruecallerSuccessCallback -> handleOnTruecallerSuccessCallbackIntent(intent)
            is LoginOptionsIntent.OnOtherLoginOptionsClicked -> handleOnLoginOptionsClickedIntent(intent)
            is LoginOptionsIntent.SetUpTruecallerVisibilityIntent -> handleSetUpLoginViewIntent(intent)
            is LoginOptionsIntent.LoginBackPressed -> handleLoginBackPressedIntent()
            LoginOptionsIntent.OnCountrySelectDropdownClicked -> {
                updateState {
                    it.copy(showSupportedCountryListDialogue = true)
                }
            }
            is LoginOptionsIntent.OnCountrySelected -> handleOnCountrySelectedIntent(intent.country)
            is LoginOptionsIntent.PhoneNumberFieldFocusChangeIntent -> handlePhoneNumberFieldFocusChangeIntent(intent)
        }
    }

    private fun handleOnCountrySelectedIntent(country: ChaloPhoneNumberCountry) {
        val selectedCountryConfig = supportedChaloCountryList.firstOrNull { it.country == country }
        val currentSelectedCountry = dataState.value.countryPhoneNumberConfig
        if (selectedCountryConfig != null && selectedCountryConfig == currentSelectedCountry) {
            updateState {
                it.copy(
                    showSupportedCountryListDialogue = false,
                )
            }
        } else if (selectedCountryConfig != null) {
            updateState {
                it.copy(
                    countryPhoneNumberConfig = selectedCountryConfig,
                    showSupportedCountryListDialogue = false,
                    phoneNumberEntered = "",
                    isContinueBtnEnabled = false
                )
            }
            emitSideEffect(LoginOptionsSideEffect.BringFocusOnPhoneNumberField)
        }
        else {
            viewModelScope.launch {
                emitSideEffect(LoginOptionsSideEffect.ShowToast(stringProvider.getString(StringEnum.SOMETHING_WENT_WRONG)))
            }
        }
    }

    private fun raiseAnalyticsEvent(
        mEventName: String,
        additionalParamsToAdd: Map<String, String>? = null
    ) {
        mEventName.let { eventName ->
            val paramsMap = mutableMapOf<String, String>().apply {
                dataState.value.addParamsToAnalyticsMap(this)
                additionalParamsToAdd?.let { this.putAll(it) }
            }

            analyticsContract.raiseAnalyticsEvent(
                name = eventName,
                source = SOURCE_LOGIN_OPTIONS_SCREEN,
                eventProperties = paramsMap
            )
        }
    }

    private fun handleInitializationIntent(intent: LoginOptionsIntent.InitializationIntent) {
        if (initialIntentHandled) return
        initialIntentHandled = true

        val defaultPhoneNumberConfig = userCountryPhoneNumberConfigProvider.resolveConfig(supportedChaloCountryList)
        viewModelScope.launch {
            val loginABTestConfig = fetchLoginAfterCitySelectionConfigUseCase()
            // TODO::KSHITIJ - come back for this
//            val isUserLoginFirstTime = loginInfoContract.isUserLoginFirstTime()
            val isUserLoginFirstTime = true
            updateState {
                it.copy(
                    source = intent.source,
                    countryPhoneNumberConfig = defaultPhoneNumberConfig,
                    isUserLoginFirstTime = isUserLoginFirstTime,
                    loginABTestConfig = loginABTestConfig,
                    supportedLoginOptionsCountryList = supportedChaloCountryList.map { config ->
                        LoginOptionsCountry(
                            country = config.country,
                            name = config.countryName,
                            callingCode = config.countryCallingCode,
                        )
                    }
                )
            }
            raiseAnalyticsEvent(
                EVENT_ENTER_NUMBER_SCREEN_OPENED,
                mapOf(LoginAnalyticsConstants.ATTR_SOURCE to intent.source.sourceName)
            )

            initTruecallerAndSetupLoginMethods()
        }
    }

    private fun initTruecallerAndSetupLoginMethods() {
        viewModelScope.launch {
            truecallerSetupHandlerProvider.handler?.activityStart?.collect {}
        }
        truecallerSetupHandlerProvider.handler?.initTruecaller(
            setupConfig = TruecallerSetupConfig.getDefaultConfig(),
            onResult = ::handleTruecallerProfileResult,
        )
        val isTruecallerUsable = truecallerSetupHandlerProvider.handler?.isTruecallerUsable() ?: false
        processIntent(LoginOptionsIntent.SetUpTruecallerVisibilityIntent(isTruecallerUsable))
    }

    private fun handleShowTruecallerOptionIfPossibleIntent() {
        if (isTruecallerUsable) {
            raiseAnalyticsEvent(EVENT_TRUECALLER_UID_FETCH_TRIED)
            updateState {
                it.copy(
                    isLoading = true,
                    loadingPurpose = LoginLoadingPurpose.GENERAL
                )
            }
            viewModelScope.launch {
                when (val uidResult = generateUidForTruecallerLoginUseCase.invoke()) {
                    is ChaloUseCaseResult.Failure -> {
                        // whatever the reason, just emit error to stop loading and bring focus on edittext
                        // we dont want to show any error toast or something
                        raiseAnalyticsEvent(EVENT_TRUECALLER_UID_FETCH_FAILED)
                        emitSideEffect(LoginOptionsSideEffect.BringFocusOnPhoneNumberField)
                        updateState {
                            it.copy(isLoading = false)
                        }
                    }
                    is ChaloUseCaseResult.Success -> {
                        raiseAnalyticsEvent(EVENT_TRUECALLER_BOTTOM_SHEET_RENDERED)
                        truecallerSetupHandlerProvider.handler?.showTruecallerLoginOptions(uidResult.data)
                        updateState {
                            it.copy(
                                isLoading = false,
                                uidForTruecaller = uidResult.data
                            )
                        }
                    }
                }
            }
        } else {
            raiseAnalyticsEvent(EVENT_TRUECALLER_UID_FETCH_FAILED)
            requestPhoneNumberHint()
            updateState {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    private fun handleOnPhoneNumberEnteredIntent(
        enteredString: String,
        autoCallOtpApiIfValid: Boolean
    ) {
        val phoneNumberConfig = dataState.value.countryPhoneNumberConfig
        if (phoneNumberConfig != null) {
            when (validatePhoneNumberUseCase(
                phoneNumberEntered = enteredString,
                phoneNumberConfig = phoneNumberConfig
            )) {
                PhoneNumberValidationResult.VALID_PHONE_NUMBER -> {
                    updateState {
                        it.copy(
                            phoneNumberEntered = enteredString,
                            numberEnterError = null,
                            isContinueBtnEnabled = true
                        )
                    }
                    if (autoCallOtpApiIfValid) {
                        handleOnContinueClickedIntent()
                    }
                }
                PhoneNumberValidationResult.NON_DIGIT_CHAR_USED -> {
                    viewModelScope.launch {
                        val error = stringProvider.getString(StringEnum.INVALID_PHONE_NUMBER)
                        updateState {
                            it.copy(
                                phoneNumberEntered = enteredString,
                                numberEnterError = error,
                                isContinueBtnEnabled = false
                            )
                        }
                    }
                }
                PhoneNumberValidationResult.INSUFFICIENT_DIGITS,
                PhoneNumberValidationResult.EXPECTED_LENGTH_EXCEEDED -> {
                    updateState {
                        it.copy(
                            phoneNumberEntered = enteredString,
                            numberEnterError = null,
                            isContinueBtnEnabled = false
                        )
                    }
                }
            }
        } else {
            updateState {
                it.copy(
                    phoneNumberEntered = enteredString,
                    numberEnterError = null,
                    isContinueBtnEnabled = false
                )
            }
        }
    }

    private fun handleOnContinueClickedIntent() {
        val countryPhoneNumberConfig = dataState.value.countryPhoneNumberConfig
        val phoneNumberEntered = dataState.value.phoneNumberEntered
        viewModelScope.launch {
            if (countryPhoneNumberConfig != null) {
                raiseAnalyticsEvent(EVENT_LOGIN_CONTINUE_BTN_CLICKED)
                updateState {
                    it.copy(
                        isLoading = true,
                        loadingPurpose = LoginLoadingPurpose.REQUESTING_OTP
                    )
                }
                val result = sendOtpForLoginUseCase(
                    phoneNumber = dataState.value.phoneNumberEntered,
                    countryCode = countryPhoneNumberConfig.countryCallingCode
                )
                when (result) {
                    is ChaloUseCaseResult.Failure -> {
                        val errorMsg = when (result.error.reason) {
                            SendOtpForLoginErrorReason.SERVER_ERROR,
                            SendOtpForLoginErrorReason.PREVIOUS_OTP_EXPIRED, // this won't happen here
                            SendOtpForLoginErrorReason.UNKNOWN_ERROR -> {
                                result.error.msg ifNullOrEmptyThen run {
                                    stringProvider.getString(
                                        StringEnum.GENERIC_ERROR_WITH_CODE,
                                        result.error.reason.getErrorCodeForReason()
                                    )
                                }
                            }

                            SendOtpForLoginErrorReason.INVALID_REF_NO,
                            SendOtpForLoginErrorReason.OTP_STATUS_FALSE,
                            SendOtpForLoginErrorReason.PARSE_EXCEPTION -> {
                                stringProvider.getString(
                                    StringEnum.GENERIC_ERROR_WITH_CODE,
                                    result.error.reason.getErrorCodeForReason()
                                )
                            }
                        }
                        raiseAnalyticsEvent(
                            EVENT_LOGIN_OTP_REQUEST_FAILED,
                            mapOf(ATTR_LOGIN_OTP_REQUEST_ERROR to errorMsg)
                        )
                        emitSideEffect(LoginOptionsSideEffect.ShowToast(errorMsg))
                        updateState {
                            it.copy(
                                isLoading = false
                            )
                        }
                    }

                    is ChaloUseCaseResult.Success -> {
                        saveCurrentCountryConfig()
                        raiseAnalyticsEvent(EVENT_OTP_SENT)
                        updateState {
                            it.copy(
                                isLoading = false
                            )
                        }
                        chaloNavigationManager.postNavigationRequest(
                            ChaloNavigationRequest.Navigate(
                                args = LoginOtpSceneArgs(
                                    refNo = result.data,
                                    phoneNumber = phoneNumberEntered,
                                    countryCallingCode = countryPhoneNumberConfig.countryCallingCode,
                                    source = args.source
                                )
                            )
                        )
                    }
                }
            } else {
                val errorMsg = stringProvider.getString(StringEnum.SOMETHING_WENT_WRONG)
                raiseAnalyticsEvent(
                    EVENT_LOGIN_OTP_REQUEST_FAILED,
                    mapOf(ATTR_LOGIN_OTP_REQUEST_ERROR to errorMsg)
                )
                emitSideEffect(LoginOptionsSideEffect.ShowToast(errorMsg))
                updateState {
                    it.copy(
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun saveCurrentCountryConfig() {
        val countryConfig = dataState.value.countryPhoneNumberConfig
        if (countryConfig != null) {
            viewModelScope.launch {
                userProfileDetailsProvider.setCurrentCountryPhoneNumberConfig(countryConfig)
            }
        }
    }

    private fun handleOnTruecallerErrorCallbackIntent(intent: LoginOptionsIntent.OnTruecallerErrorCallback) {
        raiseAnalyticsEvent(
            EVENT_TRUECALLER_LOGIN_BOTTOM_SHEET_ERROR,
            mapOf(ATTR_LOGIN_FAILED_REASON to intent.errorType.name)
        )
        when (intent.errorType) {
            TruecallerError.INVALID_ACCOUNT_STATE -> {
                viewModelScope.launch {
                    emitSideEffect(
                        LoginOptionsSideEffect.ShowToast(
                            stringProvider.getString(
                                StringEnum.TRUECALLER_INVALID_ACCOUNT_STATE_ERROR
                            )
                        )
                    )
                }
            }
            TruecallerError.CONTINUE_WITH_DIFFERENT_NUMBER -> {
                requestPhoneNumberHint()
            }
            else -> {
                if (isTruecallerUsable.not()) {
                    requestPhoneNumberHint()
                } else {
                    emitSideEffect(LoginOptionsSideEffect.BringFocusOnPhoneNumberField)
                }
            }
        }
    }

    private fun handleOnTruecallerSuccessCallbackIntent(intent: LoginOptionsIntent.OnTruecallerSuccessCallback) {
        viewModelScope.launch {
            val countryPhoneNumberConfig = dataState.value.countryPhoneNumberConfig
            val isValidPhoneNumberForCurrentCity =
                validatePrefixedPhoneNumberCountryCodeWithCurrentCityUseCase.invoke(
                    prefixedPhoneNumber = intent.numberWithPrefixedCountryCode,
                    countryPhoneNumberConfig = countryPhoneNumberConfig
                )

            if (!isValidPhoneNumberForCurrentCity) {
                val errorMsg = stringProvider.getString(StringEnum.INVALID_NUMBER_USED_FOR_LOGIN_IN_CITY)
                raiseAnalyticsEvent(
                    EVENT_LOGIN_FAILED,
                    mapOf(
                        ATTR_LOGIN_METHOD to "trueCaller",
                        ATTR_LOGIN_FAILED_REASON to errorMsg
                    )
                )
                updateState {
                    it.copy(
                        isLoading = false
                    )
                }
                emitSideEffect(LoginOptionsSideEffect.ShowToast(errorMsg))
                return@launch
            }

            if (countryPhoneNumberConfig != null) {
                raiseAnalyticsEvent(EVENT_CONTINUE_WITH_TRUECALLER)
                updateState {
                    it.copy(
                        isLoading = true,
                        loadingPurpose = LoginLoadingPurpose.VERIFYING_LOGIN
                    )
                }

                val phoneNumberWithoutCountryCode = intent.numberWithPrefixedCountryCode.replace(
                    countryPhoneNumberConfig.countryCallingCode,
                    ""
                )
                val truecallerLoginAppModel = LoginModeAppModel.TruecallerLoginModel(
                    payload = intent.payload,
                    signature = intent.signature,
                    signatureAlgorithm = intent.signatureAlgorithm,
                    uid = dataState.value.uidForTruecaller ?: "",
                    phoneNumber = phoneNumberWithoutCountryCode,
                    countryCode = countryPhoneNumberConfig.countryCallingCode,
                    firstName = intent.firstName,
                    lastName = intent.lastName,
                    emailId = intent.emailId
                )

                launch {
                    when (val result =
                        verifyLoginSuccessOnServerAndHandleTokensUseCase.invoke(truecallerLoginAppModel)) {
                        LoginVerificationResult.LoginVerified -> {
                            saveCurrentCountryConfig()
                            syncAndUpdateAnalyticsPropertiesAfterLoginUseCase.invoke()
                            raiseAnalyticsEvent(
                                EVENT_LOGIN_SUCCESS,
                                mapOf(ATTR_LOGIN_METHOD to "trueCaller")
                            )
                            chaloNavigationManager.postNavigationRequest(
                                navRequest = ChaloNavigationRequest.GoBack()
                            )
                            proceedToNextScreen()
                            userFirstTimeLoginViewed()
                            updateState {
                                it.copy(
                                    isLoading = false
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
                            raiseTruecallerErrorAnalyticsEvent(errorMsg)
                            emitSideEffect(LoginOptionsSideEffect.ShowToast(errorMsg))
                            updateState {
                                it.copy(
                                    isLoading = false
                                )
                            }
                        }

                        is LoginVerificationResult.LocalError -> {
                            val errorMsg = result.errorMsg ifNullOrEmptyThen run {
                                stringProvider.getString(
                                    StringEnum.GENERIC_ERROR_WITH_CODE,
                                    result.getErrorCodes()
                                )
                            }
                            raiseTruecallerErrorAnalyticsEvent(errorMsg)
                            emitSideEffect(LoginOptionsSideEffect.ShowToast(errorMsg))
                            updateState {
                                it.copy(
                                    isLoading = false
                                )
                            }
                        }

                        LoginVerificationResult.ServerError.InvalidTokensReceived,
                        LoginVerificationResult.ServerError.InvalidOtpEntered,
                        LoginVerificationResult.TokenProcessingError,
                        LoginVerificationResult.ServerError.InvalidProfileReceived,
                        LoginVerificationResult.ServerError.ParseError -> {
                            val errorMsg = stringProvider.getString(StringEnum.SOMETHING_WENT_WRONG)
                            raiseTruecallerErrorAnalyticsEvent(errorMsg)
                            emitSideEffect(LoginOptionsSideEffect.ShowToast(errorMsg))
                            updateState {
                                it.copy(
                                    isLoading = false
                                )
                            }
                        }
                    }
                }
            } else {
                val errorMsg = stringProvider.getString(StringEnum.SOMETHING_WENT_WRONG)
                raiseTruecallerErrorAnalyticsEvent(errorMsg)
                emitSideEffect(LoginOptionsSideEffect.ShowToast(errorMsg))
                updateState {
                    it.copy(
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun proceedToNextScreen() {
        if (cityProvider.currentCity.value == null) {
            chaloNavigationManager.postNavigationRequest(
                navRequest = ChaloNavigationRequest.Navigate(
                    args = CitySelectionArgs(
                        source = Source.LOGIN_OPTIONS_SCREEN
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

    private fun raiseTruecallerErrorAnalyticsEvent(errorMsg: String) {
        raiseAnalyticsEvent(
            EVENT_LOGIN_FAILED,
            mapOf(ATTR_LOGIN_METHOD to "trueCaller", ATTR_LOGIN_FAILED_REASON to errorMsg)
        )
    }

    private fun handleOnLoginOptionsClickedIntent(intent: LoginOptionsIntent.OnOtherLoginOptionsClicked) {
        when (intent.optionType) {
            TRUECALLER_LOGIN -> {
                processIntent(LoginOptionsIntent.ShowTruecallerOptionIfPossible)
            }

            SKIP_LOGIN -> {
                userFirstTimeLoginViewed()
                chaloNavigationManager.postNavigationRequest(ChaloNavigationRequest.GoBack())
            }

            else -> {
                userFirstTimeLoginViewed()
                chaloNavigationManager.postNavigationRequest(ChaloNavigationRequest.GoBack())
            }
        }
    }

    private fun handleSetUpLoginViewIntent(intent: LoginOptionsIntent.SetUpTruecallerVisibilityIntent) {
        isTruecallerUsable = intent.isTruecallerUsable
        if (intent.isTruecallerUsable) {
            handleShowTruecallerOptionIfPossibleIntent()
        } else {
            requestPhoneNumberHint()
        }
    }

    private fun handleLoginBackPressedIntent() {
        userFirstTimeLoginViewed()
        chaloNavigationManager.postNavigationRequest(ChaloNavigationRequest.GoBack())
    }

    private fun requestPhoneNumberHint() {
        viewModelScope.launch {
            phoneNumberHintHandlerProvider
                .phoneNumberHintHandler
                ?.hintResultState?.collect(::handlePhoneNumberHintResult)
        }
        phoneNumberHintHandlerProvider.phoneNumberHintHandler?.requestPhoneNumberHint()
    }

    private fun handlePhoneNumberHintResult(result: PhoneNumberHintResult) {
        when(result) {
            is PhoneNumberHintResult.Success -> {
                handleOnPhoneNumberSelected(result.phoneNumber)
            }
            is PhoneNumberHintResult.UnknownException,
            is PhoneNumberHintResult.NoValidPhoneNumber -> {
                emitSideEffect(LoginOptionsSideEffect.BringFocusOnPhoneNumberField)
            }
        }
    }

    private fun handleOnPhoneNumberSelected(pNumberSelected: String) {
        val phoneNoConfig = dataState.value.countryPhoneNumberConfig ?: return
        val actualPhoneNumber =
            pNumberSelected.takeLast(phoneNoConfig.phoneNumberLength)

        updateState {
            it.copy(
                phoneNumberEntered = actualPhoneNumber
            )
        }

        handleOnPhoneNumberEnteredIntent(
            enteredString = actualPhoneNumber,
            autoCallOtpApiIfValid = true
        )
    }

    private fun userFirstTimeLoginViewed() {
        // TODO::KSHITIJ - come back for this
//        viewModelScope.launch {
//            loginInfoContract.setUserLoginFirstTime(false)
//        }
    }

    private fun handleTruecallerProfileResult(
        result: ChaloUseCaseResult<TruecallerProfile, TruecallerError>
    ) {
        when(result) {
            is ChaloUseCaseResult.Failure -> {
                processIntent(LoginOptionsIntent.OnTruecallerErrorCallback(result.error))
            }
            is ChaloUseCaseResult.Success -> {
                val trueProfile = result.data
                processIntent(
                    LoginOptionsIntent.OnTruecallerSuccessCallback(
                        payload = trueProfile.payload ?: "",
                        signature = trueProfile.signature ?: "",
                        signatureAlgorithm = trueProfile.signatureAlgorithm ?: "",
                        numberWithPrefixedCountryCode = trueProfile.phoneNumber ?: "",
                        firstName = trueProfile.firstName ?: "",
                        lastName = trueProfile.lastName ?: "",
                        emailId = trueProfile.email ?: ""
                    )
                )
            }
        }
    }

    private fun handlePhoneNumberFieldFocusChangeIntent(intent: LoginOptionsIntent.PhoneNumberFieldFocusChangeIntent) {
        updateState {
            it.copy(
                isPhoneNumberInFocus = intent.hasFocus
            )
        }
    }

    override suspend fun convertToUiState(dataState: LoginOptionsDataState): LoginOptionsUIState {
        return LoginOptionsUIState(
            toolbarUIState = ToolbarUIState(elevation = ChaloThemeHelper.elevation.none),
            continueBtnUIState = continueButtonUIState(dataState.isContinueBtnEnabled),
            titleTextUIState = titleTextUIState(),
            numberEnterErrorUIState = dataState.numberEnterError?.let { numberEnterError(it) },
            selectedCountryTabUIState = dataState.countryPhoneNumberConfig?.let { selectedCountryTab(it) },
            phoneNumberTextFieldUIState = phoneNumberTextField(
                currentlyEnteredPhoneNo = dataState.phoneNumberEntered,
                maxPhoneNumberLength = dataState.countryPhoneNumberConfig?.phoneNumberLength ?: AppConstants.DEFAULT_PHONE_NUMBER_POSSIBLE_LENGTH.firstOrNull() ?: 10,
                hasFocus = dataState.isPhoneNumberInFocus
            ),
            loadingDialogUIState = if (dataState.isLoading) loadingDialog(dataState.loadingPurpose) else null,
            supportedCountryList = if (dataState.showSupportedCountryListDialogue) supportedCountryListDialog(dataState.supportedLoginOptionsCountryList) else null
        )
    }

    private suspend fun continueButtonUIState(
        isContinueBtnEnabled: Boolean
    ): ButtonUIState {
        return ButtonUIStateFactory.chaloOrangeButton(
            text = stringProvider.getString(StringEnum.GET_OTP),
            onClick = { processIntent(LoginOptionsIntent.OnContinueClicked) },
            enabled = isContinueBtnEnabled
        )
    }

    private suspend fun titleTextUIState(): ChaloTextUIState {
        return ChaloTextUIState(
            string = stringProvider.getString(StringEnum.ONBOARDING_ENTER_NUMBER),
            chaloColorToken = ChaloColorToken.Black,
            style = UITextStyle.DisplayMedium,
            fontWeight = UIFontWeight.Bold,
            textAlign = UITextAlign.Left,
            fontSize = 24
        )
    }

    private fun numberEnterError(error: String): ChaloTextUIState? {
        if (error.isEmpty()) {
            return null
        }
        return ChaloTextUIState(
            string = error,
            chaloColorToken = ChaloColorToken.ErrorColor,
            style = UITextStyle.BodyMedium
        )
    }

    private fun phoneNumberTextField(
        currentlyEnteredPhoneNo: String,
        maxPhoneNumberLength: Int,
        hasFocus: Boolean
    ): LoginPhoneNumberTextFieldUIState {
        val baseSpec = LoginPhoneNumberTextFieldUISpec()
        return LoginPhoneNumberTextFieldUIState(
            state = TextFieldUIState(
                value = currentlyEnteredPhoneNo,
                eventHandler = TextFieldEventHandlerHelper.phoneNumber(maxPhoneNumberLength) {
                    processIntent(LoginOptionsIntent.OnPhoneNumberEntered(it))
                },
                maxLength = maxPhoneNumberLength,
                keyboardOption = UIKeyboardOption(
                    keyboardType = UIKeyboardType.Number,
                    imeAction = UIImeActionHandler(
                        action = UIImeAction.Done,
                        onAction = { emitSideEffect(LoginOptionsSideEffect.HandleKeyboardVisibility(false)) }
                    )
                ),
                textStyle = UITextStyle.DisplaySmall,
                colors = UITextFieldColors(
                    focusedTextColor = ChaloColorToken.Black_87,
                    unfocusedTextColor = ChaloColorToken.Black_87,
                    focusedContainerColor = ChaloColorToken.Transparent,
                    unfocusedContainerColor = ChaloColorToken.Transparent,
                    focusedLabelColor = ChaloColorToken.Black_40,
                    disabledLabelColor = ChaloColorToken.Black_40,
                    unfocusedLabelColor = ChaloColorToken.Black_40,
                    unfocusedIndicatorColor = ChaloColorToken.Transparent,
                    focusedIndicatorColor = ChaloColorToken.Transparent,
                    disabledIndicatorColor = ChaloColorToken.Transparent,
                    errorIndicatorColor = ChaloColorToken.TextFieldIndicator,
                    cursorColor = ChaloColorToken.OrangePrimary,
                    errorCursorColor = ChaloColorToken.OrangePrimary
                )
            ),
            spec = baseSpec.copy(
                borderSpec = baseSpec.borderSpec.copy(
                    color = if (hasFocus) {
                        ChaloColorToken.Orange2
                    } else {
                        ChaloColorToken.ShadyGray
                    }
                )
            )
        )
    }

    private fun selectedCountryTab(countryConfig: CountryPhoneNumberConfig): CountryFlagTabUIState? {
        return countryConfig.country.flagIcon?.let {
            CountryFlagTabUIState(
                flagImage = ImageUIState(
                    img = it,
                    size = ImageUISize(
                        width = ChaloThemeHelper.dimens.large1,
                        height = ChaloThemeHelper.dimens.medium4
                    )
                ),
                eventHandler = UiEventHandler.fromLambda {
                    processIntent(LoginOptionsIntent.OnCountrySelectDropdownClicked)
                }
            )
        }
    }

    private suspend fun loadingDialog(purpose: LoginLoadingPurpose): LoadingDialogUIState {
        val text = when (purpose) {
            LoginLoadingPurpose.GENERAL -> stringProvider.getString(StringEnum.LOADING_THREE_DOTS)
            LoginLoadingPurpose.REQUESTING_OTP -> stringProvider.getString(StringEnum.VERIFYING_PHONE_NUMBER)
            LoginLoadingPurpose.VERIFYING_LOGIN -> stringProvider.getString(StringEnum.VERIFYING_OTP)
        }

        return DialogUIStateFactory.chaloLoadingDialog(text)
    }

    private fun supportedCountryListDialog(supportedCountryList: List<LoginOptionsCountry>): LoginSupportedCountryDialogUIState {
        return LoginSupportedCountryDialogUIState(
            title = ChaloTextUIState(
                string = "Select Country",
                style = UITextStyle.DisplaySmall,
                chaloColorToken = ChaloColorToken.Black
            ),
            supportedCountryList = supportedCountryList.map { item ->
                SupportedCountryItemUIState(
                    countryInfo = ChaloTextUIState(
                        string = "${item.name} (${item.callingCode})",
                        style = UITextStyle.BodyMedium,
                    ),
                    flagImage = item.country.flagIcon?.let {
                        ImageUIState(
                            img = it,
                            size = ImageUISize(
                                width = ChaloThemeHelper.dimens.large1,
                                height = ChaloThemeHelper.dimens.medium4
                            )
                        )
                                                           },
                    eventHandler = UiEventHandler.fromLambda {
                        processIntent(LoginOptionsIntent.OnCountrySelected(item.country))
                    },
                )
            }
        )
    }
}


 */