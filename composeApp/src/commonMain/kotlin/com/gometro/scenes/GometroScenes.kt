package com.gometro.scenes

import co.touchlab.skie.configuration.annotations.SealedInterop
import com.gometro.base.utils.CustomJsonParser
import com.gometro.scenes.args.SceneArgs
import kotlinx.serialization.encodeToString

@SealedInterop.Enabled
sealed class GometroScenes(val baseRoute: String) {
    data object SplashScreen : GometroScenes("/splashScreen")
    data object Home : GometroScenes("/home")
    data object LoginOptions : GometroScenes("/loginOptions")
    data object LoginOtp : GometroScenes("/loginOtp")
    companion object {
        const val ARGS = "args"
    }
}

fun SceneArgs.resolveCompletePath(): String {
    val scene = this.resolveScene()
    val argsJson = GometroNavigationUtils.getJson(this)
    return "${scene.baseRoute}?${GometroScenes.ARGS}=$argsJson"
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