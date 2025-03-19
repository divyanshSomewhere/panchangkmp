package com.gometro.userprofile.ui.edit

/*
class UserProfileEditViewModel(
    private val userProfileDetailsProvider: UserProfileDetailsProvider,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val stringProvider: StringProvider,
    private val imagePickerProvider: ImagePickerProvider,
    private val analyticsContract: AnalyticsContract,
    private val chaloNavigationManager: ChaloNavigationManager,
    private val systemHelper: SystemHelper,
    private val uploadFileToServerUseCase: UploadFileToServerUseCase,
    private val permissionHandlerProvider: PermissionHandlerProvider,
    private val timeUtilsContract: TimeUtilsContract
) : ChaloBaseSimpleMviViewModel<UserProfileEditIntent, UserProfileEditState, UserProfileEditSideEffect>() {

    override fun initialViewState(): UserProfileEditState {
        return UserProfileEditState()
    }

    init {
        processIntent(UserProfileEditIntent.InitializationIntent)
    }

    private val imagePicker: ImagePicker? get() = imagePickerProvider.imagePicker
    private val permissionHandler: PermissionHandler? get() = permissionHandlerProvider.permissionHandler

    override fun processIntent(intent: UserProfileEditIntent) {
        super.processIntent(intent)
        when(intent) {
            is UserProfileEditIntent.InitializationIntent -> handleInitializationIntent(intent)
            is UserProfileEditIntent.CancelBtnClicked -> handleCancelBtnClickedIntent(intent)
            is UserProfileEditIntent.UploadProfilePhotoClicked -> handleUploadProfilePhotoClickedIntent(intent)
            is UserProfileEditIntent.FirstNameEntered -> handleFirstNameEnteredIntent(intent)
            is UserProfileEditIntent.LastNameEntered -> handleLastNameEnteredIntent(intent)
            is UserProfileEditIntent.GenderFieldClicked -> handleGenderFieldClickedIntent(intent)
            is UserProfileEditIntent.GenderSelected -> handleGenderSelectedIntent(intent)
            is UserProfileEditIntent.DateOfBirthFieldClicked -> handleDateOfBirthFieldClickedIntent(intent)
            is UserProfileEditIntent.DateOfBirthSelected -> handleDateOfBirthSelectedIntent(intent)
            is UserProfileEditIntent.EmailIdEntered -> handleEmailIdEnteredIntent(intent)
            is UserProfileEditIntent.SaveBtnClicked -> handleSaveBtnClickedIntent(intent)
            is UserProfileEditIntent.ProfilePhotoSelected -> handleProfilePhotoSelectedIntent(intent)
            is UserProfileEditIntent.DismissDialog -> handleDismissDialogIntent(intent)
            is UserProfileEditIntent.PermissionRationaleDialogInteraction -> handlePermissionRationaleDialogInteraction(intent)
        }
    }

    private fun handleInitializationIntent(intent: UserProfileEditIntent.InitializationIntent) {
        viewModelScope.launch {
            val userProfile = userProfileDetailsProvider.getUserProfileDetailsAsync()
            updateState {
                it.copy(
                    firstName = userProfile?.firstName ?: "",
                    lastName = userProfile?.lastName ?: "",
                    gender = userProfile?.gender ?: Gender.NULL,
                    dobInMillis = userProfile?.dobInMillis,
                    dobString = userProfile?.dobInMillis?.let { dob -> timeUtilsContract.getDateFromEpochTime(dob) } ?: "",
                    emailId = userProfile?.emailId ?: "",
                    phoneNumber = userProfile?.mobileNumber ?: "",
                    profilePhotoUrl = userProfile?.profilePhoto,
                    newSelectedPhotoBitmap = null
                )
            }
            raiseEditScreenDisplayed()
        }
    }

    private fun raiseEditScreenDisplayed() {
        analyticsContract.raiseAnalyticsEvent(
            name = UserProfileAnalyticsConstants.EVENT_USER_PROFILE_EDIT_SCREEN_DISPLAYED,
            source = Source.USER_PROFILE_EDIT.sourceName
        )
    }

    private fun handleCancelBtnClickedIntent(
        intent: UserProfileEditIntent.CancelBtnClicked
    ) {
        raiseCancelClicked()
        chaloNavigationManager.postNavigationRequest(
            navRequest = ChaloNavigationRequest.GoBack()
        )
    }

    private fun raiseCancelClicked() {
        analyticsContract.raiseAnalyticsEvent(
            name = UserProfileAnalyticsConstants.EVENT_USER_PROFILE_EDIT_CANCEL_CLICKED,
            source = Source.USER_PROFILE_EDIT.sourceName
        )
    }

    private fun handleUploadProfilePhotoClickedIntent(
        intent: UserProfileEditIntent.UploadProfilePhotoClicked
    ) = viewModelScope.launch {
        raiseProfilePhotoClicked()
        if (permissionHandler?.checkPermission(AppPermission.CAMERA)?.isGranted() == true) {
            pickImage()
        } else {
            requestCameraPermission()
        }
    }

    private fun pickImage() {
        viewModelScope.launch {
            when(val result = imagePicker?.pickImage(ImagePickerConfig(true))) {
                is ImagePickerResult.ImagePicked -> {
                    processIntent(UserProfileEditIntent.ProfilePhotoSelected(result.image))
                }
                ImagePickerResult.PickerClosed,
                is ImagePickerResult.PickerError,
                null -> {}
            }
        }
    }

    private fun requestCameraPermission() {
        viewModelScope.launch {
            permissionHandler?.requestPermission(AppPermission.CAMERA)
                ?.collect { permissionState ->
                    when(permissionState) {
                        PermissionState.GRANTED -> {
                            pickImage()
                        }
                        PermissionState.DENIED,
                        PermissionState.REQUIRES_EXPLANATION -> {
                            updateState {
                                it.copy(
                                    dialogType = UserProfileEditDialogType.GalleryPermissionRationale
                                )
                            }
                        }
                        PermissionState.NOT_DETERMINED,
                        PermissionState.UNKNOWN -> {}
                    }
                }
        }
    }

    private fun raiseProfilePhotoClicked() {
        analyticsContract.raiseAnalyticsEvent(
            name = UserProfileAnalyticsConstants.EVENT_USER_PROFILE_EDIT_PHOTO_CLICKED,
            source = Source.USER_PROFILE_EDIT.sourceName
        )
    }

    private fun handleFirstNameEnteredIntent(
        intent: UserProfileEditIntent.FirstNameEntered
    ) {
        updateState {
            it.copy(firstName = intent.firstNameEntered)
        }
    }

    private fun handleLastNameEnteredIntent(
        intent: UserProfileEditIntent.LastNameEntered
    ) {
        updateState {
            it.copy(lastName = intent.lastNameEntered)
        }
    }

    private fun handleGenderFieldClickedIntent(
        intent: UserProfileEditIntent.GenderFieldClicked
    ) {
        raiseGenderFieldClicked()
        updateState {
            it.copy(
                dialogType = UserProfileEditDialogType.GenderSelection(
                    currentSelection = viewState.value.gender,
                    genderOptions = listOf(Gender.MALE, Gender.FEMALE, Gender.OTHER)
                )
            )
        }
    }

    private fun raiseGenderFieldClicked() {
        analyticsContract.raiseAnalyticsEvent(
            name = UserProfileAnalyticsConstants.EVENT_USER_PROFILE_GENDER_FIELD_CLICKED,
            source = Source.USER_PROFILE_EDIT.sourceName
        )
    }

    private fun handleGenderSelectedIntent(
        intent: UserProfileEditIntent.GenderSelected
    ) {
        raiseGenderSelected()
        updateState {
            it.copy(
                gender = intent.genderSelected,
                dialogType = null
            )
        }
    }

    private fun raiseGenderSelected() {
        analyticsContract.raiseAnalyticsEvent(
            name = UserProfileAnalyticsConstants.EVENT_USER_PROFILE_GENDER_CHANGED,
            source = Source.USER_PROFILE_EDIT.sourceName
        )
    }

    private fun handleDateOfBirthFieldClickedIntent(
        intent: UserProfileEditIntent.DateOfBirthFieldClicked
    ) {
        raiseDateOfBirthFieldClicked(viewState.value.dobInMillis ?: systemHelper.currentTimeInMillis())
        updateState {
            it.copy(
                dialogType = UserProfileEditDialogType.DateOfBirthSelection(
                    currentDobMillis = viewState.value.dobInMillis ?: systemHelper.currentTimeInMillis()
                )
            )
        }
    }

    private fun raiseDateOfBirthFieldClicked(
        selectedDob: Long
    ) {
        analyticsContract.raiseAnalyticsEvent(
            name = UserProfileAnalyticsConstants.EVENT_USER_PROFILE_DOB_FIELD_CLICKED,
            source = Source.USER_PROFILE_EDIT.sourceName,
            eventProperties = mapOf(
                UserProfileAnalyticsConstants.ATTR_INITIALLY_SELECTED_DATE_ON_DOB_CALENDER to selectedDob.toString()
            )
        )
    }

    private fun handleDateOfBirthSelectedIntent(
        intent: UserProfileEditIntent.DateOfBirthSelected
    ) {
        updateState {
            it.copy(
                dobInMillis = intent.selectedDobMillis,
                dobString = timeUtilsContract.getDateFromEpochTime(intent.selectedDobMillis),
                dialogType = null
            )
        }
        raiseDobChanged()
    }

    private fun raiseDobChanged() {
        analyticsContract.raiseAnalyticsEvent(
            name = UserProfileAnalyticsConstants.EVENT_USER_PROFILE_DOB_CHANGED,
            source = Source.USER_PROFILE_EDIT.sourceName
        )
    }

    private fun handleEmailIdEnteredIntent(
        intent: UserProfileEditIntent.EmailIdEntered
    ) {
        updateState {
            it.copy(
                emailId = intent.emailIdEntered
            )
        }
    }

    private fun handleSaveBtnClickedIntent(
        intent: UserProfileEditIntent.SaveBtnClicked
    ) {
        viewModelScope.launch {
            val state = viewState.value

            if (!isEmailEnteredValidOrEmpty(state.emailId)) {
                val errorMsg = stringProvider.getString(StringEnum.ENTER_VALID_EMAIL_ID_ERROR)
                updateState {
                    it.copy(
                        isLoading = false
                    )
                }
                emitSideEffect(UserProfileEditSideEffect.ShowToast(errorMsg))
                raiseProfileSaveError(errorMsg)
                return@launch
            }

            updateState {
                it.copy(
                    isLoading = true
                )
            }
            analyticsContract.raiseAnalyticsEvent(
                name = UserProfileAnalyticsConstants.EVENT_USER_PROFILE_EDIT_SAVE_CLICKED,
                source = Source.USER_PROFILE_EDIT.sourceName
            )

            val profilePhotoUrl = if (state.newSelectedPhotoBitmap == null) {
                state.profilePhotoUrl
            } else {
                val imageUploadResult = uploadFileToServerUseCase.invoke(
                    fileName = systemHelper.currentTimeInMillis().toString(),
                    fileType = UploadFileType.Image,
                    fileToUploadAsByteArray = state.newSelectedPhotoBitmap.byteArray
                ).toUploadImageResult()

                when (imageUploadResult) {
                    is UploadImageResult.UploadedSuccessfully -> {
                        // if this bitmap get successfully uploaded but update api fails
                        // and user tries again, we do not want to re-upload the bitmap, rather use the
                        // url we got last time to update profile, that's why updating photo url and setting null for bitmap
                        updateState {
                            it.copy(
                                profilePhotoUrl = imageUploadResult.imageUrl,
                                newSelectedPhotoBitmap = null
                            )
                        }
                        analyticsContract.raiseAnalyticsEvent(
                            name = UserProfileAnalyticsConstants.EVENT_USER_PROFILE_PROFILE_PHOTO_UPLOADED_SUCCESSFULLY,
                            source = Source.USER_PROFILE_EDIT.sourceName
                        )
                        imageUploadResult.imageUrl
                    }
                    UploadImageResult.InvalidImageUrlReceivedError,
                    UploadImageResult.LocalError,
                    UploadImageResult.ResponseParseError,
                    is UploadImageResult.ServerError -> {
                        val errorMsg = stringProvider.getString(
                            StringEnum.GENERIC_ERROR_WITH_CODE,
                            getImageUploadFailureErrorCode(imageUploadResult)
                        )
                        raiseProfileSaveError(errorMsg)
                        updateState {
                            it.copy(
                                isLoading = false
                            )
                        }
                        emitSideEffect(UserProfileEditSideEffect.ShowToast(errorMsg))
                        return@launch
                    }
                }
            }

            val result = updateUserProfileUseCase.invoke(
                firstName = state.firstName,
                lastName = state.lastName,
                profilePhotoUrl = profilePhotoUrl,
                gender = state.gender,
                dateOfBirthInMillis = state.dobInMillis,
                emailId = state.emailId
            )

            when (result) {
                UpdateUserProfileResult.PROFILE_UPDATED_SUCCESSFULLY -> {
                    analyticsContract.raiseAnalyticsEvent(
                        name = UserProfileAnalyticsConstants.EVENT_USER_PROFILE_EDIT_SUCCESSFUL,
                        source = Source.USER_PROFILE_EDIT.sourceName
                    )
                    updateState {
                        it.copy(
                            isLoading = false
                        )
                    }
                    emitSideEffect(UserProfileEditSideEffect.ShowToast(stringProvider.getString(StringEnum.PROFILE_UPDATE_SUCCESS)))
                    chaloNavigationManager.postNavigationRequest(
                        navRequest = ChaloNavigationRequest.GoBack()
                    )
                }
                UpdateUserProfileResult.USER_NOT_LOGGED_IN,
                UpdateUserProfileResult.INVALID_DOB,
                UpdateUserProfileResult.USER_DOES_NOT_EXIST,
                UpdateUserProfileResult.USER_ID_MISMATCH,
                UpdateUserProfileResult.UNKNOWN_LOCAL_EXCEPTION,
                UpdateUserProfileResult.SERVER_ERROR,
                UpdateUserProfileResult.RESPONSE_PARSE_EXCEPTION,
                UpdateUserProfileResult.INVALID_RESPONSE -> {
                    val errorMsg = stringProvider.getString(StringEnum.GENERIC_ERROR_WITH_CODE, result.getFeatureErrorCode())
                    raiseProfileSaveError(errorMsg)
                    updateState {
                        it.copy(
                            isLoading = false
                        )
                    }
                    emitSideEffect(UserProfileEditSideEffect.ShowToast(errorMsg))
                }
            }
        }
    }

    private fun raiseProfileSaveError(reason: String) {
        analyticsContract.raiseAnalyticsEvent(
            name = UserProfileAnalyticsConstants.EVENT_USER_PROFILE_EDIT_ERROR,
            source = Source.USER_PROFILE_EDIT.sourceName,
            eventProperties = mapOf(
                UserProfileAnalyticsConstants.ATTR_REASON to reason
            )
        )
    }

    private fun handleProfilePhotoSelectedIntent(
        intent: UserProfileEditIntent.ProfilePhotoSelected
    ) {
        raiseProfilePhotoSelected()
        updateState {
            it.copy(
                profilePhotoUrl = null,
                newSelectedPhotoBitmap = intent.selectedPhotoBitmap
            )
        }
    }

    private fun handleDismissDialogIntent(
        intent: UserProfileEditIntent.DismissDialog
    ) {
        updateState {
            it.copy(
                dialogType = null
            )
        }
    }

    private fun handlePermissionRationaleDialogInteraction(
        intent: UserProfileEditIntent.PermissionRationaleDialogInteraction
    ) {
        updateState {
            it.copy(
                dialogType = null
            )
        }

        if (intent.shouldNavigateToSettings) {
            emitSideEffect(UserProfileEditSideEffect.NavigateToSettings)
        }
    }

    private fun raiseProfilePhotoSelected() {
        analyticsContract.raiseAnalyticsEvent(
            name = UserProfileAnalyticsConstants.EVENT_USER_PROFILE_PHOTO_CHANGED,
            source = Source.USER_PROFILE_EDIT.sourceName
        )
    }

    private fun isEmailEnteredValidOrEmpty(emailIdEntered: String): Boolean {
        if (emailIdEntered.isEmpty()) {
            return true
        }

        if (Patterns.EMAIL_ADDRESS.matches(emailIdEntered)) {
            return true
        }

        return false
    }

    private fun getImageUploadFailureErrorCode(result: UploadImageResult): Int {
        return when (result) {
            UploadImageResult.InvalidImageUrlReceivedError -> UserProfileFeatureErrorCodes.UPDATE_PROFILE_PHOTO_INVALID_URL
            UploadImageResult.LocalError -> UserProfileFeatureErrorCodes.UPDATE_PROFILE_PHOTO_LOCAL_ERROR
            UploadImageResult.ResponseParseError -> UserProfileFeatureErrorCodes.UPDATE_PROFILE_PHOTO_RESPONSE_PARSE_ERROR
            is UploadImageResult.ServerError -> UserProfileFeatureErrorCodes.UPDATE_PROFILE_PHOTO_SERVER_ERROR
            is UploadImageResult.UploadedSuccessfully -> -1 // won't happen
        }
    }
}


 */