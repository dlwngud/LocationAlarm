package com.wngud.locationalarm.screen.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.wngud.locationalarm.MainActivity
import com.wngud.locationalarm.screen.util.AudioPerMissionDescriptionProvider
import com.wngud.locationalarm.screen.util.LocationPerMissionDescriptionProvider
import com.wngud.locationalarm.screen.util.NotificationPerMissionDescriptionProvider
import com.wngud.locationalarm.screen.util.PermissionDescriptionProvider
import com.wngud.locationalarm.ui.theme.LocationAlarmTheme

class PermissionActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LocationAlarmTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    PermissionScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreen() {
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    var dialogContent by remember { mutableStateOf<PermissionDescriptionProvider?>(null) }
    val permissions = listOfNotNull(
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Manifest.permission.READ_EXTERNAL_STORAGE
        } else null,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else null,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else null,
    )

    val permissionState = rememberMultiplePermissionsState(permissions = permissions) { result ->
        val allGranted = result.values.all { it }
        if (allGranted) {
            moveMainActivity(context)
        } else {
            // 각 권한에 대한 사용자의 응답을 확인
            permissions.forEach { permission ->
                if (!result[permission]!!) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            context as PermissionActivity, permission
                        )
                    ) {
                        // 사용자에게 권한 요청 이유를 설명하는 UI 표시
                        showPermissionToast(context, permission)
                    } else {
                        // 사용자가 이전에 권한을 거부했거나 "다시 묻지 않음"을 선택한 경우
                        showPermissionDialog = true
                        dialogContent = convertDescription(permission)
                    }
                }
            }
        }
    }

    if (showPermissionDialog) {
        ShowAlertDialog(onDismiss = { showPermissionDialog = false },
            onConfirmation = {
                openAppInfoSettings(context)
                showPermissionDialog = false
            },
            dialogTitle = dialogContent?.getTitle(context) ?: "",
            dialogText = dialogContent?.getDescription(context) ?: ""
        )
    }

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
        """.trimIndent(), color = Color.Gray
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
        Button(
            onClick = {
                if (hasPermissions(context)) {
                    moveMainActivity(context)
                } else {
                    permissionState.launchMultiplePermissionRequest()
                }
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "확인")
        }
    }
}

fun showPermissionToast(context: Context, permission: String) {
    val text = convertDescription(permission)
    Toast.makeText(context, text.getDescription(context), Toast.LENGTH_SHORT).show()
}

@Composable
fun ShowAlertDialog(
    onDismiss: () -> Unit, onConfirmation: () -> Unit, dialogTitle: String, dialogText: String
) {
    AlertDialog(title = { Text(text = dialogTitle) },
        text = { Text(text = dialogText) },
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirmation) {
                Text("설정으로")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("취소")
            }
        })
}

private fun convertDescription(permission: String): PermissionDescriptionProvider {
    return when (permission) {
        Manifest.permission.ACCESS_COARSE_LOCATION -> LocationPerMissionDescriptionProvider()
        Manifest.permission.ACCESS_FINE_LOCATION -> LocationPerMissionDescriptionProvider()
        Manifest.permission.POST_NOTIFICATIONS -> NotificationPerMissionDescriptionProvider()
        Manifest.permission.READ_MEDIA_AUDIO -> AudioPerMissionDescriptionProvider()
        else -> AudioPerMissionDescriptionProvider()
    }
}

private fun hasPermissions(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        }

        Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }

        else -> true
    }
}

private fun openAppInfoSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        val uri = Uri.fromParts("package", context.packageName, null)
        data = uri
    }
    context.startActivity(intent)
}

private fun moveMainActivity(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    context.startActivity(intent)
    (context as Activity).finish()
}