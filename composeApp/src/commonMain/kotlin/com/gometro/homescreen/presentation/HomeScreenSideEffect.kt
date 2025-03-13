package com.gometro.homescreen.presentation

sealed class HomeScreenSideEffect {
    data object ProceedToCheckout: HomeScreenSideEffect()
}