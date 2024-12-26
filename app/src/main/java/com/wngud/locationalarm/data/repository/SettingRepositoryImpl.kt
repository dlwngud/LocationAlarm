package com.wngud.locationalarm.data.repository

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.wngud.locationalarm.domain.repository.SettingRepository
import com.wngud.locationalarm.screen.setting.SettingState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @ApplicationContext private val context: Context
) : SettingRepository {
    companion object {
        val ALARM_VIBRATION = booleanPreferencesKey("alarm_vibration")
        val ALARM_RINGTONE_URI = stringPreferencesKey("alarm_ringtone_uri")
        val ALARM_VOLUME = floatPreferencesKey("alarm_volume")
        private val ALARM_RINGTONE_NAME = stringPreferencesKey("alarm_ringtone_name")
    }

    override suspend fun updateSetting(settingState: SettingState) {
        dataStore.edit { preference ->
            preference[ALARM_VIBRATION] = settingState.isVibration
            preference[ALARM_RINGTONE_URI] = settingState.ringtoneUri.toString()
            preference[ALARM_VOLUME] = settingState.volume
            preference[ALARM_RINGTONE_NAME] = settingState.ringtoneName
        }
    }

    override fun getSetting(): Flow<SettingState> = dataStore.data.map { preferences ->
        val vibration = preferences[ALARM_VIBRATION] ?: false
        val ringtoneUri = preferences[ALARM_RINGTONE_URI] ?: RingtoneManager.getDefaultUri(
            RingtoneManager.TYPE_RINGTONE
        ).toString()
        val volume = preferences[ALARM_VOLUME] ?: 0.0f
        val ringtoneName = RingtoneManager.getRingtone(context, Uri.parse(ringtoneUri)).getTitle(context)
        SettingState(isVibration = vibration, ringtoneUri = Uri.parse(ringtoneUri), volume = volume, ringtoneName = ringtoneName)
    }
}