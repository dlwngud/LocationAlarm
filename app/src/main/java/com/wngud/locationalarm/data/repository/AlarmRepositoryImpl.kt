package com.wngud.locationalarm.data.repository

import android.util.Log
import com.wngud.locationalarm.data.db.local.AlarmDao
import com.wngud.locationalarm.domain.Alarm
import com.wngud.locationalarm.domain.repository.AlarmRepository
import com.wngud.locationalarm.domain.toAlarm
import com.wngud.locationalarm.domain.toAlarmEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao
) : AlarmRepository {

    override fun loadAlarms(): Flow<List<Alarm>> = alarmDao.getAllAlarm().map { it.map { it.toAlarm() } }


    override suspend fun addAlarm(alarm: Alarm) {
        val alarmEntity = alarm.toAlarmEntity()
        Log.i("alarmEntity", alarmEntity.toString())
        alarmDao.addAlarm(alarmEntity)
        Log.i("성공", alarmEntity.toString())
    }

    override fun getAlarmById(id: Long): Flow<Alarm> = alarmDao.getAlarmById(id).map { it.toAlarm() }

    override suspend fun getAlarmByLatLng(latitude: Double, longitude: Double): Flow<Alarm> {
        return alarmDao.getAlarmByLatLng(latitude, longitude).map { it.toAlarm() }
    }

    override suspend fun updateAlarm(alarm: Alarm) {
        val alarmEntity = alarm.toAlarmEntity()
        alarmDao.updateAlarm(alarmEntity)
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        val alarmEntity = alarm.toAlarmEntity()
        alarmDao.deleteAlarm(alarmEntity)
    }
}