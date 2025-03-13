package com.gometro.scenes.args

import com.gometro.constants.Source
import com.gometro.scenes.GometroScenes
import kotlinx.serialization.Serializable

@Serializable
data class LoginOptionsArgs(
    val source: Source
) : SceneArgs() {
    override fun resolveScene() = GometroScenes.LoginOptions
}

@Serializable
data class LoginOtpSceneArgs(
    val refNo: String,
    val phoneNumber: String,
    val countryCallingCode: String,
    val source: Source
) : SceneArgs() {
    override fun resolveScene() = GometroScenes.LoginOtp
}