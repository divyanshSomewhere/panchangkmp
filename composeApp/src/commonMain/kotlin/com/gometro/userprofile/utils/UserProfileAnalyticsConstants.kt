package app.chalo.userprofile.utils

object UserProfileAnalyticsConstants {

    const val EVENT_USER_PROFILE_SCREEN_REFRESHED = "user profile screen refreshed"
    const val EVENT_USER_PROFILE_EDIT_CLICKED = "user profile edit clicked"
    const val EVENT_USER_PROFILE_LOGOUT_CLICKED = "user profile logout clicked"
    const val EVENT_USER_PROFILE_LOGOUT_RESULT = "user profile logout result"

    const val EVENT_USER_PROFILE_EDIT_SCREEN_DISPLAYED = "user profile edit screen displayed"
    const val EVENT_USER_PROFILE_EDIT_CANCEL_CLICKED = "user profile edit cancel clicked"
    const val EVENT_USER_PROFILE_EDIT_SAVE_CLICKED = "user profile edit save clicked"
    const val EVENT_USER_PROFILE_EDIT_ERROR = "user profile edit failed"
    const val EVENT_USER_PROFILE_EDIT_SUCCESSFUL = "user profile edit successful"
    const val EVENT_USER_PROFILE_PROFILE_PHOTO_UPLOADED_SUCCESSFULLY = "user profile photo uploaded"
    const val EVENT_USER_PROFILE_EDIT_PHOTO_CLICKED = "user profile edit photo clicked"
    const val EVENT_USER_PROFILE_PHOTO_CHANGED = "user profile photo changed"
    const val EVENT_USER_PROFILE_GENDER_FIELD_CLICKED = "user profile edit gender clicked"
    const val EVENT_USER_PROFILE_GENDER_CHANGED = "user profile gender changed"
    const val EVENT_USER_PROFILE_DOB_FIELD_CLICKED = "user profile edit dob clicked"
    const val EVENT_USER_PROFILE_DOB_CHANGED = "user profile dob changed"

    const val ATTR_USER_LOGGED_OUT = "loggedOutSuccessfully"
    const val ATTR_REASON = "reason"
    const val ATTR_INITIALLY_SELECTED_DATE_ON_DOB_CALENDER = "initiallySelectedDate"

    const val SOURCE_USER_PROFILE_DISPLAY_SCREEN = "userProfileDisplayScreen"
    const val SOURCE_USER_PROFILE_EDIT_SCREEN = "userProfileEditScreen"
}
