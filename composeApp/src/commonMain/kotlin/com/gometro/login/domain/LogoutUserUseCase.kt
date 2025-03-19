package com.gometro.login.domain

import com.gometro.base.featurecontracts.AnalyticsContract
import com.gometro.base.featurecontracts.Device
import com.gometro.constants.Source
import com.gometro.logger.CrashlyticsLogger
import com.gometro.logger.runSafelyAndReportIfException
import com.gometro.login.data.repository.LoginRepository
import com.gometro.scenes.AppNavOptions
import com.gometro.scenes.AppNavigationManager
import com.gometro.scenes.AppNavigationRequest
import com.gometro.scenes.PopUpToConfig
import com.gometro.scenes.args.LoginOptionsArgs
import com.gometro.userprofile.data.repository.UserProfileRepository

class LogoutUserUseCase(
    private val appNavigationManager: AppNavigationManager,
    private val loginRepository: LoginRepository,
    private val userProfileRepository: UserProfileRepository,
    private val crashlyticsLogger: CrashlyticsLogger,
    private val device: Device,
    private val analyticsContract: AnalyticsContract
) {

    suspend operator fun invoke(
        userTriggeredLogout: Boolean,
        url: String? = null
    ): Boolean {

        raiseLogoutEvent(url = url, userTriggeredLogout = userTriggeredLogout)

        crashlyticsLogger.runSafelyAndReportIfException {
            loginRepository.makeLogoutUserCall(deviceId = device.getDeviceId())
        }
        userProfileRepository.clearStoredUserProfileAndAuthTokensDetails()

        appNavigationManager.postNavigationRequest(
            navRequest = AppNavigationRequest.Navigate(
                args = LoginOptionsArgs(Source.NONE),
                navOptions = AppNavOptions(
                    launchSingleTop = true,
                    popUpToConfig = PopUpToConfig.ClearAll()
                )
            )
        )
        return true
    }

    private suspend fun raiseLogoutEvent(
        url: String?,
        userTriggeredLogout: Boolean
    ) {

    }
}
