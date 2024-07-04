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

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val alarmViewModel = hiltViewModel<AlarmViewModel>()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            Navigation(
                navController = navController,
                alarmViewModel = alarmViewModel
            )
        }
    }
}