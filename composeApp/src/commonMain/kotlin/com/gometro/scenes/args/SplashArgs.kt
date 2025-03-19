package com.gometro.scenes.args

import com.gometro.scenes.AppScenes
import kotlinx.serialization.Serializable

@Serializable
data object SplashArgs : SceneArgs() {
    override fun resolveScene() = AppScenes.SplashScreen
}