package com.gometro.login.domain

import com.gometro.base.utils.result.UseCaseResult
import com.gometro.login.constants.LoginFeatureErrorCodes
import com.gometro.login.data.errors.LoginRemoteErrorCodes.LOGIN_PREVIOUS_OTP_EXPIRED
import com.gometro.login.data.exceptions.SendOtpFailedException
import com.gometro.login.data.repository.LoginRepository
import com.gometro.login.constants.LoginGeneralConstants
import com.gometro.network.exception.ApiCallLocalNetworkException
import com.gometro.network.exception.NetworkSuccessResponseParseException

class SendOtpForLoginUseCase(
    private val loginRepository: LoginRepository
) {
    /**
     * @param previousRefNoToResendOtp pass previous ref no if available to resend otp, otherwise
     * fresh new otp is requested
     */
    suspend operator fun invoke(
        phoneNumber: String,
        countryCode: String,
        previousRefNoToResendOtp: String? = null
    ): UseCaseResult<String, SendOtpErrorMsgAndReason> {
        return try {
            val response = loginRepository.sendOtpForPhoneAuth(
                phoneNumber,
                countryCode,
                previousRefNoToResendOtp,
                LoginGeneralConstants.TEMPLATE_ID_FOR_OTP_LOGIN
            )

            if (response.status == false) {
                UseCaseResult.Failure(SendOtpErrorMsgAndReason(SendOtpForLoginErrorReason.OTP_STATUS_FALSE))
            } else if (response.refNo.isNullOrEmpty()) {
                UseCaseResult.Failure(SendOtpErrorMsgAndReason(SendOtpForLoginErrorReason.INVALID_REF_NO))
            } else {
                UseCaseResult.Success(response.refNo)
            }
        } catch (e: SendOtpFailedException) {
            when (e.genericApiErrorResponse?.errorCode) {
                LOGIN_PREVIOUS_OTP_EXPIRED -> {
                    UseCaseResult.Failure(SendOtpErrorMsgAndReason(SendOtpForLoginErrorReason.PREVIOUS_OTP_EXPIRED, e.message))
                }
                else -> {
                    UseCaseResult.Failure(SendOtpErrorMsgAndReason(SendOtpForLoginErrorReason.SERVER_ERROR, e.message))
                }
            }
        } catch (e: NetworkSuccessResponseParseException) {
            UseCaseResult.Failure(SendOtpErrorMsgAndReason(SendOtpForLoginErrorReason.PARSE_EXCEPTION))
        } catch (e: ApiCallLocalNetworkException) {
            UseCaseResult.Failure(SendOtpErrorMsgAndReason(SendOtpForLoginErrorReason.UNKNOWN_ERROR, e.message))
        }
    }
}

data class SendOtpErrorMsgAndReason(
    val reason: SendOtpForLoginErrorReason,
    val msg: String? = null
)

enum class SendOtpForLoginErrorReason {
    OTP_STATUS_FALSE,
    INVALID_REF_NO,
    PARSE_EXCEPTION,
    SERVER_ERROR,
    UNKNOWN_ERROR,
    PREVIOUS_OTP_EXPIRED;

    fun getErrorCodeForReason(): Int {
        return when (this) {
            OTP_STATUS_FALSE -> LoginFeatureErrorCodes.LOGIN_OPTION_SEND_OTP_OTP_STATUS_FALSE
            INVALID_REF_NO -> LoginFeatureErrorCodes.LOGIN_OPTION_SEND_OTP_INVALID_REF_NO
            PARSE_EXCEPTION -> LoginFeatureErrorCodes.LOGIN_OPTION_SEND_OTP_PARSE_EXCEPTION
            SERVER_ERROR -> LoginFeatureErrorCodes.LOGIN_OPTION_SEND_OTP_SERVER_ERROR
            UNKNOWN_ERROR -> LoginFeatureErrorCodes.LOGIN_OPTION_SEND_OTP_UNKNOWN_ERROR
            PREVIOUS_OTP_EXPIRED -> LoginFeatureErrorCodes.LOGIN_OPTION_SEND_OTP_PREVIOUS_OTP_EXPIRED
        }
    }
}
