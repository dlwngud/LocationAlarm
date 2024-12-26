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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.wngud.locationalarm.R
import com.wngud.locationalarm.domain.Alarm

@Composable
fun CloseScreen(
    alarm: Alarm?,
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
            alarm?.title?.let { Text(text = it, style = MaterialTheme.typography.h5) }
            Spacer(modifier = Modifier.height(8.dp))
            alarm?.content?.let { Text(text = it, style = MaterialTheme.typography.body1) }
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

@Composable
fun DestinationArrivalScreen(
    alarm: Alarm?,
    onClose: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.bell_animation))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Lottie Animation Section
            LottieAnimation(
                composition = composition,
                progress = progress,
                modifier = Modifier.size(80.dp),
                alignment = Alignment.Center
            )

            // Title
            Text(
                text = "목적지 도착",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.Black
                )
            )

            // Description
            alarm?.title?.let {
                Text(
                    text = it,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            alarm?.content?.let {
                Text(
                    text = it,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Button
            Button(
                onClick = { onClose() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Black,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "알림 해제", style = TextStyle(fontSize = 16.sp))
            }
        }
    }
}