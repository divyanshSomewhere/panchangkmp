package com.gometro.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class LocalDataStore(
    private val dataStore: DataStore<Preferences>
) {
    suspend fun <T> updateLocalDataStore(key: Preferences.Key<T>, value: T) {
        dataStore.edit {
            it[key] = value
        }
    }

    suspend fun updateLocalDataStore(key: String, value: String) {
        val storeKey = stringPreferencesKey(key)
        dataStore.edit {
            it[storeKey] = value
        }
    }

    suspend fun updateLocalDataStore(data: List<Pair<String, String>>) {
        dataStore.edit {
            data.forEach { (key, value) ->
                it[stringPreferencesKey(key)] = value
            }
        }
    }

    fun <T> getFlowFromLocalDataStore(key: Preferences.Key<T>): Flow<T?> {
        return dataStore.data.map  { preferences ->
            // No type safety.
            preferences[key]
        }
    }

    suspend fun getStringDataFromLocalDataStore(key: String): String? {
        val storeKey = stringPreferencesKey(key)
        return dataStore.data.map  { preferences ->
            // No type safety.
            preferences[storeKey]
        }.firstOrNull()
    }

    fun getAllKeys(): Flow<List<Preferences.Key<*>>?> {
        return dataStore.data.map {
            it.asMap().keys.toList()
        }
    }

    suspend fun getAllEntries(): Map<String, Any>? {
        return dataStore.data.map { prefs ->
            prefs.asMap().mapKeys { it.key.name }
        }.firstOrNull()
    }

    suspend fun deleteData(keyList: List<Preferences.Key<*>>) {
        dataStore.edit {
            for (key in keyList) {
                it.remove(key)
            }
        }
    }

    suspend fun containsKey(key: String): Boolean {
        val allKeys = getAllKeys().firstOrNull() ?: listOf()
        return allKeys.find { it.name == key } != null
    }

    suspend fun deleteAll() {
        dataStore.edit {
            it.clear()
        }
    }
}