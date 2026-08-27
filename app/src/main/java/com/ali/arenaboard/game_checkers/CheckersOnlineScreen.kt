package com.ali.arenaboard.game_checkers

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
fun CheckersOnlineScreen(
    roomCode: String,
    playerId: String,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var board by remember { mutableStateOf(createInitialOnlineBoard()) }
    var myColor by remember { mutableStateOf("") }
    var currentTurn by remember { mutableStateOf("BLACK") }
    var winner by remember { mutableStateOf<String?>(null) }
    var selectedCell by remember { mutableStateOf<Position?>(null) }
    var validMoves by remember { mutableStateOf<List<Position>>(emptyList()) }
    var scoreMe by remember { mutableStateOf(0) }
    var scoreOpponent by remember { mutableStateOf(0) }

    LaunchedEffect(roomCode) {
        val room = RoomRepository.getRoom(roomCode)
        if (room != null) {
            myColor = if (room.player_x == playerId) "BLACK" else "WHITE"
        }
    }

    LaunchedEffect(roomCode) {
        while (true) {
            delay(1500)
            try {
                val room = RoomRepository.getRoom(roomCode) ?: continue
                val parsed = Json.decodeFromString<List<List<String>>>(room.board)
                board = parsed.map { row ->
                    row.map { cell ->
                        when (cell) {
                            "BLACK" -> CellType.BLACK
                            "WHITE" -> CellType.WHITE
                            "BLACK_KING" -> CellType.BLACK_KING
                            "WHITE_KING" -> CellType.WHITE_KING
                            else -> CellType.EMPTY
                        }
                    }
                }
                currentTurn = room.current_player ?: "BLACK"
                if (room.winner != null && winner == null) {
                    winner = room.winner
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
                Column {
                    Text(
                        text = "DAMAS EN LÍNEA",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = PinkNeon,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Sala: $roomCode",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = if (myColor == "BLACK") "⚫" else "⚪", fontSize = 24.sp)
                    Text(text = "TÚ", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text(text = "$scoreMe", fontSize = 32.sp, fontWeight = FontWeight.Black, color = BlueNeon)
                }
                Text(
                    text = "VS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = TextMuted,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = if (myColor == "BLACK") "⚪" else "⚫", fontSize = 24.sp)
                    Text(text = "RIVAL", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text(text = "$scoreOpponent", fontSize = 32.sp, fontWeight = FontWeight.Black, color = PinkNeon)
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
                                isSelected -> Color(0xFF00E5FF).copy(alpha = 0.3f)
                                isValidMove -> Green.copy(alpha = 0.2f)
                                isDark -> Color(0xFF1A1F3D)
                                else -> Color(0xFF0D1130)
                            }

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(bgColor)
                                    .then(
                                        if (isValidMove) Modifier.border(2.dp, Green.copy(alpha = 0.5f))
                                        else Modifier
                                    )
                                    .clickable {
                                        if (currentTurn != myColor || winner != null) return@clickable
                                        val pos = Position(row, col)

                                        if (cell == CellType.BLACK || cell == CellType.BLACK_KING ||
                                            cell == CellType.WHITE || cell == CellType.WHITE_KING) {
                                            val isMyPiece = (myColor == "BLACK" && (cell == CellType.BLACK || cell == CellType.BLACK_KING)) ||
                                                    (myColor == "WHITE" && (cell == CellType.WHITE || cell == CellType.WHITE_KING))
                                            if (isMyPiece) {
                                                selectedCell = pos
                                                validMoves = getOnlineValidMoves(pos, board).map { it.to }
                                            }
                                        } else if (selectedCell != null && pos in validMoves) {
                                            scope.launch {
                                                val result = executeOnlineMove(selectedCell!!, pos, board)
                                                board = result.first
                                                val nextTurn = if (currentTurn == "BLACK") "WHITE" else "BLACK"
                                                currentTurn = nextTurn
                                                selectedCell = null
                                                validMoves = emptyList()

                                                val boardJson = Json.encodeToString(
                                                    board.map { row -> row.map { it.name } }
                                                )
                                                val win = checkOnlineCheckersWinner(board)
                                                if (win != null) winner = win

                                                RoomRepository.updateBoard(
                                                    code = roomCode,
                                                    board = boardJson,
                                                    turn = nextTurn,
                                                    winner = win
                                                )
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                when (cell) {
                                    CellType.BLACK -> OnlinePiece(Color(0xFF00E5FF), false)
                                    CellType.WHITE -> OnlinePiece(Color(0xFFFF1493), false)
                                    CellType.BLACK_KING -> OnlinePiece(Color(0xFF00E5FF), true)
                                    CellType.WHITE_KING -> OnlinePiece(Color(0xFFFF1493), true)
                                    else -> {}
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (winner != null) {
                val isWin = (winner == myColor)
                val message = if (isWin) "¡Ganaste! 🏆" else "Perdiste 😢"
                val color = if (isWin) Gold else PinkNeon
                Text(text = message, fontSize = 26.sp, fontWeight = FontWeight.Black, color = color)
            } else {
                val isMyTurn = currentTurn == myColor
                val statusColor = if (isMyTurn) Green else PinkNeon
                val statusText = if (isMyTurn) "🟢 Tu turno" else "🔴 Turno del rival"
                Text(text = statusText, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = statusColor)
            }
        }
    }
}

@Composable
fun OnlinePiece(color: Color, isKing: Boolean) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        if (isKing) {
            Text(text = "♛", fontSize = 16.sp, color = Background, fontWeight = FontWeight.Black)
        }
    }
}

private fun createInitialOnlineBoard(): List<List<CellType>> {
    val board = MutableList(8) { MutableList(8) { CellType.EMPTY } }
    for (row in 0..2) {
        for (col in 0..7) {
            if ((row + col) % 2 == 1) board[row][col] = CellType.WHITE
        }
    }
    for (row in 5..7) {
        for (col in 0..7) {
            if ((row + col) % 2 == 1) board[row][col] = CellType.BLACK
        }
    }
    return board
}

private fun getOnlineValidMoves(pos: Position, board: List<List<CellType>>): List<Move> {
    val captures = getOnlineCaptures(pos, board)
    if (captures.isNotEmpty()) return captures
    return getOnlineSimpleMoves(pos, board)
}

private fun getOnlineSimpleMoves(pos: Position, board: List<List<CellType>>): List<Move> {
    val piece = board[pos.row][pos.col]
    val directions = getOnlineDirections(piece)
    val moves = mutableListOf<Move>()
    for (dir in directions) {
        val nr = pos.row + dir.first
        val nc = pos.col + dir.second
        if (nr in 0..7 && nc in 0..7 && board[nr][nc] == CellType.EMPTY) {
            moves.add(Move(pos, Position(nr, nc)))
        }
    }
    return moves
}

private fun getOnlineCaptures(pos: Position, board: List<List<CellType>>): List<Move> {
    val piece = board[pos.row][pos.col]
    val directions = getOnlineDirections(piece)
    val captures = mutableListOf<Move>()
    for (dir in directions) {
        val mr = pos.row + dir.first
        val mc = pos.col + dir.second
        val er = pos.row + dir.first * 2
        val ec = pos.col + dir.second * 2
        if (er in 0..7 && ec in 0..7) {
            val mid = board[mr][mc]
            val end = board[er][ec]
            if (mid != CellType.EMPTY && isOnlineOpponent(piece, mid) && end == CellType.EMPTY) {
                captures.add(Move(pos, Position(er, ec), listOf(Position(mr, mc))))
            }
        }
    }
    return captures
}

private fun getOnlineDirections(piece: CellType): List<Pair<Int, Int>> {
    return when (piece) {
        CellType.BLACK -> listOf(-1 to -1, -1 to 1)
        CellType.WHITE -> listOf(1 to -1, 1 to 1)
        CellType.BLACK_KING, CellType.WHITE_KING -> listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1)
        else -> emptyList()
    }
}

private fun isOnlineOpponent(piece: CellType, other: CellType): Boolean {
    val black = setOf(CellType.BLACK, CellType.BLACK_KING)
    val white = setOf(CellType.WHITE, CellType.WHITE_KING)
    return (piece in black && other in white) || (piece in white && other in black)
}

private fun executeOnlineMove(from: Position, to: Position, board: List<List<CellType>>): Pair<List<List<CellType>>, Boolean> {
    val newBoard = board.map { it.toMutableList() }.toMutableList()
    val piece = newBoard[from.row][from.col]
    newBoard[from.row][from.col] = CellType.EMPTY

    val dr = if (to.row > from.row) 1 else -1
    val dc = if (to.col > from.col) 1 else -1
    val dist = kotlin.math.abs(to.row - from.row)

    if (dist == 2) {
        newBoard[from.row + dr][from.col + dc] = CellType.EMPTY
    }

    val finalPiece = when {
        piece == CellType.BLACK && to.row == 0 -> CellType.BLACK_KING
        piece == CellType.WHITE && to.row == 7 -> CellType.WHITE_KING
        else -> piece
    }
    newBoard[to.row][to.col] = finalPiece

    return Pair(newBoard, dist == 2)
}

private fun checkOnlineCheckersWinner(board: List<List<CellType>>): String? {
    val black = board.flatten().count { it == CellType.BLACK || it == CellType.BLACK_KING }
    val white = board.flatten().count { it == CellType.WHITE || it == CellType.WHITE_KING }
    if (white == 0) return "BLACK"
    if (black == 0) return "WHITE"
    return null
}
