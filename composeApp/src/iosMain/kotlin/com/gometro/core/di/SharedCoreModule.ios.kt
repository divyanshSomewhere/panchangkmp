package com.gometro.core.di

import com.gometro.base.providers.toast.KotlinToastManager
import com.gometro.base.providers.toast.KotlinToastManagerImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


internal actual fun platformModule() = module {

    singleOf(::KotlinToastManagerImpl) { bind<KotlinToastManager>() }


}