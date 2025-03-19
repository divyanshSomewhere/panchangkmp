package com.gometro.userprofile.ui.edit

import androidx.compose.runtime.Stable
import co.touchlab.skie.configuration.annotations.SealedInterop
import com.gometro.userprofile.data.models.Gender
import com.gometro.base.utils.bitmaputil.BitmapImage

@Stable
data class UserProfileEditState(
    val firstName: String = "",
    val lastName: String = "",
    val gender: Gender = Gender.NULL,
    val dobInMillis: Long? = null,
    val dobString: String = "",
    val emailId: String = "",
    val phoneNumber: String = "",
    val profilePhotoUrl: String? = null,
    val newSelectedPhotoBitmap: BitmapImage? = null,
    val isLoading: Boolean = false,
    val dialogType: UserProfileEditDialogType? = null
)

@SealedInterop.Enabled
sealed class UserProfileEditIntent {
    data object InitializationIntent : UserProfileEditIntent()
    data object CancelBtnClicked : UserProfileEditIntent()
    data object SaveBtnClicked : UserProfileEditIntent()
    data object UploadProfilePhotoClicked : UserProfileEditIntent()
    data class ProfilePhotoSelected(val selectedPhotoBitmap: BitmapImage) : UserProfileEditIntent()
    data class FirstNameEntered(val firstNameEntered: String) : UserProfileEditIntent()
    data class LastNameEntered(val lastNameEntered: String) : UserProfileEditIntent()
    data object GenderFieldClicked : UserProfileEditIntent()
    data class GenderSelected(val genderSelected: Gender) : UserProfileEditIntent()
    data object DateOfBirthFieldClicked : UserProfileEditIntent()
    data class DateOfBirthSelected(val selectedDobMillis: Long) : UserProfileEditIntent()
    data class EmailIdEntered(val emailIdEntered: String) : UserProfileEditIntent()
    data object DismissDialog : UserProfileEditIntent()
    data class PermissionRationaleDialogInteraction(
        val shouldNavigateToSettings: Boolean
    ) : UserProfileEditIntent()
}

@SealedInterop.Enabled
sealed class UserProfileEditSideEffect {
    data class ShowToast(val msg: String) : UserProfileEditSideEffect()
    data object NavigateToSettings : UserProfileEditSideEffect()
}

@SealedInterop.Enabled
sealed class UserProfileEditDialogType {
    data class GenderSelection(
        val currentSelection: Gender,
        val genderOptions: List<Gender>
    ) : UserProfileEditDialogType()

    data class DateOfBirthSelection(
        val currentDobMillis: Long
    ) : UserProfileEditDialogType()

    data object GalleryPermissionRationale : UserProfileEditDialogType()
}
