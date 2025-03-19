package com.gometro.base.featurecontracts

import com.gometro.base.analytics.events.AnalyticsFrequency

interface AnalyticsContract {

    @Deprecated(message = "use raiseAnalyticsEvent with Source enum instead", level = DeprecationLevel.WARNING)
    fun raiseAnalyticsEvent(
        name: String,
        source: String,
        eventProperties: Map<String, Any>? = null,
        frequency: AnalyticsFrequency = AnalyticsFrequency.Always,
        sendAirshipEvent: Boolean = false,
        sendToPlotline: Boolean = true
    )

    fun addToPeopleProperties(properties: Map<String, String>)

    fun addToSuperProperties(properties: Map<String, String>)

    fun setupAnalytics()

}