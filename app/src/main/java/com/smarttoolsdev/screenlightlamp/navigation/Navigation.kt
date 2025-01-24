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
            )
        }




    }
}