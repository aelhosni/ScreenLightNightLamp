package com.smarttoolsdev.screenlightlamp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.smarttoolsdev.screenlightlamp.data.AppViewModel
import com.smarttoolsdev.screenlightlamp.ui.screens.*

sealed class Screen(val route: String) {
    object Tutorial : Screen("tutorial")
    object Home : Screen("home")
    object RelaxingSounds : Screen("relaxing_sounds")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModel: AppViewModel
) {
    val showTutorial by viewModel.showTutorial.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (showTutorial) Screen.Tutorial.route else Screen.Home.route
    ) {
        composable(Screen.Tutorial.route) {
            TutorialScreen(
                viewModel = viewModel,
                onComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Tutorial.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToRelaxingSounds = {
                    navController.navigate(Screen.RelaxingSounds.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.RelaxingSounds.route) {
            RelaxingSoundsScreen(
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.navigateUp() },
                onTutorialClick = {
                    navController.navigate(Screen.Tutorial.route) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onPrivacyPolicyClick = { /* Handle privacy policy click */ },
                onCreditsClick = { /* Handle credits click */ },
                onShareClick = { /* Handle share click */ },
                onRateClick = { /* Handle rate click */ }
            )
        }
    }
}