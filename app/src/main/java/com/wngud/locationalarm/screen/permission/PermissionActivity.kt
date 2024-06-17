package com.wngud.locationalarm.screen.permission

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wngud.locationalarm.MainActivity
import com.wngud.locationalarm.ui.theme.LocationAlarmTheme

class PermissionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val activity = LocalContext.current as Activity
            LocationAlarmTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PermissionScreen(activity)
                }
            }
        }
    }
}

@Composable
fun PermissionScreen(activity: Activity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
    ) {
        Text(text = "위치링 접근 권한 안내", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = """
            위치링 서비스를 제공하는데 필요한 권한입니다.
            앱을 사용하기 위해서는
            다음과 같은 권한 설정이 필요합니다.
        """.trimIndent(),
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "필수 접근 권한",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp)
        ) {
            Text(text = "• 알림", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "알람 설정 시 알림을 제공하기 위해 필요합니다.",
                color = Color.Gray,
                modifier = Modifier.padding(start = 10.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "• 위치", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "설정된 위치 범위 내에 진입할 때 알림을 제공하기 위해 필요합니다.",
                color = Color.Gray,
                modifier = Modifier.padding(start = 10.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "• 음악 및 오디오", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "알람 설정 시 사용자가 선택한 벨소리를 재생하기 위해 필요합니다.",
                color = Color.Gray,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = { moveMainActivity(activity) }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "확인")
        }
    }
}

fun moveMainActivity(activity: Activity) {
    val intent = Intent(activity, MainActivity::class.java)
    activity.startActivity(intent)
    activity.finish()
}