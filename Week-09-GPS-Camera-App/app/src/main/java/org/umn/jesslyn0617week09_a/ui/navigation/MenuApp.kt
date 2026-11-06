package org.umn.jesslyn0617week09_a.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.umn.jesslyn0617week09_a.data.AppDestination
import org.umn.jesslyn0617week09_a.data.Routes
import org.umn.jesslyn0617week09_a.ui.screens.CameraScreen
import org.umn.jesslyn0617week09_a.ui.screens.GPSScreen
import org.umn.jesslyn0617week09_a.ui.screens.HomeScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuApp(){
    val navController = rememberNavController()
    val destinations = listOf(
        AppDestination(Routes.HOME, "Home", Icons.Default.Home),
        AppDestination(Routes.GPS, "Location", Icons.Default.LocationOn),
        AppDestination(Routes.CAMERA, "Camera", Icons.Default.CameraAlt)
    )
    val backStackEntry by
    navController.currentBackStackEntryAsState()

    val currentRoute = backStackEntry?.destination?.route
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Jesslyn 0617 - GPS & Camera")
                }
            )
        },
        bottomBar = {
            NavigationBar {
                destinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(
                                    navController.graph
                                        .findStartDestination().id
                                ){
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label
                            )
                        },
                        label = {
                            Text(destination.label)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onOpenGps = {
                        navController.navigate(Routes.GPS)
                    },
                    onOpenCamera = {
                        navController.navigate(Routes.CAMERA)
                    }
                )
            }
            composable(Routes.GPS) {
                GPSScreen()
            }
            composable(Routes.CAMERA){
                CameraScreen()
            }
        }
    }
}