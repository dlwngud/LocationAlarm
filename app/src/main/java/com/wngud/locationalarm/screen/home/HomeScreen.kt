package com.wngud.locationalarm.screen.home

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.compose.CameraPositionState
import com.naver.maps.map.compose.CircleOverlay
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalNaverMapApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onBackPressed: () -> Unit) {
    var mapProperties by remember {
        mutableStateOf(
            MapProperties(
                maxZoom = 18.0, minZoom = 10.0, locationTrackingMode = LocationTrackingMode.Follow
            )
        )
    }
    var mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(isLocationButtonEnabled = true)
        )
    }

    BackHandler(enabled = true, onBack = {
        onBackPressed()
    })

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val locationSource = rememberFusedLocationSource()
    val cameraPositionState = rememberCameraPositionState()
    var isFirstLoad by remember { mutableStateOf(true) }
    var isMapClick by remember { mutableStateOf(LatLng(-1.0, -1.0)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        NaverMap(
            properties = mapProperties,
            uiSettings = mapUiSettings,
            locationSource = locationSource,
            cameraPositionState = cameraPositionState,
            onLocationChange = {
                if (isFirstLoad) {
                    cameraPositionState.position =
                        CameraPosition(LatLng(it.latitude, it.longitude), 15.0)
                    isFirstLoad = false
                }
            },
            onMapLongClick = { _, latLng ->
                isMapClick = latLng
            }
        ) {
            if (isMapClick != LatLng(-1.0, -1.0)) {
                showMarker(latLng = isMapClick, cameraPositionState = cameraPositionState, scope = rememberCoroutineScope())
                showBottomSheet = true
            }
        }
    }

    if (showBottomSheet) {
        ShowBottomSheet(
            onDismiss = {
                showBottomSheet = false
                isMapClick = LatLng(-1.0, -1.0)
            },
            sheetState = sheetState,
            scope = rememberCoroutineScope()
        )
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun showMarker(latLng: LatLng, cameraPositionState: CameraPositionState, scope: CoroutineScope) {
    scope.launch {
        cameraPositionState.animate(
            update = CameraUpdate.toCameraPosition(CameraPosition(latLng, 15.0)),
            animation = CameraAnimation.Easing
        )
    }
    Marker(
        state = MarkerState(position = latLng),
    )
    CircleOverlay(
        center = latLng,
        radius = 100.0,
        outlineColor = MaterialTheme.colorScheme.primary,
        outlineWidth = 2.dp,
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowBottomSheet(onDismiss: () -> Unit, sheetState: SheetState, scope: CoroutineScope) {
    var sliderPosition by remember { mutableFloatStateOf(1f) }
    var title by rememberSaveable { mutableStateOf("") }
    var content by rememberSaveable { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState
    ) {
        // Sheet content
        Column {
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
                valueRange = 1f..100f
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
                onClick = { onDismiss() }
            ) {
                Text(text = "저장하기")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
//    HomeScreen()
}