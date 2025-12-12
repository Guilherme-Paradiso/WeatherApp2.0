package com.weathersecondapp.ui.theme.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.weathersecondapp.model.MainViewModel
import com.weathersecondapp.ui.theme.HomePage
import com.weathersecondapp.ui.theme.ListPage
import com.weathersecondapp.ui.theme.MapPage

@Composable
fun MainNavHost(navController: NavHostController,
                viewModel: MainViewModel
) {
    NavHost(navController, startDestination = Route.Home) {
        composable<Route.Home> { HomePage(viewModel = viewModel) }
        composable<Route.List> { ListPage( viewModel = viewModel) }
        composable<Route.Map> { MapPage( viewModel = viewModel) }
    }
}