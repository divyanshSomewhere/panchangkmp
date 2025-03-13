package com.gometro.scenes.args

import com.gometro.scenes.GometroScenes
import kotlinx.serialization.Serializable

@Serializable
data object SplashArgs : SceneArgs() {
    override fun resolveScene() = GometroScenes.SplashScreen
}