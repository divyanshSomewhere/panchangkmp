package com.gometro.login.domain

import com.gometro.base.featurecontracts.Device
import com.gometro.login.data.exceptions.InvalidRefreshAuthTokensException
import com.gometro.login.data.exceptions.RefreshAuthTokensFailedException
import com.gometro.login.data.models.app.PostLoginAuthTokensAppModel
import com.gometro.login.data.repository.LoginRepository
import com.gometro.network.exception.ApiCallLocalNetworkException
import com.gometro.network.exception.NetworkSuccessResponseParseException


class RefreshAuthTokensForUserUseCase(
    private val loginRepository: LoginRepository,
    private val parseAndStoreTokensUseCase: ParseAndStoreTokensUseCase,
    private val device: Device
) {

    suspend operator fun invoke(): RefreshAuthTokenResult {
        val refreshAuthTokenModel = try {
            val refreshToken = loginRepository.getRefreshToken()
                ?: return RefreshAuthTokenResult.REFRESH_TOKEN_FOR_EXPIRED_TOKEN_NOT_PRESENT

            val userId = loginRepository.getUserId()
                ?: return RefreshAuthTokenResult.USER_ID_NOT_PRESENT

            loginRepository.refreshAuthTokens(
                refreshToken = refreshToken,
                userId = userId,
                deviceId = device.getDeviceId()
            )
        } catch (e: RefreshAuthTokensFailedException) {
            return RefreshAuthTokenResult.SERVER_ERROR
        } catch (e: InvalidRefreshAuthTokensException) {
            return RefreshAuthTokenResult.INVALID_TOKENS_RECEIVED
        } catch (e: ApiCallLocalNetworkException) {
            when (e) {
                is ApiCallLocalNetworkException.NoInternetException,
                is ApiCallLocalNetworkException.RequestCancelledException,
                is ApiCallLocalNetworkException.ServerErrorException,
                is ApiCallLocalNetworkException.NoUpdateInFetchedDataBasedOnEtagVersion,
                is ApiCallLocalNetworkException.UnexpectedException -> {
                    return RefreshAuthTokenResult.UNKNOWN_LOCAL_ERROR
                }
                is ApiCallLocalNetworkException.SecureCallUnauthorizedAndRefreshTokenServerUnreachableException -> {
                    // if this exception comes here, then that means
                    // - we were trying to refresh an expired token
                    // - and refresh token call itself gave us 401 error failing in refreshing the token
                    // in this case we have to logout user without any sync call
                    return RefreshAuthTokenResult.REFRESH_CALL_UNAUTHORIZED
                }
            }
        } catch (e: NetworkSuccessResponseParseException) {
            return RefreshAuthTokenResult.RESPONSE_PARSE_EXCEPTION
        }

        val isTokenSuccessfullyParsedAndStored = parseAndStoreTokensUseCase.invoke(
            PostLoginAuthTokensAppModel(
                refreshAuthTokenModel.accessToken,
                refreshAuthTokenModel.refreshToken
            )
        )

        return if (isTokenSuccessfullyParsedAndStored) {
            RefreshAuthTokenResult.TOKENS_REFRESHED
        } else {
            RefreshAuthTokenResult.TOKENS_PARSE_EXCEPTION
        }
    }
}

enum class RefreshAuthTokenResult {
    TOKENS_REFRESHED,

    USER_ID_NOT_PRESENT,
    REFRESH_TOKEN_FOR_EXPIRED_TOKEN_NOT_PRESENT,

    SERVER_ERROR,
    INVALID_TOKENS_RECEIVED,
    REFRESH_CALL_UNAUTHORIZED,
    UNKNOWN_LOCAL_ERROR,
    RESPONSE_PARSE_EXCEPTION,

    TOKENS_PARSE_EXCEPTION
}
