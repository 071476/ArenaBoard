package com.ali.arenaboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ali.arenaboard.core.network.RoomRepository
import com.ali.arenaboard.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WaitingRoomScreen(
    roomCode: String,
    gameType: String,
    playerId: String,
    onGameStart: () -> Unit,
    onCancel: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var dots by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Polling: revisar si alguien se unió
    LaunchedEffect(roomCode) {
        while (true) {
            delay(2000)
            try {
                val room = RoomRepository.getRoom(roomCode)
                if (room?.status == "playing") {
                    onGameStart()
                    break
                }
            } catch (_: Exception) {}
        }
    }

    // Animación de puntos
    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            dots = when (dots.length) {
                0 -> "."
                1 -> ".."
                2 -> "..."
                else -> ""
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    fontSize = 28.sp,
                    color = BlueNeon,
                    modifier = Modifier.clickable {
                        scope.launch {
                            try { RoomRepository.deleteRoom(roomCode) } catch (_: Exception) {}
                        }
                        onCancel()
                    }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "SALA CREADA",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Green,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(80.dp))

            Text(
                text = "🔄",
                fontSize = 60.sp,
                modifier = Modifier.rotate(rotation)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Escontrando rival$dots",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Comparte este código:",
                fontSize = 16.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .padding(horizontal = 40.dp, vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = roomCode,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = BlueNeon,
                    letterSpacing = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Envíalo por WhatsApp, Telegram, etc.",
                fontSize = 14.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}
