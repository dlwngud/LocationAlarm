package com.wngud.locationalarm.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource


@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun HomeScreen() {
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
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val locationSource = rememberFusedLocationSource()
        val cameraPositionState = rememberCameraPositionState()
        var isFirstLoad by remember { mutableStateOf(true) }

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
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}