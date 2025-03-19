package com.gometro.login.manager

import com.gometro.login.data.repository.LoginRepository
import com.gometro.login.domain.LogoutUserUseCase
import com.gometro.login.domain.RefreshAuthTokenResult
import com.gometro.login.domain.RefreshAuthTokensForUserUseCase
import com.gometro.network.AuthSecurityManager

class AuthSecurityManagerImpl(
    private val loginRepository: LoginRepository,
    private val refreshAuthTokensForUserUseCase: RefreshAuthTokensForUserUseCase,
    private val logoutUserUseCase: LogoutUserUseCase
) : AuthSecurityManager {

    override suspend fun getUserId(): String {
        return loginRepository.getUserId() ?: ""
    }

    override suspend fun getAccessToken(): String {
        return loginRepository.getAccessToken() ?: ""
    }

    override suspend fun refreshToken(): Boolean {
        when(refreshAuthTokensForUserUseCase.invoke()) {
            RefreshAuthTokenResult.TOKENS_REFRESHED -> {
                return true
            }
            RefreshAuthTokenResult.USER_ID_NOT_PRESENT,
            RefreshAuthTokenResult.REFRESH_TOKEN_FOR_EXPIRED_TOKEN_NOT_PRESENT -> {
                // ideally these 2 cases should not happen because if we were trying to make a secured api call
                // then that means user was supposed to be logged in having a userId and refresh token
                // if that's not the case - we should clear user data and logout the user without sync

                forceLogoutUser("refreshToken")
                return false
            }
            RefreshAuthTokenResult.REFRESH_CALL_UNAUTHORIZED -> {
                // - we got 401 from a secured api
                // - we tried refreshing access token
                // - refresh token server itself gave us 401
                // now clear data and logout user without any sync

                forceLogoutUser("refreshToken")
                return false
            }
            RefreshAuthTokenResult.SERVER_ERROR,
            RefreshAuthTokenResult.INVALID_TOKENS_RECEIVED,
            RefreshAuthTokenResult.UNKNOWN_LOCAL_ERROR,
            RefreshAuthTokenResult.RESPONSE_PARSE_EXCEPTION,
            RefreshAuthTokenResult.TOKENS_PARSE_EXCEPTION -> {
                // these are general error cases in which we should give user option to retry,
                // because we tried to refresh token but because of any server or network
                // issues api failed
                return false
            }
        }
    }

    override suspend fun onRetryLimitExceeded(url: String) {
        forceLogoutUser(url)
    }

    private suspend fun forceLogoutUser(url: String) {
        logoutUserUseCase.invoke(userTriggeredLogout = false, url = url)
    }
}