package com.wngud.locationalarm.screen.alarm

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.CircleOverlay
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.wngud.locationalarm.domain.Alarm
import com.wngud.locationalarm.screen.AppBar
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalNaverMapApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DetailAlarmScreen(
    navController: NavHostController,
    alarmViewModel: AlarmViewModel,
    id: Long
) {
    var sliderPosition by rememberSaveable { mutableFloatStateOf(1f) }
    var title by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }
    var showMarker by rememberSaveable { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState()
    var mapProperties by remember {
        mutableStateOf(
            MapProperties()
        )
    }
    var mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                isLocationButtonEnabled = false,
                isZoomControlEnabled = false,
                isScrollGesturesEnabled = false,
                isZoomGesturesEnabled = false,
                isTiltGesturesEnabled = false,
                isRotateGesturesEnabled = false
            )
        )
    }

    val alarm = alarmViewModel.getAlarmById(id).collectAsState(initial = Alarm()).value

    if (alarm != Alarm() && id != -1L && !showMarker) {
        alarmViewModel.alarmDetailState = alarm
        showMarker = true
        title = alarm.title
        content = alarm.content
        sliderPosition = (alarm.radius / 100).toFloat()
        cameraPositionState.position =
            CameraPosition(LatLng(alarm.latitude, alarm.longitude), (-sliderPosition * 0.16) + 15)
    }

    Scaffold(
        topBar = {
            AppBar(
                title = "알람 설정",
                hasBackButton = true,
                onBackNavClicked = {
                    navController.navigateUp()
                })
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
            ) {
                NaverMap(
                    modifier = Modifier.fillMaxSize(),
                    properties = mapProperties,
                    uiSettings = mapUiSettings,
                    cameraPositionState = cameraPositionState
                ) {
                    if (showMarker) {
                        ShowMarker(
                            alarm = alarmViewModel.alarmDetailState,
                            cameraPositionState = cameraPositionState,
                            radius = sliderPosition.toDouble() * 100,
                            zoom = (-sliderPosition * 0.16) + 15
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "범위")
                Text(text = String.format("%.1fkm", sliderPosition / 10.0))
            }
            Slider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                steps = 0,
                valueRange = 1f..20f
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = {
                    Text(
                        "알람 이름",
                        fontWeight = FontWeight.Thin
                    )
                },
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = {
                    Text(
                        "내용",
                        fontWeight = FontWeight.Thin
                    )
                },
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.LightGray,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    navController.navigateUp()
                    TODO("저장하기")
                }
            ) {
                Text(text = "저장하기")
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun ShowMarker(
    alarm: Alarm,
    cameraPositionState: CameraPositionState,
    radius: Double,
    zoom: Double,
) {
    val scope = rememberCoroutineScope()
    val latLng = LatLng(alarm.latitude, alarm.longitude)
    scope.launch {
        cameraPositionState.animate(
            update = CameraUpdate.toCameraPosition(CameraPosition(latLng, zoom)),
            animation = CameraAnimation.Easing
        )
    }
    Marker(
        state = MarkerState(position = latLng),
    )
    CircleOverlay(
        center = latLng,
        radius = radius,
        outlineColor = MaterialTheme.colorScheme.primary,
        outlineWidth = 2.dp,
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    )
}

@Preview(showBackground = true)
@Composable
fun DetailAlarmScreenPreview() {
//    DetailAlarmScreen()
}