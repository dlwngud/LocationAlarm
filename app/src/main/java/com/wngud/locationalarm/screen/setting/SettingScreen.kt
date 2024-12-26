package com.wngud.locationalarm.screen.setting

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavHostController
import com.wngud.locationalarm.R
import com.wngud.locationalarm.screen.AppBar

@SuppressLint("StateFlowValueCalledInComposition", "NewApi")
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun SettingScreen(
    navController: NavHostController,
    settingViewModel: SettingViewModel = hiltViewModel<SettingViewModel>(),
    onBackPressed: () -> Unit
) {
    val settingState = settingViewModel.settingState.collectAsState().value
    val context = LocalContext.current
//    val mediaPlayer = remember { mutableStateOf<MediaPlayer?>(null) }
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    var sliderPosition by remember { mutableFloatStateOf(settingState.volume) }

    var selectedRingtone by remember { mutableStateOf<Uri?>(settingState.ringtoneUri) }
    var ringtoneName by remember { mutableStateOf(settingState.ringtoneName) }
    var isPlaying by remember { mutableStateOf(false) }

//    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    var isVibrating by remember { mutableStateOf(settingState.isVibration) }

//    val mediaPlayer by settingViewModel.mediaPlayer.collectAsState()
//    val vibrator by settingViewModel.vibrator.collectAsState()

    // 벨소리 선택을 위한 launcher
    val ringtonePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                ?.let { uri ->
                    selectedRingtone = uri
                    ringtoneName = RingtoneManager.getRingtone(context, uri).getTitle(context)

                    // 설정 저장
                    settingViewModel.updateSetting(
                        settingState.copy(
                            ringtoneUri = uri,
                            ringtoneName = ringtoneName
                        )
                    )
                }
        }
    }


    BackHandler(onBack = {
        onBackPressed()
    })

    Scaffold(
        topBar = {
            AppBar(title = stringResource(R.string.setting), hasBackButton = false)
        },
        backgroundColor = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "벨소리", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = {
                    val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                        putExtra(
                            RingtoneManager.EXTRA_RINGTONE_TYPE,
                            RingtoneManager.TYPE_NOTIFICATION
                        )
                        putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "알림음 선택")
                        putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, selectedRingtone)
                    }
                    ringtonePicker.launch(intent)
                }) {
                    Text(ringtoneName)
                }
            }

            Divider()

            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "볼륨", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    val volume = (sliderPosition * 15).toInt()
                    Text(text = volume.toString())
                }

                Slider(
                    value = sliderPosition,
                    onValueChange = { newVolume ->
                        sliderPosition = newVolume
                        settingViewModel.updateSetting(settingState.copy(volume = sliderPosition))
                        setAlarmVolume(audioManager, newVolume)
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    steps = 14,
                    valueRange = 0f..1f
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "진동", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Switch(
                        checked = isVibrating,
                        onCheckedChange = { isVibration ->
                            isVibrating = isVibration
                            if (isVibration) {
//                                vibrateOnce(context, vibrator)
                                settingViewModel.vibrateOnce(context)
                            }
                            settingViewModel.updateSetting(settingState.copy(isVibration = isVibration))
                        }
                    )
                }
                TextButton(
                    onClick = {
                        if (isPlaying) {
                            settingViewModel.stopAlarmSound()
//                            stopAlarmSound(mediaPlayer)
                            if (isVibrating) {
                                settingViewModel.stopVibration()
//                                stopVibration(vibrator)
                            }

                            isPlaying = false
                        } else {
                            settingViewModel.playAlarmSound(context, sliderPosition, selectedRingtone)
//                            playAlarmSound(context, mediaPlayer, sliderPosition, selectedRingtone)
                            if (isVibrating) {
                                settingViewModel.startVibration(context)
//                                startVibration(vibrator)
                            }

                            isPlaying = true
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(if (isPlaying) "그만듣기" else "미리듣기")
                }
            }

            Divider()
        }
    }
}

fun setAlarmVolume(audioManager: AudioManager, volume: Float) {
    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
    val newVolume = (maxVolume * volume).toInt()
    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, newVolume, 0)
}

fun playAlarmSound(
    context: Context,
    mediaPlayerState: MutableState<MediaPlayer?>,
    volume: Float,
    uri: Uri?
) {
    Log.d("dddddd", volume.toString())
    // 기존 MediaPlayer 중지 및 해제
    mediaPlayerState.value?.stop()
    mediaPlayerState.value?.release()

    // 새 MediaPlayer 생성
    val mediaPlayer = MediaPlayer().apply {
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
    mediaPlayerState.value = mediaPlayer
}

fun stopAlarmSound(mediaPlayerState: MutableState<MediaPlayer?>) {
    mediaPlayerState.value?.stop()
    mediaPlayerState.value?.release()
    mediaPlayerState.value = null
}

fun vibrateOnce(context: Context, vibrator: Vibrator, duration: Long = 100L) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // API 26 이상
        val vibrationEffect =
            VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect)
    } else {
        // API 26 미만
        vibrator.vibrate(duration)
    }
}

fun startVibration(vibrator: Vibrator) {
    val pattern = longArrayOf(0, 500, 500)  // 대기시간, 진동시간, 정지시간
    vibrator.vibrate(pattern, 0)
}

fun stopVibration(vibrator: Vibrator) {
    vibrator.cancel()
}