package com.wngud.locationalarm

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.google.android.gms.ads.MobileAds
import com.naver.maps.map.NaverMapSdk
import com.wngud.locationalarm.Constants.GEOFENCE_CHANNEL_ID
import com.wngud.locationalarm.Constants.SERVICE_CHANNEL_ID
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        NaverMapSdk.getInstance(this).client = NaverMapSdk.NaverCloudPlatformClient(BuildConfig.NAVER_CLIENT_ID)
        createNotificationChannel()

        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            MobileAds.initialize(this@App)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val serviceChannel = NotificationChannel(
                SERVICE_CHANNEL_ID,
                "Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                setShowBadge(true)
            }
            manager.createNotificationChannel(serviceChannel)

            val geofenceChannel = NotificationChannel(
                GEOFENCE_CHANNEL_ID,
                "Geofence Monitoring",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(true)
            }
            manager.createNotificationChannel(geofenceChannel)
        }
    }
}