package com.wngud.locationalarm.di

import com.wngud.locationalarm.data.repository.AlarmRepositoryImpl
import com.wngud.locationalarm.data.repository.SettingRepositoryImpl
import com.wngud.locationalarm.domain.AlarmRepository
import com.wngud.locationalarm.domain.SettingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAlarmRepository(alarmRepositoryImpl: AlarmRepositoryImpl): AlarmRepository

    @Binds
    @Singleton
    abstract fun bindSettingRepository(settingRepositoryImpl: SettingRepositoryImpl): SettingRepository
}