package com.gometro.scenes

import co.touchlab.skie.configuration.annotations.SealedInterop
import com.gometro.base.utils.CustomJsonParser
import com.gometro.scenes.args.SceneArgs
import kotlinx.serialization.encodeToString

@SealedInterop.Enabled
sealed class AppScenes(val baseRoute: String) {
    data object SplashScreen : AppScenes("/splashScreen")
    data object Home : AppScenes("/home")
    data object LoginOptions : AppScenes("/loginOptions")
    data object LoginOtp : AppScenes("/loginOtp")
    companion object {
        const val ARGS = "args"
    }
}

fun SceneArgs.resolveCompletePath(): String {
    val scene = this.resolveScene()
    val argsJson = GometroNavigationUtils.getJson(this)
    return "${scene.baseRoute}?${AppScenes.ARGS}=$argsJson"
}

object GometroNavigationUtils {

    val specialCharacters = listOf(
        ("?" to "QSymbol"),
        ("&" to "AmpSymbol"),
        ("=" to "EqSymbol")
    )

    fun getJson(arg: SceneArgs): String {
        var argsJson = CustomJsonParser.Json.encodeToString(arg)
        specialCharacters.forEach {
            argsJson = argsJson.replace(it.first, it.second)
        }
        return argsJson
    }

//    inline fun <reified T: SceneArgs> BackStackEntry.getQueryArgsOrNullIfInvalid(): T? {
//        var argsString = this.query<String>(GometroScenes.ARGS) ?: return null
//        specialCharacters.forEach {
//            argsString = argsString.replace(it.second, it.first)
//        }
//        return ChaloJson.Json.decodeFromStringSafely<T>(argsString)
//    }

}