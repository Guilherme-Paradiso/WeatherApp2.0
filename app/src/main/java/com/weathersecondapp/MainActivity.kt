package com.weathersecondapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.weathersecondapp.ui.theme.CityDialog
import com.weathersecondapp.ui.theme.info.WeatherSecondAPPTheme
import com.weathersecondapp.ui.theme.nav.BottomNavBar
import com.weathersecondapp.ui.theme.nav.BottomNavItem
import com.weathersecondapp.ui.theme.nav.MainNavHost
import com.weathersecondapp.ui.theme.nav.Route
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.weathersecondapp.api.WeatherService
import com.weathersecondapp.db.fb.FBDatabase
import com.weathersecondapp.monitor.ForecastMonitor
import androidx.core.util.Consumer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weathersecondapp.db.fb.local.LocalDatabase
import com.weathersecondapp.repo.Repository

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val fbDB = remember { FBDatabase() }
            val weatherService = remember { WeatherService(this) }
            val monitor = ForecastMonitor(this)
            val userUid = Firebase.auth.currentUser?.uid ?: "default_user"
            val localDB = remember { LocalDatabase(this, userUid) }
            val repository = remember { Repository(fbDB, localDB) }
            val viewModel : MainViewModel = viewModel(
                factory = MainViewModelFactory(repository, monitor, weatherService)
            )
            val user = viewModel.user.collectAsStateWithLifecycle(null).value
            DisposableEffect(Unit) {
                val listener = Consumer<Intent> { intent ->
                    viewModel.city = intent.getStringExtra("city")
                    viewModel.page = Route.Home
                }
                addOnNewIntentListener(listener)
                onDispose { removeOnNewIntentListener(listener) }
            }
            val navController = rememberNavController()
            val currentRoute = navController.currentBackStackEntryAsState()
            val showButton = currentRoute.value?.destination?.hasRoute(Route.List::class) == true
            val launcher = rememberLauncherForActivityResult(contract =
                ActivityResultContracts.RequestPermission(), onResult = {} )
            var showDialog by remember { mutableStateOf(false) }
            WeatherSecondAPPTheme {
                if (showDialog) CityDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = { city ->
                        if (city.isNotBlank()) viewModel.addCity(name = city)
                        showDialog = false
                    })
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                val name = user?.name?:"[carregando...]"
                                Text("Bem-vindo/a! $name")
                            },
                                    actions = {
                                IconButton( onClick = {
                                    Firebase.auth.signOut()
                                } ) {
                                    Icon(
                                        imageVector =
                                            Icons.AutoMirrored.Filled.ExitToApp,
                                        contentDescription = "Localized description"
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        val items = listOf(
                            BottomNavItem.HomeButton,
                            BottomNavItem.ListButton,
                            BottomNavItem.MapButton,
                        )
                        BottomNavBar(viewModel, items)
                    },
                    floatingActionButton = {
                        if (showButton) {
                            FloatingActionButton(onClick = { showDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = "Adicionar")
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        launcher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        MainNavHost(navController = navController, viewModel)
                    }

                    LaunchedEffect(viewModel.page) {
                        navController.navigate(viewModel.page) {
                            // Volta pilha de navegação até HomePage (startDest).
                            navController.graph.startDestinationRoute?.let {
                                popUpTo(it) {
                                    saveState = true
                                }
                                restoreState = true
                            }
                            launchSingleTop = true
                        }
                    }

                }
            }
        }
    }
}

