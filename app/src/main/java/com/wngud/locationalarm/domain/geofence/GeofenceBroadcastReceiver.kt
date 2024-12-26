package com.wngud.locationalarm.domain.geofence

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.PowerManager
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.wngud.locationalarm.Constants.GEOFENCE_CHANNEL_ID
import com.wngud.locationalarm.data.repository.SettingRepositoryImpl.Companion.ALARM_RINGTONE_URI
import com.wngud.locationalarm.data.repository.SettingRepositoryImpl.Companion.ALARM_VIBRATION
import com.wngud.locationalarm.data.repository.SettingRepositoryImpl.Companion.ALARM_VOLUME
import com.wngud.locationalarm.domain.service.GeofenceService
import com.wngud.locationalarm.screen.close.CloseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

const val GROUP_KEY = "GEOFENCE"

@AndroidEntryPoint
class GeofenceBroadcastReceiver : BroadcastReceiver() {

    @Inject lateinit var dataStore: DataStore<Preferences>

    companion object {
        private var vibrator: Vibrator? = null
        private var mediaPlayer: MediaPlayer? = null

        private fun startVibration(context: Context) {
            if(vibrator == null) vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            val pattern = longArrayOf(0, 500, 500)  // 대기시간, 진동시간, 정지시간
            vibrator?.vibrate(pattern, 0)
        }

        fun stopVibration() {
            vibrator?.cancel()
            vibrator = null
            Log.d("dddddd","진동 종료")
        }

        fun playAlarmSound(
            context: Context,
            volume: Float,
            uri: Uri?
        ) {
            // 기존 MediaPlayer 중지 및 해제
            mediaPlayer?.stop()
            mediaPlayer?.release()

            // 새 MediaPlayer 생성
            val newMediaPlayer = MediaPlayer().apply {
                val uriToPlay = uri ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                setAudioStreamType(AudioManager.STREAM_ALARM)
                setDataSource(context, uriToPlay)

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
                setAudioAttributes(audioAttributes)

                // MediaPlayer에서 추가로 볼륨 설정
                setVolume(volume, volume)
                prepare()
                isLooping = true
                start()
            }
            mediaPlayer = newMediaPlayer
        }

        fun stopAlarmSound() {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == "START_VIBRATION") {
            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            val pattern = longArrayOf(0, 500, 500)  // 대기시간, 진동시간, 정지시간
            vibrator?.vibrate(pattern, 0)
        } else if (action == "STOP_VIBRATION") {
            stopAlarmSound()
            stopVibration()
        }

        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent != null) {
            if (geofencingEvent.hasError()) {
                Log.e("Geofence", "Error: ${geofencingEvent.errorCode}")
                return
            }
        }

        val triggeringGeofences = geofencingEvent?.triggeringGeofences

        val geofenceTransition = geofencingEvent?.geofenceTransition
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            val preferences  = runBlocking { dataStore.data.first() }
            val vibration = preferences[ALARM_VIBRATION] ?: false
            val ringtoneUri = preferences[ALARM_RINGTONE_URI] ?: RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_RINGTONE).toString()
            val volume = preferences[ALARM_VOLUME] ?: 0.0f
            Log.i("geofence", "입장 $vibration $ringtoneUri")
            showNotification(context, "해제하려면 클릭해주세요", triggeringGeofences!!.first().requestId)
            wakeScreen(context)
            playAlarmSound(context, volume, Uri.parse(ringtoneUri))
            if(vibration) {
                startVibration(context)
            }

            val serviceIntent = Intent(context, GeofenceService::class.java)
            serviceIntent.action = "REMOVE_GEOFENCE"
            serviceIntent.putExtra("requestId", triggeringGeofences!!.first().requestId)
            context.startService(serviceIntent)

            Log.d("geofence", "줬음: ${triggeringGeofences.first().requestId}")
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.i("geofence", "나감")
        }
    }
}

fun showNotification(context: Context, message: String, requestId: String) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val intent = Intent(context, CloseActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    intent.putExtra("requestId", requestId)
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

    // 알림 생성
    val notification = NotificationCompat.Builder(context, GEOFENCE_CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_notification_overlay) // 알림 아이콘 (리소스는 직접 추가해야 합니다)
        .setContentTitle("Geofence Alert")
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true) // 알림 클릭 시 사라지게 설정
        .setContentIntent(pendingIntent)
        .setGroup(GROUP_KEY)
        .build()

//    val summaryNotification = NotificationCompat.Builder(context, GEOFENCE_CHANNEL_ID)
//        .setSmallIcon(android.R.drawable.ic_notification_overlay)
//        .setGroup(GROUP_KEY)
//        .setGroupSummary(true)
//        .setPriority(NotificationCompat.PRIORITY_HIGH)
//        .build()

    // 알림 표시
    val notificationId = System.currentTimeMillis().toInt()
    notificationManager.notify(notificationId, notification)
//    notificationManager.notify(GROUP_KEY.hashCode(), summaryNotification)
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