package com.gometro.kmpapp

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import coil3.compose.AsyncImage
import com.gometro.base.providers.toast.KotlinToastData
import com.gometro.base.providers.toast.KotlinToastManager
import com.gometro.homescreen.presentation.HomeScreenRoot
import com.gometro.kmpapp.screens.detail.DetailScreen
import com.gometro.kmpapp.screens.list.ListScreen
import com.gometro.scenes.GometroScenes
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject


@Composable
fun App() {
    val kotlinToastManager = koinInject<KotlinToastManager>()

    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Surface {


            SetupToast(
                stream = kotlinToastManager.msgStream
            )

            val navController: NavHostController = rememberNavController()
            NavHost(navController = navController, startDestination = NavDestination.HomeScreen) {
                composable<NavDestination.ListDestination> {
                    ListScreen(navigateToDetails = { objectId ->
                        navController.navigate(NavDestination.DetailDestination(objectId))
                    })
                }
                composable<NavDestination.DetailDestination> { backStackEntry ->
                    DetailScreen(
                        objectId = backStackEntry.toRoute<NavDestination.DetailDestination>().objectId,
                        navigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable<NavDestination.HomeScreen> { backStackEntry ->
                    HomeScreenRoot()
                }

            }
        }
    }
}

@Composable
private fun SetupToast(
    stream: SharedFlow<KotlinToastData>,
) {

}



sealed class NavDestination {

    @Serializable
    object ListDestination

    @Serializable
    data class DetailDestination(val objectId: Int)

    @Serializable
    object HomeScreen

}

//private fun RouteBuilder.scene(
//    scene: GometroScenes,
//    deepLinks: List<String> = emptyList(),
//    navTransition: NavTransition? = null,
//    swipeProperties: SwipeProperties? = null,
//    content: @Composable (BackStackEntry) -> Unit,
//) {
//    scene(
//        route = scene.baseRoute,
//        deepLinks = deepLinks,
//        navTransition = navTransition,
//        swipeProperties = swipeProperties,
//        content = content
//    )
//}
