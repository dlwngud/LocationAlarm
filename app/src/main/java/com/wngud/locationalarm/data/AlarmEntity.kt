package com.wngud.locationalarm.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_table")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val latitude: Double, // 위도
    val longitude: Double, // 경도
    val radius: Int, // 범위 (미터)
    val title: String, // 제목
    val content: String, // 내용
    val isChecked: Boolean // 활성화 여부
)
