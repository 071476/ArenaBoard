package com.ali.arenaboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ali.arenaboard.core.network.RoomRepository
import com.ali.arenaboard.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun OnlineLobbyScreen(
    gameType: String,
    onBack: () -> Unit,
    onRoomCreated: (String, String) -> Unit,
    onRoomJoined: (String, String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var joinCode by remember { mutableStateOf("") }
    var showJoin by remember { mutableStateOf(false) }

    val playerId = remember { "player_${System.currentTimeMillis()}" }

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
                    modifier = Modifier.clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "$gameType EN LÍNEA",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = White,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            if (!showJoin) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.horizontalGradient(listOf(androidx.compose.ui.graphics.Color(0xFF003D1A), androidx.compose.ui.graphics.Color(0xFF006B2E))))
                        .clickable(enabled = !isLoading) {
                            scope.launch {
                                isLoading = true
                                errorMessage = null
                                try {
                                    val code = RoomRepository.createRoom(gameType, playerId)
                                    onRoomCreated(code, playerId)
                                } catch (e: Exception) {
                                    errorMessage = "Error: ${e.message}"
                                }
                                isLoading = false
                            }
                        }
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔑", fontSize = 44.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isLoading) "Creando sala..." else "Crear sala",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Green,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "Invita a un amigo",
                            fontSize = 14.sp,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.horizontalGradient(listOf(androidx.compose.ui.graphics.Color(0xFF3D0033), androidx.compose.ui.graphics.Color(0xFF7A0066))))
                        .clickable { showJoin = true }
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🚪", fontSize = 44.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Unirse a sala",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = PinkNeon,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "Escribe el código de tu amigo",
                            fontSize = 14.sp,
                            color = TextMuted
                        )
                    }
                }
            } else {
                Text(
                    text = "Escribe el código",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Surface)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicTextField(
                        value = joinCode,
                        onValueChange = { joinCode = it.uppercase().take(5) },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = BlueNeon,
                            letterSpacing = 12.sp,
                            textAlign = TextAlign.Center
                        ),
                        cursorBrush = SolidColor(BlueNeon),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.Center) {
                                if (joinCode.isEmpty()) {
                                    Text(
                                        text = "A7X9K",
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Black,
                                        color = TextMuted.copy(alpha = 0.3f),
                                        letterSpacing = 12.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (joinCode.length == 5) PinkNeon else TextMuted)
                        .clickable(enabled = joinCode.length == 5 && !isLoading) {
                            scope.launch {
                                isLoading = true
                                errorMessage = null
                                try {
                                    val room = RoomRepository.joinRoom(joinCode, playerId)
                                    if (room != null) {
                                        onRoomJoined(joinCode, playerId)
                                    } else {
                                        errorMessage = "Sala no encontrada"
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Error: ${e.message}"
                                }
                                isLoading = false
                            }
                        }
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isLoading) "Buscando..." else "Unirse",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Background
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "← Volver",
                    fontSize = 16.sp,
                    color = BlueNeon,
                    modifier = Modifier.clickable { showJoin = false }
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = errorMessage!!,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PinkNeon
                )
            }
        }
    }
}
