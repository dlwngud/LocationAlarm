package com.wngud.locationalarm.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Upsert
    fun upsertAlarm(alarmEntity: AlarmEntity)

    @Query("SELECT * FROM alarm_table")
    fun getAllAlarm(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarm_table where id=:id")
    fun getAlarmById(id: Long): Flow<AlarmEntity>

    @Delete
    fun deleteAlarm(alarmEntity: AlarmEntity)
}