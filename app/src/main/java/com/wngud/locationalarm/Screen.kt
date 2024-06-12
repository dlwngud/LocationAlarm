package com.wngud.locationalarm

import androidx.annotation.DrawableRes

sealed class Screen(
    val route: String,
    val name: String,
    @DrawableRes val icon: Int
) {
    object HomeScreen : Screen("homeScreen", "홈", R.drawable.baseline_home_24)
    object AlarmScreen : Screen("alarmScreen", "알람", R.drawable.baseline_alarm_on_24)
    object SettingScreen : Screen("settingScreen", "설정", R.drawable.baseline_settings_24)
    object DetailAlarmScreen : Screen("detailAlarmScreen", "", -1)
}