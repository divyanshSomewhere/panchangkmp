package com.gometro.base.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gometro.buildconfig.Environment
import com.gometro.buildconfig.AppBuildConfig

class UpdateGometroBuildConfigUseCase(
    private val envChangeStore: DataStore<Preferences>,
    private val chaloBuildConfig: AppBuildConfig
) {

    suspend fun invoke(env: Environment) {
        if (chaloBuildConfig.isDebugBuild) {

            envChangeStore.edit {
                it[envKey] = env.name
            }
        }
    }

    companion object {
        val envKey = stringPreferencesKey("ENV")
    }

}