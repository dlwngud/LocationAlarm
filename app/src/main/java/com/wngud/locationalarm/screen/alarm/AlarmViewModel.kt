package com.wngud.locationalarm.screen.alarm

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wngud.locationalarm.domain.Alarm
import com.wngud.locationalarm.domain.repository.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AlarmState(
    val loading: Boolean = true,
    val alarms: List<Alarm> = emptyList()
)

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository
) : ViewModel() {
    private val _alarmsState = MutableStateFlow(AlarmState())
    val alarmsState = _alarmsState.asStateFlow()

    var alarmDetailState by mutableStateOf(Alarm())

    private val _alarmCloseState = MutableStateFlow<Alarm?>(null)
    val alarmCloseState = _alarmCloseState.asStateFlow()

    init {
        loadAlarms()
    }

    fun loadAlarms() = viewModelScope.launch {
        alarmRepository.loadAlarms().collectLatest { result ->
            _alarmsState.update { it.copy(alarms = result) }
        }
    }

    fun addAlarm(alarm: Alarm) = viewModelScope.launch(Dispatchers.IO) {
        alarmRepository.addAlarm(alarm)
    }

    fun getAlarmById(id: Long) = alarmRepository.getAlarmById(id)

    fun getAlarmByRequestId(requestId: String) = viewModelScope.launch {
        val (lat, lng) = requestId.split(", ").map { it.toDouble() }
        alarmRepository.getAlarmByLatLng(lat, lng).collect { alarm ->
            _alarmCloseState.value = alarm
        }
    }

    fun updateAlarm(alarm: Alarm) = viewModelScope.launch {
        alarmRepository.updateAlarm(alarm)
    }

    fun deleteAlarm(alarm: Alarm) = viewModelScope.launch {
        alarmRepository.deleteAlarm(alarm)
    }
}