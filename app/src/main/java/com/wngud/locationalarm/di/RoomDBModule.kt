package com.wngud.locationalarm.di

import android.content.Context
import androidx.room.Room
import com.wngud.locationalarm.data.db.AlarmDao
import com.wngud.locationalarm.data.db.AlarmDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomDBModule {

    @Provides
    @Singleton
    fun provideAlarmDatabase(@ApplicationContext context: Context): AlarmDatabase =
        Room.databaseBuilder(context, AlarmDatabase::class.java, "alarm_table").build()

    @Provides
    @Singleton
    fun provideAlarmDao(alarmDatabase: AlarmDatabase): AlarmDao = alarmDatabase.alarmDao()
}