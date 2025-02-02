package com.smarttoolsdev.screenlightlamp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.smarttoolsdev.screenlightlamp.data.AppViewModel
import com.smarttoolsdev.screenlightlamp.data.ScenesViewModel
import com.smarttoolsdev.screenlightlamp.data.TimerViewModel
import com.smarttoolsdev.screenlightlamp.ui.components.BottomNav
import com.smarttoolsdev.screenlightlamp.ui.components.TimerOverlay
import com.smarttoolsdev.screenlightlamp.ui.screens.HomeScreen
import com.smarttoolsdev.screenlightlamp.ui.screens.PresetsScreen
import com.smarttoolsdev.screenlightlamp.ui.screens.TimerScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: AppViewModel,
    timerViewModel: TimerViewModel,
    scenesViewModel: ScenesViewModel
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val timerState by timerViewModel.timerState.collectAsState()

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        bottomBar = {
            BottomNav(
                currentRoute = currentRoute,
                onNavigate = { navController.navigate(it.route) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(
                navController = navController,
                startDestination = NavItem.Home.route
            ) {
                composable(NavItem.Home.route) {
                    HomeScreen(viewModel = viewModel)
                }
                composable(NavItem.Timer.route) {
                    TimerScreen(
                        viewModel = timerViewModel,
                        onSelectPreset = { timerViewModel.startTimer(it) },
                        onNavigateBack = {
                            navController.navigate(NavItem.Home.route) {
                                popUpTo(NavItem.Timer.route) { inclusive = true }
                            }
                        }
                    )
                }
                composable(NavItem.Presets.route) {
                    PresetsScreen(
                        viewModel = scenesViewModel,
                        onNavigateToHome = {
                            navController.navigate(NavItem.Home.route) {
                                // Pop up to the home destination to avoid building up a large back stack
                                popUpTo(NavItem.Home.route) { inclusive = true }
                            }
                        }
                    )
                }
                composable(NavItem.Settings.route) {
                    // Implement later
                }
            }

            TimerOverlay(
                state = timerState,
                onCancel = { timerViewModel.cancelTimer() }
            )
        }
    }
}