package com.wngud.locationalarm.domain

import kotlinx.coroutines.flow.Flow

interface AlarmRepository {

    fun loadAlarms(): Flow<List<Alarm>>

    suspend fun addAlarm(alarm: Alarm)

    fun getAlarmById(id: Long): Flow<Alarm>

    suspend fun updateAlarm(alarm: Alarm)

    suspend fun deleteAlarm(alarm: Alarm)
}