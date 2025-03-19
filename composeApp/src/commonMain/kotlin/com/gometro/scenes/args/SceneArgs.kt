package com.gometro.scenes.args

import co.touchlab.skie.configuration.annotations.SealedInterop
import com.gometro.scenes.AppScenes
import kotlinx.serialization.Serializable

@SealedInterop.Enabled
@Serializable
sealed class SceneArgs {
    abstract fun resolveScene(): AppScenes
}