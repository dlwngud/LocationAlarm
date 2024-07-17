package com.wngud.locationalarm.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.wngud.locationalarm.domain.SettingRepository
import com.wngud.locationalarm.screen.setting.SettingState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
): SettingRepository {
    companion object{
        private val ALARM_VIBRATION = booleanPreferencesKey("alarm_vibration")
    }

    override suspend fun updateSetting(settingState: SettingState) {
        dataStore.edit { preference ->
            preference[ALARM_VIBRATION] = settingState.isVibration
        }
    }

    override fun getSetting(): Flow<SettingState> = dataStore.data.map { preferences ->
        val vibration = preferences[ALARM_VIBRATION] ?: false
        SettingState(isVibration = vibration)
    }
}