package com.wngud.locationalarm.domain

import com.wngud.locationalarm.screen.setting.SettingState
import kotlinx.coroutines.flow.Flow

interface SettingRepository {

    suspend fun updateSetting(settingState: SettingState)

    fun getSetting(): Flow<SettingState>
}