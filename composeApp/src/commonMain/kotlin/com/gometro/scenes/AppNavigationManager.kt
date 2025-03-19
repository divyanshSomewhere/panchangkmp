package com.gometro.scenes

import co.touchlab.skie.configuration.annotations.FlowInterop
import co.touchlab.skie.configuration.annotations.SealedInterop
import com.gometro.base.featurecontracts.CoroutineContextProvider
import com.gometro.scenes.args.SceneArgs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface AppNavigationManager {

    @FlowInterop.Enabled
    val navStream: SharedFlow<AppNavigationRequest>

    fun postNavigationRequest(navRequest: AppNavigationRequest)

}

class AppNavigationManagerImpl(
    coroutineContextProvider: CoroutineContextProvider
) : AppNavigationManager {

    private val _navStream = MutableSharedFlow<AppNavigationRequest>()
    @FlowInterop.Enabled
    override val navStream: SharedFlow<AppNavigationRequest> = _navStream.asSharedFlow()

    private val mutex = Mutex()
    private val tagScreensMap = mutableMapOf<String, AppScenes>()

    private val scope = CoroutineScope(coroutineContextProvider.main)

    override fun postNavigationRequest(navRequest: AppNavigationRequest) {
        scope.launch {
            _navStream.emit(updateNavigationRequest(navRequest))
        }
    }

    private suspend fun updateNavigationRequest(request: AppNavigationRequest): AppNavigationRequest {
        return when(request) {
            is AppNavigationRequest.Navigate -> {
                request.startDestinationTag?.let { addTagAndScene(it, request.args.resolveScene()) }
                request.copy(
                    navOptions = request.navOptions?.copy(
                        popUpToConfig = request.navOptions.popUpToConfig.getUpdatedConfig()
                    )
                )
            }
            is AppNavigationRequest.GoBack -> {
                request.copy(
                    popUpToConfig = request.popUpToConfig?.getUpdatedConfig()
                )
            }
        }
    }

    private suspend fun addTagAndScene(tag: String, scene: AppScenes) {
        mutex.withLock { tagScreensMap[tag] = scene }
    }

    private suspend fun getSceneFromTag(tag: String): AppScenes? {
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
sealed class AppNavigationRequest {
    data class Navigate(
        val args: SceneArgs,
        val navOptions: AppNavOptions? = null,
        val startDestinationTag: String? = null
    ) : AppNavigationRequest()

    data class GoBack(
        val popUpToConfig: PopUpToConfig? = null
    ) : AppNavigationRequest()
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
data class AppNavOptions(
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
        val scene: AppScenes,
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
