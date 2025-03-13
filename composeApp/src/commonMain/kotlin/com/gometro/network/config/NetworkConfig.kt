package com.gometro.network.config

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class NetworkConfig(
    private val networkConfigDataStore: DataStore<Preferences>
) {

    suspend fun getConnectionTimeout(): Int {
        return getInt(
            KEY_CONNECT_TIMEOUT,
            NetworkConstants.Config.CONNECTION_TIMEOUT_IN_MILLIS
        )
    }

    suspend fun setConnectionTimeout(connectionTimeout: Int) {
        saveInteger(KEY_CONNECT_TIMEOUT, connectionTimeout)
    }

    suspend fun getReadTimeout(): Int {
        return getInt(
            KEY_READ_TIMEOUT,
            NetworkConstants.Config.OK_HTTP_READ_TIMEOUT_IN_MILLIS
        )
    }

    suspend fun setReadTimeout(readTimeout: Int) {
        saveInteger(KEY_READ_TIMEOUT, readTimeout)
    }

    suspend fun getWriteTimeout(): Int {
        return getInt(
            KEY_WRITE_TIMEOUT,
            NetworkConstants.Config.OK_HTTP_WRITE_TIMEOUT_IN_MILLIS
        )
    }

    suspend fun setWriteTimeout(writeTimeout: Int) {
        saveInteger(KEY_WRITE_TIMEOUT, writeTimeout)
    }

    suspend fun setKeepAliveTime(keepAliveTime: Long) {
        saveLong(KEY_KEEP_ALIVE_TIME, keepAliveTime)
    }

    suspend fun getKeepAliveTime(): Long {
        return getLong(
            KEY_KEEP_ALIVE_TIME,
            NetworkConstants.Config.MAX_KEEP_ALIVE
        )
    }

    private suspend fun saveInteger(key: Preferences.Key<Int>, value: Int) {
        networkConfigDataStore.edit { it[key] = value }
    }

    private suspend fun getInt(key: Preferences.Key<Int>, defaultValue: Int): Int {
        return networkConfigDataStore.data.map { it[key] }.firstOrNull() ?: defaultValue
    }

    private suspend fun saveLong(key: Preferences.Key<Long>, value: Long) {
        networkConfigDataStore.edit { it[key] = value }
    }

    private suspend fun getLong(key: Preferences.Key<Long>, defaultValue: Long): Long {
        return networkConfigDataStore.data.map { it[key] }.firstOrNull() ?: defaultValue
    }

    companion object {
        const val NETWORK_CONFIG_FILE = "network_config.preferences_pb"
        const val NETWORK_CONFIG_PREFS_NAME = "network_config"
        internal val KEY_CONNECT_TIMEOUT = intPreferencesKey("connect_timeout")
        internal val KEY_READ_TIMEOUT = intPreferencesKey("read_timeout")
        internal val KEY_WRITE_TIMEOUT = intPreferencesKey("write_timeout")
        internal val KEY_KEEP_ALIVE_TIME = longPreferencesKey("keep_alive")
    }

}