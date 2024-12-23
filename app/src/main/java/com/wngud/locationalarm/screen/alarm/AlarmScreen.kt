package com.wngud.locationalarm.screen.alarm

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wngud.locationalarm.R
import com.wngud.locationalarm.Screen
import com.wngud.locationalarm.domain.Alarm
import com.wngud.locationalarm.domain.service.GeofenceService
import com.wngud.locationalarm.screen.AppBar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlarmScreen(
    navController: NavHostController,
    onBackPressed: () -> Unit,
    alarmViewModel: AlarmViewModel
) {
    val alarmState = alarmViewModel.alarmsState.collectAsState().value
    val checkedAlarms = alarmState.alarms.filter { it.isChecked }
    val updatedCheckedAlarms = rememberUpdatedState(checkedAlarms).value
    val context = LocalContext.current

    LaunchedEffect(updatedCheckedAlarms) {
        if (updatedCheckedAlarms.isNotEmpty()) {
            startGeofencingService(context, updatedCheckedAlarms)
        } else {
            stopGeofencingService(context)
        }
    }

    BackHandler(enabled = true, onBack = {
        onBackPressed()
    })

    Scaffold(
        topBar = {
            AppBar(
                title = stringResource(R.string.alarm), hasBackButton = false
            )
        },
        backgroundColor = MaterialTheme.colorScheme.background,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            items(alarmState.alarms, key = { wish -> wish.id }) { alarm ->
                StyledCard(alarm, navController, alarmViewModel = alarmViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StyledCard(
    alarm: Alarm,
    navController: NavHostController,
    modifier: Modifier = Modifier,
    alarmViewModel: AlarmViewModel
) {
    val shape = RoundedCornerShape(16.dp)
    var showDialog by remember { mutableStateOf(false) }
    val backgroundColor = Color(220, 220, 220)
    if (showDialog) {
        ShowDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = { alarmViewModel.deleteAlarm(alarm) },
            dialogTitle = "해당 알람을 삭제하시겠습니까?",
            dialogText = "되돌릴 수 없습니다.",
            icon = Icons.Default.Warning
        )
    }
    val dismissState = rememberDismissState(confirmStateChange = { dismissValue ->
        when (dismissValue) {
            DismissValue.Default -> { // dismissThresholds 만족 안한 상태
                false
            }

            DismissValue.DismissedToEnd -> { // -> 방향 스와이프 (수정)
                navController.navigate(Screen.DetailAlarmScreen.createRoute(alarm.id))
                false
            }

            DismissValue.DismissedToStart -> { // <- 방향 스와이프 (삭제)
                showDialog = true
                false
            }
        }
    })

    SwipeToDismiss(
        state = dismissState,
        dismissThresholds = { FractionalThreshold(0.25f) },
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 8.dp)
            .padding(horizontal = 8.dp)
            .clip(shape),
        dismissContent = {
            AlarmItem(alarm, shape, alarmViewModel) {
                /*TODO("알림 상세창 이동")*/
            }
        },
        background = { // dismiss content
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.Default -> backgroundColor.copy(alpha = 0.5f) // dismissThresholds 만족 안한 상태
                    DismissValue.DismissedToEnd -> Color.Green.copy(alpha = 0.4f) // -> 방향 스와이프 (수정)
                    DismissValue.DismissedToStart -> Color.Red.copy(alpha = 0.5f) // <- 방향 스와이프 (삭제)
                }, label = ""
            )
            val icon = when (dismissState.targetValue) {
                DismissValue.Default -> Icons.Default.Face
                DismissValue.DismissedToEnd -> Icons.Default.Edit
                DismissValue.DismissedToStart -> Icons.Default.Delete
            }
            val scale by animateFloatAsState(
                when (dismissState.targetValue == DismissValue.Default) {
                    true -> 0.8f
                    else -> 1.5f
                }, label = ""
            )
            val alignment = when (direction) {
                DismissDirection.EndToStart -> Alignment.CenterEnd
                DismissDirection.StartToEnd -> Alignment.CenterStart
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 30.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    modifier = Modifier.scale(scale),
                    imageVector = icon,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
fun AlarmItem(
    alarm: Alarm,
    shape: Shape,
    alarmViewModel: AlarmViewModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .clip(shape)
            .clickable { onClick() },
        elevation = 10.dp,
        backgroundColor = Color.LightGray
    ) {
        Row(
            modifier = Modifier.padding(end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Text(text = alarm.title, fontWeight = FontWeight.ExtraBold)
                Text(text = alarm.content)
            }
            Switch(
                checked = alarm.isChecked,
                onCheckedChange = {
                    alarmViewModel.updateAlarm(alarm.copy(isChecked = it))
                },
                thumbContent = if (alarm.isChecked) {
                    {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(SwitchDefaults.IconSize),
                        )
                    }
                } else {
                    null
                }
            )
        }

    }
}

@Composable
fun ShowDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        containerColor = Color.White,
        icon = { Icon(icon, contentDescription = "Warning") },
        title = { Text(text = dialogTitle) },
        text = { Text(text = dialogText) },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(
                onClick = { onConfirmation() }
            ) { Text("삭제") }
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissRequest() }
            ) { Text("취소") }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
private fun startGeofencingService(context: Context, checkedAlarms: List<Alarm>) {
    val serviceIntent = Intent(context, GeofenceService::class.java)
    serviceIntent.putParcelableArrayListExtra("alarms", checkedAlarms as ArrayList)
    context.startForegroundService(serviceIntent)
}

private fun stopGeofencingService(context: Context) {
    val serviceIntent = Intent(context, GeofenceService::class.java)
    context.stopService(serviceIntent)
}