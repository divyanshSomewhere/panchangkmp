package com.gometro.base.analytics.events

data class AnalyticsEvent(
    val eventName: String,
    private val eventProperties: Map<String, Any>,
    val frequency: AnalyticsFrequency = AnalyticsFrequency.Always
) {
    private val additionalProperties by lazy { mutableMapOf<String, Any>() }
    internal val propertiesToReport: Map<String, Any>
        get() {
            val map = mutableMapOf<String, Any>()
            map.putAll(eventProperties)
            map.putAll(additionalProperties)
            return map
        }

    fun addProperty(name: String, value: Any) {
        additionalProperties[name] = value
    }
}
