package com.ali.arenaboard.game_tictactoe

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ali.arenaboard.core.network.RoomRepository
import com.ali.arenaboard.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

@Composable
fun TicTacToeOnlineScreen(
    roomCode: String,
    playerId: String,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var board by remember { mutableStateOf(List(9) { "" }) }
    var mySymbol by remember { mutableStateOf("") }
    var currentTurn by remember { mutableStateOf("X") }
    var winner by remember { mutableStateOf<String?>(null) }
    var scoreMe by remember { mutableStateOf(0) }
    var scoreOpponent by remember { mutableStateOf(0) }
    var opponentName by remember { mutableStateOf("Rival") }

    LaunchedEffect(roomCode) {
        val room = RoomRepository.getRoom(roomCode)
        if (room != null) {
            mySymbol = if (room.player_x == playerId) "X" else "O"
            opponentName = if (mySymbol == "X") "Jugador O" else "Jugador X"
        }
    }

    LaunchedEffect(roomCode) {
        while (true) {
            delay(1500)
            try {
                val room = RoomRepository.getRoom(roomCode) ?: continue
                val parsed = Json.decodeFromString<List<String>>(room.board)
                board = parsed
                currentTurn = room.current_turn
                if (room.winner != null && winner == null) {
                    winner = room.winner
                    if (room.winner == mySymbol) scoreMe++ else if (room.winner != "Empate") scoreOpponent++
                }
            } catch (_: Exception) {}
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
                    modifier = Modifier.clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "GATO EN LÍNEA",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = BlueNeon,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Sala: $roomCode",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OnlineScoreCard(label = "TÚ ($mySymbol)", score = scoreMe, color = BlueNeon, symbol = mySymbol)
                Text(
                    text = "VS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = TextMuted,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                OnlineScoreCard(label = opponentName, score = scoreOpponent, color = PinkNeon, symbol = if (mySymbol == "X") "O" else "X")
            }

            Spacer(modifier = Modifier.height(32.dp))

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
                            OnlineCell(
                                value = board[index],
                                onClick = {
                                    if (board[index].isEmpty() && currentTurn == mySymbol && winner == null) {
                                        scope.launch {
                                            val newBoard = board.toMutableList()
                                            newBoard[index] = mySymbol
                                            board = newBoard
                                            val nextTurn = if (mySymbol == "X") "O" else "X"
                                            val win = checkOnlineWinner(newBoard)
                                            currentTurn = nextTurn
                                            RoomRepository.updateBoard(
                                                code = roomCode,
                                                board = Json.encodeToString(newBoard),
                                                turn = nextTurn,
                                                winner = win
                                            )
                                            if (win != null) {
                                                winner = win
                                                if (win == mySymbol) scoreMe++ else if (win != "Empate") scoreOpponent++
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (winner != null) {
                val message = when (winner) {
                    "Empate" -> "¡Empate!"
                    mySymbol -> "¡Ganaste! 🏆"
                    else -> "Perdiste 😢"
                }
                val color = when (winner) {
                    "Empate" -> TextMuted
                    mySymbol -> Gold
                    else -> PinkNeon
                }
                Text(
                    text = message,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = color
                )
            } else {
                val isMyTurn = currentTurn == mySymbol
                val statusColor = if (isMyTurn) Green else PinkNeon
                val statusText = if (isMyTurn) "🟢 Tu turno" else "🔴 Turno del rival"
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
fun OnlineScoreCard(label: String, score: Int, color: Color, symbol: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = symbol, fontSize = 24.sp)
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextMuted)
        Text(text = "$score", fontSize = 36.sp, fontWeight = FontWeight.Black, color = color)
    }
}

@Composable
fun OnlineCell(value: String, onClick: () -> Unit) {
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

private fun checkOnlineWinner(board: List<String>): String? {
    val patterns = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
        listOf(0, 4, 8), listOf(2, 4, 6)
    )
    for (p in patterns) {
        if (board[p[0]].isNotEmpty() && board[p[0]] == board[p[1]] && board[p[1]] == board[p[2]]) {
            return board[p[0]]
        }
    }
    if (board.all { it.isNotEmpty() }) return "Empate"
    return null
}
