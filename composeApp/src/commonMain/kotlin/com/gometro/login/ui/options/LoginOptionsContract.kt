package com.gometro.login.ui.options

import androidx.compose.runtime.Immutable
import co.touchlab.skie.configuration.annotations.SealedInterop
import com.gometro.constants.Source
import com.gometro.login.ui.LoginLoadingPurpose

data class LoginOptionsDataState(
    val phoneNumberEntered: String = "",
    val numberEnterError: String? = null,
    val isLoading: Boolean = false,
    val loadingPurpose: LoginLoadingPurpose = LoginLoadingPurpose.GENERAL,
    val isContinueBtnEnabled: Boolean = false,
    val uidForTruecaller: String? = null,
    val source: Source? = null,
    val isSkipBtnVisible: Boolean = false,
    val isUserLoginFirstTime: Boolean = false,
    val showSupportedCountryListDialogue: Boolean = false,
    val isPhoneNumberInFocus: Boolean = false
)
/*

data class LoginOptionsUIState(
    val screenSpecs: LoginOptionsScreenSpecs = LoginOptionsScreenSpecs(),
    val toolbarUIState: ToolbarUIState = ToolbarUIState(title = ChaloTextUIState("")),
    val continueBtnUIState: ButtonUIState = ButtonUIStateFactory.noOp(),
    val titleTextUIState: ChaloTextUIState = ChaloTextUIState(""),
    val numberEnterErrorUIState: ChaloTextUIState? = null,
    val selectedCountryTabUIState: CountryFlagTabUIState? = null,
    val phoneNumberTextFieldUIState: LoginPhoneNumberTextFieldUIState? = null,
    val loadingDialogUIState: LoadingDialogUIState? = null,
    val supportedCountryList: LoginSupportedCountryDialogUIState? = null
)

@Immutable
data class LoginPhoneNumberTextFieldUIState(
    val state: TextFieldUIState,
    val spec: LoginPhoneNumberTextFieldUISpec = LoginPhoneNumberTextFieldUISpec()
)

@Immutable
data class LoginPhoneNumberTextFieldUISpec(
    val borderSpec: BorderUISpec = BorderUISpec(
        width = ChaloThemeHelper.dimens.small0,
        color = ChaloColorToken.ShadyGray,
        shape = UIShape.MediumRoundedCorner
    ),
    val padding: ChaloPadding = ChaloPadding(
        horizontal = ChaloThemeHelper.dimens.medium2,
        vertical = ChaloThemeHelper.dimens.medium1
    )
)

fun LoginOptionsDataState.addParamsToAnalyticsMap(paramsMap: MutableMap<String, String>) {
    paramsMap.apply {
        put(LoginAnalyticsConstants.ATTR_COUNTRY_CALLING_CODE, (countryPhoneNumberConfig?.countryCallingCode ?: ""))
        put(LoginAnalyticsConstants.ATTR_IS_TRUECALLER_INSTALLED, (uidForTruecaller != null).toString())
        put(LoginAnalyticsConstants.ATTR_SOURCE, (source?.sourceName ?: ""))
    }
}

@SealedInterop.Enabled
sealed class LoginOptionsIntent {
    data class InitializationIntent(val source: Source) : LoginOptionsIntent()
    object ShowTruecallerOptionIfPossible : LoginOptionsIntent()
    data class OnPhoneNumberEntered(val enteredString: String) : LoginOptionsIntent()
    object OnContinueClicked : LoginOptionsIntent()
    data class OnOtherLoginOptionsClicked(val optionType: String) : LoginOptionsIntent()
    data class OnTruecallerSuccessCallback(
        val payload: String,
        val signature: String,
        val numberWithPrefixedCountryCode: String,
        val signatureAlgorithm: String,
        val firstName: String,
        val lastName: String,
        val emailId: String
    ) : LoginOptionsIntent()
    data class OnTruecallerErrorCallback(val errorType: TruecallerError) : LoginOptionsIntent()
    data class SetUpTruecallerVisibilityIntent(val isTruecallerUsable: Boolean) : LoginOptionsIntent()
    object LoginBackPressed : LoginOptionsIntent()
    object OnCountrySelectDropdownClicked : LoginOptionsIntent()
    data class OnCountrySelected(val country: ChaloPhoneNumberCountry) : LoginOptionsIntent()
    data class PhoneNumberFieldFocusChangeIntent(val hasFocus: Boolean) : LoginOptionsIntent()

}

@SealedInterop.Enabled
sealed class LoginOptionsSideEffect {
    object BringFocusOnPhoneNumberField : LoginOptionsSideEffect()
    data class ShowToast(val msg: String) : LoginOptionsSideEffect()
    data class HandleKeyboardVisibility(val show: Boolean): LoginOptionsSideEffect()
}

@Immutable
data class CountryFlagTabUIState(
    val flagImage: ImageUIState,
    val interactionIcon: ImageUIState = ImageUIState(
        img = IconType.ARROW_DROP_DOWN,
        size = ImageUISize(all = ChaloThemeHelper.dimens.medium4)
    ),
    val spec: CountryFlagTabSpec = CountryFlagTabSpec(),
    val eventHandler: UiEventHandler
)

@Immutable
data class CountryFlagTabSpec(
    val flagAndInteractionIconSpacing: ChaloPadding = ChaloPadding(all = ChaloThemeHelper.dimens.small3),
    val borderSpec: BorderUISpec = BorderUISpec(
        width = ChaloThemeHelper.dimens.small0,
        color = ChaloColorToken.StrikeOffFontColor,
        shape = UIShape.MediumRoundedCorner
    ),
    val internalPadding: ChaloPadding = ChaloPadding(horizontal = ChaloThemeHelper.dimens.medium3)
)

@Immutable
data class LoginSupportedCountryDialogUIState(
    val title: ChaloTextUIState,
    val supportedCountryList: List<SupportedCountryItemUIState>,
    val dialogUIEventHandler: DialogUIEventHandler = DialogUIEventHandler(
        dismissOnClickOutside = false,
        dismissOnBackPress = false,
        onDismissRequest = DialogDismissRequest.NO_OP
    ),
    val spec: LoginSupportedCountryDialogSpec = LoginSupportedCountryDialogSpec()
)

@Immutable
data class LoginSupportedCountryDialogSpec(
    val titleAndListSpace: Dp = ChaloThemeHelper.dimens.medium4,
    val topSpacingForAllExceptFirstItem: Dp = ChaloThemeHelper.dimens.medium3,
    val bottomSpacingForAllExceptLastItem: Dp = ChaloThemeHelper.dimens.medium3,
    val containerBackground: ChaloColorToken = ChaloColorToken.White,
    val containerShape: UIShape = UIShape.MediumRoundedCorner,
    val containerPadding: ChaloPadding = ChaloPadding(all = ChaloThemeHelper.dimens.medium3),
)

@Immutable
data class SupportedCountryItemUIState(
    val countryInfo: ChaloTextUIState,
    val flagImage: ImageUIState?,
    val spec: SupportedCountryItemUISpec = SupportedCountryItemUISpec(),
    val eventHandler: UiEventHandler,
)

@Immutable
data class SupportedCountryItemUISpec(
    val unavailableIconSize: ImageUISize = ImageUISize(ChaloThemeHelper.dimens.large5),
    val unavailableIconBackgroundColor: ChaloColorToken = ChaloColorToken.Gray85,
    val iconAndCallingCodeSpace: Dp = ChaloThemeHelper.dimens.medium3
)

@Immutable
data class LoginOptionsScreenSpecs(
    val mainContentPadding: ChaloPadding = ChaloPadding(horizontal = ChaloThemeHelper.dimens.medium3),
    val continueBtnPadding: ChaloPadding = ChaloPadding(
        horizontal = ChaloThemeHelper.dimens.medium3,
        vertical = ChaloThemeHelper.dimens.medium3
    ),
    val containerColor: ChaloColorToken = ChaloColorToken.White
)



 */