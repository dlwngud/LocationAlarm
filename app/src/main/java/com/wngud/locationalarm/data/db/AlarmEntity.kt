package com.wngud.locationalarm.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_table")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val latitude: Double, // 위도
    val longitude: Double, // 경도
    val radius: Double, // 범위 (미터)
    val title: String, // 제목
    val content: String, // 내용
    val isChecked: Boolean // 활성화 여부
)

object DummyAlarm {
    val alarmList = listOf(
        AlarmEntity(id = 0, latitude = 37.37077, longitude = 127.2477945, radius = 100.0, title = "제목1", content = "내용1", isChecked = false),
        AlarmEntity(id = 1, latitude = 37.3847851, longitude = 127.2308549, radius = 200.0, title = "제목2", content = "내용2", isChecked = true),
        AlarmEntity(id = 2, latitude = 37.3961906, longitude = 127.1114715, radius = 500.0, title = "제목3", content = "내용3", isChecked = true),
        AlarmEntity(id = 3, latitude = 37.5133196, longitude = 127.1001066, radius = 1000.0, title = "제목4", content = "내용4", isChecked = false),
    )
}