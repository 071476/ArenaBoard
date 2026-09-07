package com.ali.arenaboard.game_checkers

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ali.arenaboard.ui.theme.*

val DarkSquare = Color(0xFF1A1F3D)
val LightSquare = Color(0xFF0D1130)
val HighlightSquare = Color(0xFF00E5FF).copy(alpha = 0.3f)
val CaptureGlow = Color(0xFFFF1493)

@Composable
fun CheckersScreen(
    onBack: () -> Unit,
    rules: GameRules = GameRules.AMERICAN,
    viewModel: CheckersViewModel = viewModel()
) {
    LaunchedEffect(rules) {
        viewModel.aplicarReglas(rules)
    }

    val board = viewModel.board
    val selectedCell = viewModel.selectedCell
    val validMoves = viewModel.validMoves
    val currentPlayer = viewModel.currentPlayer
    val winner = viewModel.winner

    val blackPieces = board.flatten().count { it == CellType.BLACK || it == CellType.BLACK_KING }
    val whitePieces = board.flatten().count { it == CellType.WHITE || it == CellType.WHITE_KING }
    val movablePieces = viewModel.getMovablePieces()
    val mustCapture = viewModel.hasForcedCaptures()

    // Animación pulsante
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Datos de la animación de la IA
    val aiPhase = viewModel.aiPhase
    val aiFromCell = viewModel.aiFromCell
    val aiToCell = viewModel.aiToCell
    val aiCaptureTarget = viewModel.aiCaptureTarget

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

            Spacer(modifier = Modifier.height(8.dp))

            val rulesText = if (viewModel.rules == GameRules.AMERICAN) "🇺🇸 Americanas" else "🌍 Internacionales"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Surface)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = rulesText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (viewModel.rules == GameRules.AMERICAN) Green else PinkNeon
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "⚫", fontSize = 24.sp)
                    Text(text = "TÚ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text(text = "$blackPieces fichas", fontSize = 18.sp, fontWeight = FontWeight.Black, color = BlueNeon)
                    Text(text = "Ganadas: ${viewModel.scorePlayer}", fontSize = 12.sp, color = TextMuted)
                }
                Text(text = "VS", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextMuted, modifier = Modifier.align(Alignment.CenterVertically))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "⚪", fontSize = 24.sp)
                    Text(text = "APP", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text(text = "$whitePieces fichas", fontSize = 18.sp, fontWeight = FontWeight.Black, color = PinkNeon)
                    Text(text = "Ganadas: ${viewModel.scoreApp}", fontSize = 12.sp, color = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Indicador de estado
            when {
                aiPhase == AiPhase.THINKING -> {
                    Text(
                        text = "🤖 La App está pensando...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PinkNeon
                    )
                }
                aiPhase == AiPhase.MOVING -> {
                    if (aiCaptureTarget != null) {
                        Text(
                            text = "🤖 ¡La App va a comer!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF4444)
                        )
                    } else {
                        Text(
                            text = "🤖 La App se mueve...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PinkNeon
                        )
                    }
                }
                winner == null && currentPlayer == CellType.BLACK -> {
                    if (mustCapture && selectedCell == null) {
                        Text(
                            text = "⚡ ¡DEBES COMER! Toca la ficha que brilla ⚡",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CaptureGlow
                        )
                    } else if (selectedCell != null && mustCapture) {
                        Text(
                            text = "⚡ Toca la casilla ROJA para comer ⚡",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CaptureGlow
                        )
                    } else if (selectedCell == null) {
                        Text(
                            text = "Toca una ficha con borde brillante ✨",
                            fontSize = 13.sp,
                            color = Gold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tablero
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
                            val pos = Position(row, col)
                            val isSelected = selectedCell == pos
                            val isValidMove = pos in validMoves
                            val canMove = pos in movablePieces
                            val cell = board[row][col]

                            val hasCapture = canMove && mustCapture && viewModel.pieceHasCapture(pos)

                            // Animación de la IA
                            val isAiFrom = pos == aiFromCell
                            val isAiTo = pos == aiToCell
                            val isAiCapture = pos == aiCaptureTarget

                            val bgColor = when {
                                isAiFrom && aiPhase != AiPhase.NONE -> Color(0xFFFF1493).copy(alpha = pulseAlpha * 0.3f)
                                isAiTo && aiPhase == AiPhase.MOVING -> Color(0xFF00E676).copy(alpha = pulseAlpha * 0.3f)
                                isAiCapture && aiPhase == AiPhase.MOVING -> Color(0xFFFF4444).copy(alpha = pulseAlpha * 0.35f)
                                isSelected -> HighlightSquare
                                isValidMove && mustCapture -> CaptureGlow.copy(alpha = 0.25f)
                                isValidMove -> Color(0xFF00E676).copy(alpha = 0.2f)
                                isDark -> DarkSquare
                                else -> LightSquare
                            }

                            val borderModifier = when {
                                isAiFrom && aiPhase != AiPhase.NONE -> Modifier.border(
                                    3.dp,
                                    Color(0xFFFF1493).copy(alpha = pulseAlpha),
                                    RoundedCornerShape(4.dp)
                                )
                                isAiTo && aiPhase == AiPhase.MOVING -> Modifier.border(
                                    3.dp,
                                    Color(0xFF00E676).copy(alpha = pulseAlpha),
                                    RoundedCornerShape(4.dp)
                                )
                                isAiCapture && aiPhase == AiPhase.MOVING -> Modifier.border(
                                    3.dp,
                                    Color(0xFFFF4444).copy(alpha = pulseAlpha),
                                    RoundedCornerShape(4.dp)
                                )
                                isValidMove && mustCapture -> Modifier.border(
                                    3.dp,
                                    CaptureGlow.copy(alpha = pulseAlpha),
                                    RoundedCornerShape(4.dp)
                                )
                                isValidMove -> Modifier.border(2.dp, Green.copy(alpha = 0.5f))
                                hasCapture && selectedCell == null -> Modifier.border(
                                    3.dp,
                                    CaptureGlow.copy(alpha = pulseAlpha),
                                    RoundedCornerShape(4.dp)
                                )
                                canMove && selectedCell == null -> Modifier.border(2.dp, Gold.copy(alpha = 0.6f))
                                else -> Modifier
                            }

                            val glowModifier = if (hasCapture && selectedCell == null) {
                                Modifier.drawBehind {
                                    drawCircle(
                                        color = CaptureGlow.copy(alpha = pulseAlpha * 0.3f),
                                        radius = this.size.minDimension * pulseScale * 0.7f
                                    )
                                }
                            } else if (isAiCapture && aiPhase == AiPhase.MOVING) {
                                Modifier.drawBehind {
                                    drawCircle(
                                        color = Color(0xFFFF4444).copy(alpha = pulseAlpha * 0.4f),
                                        radius = this.size.minDimension * pulseScale * 0.8f
                                    )
                                }
                            } else Modifier

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(bgColor)
                                    .then(borderModifier)
                                    .clickable(enabled = aiPhase == AiPhase.NONE) { viewModel.onCellClick(row, col) },
                                contentAlignment = Alignment.Center
                            ) {
                                Box(modifier = glowModifier) {
                                    val shouldGlow = (isAiFrom && aiPhase != AiPhase.NONE) ||
                                            (hasCapture && selectedCell == null)
                                    when (cell) {
                                        CellType.BLACK -> Piece(Color(0xFF00E5FF), false, hasCapture && selectedCell == null, pulseAlpha)
                                        CellType.WHITE -> Piece(Color(0xFFFF1493), false, isAiFrom && aiPhase != AiPhase.NONE, pulseAlpha)
                                        CellType.BLACK_KING -> Piece(Color(0xFF00E5FF), true, hasCapture && selectedCell == null, pulseAlpha)
                                        CellType.WHITE_KING -> Piece(Color(0xFFFF1493), true, isAiFrom && aiPhase != AiPhase.NONE, pulseAlpha)
                                        else -> {}
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (winner != null) {
                val color = if (winner!!.contains("Ganaste")) Gold else PinkNeon
                Text(text = winner!!, fontSize = 26.sp, fontWeight = FontWeight.Black, color = color)
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(PinkNeon)
                        .clickable { viewModel.resetGame() }
                        .padding(horizontal = 32.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Revancha 🔄", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Background)
                }
            } else if (aiPhase == AiPhase.NONE) {
                val statusColor = if (currentPlayer == CellType.BLACK) Green else PinkNeon
                val statusText = if (currentPlayer == CellType.BLACK) "🟢 Tu turno" else "🔴 Turno de la App"
                Text(text = statusText, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = statusColor)
            }
        }
    }
}

@Composable
fun Piece(color: Color, isKing: Boolean, isGlowing: Boolean, pulseAlpha: Float) {
    val pieceColor = if (isGlowing) color.copy(alpha = 0.4f + pulseAlpha * 0.6f) else color

    Box(
        modifier = Modifier
            .size(30.dp)
            .shadow(if (isGlowing) 8.dp else 4.dp, CircleShape)
            .clip(CircleShape)
            .background(pieceColor),
        contentAlignment = Alignment.Center
    ) {
        if (isKing) {
            Text(
                text = "♛",
                fontSize = 16.sp,
                color = Background,
                fontWeight = FontWeight.Black,
                style = if (isGlowing) TextStyle(
                    shadow = Shadow(
                        color = color,
                        offset = Offset(0f, 0f),
                        blurRadius = 12f
                    )
                ) else TextStyle.Default
            )
        }
    }
}
