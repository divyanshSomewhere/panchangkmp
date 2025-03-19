package com.gometro.login.manager

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gometro.base.userprofile.UserProfileAndAuthStoreManager
import com.gometro.userprofile.data.models.UserProfileDatastoreModel
import com.gometro.base.utils.CustomJsonParser
import com.gometro.base.utils.CustomJsonParser.decodeFromStringSafely
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.encodeToString

class UserProfileAndAuthStoreManagerImpl(
    private val userDetailsDataStore: DataStore<Preferences>
) : UserProfileAndAuthStoreManager {

    override suspend fun storeUserProfileDetails(userProfileDatastoreModel: UserProfileDatastoreModel) {
        userDetailsDataStore.edit {
            it[userInfoKey] = CustomJsonParser.Json.encodeToString(userProfileDatastoreModel)
        }
    }

    override fun getUserProfileDetails(): Flow<UserProfileDatastoreModel?> {
        return userDetailsDataStore.data.map {
            it[userInfoKey]?.let { profileStringInfo ->
                try {
                    CustomJsonParser.Json.decodeFromStringSafely(profileStringInfo)
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    override suspend fun storeAuthTokens(
        accessToken: String,
        refreshToken: String,
        expiryTime: Long,
        delta: Long
    ) {
        userDetailsDataStore.edit {
            it[accessTokenKey] = accessToken
            it[refreshTokenKey] = refreshToken
            it[expiryTimeKey] = expiryTime
            it[deltaKey] = delta
        }
    }

    override suspend fun getAccessToken(): String? {
        return userDetailsDataStore.data.map { it[accessTokenKey] }.firstOrNull()
    }

    override suspend fun getRefreshToken(): String? {
        return userDetailsDataStore.data.map { it[refreshTokenKey] }.firstOrNull()
    }

    override suspend fun getAccessTokenExpiryTime(): Long {
        return userDetailsDataStore.data.map { it[expiryTimeKey] }.firstOrNull() ?: -1
    }

    override suspend fun getDeltaTime(): Long {
        return userDetailsDataStore.data.map { it[deltaKey] }.firstOrNull() ?: -1
    }

    override suspend fun clearStoredUserDetails() {
        userDetailsDataStore.edit {
            it.remove(userInfoKey)
        }
    }

    override suspend fun clearStoredAuthTokensDetails() {
        userDetailsDataStore.edit {
            it.remove(accessTokenKey)
            it.remove(refreshTokenKey)
            it.remove(expiryTimeKey)
            it.remove(deltaKey)
        }
    }

    override suspend fun setIsUserLoginFirstTime(isUserLoginFirstTime: Boolean) {
        userDetailsDataStore.edit {
            it[firstLoginKey] = isUserLoginFirstTime
        }
    }

    override suspend fun isUserLoginFirstTime(): Boolean {
        return userDetailsDataStore.data.mapNotNull { it[firstLoginKey] }.firstOrNull() ?: false
    }

    companion object {
        const val USER_DETAILS_DATA_STORE_NAME = "user_details_datastore.preferences_pb"

        private const val USER_INFO_KEY_NAME = "user_info"
        private const val ACCESS_TOKEN_KEY_NAME = "accessToken"
        private const val REFRESH_TOKEN_KEY_NAME = "refreshToken"
        private const val EXPIRY_TIME_KEY_NAME = "expiryTime"
        private const val DELTA_KEY_NAME = "delta"
        private const val FIRST_LOGIN_KEY_NAME = "firstLogin"
        private const val KEY_COUNTRY_CONFIG = "countryConfig"

        val userInfoKey = stringPreferencesKey(USER_INFO_KEY_NAME)
        val accessTokenKey = stringPreferencesKey(ACCESS_TOKEN_KEY_NAME)
        val refreshTokenKey = stringPreferencesKey(REFRESH_TOKEN_KEY_NAME)
        val expiryTimeKey = longPreferencesKey(EXPIRY_TIME_KEY_NAME)
        val deltaKey = longPreferencesKey(DELTA_KEY_NAME)
        val firstLoginKey = booleanPreferencesKey(FIRST_LOGIN_KEY_NAME)
        val keyCountryConfig = stringPreferencesKey(KEY_COUNTRY_CONFIG)
    }
}