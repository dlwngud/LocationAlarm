package com.wngud.locationalarm

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wngud.locationalarm.screen.alarm.AlarmScreen
import com.wngud.locationalarm.screen.home.HomeScreen
import com.wngud.locationalarm.screen.setting.SettingScreen

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
        composable(Screen.HomeScreen.route) {
            HomeScreen()
        }
        composable(Screen.AlarmScreen.route) {
            AlarmScreen(navController)
        }
        composable(Screen.SettingScreen.route) {
            SettingScreen()
        }
    }
}