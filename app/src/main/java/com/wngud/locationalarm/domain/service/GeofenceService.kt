package com.wngud.locationalarm.domain.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.wngud.locationalarm.Constants.GEOFENCE_CHANNEL_ID
import com.wngud.locationalarm.Constants.SERVICE_CHANNEL_ID
import com.wngud.locationalarm.R
import com.wngud.locationalarm.domain.Alarm
import com.wngud.locationalarm.domain.geofence.GeofenceBroadcastReceiver
import com.wngud.locationalarm.domain.repository.AlarmRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GeofenceService : Service() {
    @Inject lateinit var alarmRepository: AlarmRepository

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var geofencingClient: GeofencingClient
    private val onNotificationDismissedReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun onReceive(context: Context?, intent: Intent?) {
            val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = createNotification()
            notificationManager.notify(1, notification)
        }
    }

    private lateinit var localBroadcastManager: LocalBroadcastManager

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate() {
        super.onCreate()
        Log.i("geofence", "서비스 시작")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geofencingClient = LocationServices.getGeofencingClient(this)
        startLocationUpdates()

        registerReceiver(
            onNotificationDismissedReceiver,
            IntentFilter("DISMISSED_ACTION"),
            RECEIVER_NOT_EXPORTED // This is required on Android 14
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val alarmList: ArrayList<Alarm>? = intent?.getParcelableArrayListExtra("alarms")
        alarmList?.forEach {
            Log.i("geofence", "${it}}")
        }

        when (intent?.action) {
            "STOP_GEOFENCE" -> {
                // 사용자가 STOP 버튼을 눌렀을 때 처리
                removeGeofences()
                stopSelf() // 서비스 중지
                return START_NOT_STICKY
            }

            else -> {
                // 알림 생성 및 포그라운드 서비스 시작
                Log.i("geofence", "알림 생성 ${alarmList?.size}")
                startForegroundService()
                alarmList?.let { addGeofence(it) }
                return START_STICKY
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000 // 10초마다 위치 업데이트
            fastestInterval = 5000 // 5초
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    Log.i("geofence", "Location: ${location.latitude}, ${location.longitude}")
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    @SuppressLint("ForegroundServiceType")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(1, notification)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    private fun createNotification(): Notification {

        // STOP 버튼 인텐트 생성
        val stopIntent = Intent(this, GeofenceService::class.java).apply {
            action = "STOP_GEOFENCE"
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

    val dismissedIntent = Intent("DISMISSED_ACTION")
        dismissedIntent.setPackage(packageName) // This is required on Android 14
        val dismissedPendingIntent = PendingIntent.getBroadcast(
            this,
            1,
            dismissedIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        return NotificationCompat.Builder(this, SERVICE_CHANNEL_ID)
            .setContentTitle("지오펜싱 서비스")
            .setContentText("지오펜싱이 활성화되었습니다.")
            .setSmallIcon(R.drawable.baseline_alarm_on_24)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setDeleteIntent(dismissedPendingIntent)
            .addAction(
                android.R.drawable.ic_delete, // STOP 버튼의 아이콘
                "Stop", // STOP 버튼의 텍스트
                stopPendingIntent // 버튼 클릭 시 동작
            )
            .build()
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(geofenceDataList: List<Alarm>) {
        val geofenceList = mutableListOf<Geofence>()

        geofenceDataList.forEach { data ->
            val geofence = Geofence.Builder()
                .setRequestId("${data.latitude}, ${data.longitude}")
                .setCircularRegion(
                    data.latitude,
                    data.longitude,
                    data.radius.toFloat()
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                .build()

            geofenceList.add(geofence)
        }

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofenceList)
            .build()

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)
            .addOnSuccessListener {
                Log.d("geofence", "지오펜스 추가 성공")
            }
            .addOnFailureListener {
                Log.e("geofence", "지오펜스 추가 실패: ${it.message}")
            }
    }

    private fun removeGeofences() {
        // 지오펜싱 제거
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            Intent(this, GeofenceBroadcastReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        CoroutineScope(Dispatchers.IO).launch {
            alarmRepository.loadAlarms().collectLatest { alarmList ->
                Log.i("geofence", "$alarmList")
                alarmList.forEach {
                    alarmRepository.updateAlarm(it.copy(isChecked = false))
                }
                cancel()
            }
        }

        geofencingClient.removeGeofences(pendingIntent)
            .addOnSuccessListener {
                Log.i("geofence", "지오펜싱 해제 성공")
            }
            .addOnFailureListener { e ->
                Log.e("geofence", "지오펜싱 해제 실패: ${e.message}")
            }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        stopSelf()
        unregisterReceiver(onNotificationDismissedReceiver)
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.i("geofence", "서비스 종료")
    }
}