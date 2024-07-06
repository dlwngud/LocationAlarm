package com.wngud.locationalarm

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.wngud.locationalarm.screen.alarm.AlarmScreen
import com.wngud.locationalarm.screen.alarm.AlarmViewModel
import com.wngud.locationalarm.screen.alarm.DetailAlarmScreen
import com.wngud.locationalarm.screen.home.HomeScreen
import com.wngud.locationalarm.screen.setting.SettingScreen
import kotlin.system.exitProcess

@Composable
fun Navigation(
    navController: NavHostController,
    alarmViewModel: AlarmViewModel
) {
    val shouldExitApp = remember { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
        composable(Screen.HomeScreen.route) {
            HomeScreen(
                onBackPressed = {
                    shouldExitApp.value = true
                },
                alarmViewModel = alarmViewModel
            )
        }
        composable(Screen.AlarmScreen.route) {
            AlarmScreen(
                navController = navController,
                onBackPressed = {
                    navController.popBackStack(Screen.AlarmScreen.route, false)
                    shouldExitApp.value = true
                },
                alarmViewModel = alarmViewModel
            )
        }
        composable(Screen.SettingScreen.route) {
            SettingScreen(
                navController = navController,
                onBackPressed = {
                    navController.popBackStack(Screen.SettingScreen.route, false)
                    shouldExitApp.value = true
                }
            )
        }
        composable(
            Screen.DetailAlarmScreen.route,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                    defaultValue = -1L
                    nullable = false
                }
            )) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: -1L
            DetailAlarmScreen(
                navController = navController,
                alarmViewModel = alarmViewModel,
                id = id
            )
        }
    }

    if (shouldExitApp.value) {
        AlertDialog(
            onDismissRequest = { shouldExitApp.value = false },
            title = { Text("앱을 종료하시겠습니까?") },
            confirmButton = {
                Button(
                    onClick = {
                        shouldExitApp.value = false
                        exitProcess(0)
                    }
                ) {
                    Text("종료")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        shouldExitApp.value = false
                    }
                ) {
                    Text("취소")
                }
            }
        )
    }
}