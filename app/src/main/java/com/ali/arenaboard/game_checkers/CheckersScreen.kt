package com.ali.arenaboard.game_checkers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ali.arenaboard.ui.theme.*

val DarkSquare = Color(0xFF1A1F3D)
val LightSquare = Color(0xFF0D1130)
val HighlightSquare = Color(0xFF00E5FF).copy(alpha = 0.3f)

@Composable
fun CheckersScreen(
    onBack: () -> Unit,
    viewModel: CheckersViewModel = viewModel()
) {
    val board = viewModel.board
    val selectedCell = viewModel.selectedCell
    val validMoves = viewModel.validMoves
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
                .padding(16.dp),
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
                    color = PinkNeon,
                    modifier = Modifier.clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "DAMAS",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = PinkNeon,
                    letterSpacing = 3.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "⚫", fontSize = 24.sp)
                    Text(text = "TÚ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text(
                        text = "${viewModel.scorePlayer}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = BlueNeon
                    )
                }
                Text(
                    text = "VS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = TextMuted,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "⚪", fontSize = 24.sp)
                    Text(text = "APP", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text(
                        text = "${viewModel.scoreApp}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = PinkNeon
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .shadow(16.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF050818))
                    .padding(4.dp)
            ) {
                for (row in 0..7) {
                    Row {
                        for (col in 0..7) {
                            val isDark = (row + col) % 2 == 1
                            val isSelected = selectedCell?.row == row && selectedCell?.col == col
                            val isValidMove = Position(row, col) in validMoves
                            val cell = board[row][col]

                            val bgColor = when {
                                isSelected -> HighlightSquare
                                isValidMove -> Green.copy(alpha = 0.2f)
                                isDark -> DarkSquare
                                else -> LightSquare
                            }

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(bgColor)
                                    .then(
                                        if (isValidMove) Modifier.border(2.dp, Green.copy(alpha = 0.5f))
                                        else Modifier
                                    )
                                    .clickable { viewModel.onCellClick(row, col) },
                                contentAlignment = Alignment.Center
                            ) {
                                when (cell) {
                                    CellType.BLACK -> Piece(Color(0xFF00E5FF), false)
                                    CellType.WHITE -> Piece(Color(0xFFFF1493), false)
                                    CellType.BLACK_KING -> Piece(Color(0xFF00E5FF), true)
                                    CellType.WHITE_KING -> Piece(Color(0xFFFF1493), true)
                                    else -> {}
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (winner != null) {
                val color = when {
                    winner!!.contains("Ganaste") -> Gold
                    else -> PinkNeon
                }
                Text(
                    text = winner!!,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = color
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(PinkNeon)
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
                val statusColor = if (currentPlayer == CellType.BLACK) Green else PinkNeon
                val statusText = if (currentPlayer == CellType.BLACK) "🟢 Tu turno" else "🔴 Turno de la App"
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
fun Piece(color: Color, isKing: Boolean) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        if (isKing) {
            Text(
                text = "♛",
                fontSize = 16.sp,
                color = Background,
                fontWeight = FontWeight.Black
            )
        }
    }
}
