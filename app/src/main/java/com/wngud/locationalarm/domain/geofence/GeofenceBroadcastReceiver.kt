package com.wngud.locationalarm.domain.geofence

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.wngud.locationalarm.Constants.GEOFENCE_CHANNEL_ID

const val GROUP_KEY = "GEOFENCE"

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i("geofence", "리시버 실행")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                Log.e("Geofence", "Error: ${geofencingEvent.errorCode}")
                return
            }
        }

        val geofenceTransition = geofencingEvent?.geofenceTransition
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.i("geofence", "입장")
            showNotification(context, "입장")
            wakeScreen(context)
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.i("geofence", "나감")
            showNotification(context, "나감")
        }
    }
}

fun showNotification(context: Context, message: String) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // 알림 생성
    val notification = NotificationCompat.Builder(context, GEOFENCE_CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_notification_overlay) // 알림 아이콘 (리소스는 직접 추가해야 합니다)
        .setContentTitle("Geofence Alert")
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true) // 알림 클릭 시 사라지게 설정
        .setGroup(GROUP_KEY)
        .build()

    val summaryNotification = NotificationCompat.Builder(context, GEOFENCE_CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_notification_overlay)
        .setGroup(GROUP_KEY)
        .setGroupSummary(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    // 알림 표시
    val notificationId = System.currentTimeMillis().toInt()
    notificationManager.notify(notificationId, notification)
    notificationManager.notify(GROUP_KEY.hashCode(), summaryNotification)
}

private fun wakeScreen(context: Context) {
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    val wakeLock = powerManager.newWakeLock(
        PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
        "ScreenToggleApp::MyWakeLockTag"
    )
    wakeLock.acquire(60 * 1000L) // 1분 유지
    wakeLock.release()
}