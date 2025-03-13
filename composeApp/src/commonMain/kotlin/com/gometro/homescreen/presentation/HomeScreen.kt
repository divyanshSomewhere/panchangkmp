package com.gometro.homescreen.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gometro.kmpapp.NavDestination
import com.gometro.kmpapp.screens.list.ListViewModel
import com.gometro.logger.GometroLogger
import org.koin.compose.getKoin
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreenRoot() {

    val viewModel = koinViewModel<HomeScreenViewModel>()

    val gometroLogger = koinInject<GometroLogger>()

    val state by viewModel.viewState.collectAsStateWithLifecycle()
    gometroLogger.debug("observing state = ", "state = ${state.testCopy}")
    HomeScreen(state)
    viewModel.onAction(HomeScreenAction.OnScreenOpened)

//
//    HomeScreen(
//        homeScreenUIState = state.,
//        onAction = viewModel::onAction,
//        modifier = modifier
//    )

}

@Composable
fun HomeScreen(
    homeScreenUIState: HomeScreenUIState,
//    onAction: (HomeScreenAction) -> Unit,
//    modifier: Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(Color.Yellow),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = homeScreenUIState.testCopy.asString(), color = Color.Blue, fontSize = 24.sp)
    }
}