package com.wngud.locationalarm.screen.setting

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wngud.locationalarm.domain.repository.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingState(
    val loading: Boolean = true,
    var ringtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE),
    var ringtoneName: String = "기본 벨소리",
    var volume: Float = 0.0f,
    val isVibration: Boolean = true,
)

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingRepository: SettingRepository,
) : ViewModel() {
    private val _settingState = MutableStateFlow(SettingState())
    val settingState = _settingState.asStateFlow()

    private val _mediaPlayer = MutableStateFlow<MediaPlayer?>(null)
    val mediaPlayer = _mediaPlayer.asStateFlow()

    private val _vibrator = MutableStateFlow<Vibrator?>(null)
    val vibrator = _vibrator.asStateFlow()

    init {
        loadAlarms()
    }

    fun loadAlarms() = viewModelScope.launch {
        settingRepository.getSetting().collectLatest { result ->
            _settingState.update { result }
        }
    }

    fun updateSetting(settingState: SettingState) = viewModelScope.launch {
        settingRepository.updateSetting(settingState)
    }

    fun playAlarmSound(
        context: Context,
        volume: Float,
        uri: Uri?
    ) {
        // 기존 MediaPlayer 중지 및 해제
        _mediaPlayer.value?.stop()
        _mediaPlayer.value?.release()

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
        _mediaPlayer.value = mediaPlayer
    }

    fun stopAlarmSound() {
        _mediaPlayer.value?.stop()
        _mediaPlayer.value?.release()
        _mediaPlayer.value = null
    }

    fun vibrateOnce(context: Context, duration: Long = 100L) {
        if(_vibrator.value == null) _vibrator.value = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // API 26 이상
            val vibrationEffect =
                VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
            _vibrator.value?.vibrate(vibrationEffect)
        } else {
            // API 26 미만
            _vibrator.value?.vibrate(duration)
        }
    }

    fun startVibration(context: Context) {
        if(_vibrator.value == null) _vibrator.value = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val pattern = longArrayOf(0, 500, 500)  // 대기시간, 진동시간, 정지시간
        _vibrator.value?.vibrate(pattern, 0)
    }

    fun stopVibration() {
        _vibrator.value?.cancel()
    }
}