package com.gometro.scenes

import co.touchlab.skie.configuration.annotations.FlowInterop
import co.touchlab.skie.configuration.annotations.SealedInterop
import com.gometro.base.providers.CoroutineContextProvider
import com.gometro.scenes.args.SceneArgs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface GometroNavigationManager {

    @FlowInterop.Enabled
    val navStream: SharedFlow<GometroNavigationRequest>

    fun postNavigationRequest(navRequest: GometroNavigationRequest)

}

class GometroNavigationManagerImpl(
    coroutineContextProvider: CoroutineContextProvider
) : GometroNavigationManager {

    private val _navStream = MutableSharedFlow<GometroNavigationRequest>()
    @FlowInterop.Enabled
    override val navStream: SharedFlow<GometroNavigationRequest> = _navStream.asSharedFlow()

    private val mutex = Mutex()
    private val tagScreensMap = mutableMapOf<String, GometroScenes>()

    private val scope = CoroutineScope(coroutineContextProvider.main)

    override fun postNavigationRequest(navRequest: GometroNavigationRequest) {
        scope.launch {
            _navStream.emit(updateNavigationRequest(navRequest))
        }
    }

    private suspend fun updateNavigationRequest(request: GometroNavigationRequest): GometroNavigationRequest {
        return when(request) {
            is GometroNavigationRequest.Navigate -> {
                request.startDestinationTag?.let { addTagAndScene(it, request.args.resolveScene()) }
                request.copy(
                    navOptions = request.navOptions?.copy(
                        popUpToConfig = request.navOptions.popUpToConfig.getUpdatedConfig()
                    )
                )
            }
            is GometroNavigationRequest.GoBack -> {
                request.copy(
                    popUpToConfig = request.popUpToConfig?.getUpdatedConfig()
                )
            }
        }
    }

    private suspend fun addTagAndScene(tag: String, scene: GometroScenes) {
        mutex.withLock { tagScreensMap[tag] = scene }
    }

    private suspend fun getSceneFromTag(tag: String): GometroScenes? {
        return mutex.withLock { tagScreensMap[tag] }
    }

    private suspend fun PopUpToConfig.getUpdatedConfig(): PopUpToConfig {
        return when(this) {
            is PopUpToConfig.ClearAll,
            PopUpToConfig.None,
            PopUpToConfig.Prev,
            is PopUpToConfig.Scene -> this
            is PopUpToConfig.Tag -> {
                val scene = getSceneFromTag(this.tag)
                return if (scene != null) {
                    PopUpToConfig.Scene(
                        scene = scene,
                        inclusive = true
                    )
                } else {
                    PopUpToConfig.None
                }
            }
        }
    }
}

@SealedInterop.Enabled
sealed class GometroNavigationRequest {
    data class Navigate(
        val args: SceneArgs,
        val navOptions: GometroNavOptions? = null,
        val startDestinationTag: String? = null
    ) : GometroNavigationRequest()

    data class GoBack(
        val popUpToConfig: PopUpToConfig? = null
    ) : GometroNavigationRequest()
}

/**
 * @param launchSingleTop there will be at most one copy of a given destination on the top of the
 * back stack regardless of args. Eg. when backstack contains "/home" on top and we post navigation for
 * "/home?args=something", setting this flag true will remove /home from backstack and add new scene in backstack
 * @param includePath overrides the default [launchSingleTop] behavior allowing
 * single-top launch of destinations with variable path parameters.
 * This override has no effect when [launchSingleTop] is false, and it is disabled by default.
 * Eg. when backstack contains "/home" on top and we post navigation for "/home?args=something",
 * setting this flag true will not remove /home from backstack because the existing path and new path
 * are not exactly same and hence it will add "/home?args=something" on top of "/home"
 */
data class GometroNavOptions(
    val launchSingleTop: Boolean = false,
    val includePath: Boolean = false,
    val popUpToConfig: PopUpToConfig = PopUpToConfig.None
)

@SealedInterop.Enabled
sealed class PopUpToConfig {
    open val inclusive: Boolean get() = false

    data object None : PopUpToConfig()

    data object Prev : PopUpToConfig() {
        override val inclusive: Boolean get() = true
    }

    data class Scene(
        val scene: GometroScenes,
        override val inclusive: Boolean
    ) : PopUpToConfig()

    data class ClearAll(
        override val inclusive: Boolean = true
    ) : PopUpToConfig()

    data class Tag(
        val tag: String,
        override val inclusive: Boolean = true
    ) : PopUpToConfig()
}

/*
fun GometroNavOptions.toNavOptions(): NavOptions {
    return NavOptions(
        launchSingleTop = launchSingleTop,
        includePath = includePath,
        popUpTo = popUpToConfig.toPopUpTo()
    )
}

fun PopUpToConfig.toPopUpTo(): PopUpTo {
    return when(this) {
        PopUpToConfig.None -> PopUpTo.None
        PopUpToConfig.Prev -> PopUpTo.Prev
        is PopUpToConfig.Scene -> {
            PopUpTo.Route(
                route = this.scene.baseRoute,
                inclusive = this.inclusive
            )
        }
        is PopUpToConfig.ClearAll -> PopUpTo.First(inclusive = this.inclusive)
        is PopUpToConfig.Tag -> PopUpTo.None // this won't come
    }
}

 */
