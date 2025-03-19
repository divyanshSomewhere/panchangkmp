package com.gometro.login.domain

import com.gometro.base.featurecontracts.BasicInfoContract
import com.gometro.login.data.models.app.PostLoginAuthTokensAppModel
import com.gometro.login.data.repository.LoginRepository


class ParseAndStoreTokensUseCase(
    private val loginRepository: LoginRepository,
    private val accessTokenParser: JwtEncodedAccessTokenParser,
    private val basicInfoContract: BasicInfoContract
) {

    suspend operator fun invoke(tokensAppModel: PostLoginAuthTokensAppModel): Boolean {
        return try {
            val parsedAccessTokenData = accessTokenParser.invoke(tokensAppModel.accessToken)
            val currentTimeInSeconds = basicInfoContract.getSystemTime() / 1000

            loginRepository.storeTokensPostLogin(
                accessToken = tokensAppModel.accessToken,
                refreshToken = tokensAppModel.refreshToken,
                expiryTime = parsedAccessTokenData.expiryTime,
                delta = currentTimeInSeconds - (parsedAccessTokenData.issuedAt ?: currentTimeInSeconds)
            )
            true
        } catch (e: AccessTokenParseFailedException) {
            false
        }
    }
}
