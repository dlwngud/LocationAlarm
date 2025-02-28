package com.wngud.locationalarm

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.wngud.locationalarm.screen.BottomNavigationBar
import com.wngud.locationalarm.screen.alarm.AlarmViewModel
import com.wngud.locationalarm.screen.setting.SettingViewModel

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val alarmViewModel = hiltViewModel<AlarmViewModel>()
    val settingViewModel = hiltViewModel<SettingViewModel>()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(bottom = it.calculateBottomPadding())) {
            Navigation(
                navController = navController,
                alarmViewModel = alarmViewModel,
                settingViewModel = settingViewModel,
                paddingValues = it.calculateBottomPadding()
            )
        }
    }
}