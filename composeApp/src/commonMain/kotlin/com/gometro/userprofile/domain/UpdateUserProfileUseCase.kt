package com.gometro.userprofile.domain

import app.chalo.userprofile.utils.UserProfileFeatureErrorCodes
import com.gometro.login.data.exceptions.ProfileAndTokensExceptions
import com.gometro.network.exception.ApiCallLocalNetworkException
import com.gometro.network.exception.NetworkSuccessResponseParseException
import com.gometro.userprofile.data.errors.UserProfileRemoteErrorCodes
import com.gometro.userprofile.data.exceptions.UpdateUserProfileFailedException
import com.gometro.userprofile.data.models.Gender
import com.gometro.userprofile.data.repository.UserProfileRepository

class UpdateUserProfileUseCase(
    private val userProfileDetailsProvider: UserProfileDetailsProvider,
    private val userProfileRepository: UserProfileRepository,
) {

    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        profilePhotoUrl: String?,
        gender: Gender?,
        dateOfBirthInMillis: Long?,
        emailId: String?
    ): UpdateUserProfileResult {
        if (!userProfileDetailsProvider.isLoggedIn.value) {
            return UpdateUserProfileResult.USER_NOT_LOGGED_IN
        }

        val userId = userProfileDetailsProvider.getUserIdAsync()
            ?: return UpdateUserProfileResult.USER_NOT_LOGGED_IN

        val userProfileAppModel = try {
            userProfileRepository.updateUserProfileOnServer(
                userId = userId,
                firstName = firstName,
                lastName = lastName,
                profilePhoto = profilePhotoUrl,
                gender = gender?.toString()?.uppercase() ?: "",
                dateOfBirth = dateOfBirthInMillis,
                emailId = emailId
            )
        } catch (e: UpdateUserProfileFailedException) {
            return when (e.genericChaloErrorResponse?.errorCode) {
                UserProfileRemoteErrorCodes.INVALID_DOB -> UpdateUserProfileResult.INVALID_DOB
                UserProfileRemoteErrorCodes.USER_DOES_NOT_EXIST -> UpdateUserProfileResult.USER_DOES_NOT_EXIST
                UserProfileRemoteErrorCodes.USER_ID_MISMATCH -> UpdateUserProfileResult.USER_ID_MISMATCH
                else -> UpdateUserProfileResult.SERVER_ERROR
            }
        } catch (e: ApiCallLocalNetworkException) {
            return UpdateUserProfileResult.UNKNOWN_LOCAL_EXCEPTION
        } catch (e: NetworkSuccessResponseParseException) {
            return UpdateUserProfileResult.RESPONSE_PARSE_EXCEPTION
        } catch (e: ProfileAndTokensExceptions.InvalidProfileDetails) {
            return UpdateUserProfileResult.INVALID_RESPONSE
        }

        userProfileRepository.updateUserProfileLocally(userProfileAppModel)

        return UpdateUserProfileResult.PROFILE_UPDATED_SUCCESSFULLY
    }
}

enum class UpdateUserProfileResult {
    PROFILE_UPDATED_SUCCESSFULLY,

    USER_NOT_LOGGED_IN, // should not happen though
    INVALID_DOB,
    USER_DOES_NOT_EXIST,
    USER_ID_MISMATCH,
    UNKNOWN_LOCAL_EXCEPTION,
    SERVER_ERROR,
    RESPONSE_PARSE_EXCEPTION,
    INVALID_RESPONSE;

    fun getFeatureErrorCode(): Int {
        return when (this) {
            PROFILE_UPDATED_SUCCESSFULLY -> -1
            USER_NOT_LOGGED_IN -> UserProfileFeatureErrorCodes.UPDATE_PROFILE_USER_NOT_LOGGED_IN_ERROR
            INVALID_DOB -> UserProfileFeatureErrorCodes.UPDATE_PROFILE_INVALID_DOB_ERROR
            USER_DOES_NOT_EXIST -> UserProfileFeatureErrorCodes.UPDATE_PROFILE_USER_DOES_NOT_EXIST_ERROR
            USER_ID_MISMATCH -> UserProfileFeatureErrorCodes.UPDATE_PROFILE_ID_MISMATCH_ERROR
            UNKNOWN_LOCAL_EXCEPTION -> UserProfileFeatureErrorCodes.UPDATE_PROFILE_LOCAL_EXCEPTION_ERROR
            SERVER_ERROR -> UserProfileFeatureErrorCodes.UPDATE_PROFILE_SERVER_ERROR
            RESPONSE_PARSE_EXCEPTION -> UserProfileFeatureErrorCodes.UPDATE_PROFILE_RESPONSE_PARSE_EXCEPTION_ERROR
            INVALID_RESPONSE -> UserProfileFeatureErrorCodes.UPDATE_PROFILE_INVALID_RESPONSE_ERROR
        }
    }
}
