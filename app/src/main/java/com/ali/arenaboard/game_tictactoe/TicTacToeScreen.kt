package com.ali.arenaboard.game_tictactoe

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ali.arenaboard.ui.theme.*

@Composable
fun TicTacToeScreen(
    onBack: () -> Unit,
    viewModel: TicTacToeViewModel = viewModel()
) {
    val board = viewModel.board
    val currentPlayer = viewModel.currentPlayer
    val winner = viewModel.winner

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

            // Botón atrás
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
                    text = "GATO",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = BlueNeon,
                    letterSpacing = 3.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Marcador
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ScoreCard(label = "TÚ", score = viewModel.scoreX, color = BlueNeon, symbol = "X")
                Text(
                    text = "VS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = TextMuted,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                ScoreCard(label = "APP", score = viewModel.scoreO, color = PinkNeon, symbol = "O")
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Tablero
            Column(
                modifier = Modifier
                    .shadow(16.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Surface)
                    .padding(16.dp)
            ) {
                for (row in 0..2) {
                    Row {
                        for (col in 0..2) {
                            val index = row * 3 + col
                            Cell(
                                value = board[index],
                                onClick = { viewModel.onCellClick(index) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Estado
            if (winner != null) {
                val message = when (winner) {
                    "Empate" -> "¡Empate!"
                    "X" -> "¡Ganaste! 🏆"
                    else -> "La App ganó 🤖"
                }
                val color = when (winner) {
                    "Empate" -> TextMuted
                    "X" -> Gold
                    else -> PinkNeon
                }
                Text(
                    text = message,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = color
                )
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(BlueNeon)
                        .clickable { viewModel.resetGame() }
                        .padding(horizontal = 32.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Revancha 🔄",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Background
                    )
                }
            } else {
                val statusColor = if (currentPlayer == "X") Green else PinkNeon
                val statusText = if (currentPlayer == "X") "🟢 Tu turno" else "🔴 Turno de la App"
                Text(
                    text = statusText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
        }
    }
}

@Composable
fun ScoreCard(label: String, score: Int, color: Color, symbol: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = symbol,
            fontSize = 24.sp
        )
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
        )
        Text(
            text = "$score",
            fontSize = 36.sp,
            fontWeight = FontWeight.Black,
            color = color
        )
    }
}

@Composable
fun Cell(value: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .padding(4.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0D1130))
            .clickable(enabled = value.isEmpty()) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value,
            fontSize = 40.sp,
            fontWeight = FontWeight.Black,
            color = when (value) {
                "X" -> BlueNeon
                "O" -> PinkNeon
                else -> Color.Transparent
            }
        )
    }
}
