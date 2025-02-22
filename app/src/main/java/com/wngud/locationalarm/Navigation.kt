package com.wngud.locationalarm

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.wngud.locationalarm.admob.NativeAd
import com.wngud.locationalarm.screen.alarm.AlarmScreen
import com.wngud.locationalarm.screen.alarm.AlarmViewModel
import com.wngud.locationalarm.screen.alarm.DetailAlarmScreen
import com.wngud.locationalarm.screen.home.HomeScreen
import com.wngud.locationalarm.screen.setting.SettingScreen
import com.wngud.locationalarm.screen.setting.SettingViewModel
import kotlin.system.exitProcess

@Composable
fun Navigation(
    navController: NavHostController,
    alarmViewModel: AlarmViewModel,
    settingViewModel: SettingViewModel,
    paddingValues: Dp
) {
    val shouldExitApp = remember { mutableStateOf(false) }
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        loadNativeAd(context, "ca-app-pub-3940256099942544/2247696110") {
            nativeAd = it
        }
    }

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
                alarmViewModel = alarmViewModel,
                paddingValues = paddingValues
            )
        }
        composable(Screen.SettingScreen.route) {
            SettingScreen(
                navController = navController,
                settingViewModel = settingViewModel,
                onBackPressed = {
                    navController.popBackStack(Screen.SettingScreen.route, false)
                    shouldExitApp.value = true
                },
                paddingValues = paddingValues
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
                id = id,
                paddingValues = paddingValues
            )
        }
    }

    if (shouldExitApp.value) {
        ExitDialog(nativeAd) { shouldExitApp.value = false }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ExitDialog(
    nativeAd: NativeAd?,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                nativeAd?.let { ad ->
                    NativeAd(ad)
                }
                Text(
                    text = "종료하시겠습니까?"
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(16.dp),
                        onClick = {
                            onDismissRequest()
                        }
                    ) {
                        Text("취소")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        onClick = {
                            onDismissRequest()
                            exitProcess(0)
                        }
                    ) {
                        Text("종료")
                    }
                }
            }
        }
    }
}

fun loadNativeAd(context: Context, adUnitId: String, nativeAd: (NativeAd) -> Unit) {
    val adLoader = AdLoader.Builder(context, adUnitId)
        .forNativeAd { ad -> nativeAd(ad) }
        .withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.e("AdMob", "Failed to load native ad: ${error.message}")
            }

            override fun onAdClicked() {
                Log.e("AdMob", "설치 클릭")
            }
        })
        .build()

    adLoader.loadAd(AdRequest.Builder().build())
}