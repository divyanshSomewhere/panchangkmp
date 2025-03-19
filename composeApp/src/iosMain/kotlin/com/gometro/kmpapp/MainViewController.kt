package com.gometro.kmpapp

import androidx.compose.ui.window.ComposeUIViewController
import com.gometro.core.di.KoinHelper
import com.gometro.core.init.ApplicationInitManager


class abs {
    private val applicationInitManager: ApplicationInitManager by inject()

}

fun MainViewController() = ComposeUIViewController(
configure = {
    KoinHelper.initKoin(
        platformDependencyFactory = IosDependencyFactory()
    )
    ApplicationInitManager(

    ).init()
}
) { App() }