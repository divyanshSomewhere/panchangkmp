package com.gometro.base.analytics.events

sealed class AnalyticsFrequency(open val frequency: Long) {
    data object Always : AnalyticsFrequency(Long.MIN_VALUE)
    data object Once : AnalyticsFrequency(Long.MAX_VALUE)
    data object Daily : AnalyticsFrequency(24 * 60 * 60 * 1000L)
    data object Hourly : AnalyticsFrequency(60 * 60 * 1000L)
    data class Custom(override val frequency: Long) : AnalyticsFrequency(frequency)
}