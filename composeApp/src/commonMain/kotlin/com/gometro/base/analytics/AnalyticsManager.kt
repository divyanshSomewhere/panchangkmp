package com.gometro.base.analytics

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.gometro.base.analytics.events.AnalyticsEvent
import com.gometro.base.analytics.events.AnalyticsFrequency
import com.gometro.base.analytics.senders.AnalyticsSender
import com.gometro.base.featurecontracts.BasicInfoContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AnalyticsManager(
    private val senders: List<AnalyticsSender>,
    private val dataStore: DataStore<Preferences>,
    private val basicInfoContract: BasicInfoContract
) {

    private val scope = CoroutineScope(Dispatchers.IO)

    fun reportEvent(event: AnalyticsEvent) {
        when(event.frequency) {
            AnalyticsFrequency.Always -> {
                reportEventToSenders(event)
            }
            else -> {
                checkAndReportEventIfNeeded(event)
            }
        }
    }

    fun flushEvents() {
        senders.forEach { it.flushEvents() }
    }

    fun setSuperProperties(properties: Map<String, String>) {
        senders.forEach { it.setSuperProperties(properties) }
    }

    fun setUserProperties(properties: Map<String, String>) {
        senders.forEach { it.setUserProperties(properties) }
    }

    fun incrementProperty(property: String, increment: Double) {
        senders.forEach { it.incrementProperty(property, increment) }
    }

    fun setUserName(userName: String) {
        senders.forEach { it.setUserName(userName) }
    }

    fun setEmail(email: String) {
        senders.forEach { it.setEmail(email) }
    }

    private fun checkAndReportEventIfNeeded(event: AnalyticsEvent) {
        scope.launch {
            if (isMetricToBeReported(event)) {
                reportEventToSenders(event)
                setLastReportedTime(event.eventName)
            }
        }
    }

    private fun reportEventToSenders(event: AnalyticsEvent) {
        event.addProperty("time", basicInfoContract.getSystemTime().toString())
        senders.forEach { it.reportEvent(event) }
    }


    private suspend fun isMetricToBeReported(event: AnalyticsEvent): Boolean {
        return when(event.frequency) {
            AnalyticsFrequency.Always -> true
            is AnalyticsFrequency.Custom,
            AnalyticsFrequency.Daily,
            AnalyticsFrequency.Hourly,
            AnalyticsFrequency.Once -> {
                val lastReportedTime = getLastReportedTime(event.eventName) ?: return true
                val timePassedMillis = basicInfoContract.getSystemTime() - lastReportedTime
                timePassedMillis >= event.frequency.frequency
            }
        }
    }

    private suspend fun setLastReportedTime(eventName: String) {
        dataStore.edit {
            it[longPreferencesKey(eventName)] = basicInfoContract.getSystemTime()
        }
    }

    private suspend fun getLastReportedTime(name: String): Long? {
        return dataStore.data.map { it[longPreferencesKey(name)] }.firstOrNull()
    }

    companion object {
        const val ANALYTICS_DATA_STORE_NAME = "analytics.preferences_pb"
    }

}