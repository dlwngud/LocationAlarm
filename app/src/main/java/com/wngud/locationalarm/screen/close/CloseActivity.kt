package com.wngud.locationalarm.screen.close

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.wngud.locationalarm.domain.geofence.GeofenceBroadcastReceiver
import com.wngud.locationalarm.screen.close.ui.theme.LocationAlarmTheme

class CloseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            LocationAlarmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CloseScreen { stopVibration(context) }
                }
            }
        }
    }
}

fun stopVibration(context: Context) {
    val intent = Intent(context, GeofenceBroadcastReceiver::class.java).apply {
        action = "STOP_VIBRATION"
    }
    context.sendBroadcast(intent)
    if (context is CloseActivity) {
        context.finish()
    }
}