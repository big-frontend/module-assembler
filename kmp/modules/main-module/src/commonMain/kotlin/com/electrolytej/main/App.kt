package com.electrolytej.main

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.electrolytej.main.page.splash.SplashScreen
import com.electrolytej.main.page.welcome.WelcomeScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import com.electrolytej.main.page.home.HomeScreen

@Composable
@Preview
fun App(
    viewModel: AppViewModel = viewModel { AppViewModel() },
) {
    val controller = rememberNavController()
    NavHost(
        navController = controller,
        startDestination = "splash",
        enterTransition = {
            EnterTransition.None
        },
        exitTransition = {
            ExitTransition.None
        },
    ) {
        composable("splash") {
            SplashScreen(controller)
        }
        composable("home") {
            HomeScreen(controller)
        }
        composable("welcome") {
            WelcomeScreen(controller)
        }
    }
}

class AppViewModel : ViewModel() {

}