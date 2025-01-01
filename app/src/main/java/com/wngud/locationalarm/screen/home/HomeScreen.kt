package com.wngud.locationalarm.screen.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
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
import com.wngud.locationalarm.R
import com.wngud.locationalarm.domain.Alarm
import com.wngud.locationalarm.screen.alarm.AlarmViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale


@OptIn(ExperimentalNaverMapApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onBackPressed: () -> Unit,
    alarmViewModel: AlarmViewModel,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val alarmState = alarmViewModel.alarmsState.collectAsState().value
    val searchResults = homeViewModel.searchResults.collectAsState().value
    val isLoading = homeViewModel.isLoading.collectAsState().value

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
    var locationTitle by remember { mutableStateOf("") }
    val locationSource = rememberFusedLocationSource()
    val cameraPositionState = rememberCameraPositionState()
    var isFirstLoad by remember { mutableStateOf(true) }
    var isMapClick by remember { mutableStateOf(LatLng(-1.0, -1.0)) }
    var sliderPosition by remember { mutableFloatStateOf(1f) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    DisposableEffect(Unit) {
        onDispose {
            homeViewModel.initResults()
        }
    }

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
            onMapClick = { _, latLng ->
                isMapClick = latLng
            }
        ) {
            if (isMapClick != LatLng(-1.0, -1.0)) {
                showMarker(
                    latLng = isMapClick,
                    cameraPositionState = cameraPositionState,
                    radius = sliderPosition.toDouble() * 100,
                    scope = scope,
                    zoom = (-sliderPosition * 0.16) + 15
                )
                showBottomSheet = true
            }

            alarmState.alarms.forEach {
                Marker(
                    state = MarkerState(position = LatLng(it.latitude, it.longitude)),
                    captionText = it.content
                )
                CircleOverlay(
                    center = LatLng(it.latitude, it.longitude),
                    radius = it.radius,
                    outlineColor = MaterialTheme.colorScheme.primary,
                    outlineWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            }
        }

        var searchQuery by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .pointerInput(Unit) { detectDragGestures { change, dragAmount -> } }
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearchConfirmed = {
                    homeViewModel.searchLocation(searchQuery)
                },
                isLoading = isLoading,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .shadow(4.dp, RoundedCornerShape(16.dp))
            )

            if (searchResults.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                        .background(
                            color = MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    items(searchResults) { result ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .clickable {
                                    getLatLongFromAddress(context, result.address)?.let {
                                        isMapClick = it
                                        locationTitle = result.title
                                        homeViewModel.initResults()
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = result.title,
                                    maxLines = 1,
                                    fontSize = 16.sp
                                )
                                if (result.roadAddress.isNotEmpty()) {
                                    Text(
                                        text = result.roadAddress,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                } else if (result.address.isNotEmpty()) {
                                    Text(
                                        text = result.address,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = Color.Gray,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                            Text(
                                text = result.category,
                                fontSize = 14.sp,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        ShowBottomSheet(
            onDismiss = {
                showBottomSheet = false
                isMapClick = LatLng(-1.0, -1.0)
                sliderPosition = 1f
            },
            sheetState = sheetState,
            sliderPosition = sliderPosition,
            sliderChange = { sliderPosition = it },
            alarmViewModel = alarmViewModel,
            scope = rememberCoroutineScope(),
            latLng = isMapClick,
            radius = sliderPosition.toDouble() * 100,
            locationTitle = locationTitle,
            onValueChange = { locationTitle = it }
        )
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalNaverMapApi::class)
@Composable
fun showMarker(
    latLng: LatLng,
    cameraPositionState: CameraPositionState,
    scope: CoroutineScope,
    radius: Double,
    zoom: Double
) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowBottomSheet(
    onDismiss: () -> Unit,
    latLng: LatLng,
    radius: Double,
    sheetState: SheetState,
    sliderPosition: Float,
    sliderChange: (Float) -> Unit,
    alarmViewModel: AlarmViewModel,
    locationTitle: String,
    onValueChange: (String) -> Unit,
    scope: CoroutineScope
) {
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
                onValueChange = sliderChange,
                steps = 0,
                valueRange = 1f..20f
            )

            OutlinedTextField(
                value = locationTitle,
                onValueChange = { newValue -> onValueChange(newValue) },
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
                    val alarm = Alarm(
                        latitude = latLng.latitude,
                        longitude = latLng.longitude,
                        radius = radius,
                        title = locationTitle,
                        content = content,
                        isChecked = false
                    )
                    alarmViewModel.addAlarm(alarm = alarm)
                    onDismiss()
                }
            ) {
                Text(text = "저장하기")
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchConfirmed: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    placeholder: String = "장소를 입력하세요"
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_animation))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "검색",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.clickable(false) {}
        )

        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onSearchConfirmed()
                    keyboardController?.hide()
                }
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart // 수직 정렬을 중앙으로 설정
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    innerTextField() // 커서와 텍스트 필드
                }
            }
        )

        if (isLoading) {
            LottieAnimation(
                composition = composition,
                progress = progress,
                modifier = Modifier
                    .size(80.dp)
                    .clickable(false) {},
                alignment = Alignment.Center
            )
        }
    }
}

fun getLatLongFromAddress(context: Context, address: String): LatLng? {
    val geocoder = Geocoder(context, Locale.getDefault())
    return try {
        val addresses = geocoder.getFromLocationName(address, 1)
        addresses?.let {
            val location = it[0]
            LatLng(location.latitude, location.longitude)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null // 예외 발생 시 null 반환
    }
}