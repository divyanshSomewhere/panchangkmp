package com.gometro.base.analytics

import com.gometro.base.analytics.events.AnalyticsEvent
import com.gometro.base.analytics.events.AnalyticsFrequency
import com.gometro.base.featurecontracts.AnalyticsContract
import com.gometro.base.featurecontracts.BasicInfoContract
import com.gometro.base.featurecontracts.CoroutineContextProvider
import com.gometro.buildconfig.AppBuildConfig
import com.gometro.constants.AnalyticsEventConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AnalyticsContractImpl(
    private val analyticsManager: AnalyticsManager,
    private val basicInfoContract: BasicInfoContract,
    private val appBuildConfig: AppBuildConfig,
    coroutineContextProvider: CoroutineContextProvider
) : AnalyticsContract {

    private val scope = CoroutineScope(coroutineContextProvider.main)

    @Deprecated(
        "",
        level = DeprecationLevel.WARNING,
        replaceWith = ReplaceWith("raiseAnalyticsEvent with Source enum instead")
    )
    override fun raiseAnalyticsEvent(
        name: String,
        source: String,
        eventProperties: Map<String, Any>?,
        frequency: AnalyticsFrequency,
        sendAirshipEvent: Boolean,
        sendToPlotline: Boolean
    ) {
        val event = AnalyticsEvent(
            eventName = name,
            eventProperties = eventProperties ?: mapOf(),
            frequency = frequency
        )
        event.addProperty(AnalyticsEventConstants.ATTRIBUTE_SOURCE, source)
        analyticsManager.reportEvent(event)
    //        if (sendToPlotline) {
//            sendAnalyticsEventToPlotline(name, source, eventProperties)
//        }
    }

    override fun addToPeopleProperties(properties: Map<String, String>) {
        analyticsManager.setUserProperties(properties)
    }

    override fun addToSuperProperties(properties: Map<String, String>) {
        analyticsManager.setSuperProperties(properties)
    }

    override fun setupAnalytics() {
        scope.launch {
            val properties = mutableMapOf<String, String>()
            properties["appVersionCode"] = appBuildConfig.versionCode.toString()

//            properties["favCount"] = favoriteStore.favoriteCount.toString()
//            lProperties["time zone"] = TimeZone.getDefault().id.toString()
            analyticsManager.setSuperProperties(properties)
            analyticsManager.setUserProperties(properties)
        }
    }
}
