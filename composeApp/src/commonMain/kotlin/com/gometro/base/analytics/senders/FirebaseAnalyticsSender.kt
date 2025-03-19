package com.gometro.base.analytics.senders

import com.gometro.base.analytics.events.AnalyticsEvent

class FirebaseAnalyticsSender : AnalyticsSender {

    override fun reportEvent(event: AnalyticsEvent) {

    }

    override fun flushEvents() {

    }

    override fun setSuperProperties(properties: Map<String, String>) {

    }

    override fun setUserProperties(properties: Map<String, String>) {

    }

    override fun incrementProperty(property: String, increment: Double) {

    }

    override fun setUserName(userName: String) {

    }

    override fun setEmail(email: String) {

    }
}