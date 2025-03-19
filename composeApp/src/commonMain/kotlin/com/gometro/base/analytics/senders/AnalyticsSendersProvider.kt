package com.gometro.base.analytics.senders

import androidx.annotation.CallSuper

internal abstract class AnalyticsSendersProvider(
    private val firebaseAnalyticsSender: FirebaseAnalyticsSender
) {

    @CallSuper
    open fun provideAnalyticsSenders(): List<AnalyticsSender> {
        return listOf(firebaseAnalyticsSender)
    }

}