package com.unitip.mobile.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.unitip.mobile.features.auth.presentation.AuthScreen
import com.unitip.mobile.features.home.presentation.HomeScreen
import com.unitip.mobile.features.job.presentation.CreateJobScreen

@Composable
fun RootNavigationGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.CreateJob,
    ) {
        composable<Routes.Auth> { AuthScreen() }
        composable<Routes.Home> { HomeScreen() }
        composable<Routes.CreateJob> { CreateJobScreen() }
    }
}