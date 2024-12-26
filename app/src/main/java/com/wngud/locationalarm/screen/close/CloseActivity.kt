package com.wngud.locationalarm.screen.close

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.wngud.locationalarm.domain.geofence.GeofenceBroadcastReceiver
import com.wngud.locationalarm.screen.alarm.AlarmViewModel
import com.wngud.locationalarm.screen.close.ui.theme.LocationAlarmTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CloseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val requestId = intent.getStringExtra("requestId")

        enableEdgeToEdge()
        setContent {
            val alarmViewModel = hiltViewModel<AlarmViewModel>()
            requestId?.let { alarmViewModel.getAlarmByRequestId(it) }
            val alarm = alarmViewModel.alarmCloseState.collectAsState().value

            val context = LocalContext.current
            LocationAlarmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    CloseScreen(alarm) { stopVibration(context) }
                    DestinationArrivalScreen(alarm) {
                        stopVibration(context)
                    }
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