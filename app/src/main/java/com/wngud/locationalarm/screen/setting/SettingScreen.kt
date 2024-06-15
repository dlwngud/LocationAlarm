package com.wngud.locationalarm.screen.setting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.wngud.locationalarm.R
import com.wngud.locationalarm.screen.AppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavHostController) {
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    val coffeeDrinks = arrayOf("진동", "벨소리", "진동 + 벨소리", "이어폰")
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(coffeeDrinks[0]) }

    Scaffold(topBar = {
        AppBar(title = stringResource(R.string.alarm), onBackNavClicked = {
            navController.navigateUp()
        })
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "벨소리", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "선택한 벨소리")
            }

            Divider()

            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "볼륨", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    steps = 9,
                    valueRange = 0f..10f
                )
                Text(text = sliderPosition.toString())
            }

            Divider()

            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "모드", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = {
                        expanded = !expanded
                    }) {
                        TextField(value = selectedText,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )

                        ExposedDropdownMenu(expanded = expanded,
                            onDismissRequest = { expanded = false }) {
                            coffeeDrinks.forEach { item ->
                                DropdownMenuItem(text = { Text(text = item) }, onClick = {
                                    selectedText = item
                                    expanded = false
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}