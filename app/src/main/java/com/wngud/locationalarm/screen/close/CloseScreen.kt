package com.wngud.locationalarm.screen.close

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun CloseScreen(
    title: String = "집",
    content: String = "쉬기",
    onClose: () -> Unit
) {
    var buttonSize by remember { mutableStateOf(100f) } // 원형 버튼의 초기 크기
    val maxButtonSize = 300f // 버튼이 최대 커질 크기
    val alpha by animateFloatAsState(targetValue = buttonSize / maxButtonSize)

    val buttonModifier = Modifier
        .size(buttonSize.dp)
        .graphicsLayer(alpha = alpha)
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                change.consume() // 터치 이벤트 소비
                buttonSize = (buttonSize + dragAmount.y).coerceIn(100f, maxButtonSize) // 크기 조절
                if (buttonSize >= maxButtonSize) {
                    onClose() // 최대 크기에 도달하면 화면 종료
                }
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = title, style = MaterialTheme.typography.h5)
            Spacer(modifier = Modifier.height(8.dp))
            if(content != "Default Content") {
                Text(text = content, style = MaterialTheme.typography.body1)
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = buttonModifier
                    .background(Color.Red, shape = CircleShape)
                    .border(2.dp, Color.Black, shape = CircleShape)
            )
        }
    }
}