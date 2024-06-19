package com.wngud.locationalarm.screen.util

import android.content.Context

interface PermissionDescriptionProvider {

    fun getTitle(context: Context): String

    fun getDescription(context: Context): String
}

class NotificationPerMissionDescriptionProvider : PermissionDescriptionProvider {
    override fun getTitle(context: Context): String {
        return "[필수] 알림 설정"
    }

    override fun getDescription(context: Context): String {
        return "알람 설정 시 알림을 제공하기 위해 필요합니다.\n${"[설정] -> [알림]에서 권한을 설정해주세요"}"
    }
}

class LocationPerMissionDescriptionProvider : PermissionDescriptionProvider {
    override fun getTitle(context: Context): String {
        return "[필수] 위치 정보"
    }

    override fun getDescription(context: Context): String {
        return "설정된 위치 범위 내에 진입할 때 알림을 제공하기 위해 필요합니다.\n${"[설정] -> [권한] -> [위치]에서 (항상 허용)으로 설정해주세요."}"
    }
}

class AudioPerMissionDescriptionProvider : PermissionDescriptionProvider {
    override fun getTitle(context: Context): String {
        return "[필수] 음악 및 오디오 설정"
    }

    override fun getDescription(context: Context): String {
        return "알람 설정 시 사용자가 선택한 벨소리를 재생하기 위해 필요합니다.\n${"[설정] -> [권한] -> [음악 및 오디오]에서 권한을 설정해주세요"}"
    }
}