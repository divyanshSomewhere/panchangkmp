package com.gometro.scenes.args

import com.gometro.scenes.AppScenes
import kotlinx.serialization.Serializable

/**
 * @param destinationTabTypeString `destinationTabType` from `DestinationTabType` enum
 */
@Serializable
data class HomeArgs(
    val destinationTabTypeString: String? = null
) : SceneArgs() {
    override fun resolveScene() = AppScenes.Home
}