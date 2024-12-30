package com.wngud.locationalarm.data.db.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addAlarm(alarmEntity: AlarmEntity)

    @Query("SELECT * FROM alarm_table")
    fun getAllAlarm(): Flow<List<AlarmEntity>>

    @Query("SELECT * FROM alarm_table where id=:id")
    fun getAlarmById(id: Long): Flow<AlarmEntity>

    @Query("SELECT * FROM alarm_table WHERE latitude = :latitude AND longitude = :longitude")
    fun getAlarmByLatLng(latitude: Double, longitude: Double): Flow<AlarmEntity>

    @Update
    suspend fun updateAlarm(alarmEntity: AlarmEntity)

    @Delete
    suspend fun deleteAlarm(alarmEntity: AlarmEntity)
}