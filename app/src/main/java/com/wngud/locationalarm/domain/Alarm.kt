package com.wngud.locationalarm.domain

import com.wngud.locationalarm.data.db.AlarmEntity

data class Alarm(
    val id: Long = 0L,
    val latitude: Double = 0.0, // 위도
    val longitude: Double = 0.0, // 경도
    val radius: Double = 0.0, // 범위 (미터)
    val title: String = "", // 제목
    val content: String = "", // 내용
    val isChecked: Boolean = false // 활성화 여부
)

fun Alarm.toAlarmEntity(): AlarmEntity = AlarmEntity(
    id = this.id,
    latitude = this.latitude,
    longitude = this.longitude,
    radius = this.radius,
    title = this.title,
    content = this.content,
    isChecked = this.isChecked
)

fun AlarmEntity.toAlarm(): Alarm = Alarm(
    id = this.id,
    latitude = this.latitude,
    longitude = this.longitude,
    radius = this.radius,
    title = this.title,
    content = this.content,
    isChecked = this.isChecked
)