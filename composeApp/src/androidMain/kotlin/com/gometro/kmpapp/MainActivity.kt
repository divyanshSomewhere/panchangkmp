package com.gometro.kmpapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import com.gometro.login.phonenumberhint.PhoneNumberHintHandlerSetter
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {


    private val phoneNumberHintHandlerSetter by inject<PhoneNumberHintHandlerSetter>()
//    private val permissionHandlerSetter by inject<PermissionHandlerSetter>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Remove when https://issuetracker.google.com/issues/364713509 is fixed
            LaunchedEffect(isSystemInDarkTheme()) {
                enableEdgeToEdge()
            }
            App()
        }
    }
}
