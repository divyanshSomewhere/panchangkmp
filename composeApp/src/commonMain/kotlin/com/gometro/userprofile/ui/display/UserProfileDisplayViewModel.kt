package com.gometro.userprofile.ui.display

/*
class UserProfileDisplayViewModel(
    private val userProfileDetailsProvider: UserProfileDetailsProvider,
    private val logoutUserUseCase: LogoutUserUseCase,
    private val stringProvider: StringProvider,
    private val analyticsContract: AnalyticsContract,
    private val chaloNavigationManager: ChaloNavigationManager,
    private val timeUtilsContract: TimeUtilsContract
) : ChaloBaseSimpleMviViewModel<UserProfileDisplayIntent, UserProfileDisplayState, UserProfileDisplaySideEffect>() {

    override fun initialViewState(): UserProfileDisplayState {
        return UserProfileDisplayState()
    }

    init {
        userProfileDetailsProvider
            .getUserProfileDetailsAsFlow()
            .map { (it to true) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), (null to false))
            .filter { it.second }
            .onEach { (profile, isValid) ->
                if (!isValid) {
                    // this means this profile is null as per initial value
                    return@onEach
                }

                if (profile != null) {
                    handleProfileAvailableState(profile)
                    raiseUserProfileScreenRefreshedEvent()
                } else {
                    // ideally this should never happen coz now login is mandatory
                    // so if profile is null and we land on this screen simply clear stack and go
                    // to login screen
                    chaloNavigationManager.postNavigationRequest(
                        navRequest = ChaloNavigationRequest.Navigate(
                            args = LoginOptionsArgs(Source.USER_PROFILE_DETAILS),
                            navOptions = ChaloNavOptions(
                                launchSingleTop = true,
                                popUpToConfig = PopUpToConfig.ClearAll()
                            )
                        )
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun processIntent(intent: UserProfileDisplayIntent) {
        super.processIntent(intent)
        when (intent) {
            is UserProfileDisplayIntent.EditUserProfileClicked -> handleEditUserProfileClickedIntent(intent)
            is UserProfileDisplayIntent.LogoutClicked -> handleLogoutClickedIntent(intent)
            is UserProfileDisplayIntent.ProfilePhotoClicked -> handleProfilePhotoClickedIntent(intent)
            is UserProfileDisplayIntent.DismissLogoutFailedDialog -> handleDismissLogoutFailedDialog(intent)
            is UserProfileDisplayIntent.DismissZoomedInProfilePhoto -> handleDismissZoomedProfilePhoto(intent)
        }
    }

    private fun handleDismissLogoutFailedDialog(intent: UserProfileDisplayIntent.DismissLogoutFailedDialog) {
        updateState { it.copy(shouldShowLogoutFailedDialog = false) }
    }

    private fun handleEditUserProfileClickedIntent(intent: UserProfileDisplayIntent.EditUserProfileClicked) {
        raiseUserProfileEditClickedEvent()
        chaloNavigationManager.postNavigationRequest(
            ChaloNavigationRequest.Navigate(
                args = UserProfileEditArgs
            )
        )
    }

    private fun handleLogoutClickedIntent(intent: UserProfileDisplayIntent.LogoutClicked) {
        viewModelScope.launch {
            raiseUserProfileLogoutClickedEvent()
            updateState { it.copy(isLoading = true) }
            val loggedOutSuccessfully = logoutUserUseCase.invoke(userTriggeredLogout = true)
            updateState { it.copy(isLoading = false) }

            if (loggedOutSuccessfully) {
                raiseUserProfileLogoutResultEvent(loggedOutSuccessfully = true)
                emitSideEffect(UserProfileDisplaySideEffect.ShowToast(stringProvider.getString(StringEnum.PROFILE_LOGOUT_SUCCESSFUL)))
            } else {
                raiseUserProfileLogoutResultEvent(loggedOutSuccessfully = false)
                updateState { it.copy(shouldShowLogoutFailedDialog = true) }
            }
        }
    }

    private fun handleProfilePhotoClickedIntent(intent: UserProfileDisplayIntent.ProfilePhotoClicked) {
        if (viewState.value.profilePhotoUrl.isNotEmpty()) {
            updateState { it.copy(shouldZoomProfilePhoto = true) }
        }
    }

    private fun handleDismissZoomedProfilePhoto(intent: UserProfileDisplayIntent.DismissZoomedInProfilePhoto) {
        updateState { it.copy(shouldZoomProfilePhoto = false) }
    }

    private fun handleProfileAvailableState(profile: UserProfileAppModel) {
        with(profile) {
            updateState {
                it.copy(
                    firstName = firstName,
                    lastName = lastName,
                    profilePhotoUrl = profilePhoto,
                    gender = gender,
                    mobileNumber = mobileNumber,
                    dobInMillis = dobInMillis,
                    dobString = dobInMillis?.let { dob -> timeUtilsContract.getDateFromEpochTime(dob) },
                    emailId = emailId,
                    shouldShowProfileDetailsFields = true
                )
            }
        }
    }

    private fun raiseUserProfileScreenRefreshedEvent() {
        analyticsContract.raiseAnalyticsEvent(
            name = EVENT_USER_PROFILE_SCREEN_REFRESHED,
            source = SOURCE_USER_PROFILE_DISPLAY_SCREEN
        )
    }

    private fun raiseUserProfileEditClickedEvent() {
        analyticsContract.raiseAnalyticsEvent(
            name = EVENT_USER_PROFILE_EDIT_CLICKED,
            source = SOURCE_USER_PROFILE_DISPLAY_SCREEN
        )
    }

    private fun raiseUserProfileLogoutClickedEvent() {
        analyticsContract.raiseAnalyticsEvent(
            name = EVENT_USER_PROFILE_LOGOUT_CLICKED,
            source = SOURCE_USER_PROFILE_DISPLAY_SCREEN
        )
    }

    private fun raiseUserProfileLogoutResultEvent(loggedOutSuccessfully: Boolean) {
        analyticsContract.raiseAnalyticsEvent(
            name = EVENT_USER_PROFILE_LOGOUT_RESULT,
            source = SOURCE_USER_PROFILE_DISPLAY_SCREEN,
            eventProperties = mapOf(
                ATTR_USER_LOGGED_OUT to loggedOutSuccessfully.toString()
            )
        )
    }
}


 */