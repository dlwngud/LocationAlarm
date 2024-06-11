package com.wngud.locationalarm

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wngud.locationalarm.screen.AlarmScreen
import com.wngud.locationalarm.screen.HomeScreen
import com.wngud.locationalarm.screen.SettingScreen

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
        composable(Screen.HomeScreen.route) {
            HomeScreen()
        }
        composable(Screen.AlarmScreen.route) {
            AlarmScreen()
        }
        composable(Screen.SettingScreen.route) {
            SettingScreen()
        }
    }
}