package com.gometro.base.analytics.senders

import com.gometro.base.analytics.events.AnalyticsEvent


interface AnalyticsSender {

    fun reportEvent(event: AnalyticsEvent)

    fun flushEvents()

    fun setSuperProperties(properties: Map<String, String>)

    fun setUserProperties(properties: Map<String, String>)

    fun incrementProperty(property: String, increment: Double)

    fun setUserName(userName: String)

    fun setEmail(email: String)

}