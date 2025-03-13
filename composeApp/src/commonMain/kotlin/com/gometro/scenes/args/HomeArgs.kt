package com.gometro.scenes.args

import com.gometro.scenes.GometroScenes
import kotlinx.serialization.Serializable

/**
 * @param destinationTabTypeString `destinationTabType` from `DestinationTabType` enum
 */
@Serializable
data class HomeArgs(
    val destinationTabTypeString: String? = null
) : SceneArgs() {
    override fun resolveScene() = GometroScenes.Home
}