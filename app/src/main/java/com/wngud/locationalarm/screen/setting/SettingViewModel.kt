package com.wngud.locationalarm.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wngud.locationalarm.domain.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingState(
    val loading: Boolean = true,
    val isVibration: Boolean = true
)

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingRepository: SettingRepository
): ViewModel() {
    private val _settingState = MutableStateFlow(SettingState())
    val settingState = _settingState.asStateFlow()

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
}