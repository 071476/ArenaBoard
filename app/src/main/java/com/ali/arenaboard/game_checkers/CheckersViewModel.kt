package com.ali.arenaboard.game_checkers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class Position(val row: Int, val col: Int)

data class Move(val from: Position, val to: Position, val captures: List<Position> = emptyList())

enum class CellType { EMPTY, BLACK, WHITE, BLACK_KING, WHITE_KING }

enum class GameRules { AMERICAN, INTERNATIONAL }

enum class AiPhase { THINKING, MOVING, NONE }

class CheckersViewModel : ViewModel() {

    var rules by mutableStateOf(GameRules.AMERICAN)
        private set

    var board by mutableStateOf(createInitialBoard())
        private set

    var selectedCell by mutableStateOf<Position?>(null)
        private set

    var validMoves by mutableStateOf<List<Position>>(emptyList())
        private set

    var currentPlayer by mutableStateOf(CellType.BLACK)
        private set

    var winner by mutableStateOf<String?>(null)
        private set

    var scorePlayer by mutableIntStateOf(0)
        private set

    var scoreApp by mutableIntStateOf(0)
        private set

    // Animación de la IA
    var aiPhase by mutableStateOf(AiPhase.NONE)
        private set
    var aiFromCell by mutableStateOf<Position?>(null)
        private set
    var aiToCell by mutableStateOf<Position?>(null)
        private set
    var aiCaptureTarget by mutableStateOf<Position?>(null)
        private set
    private var pendingAiMove: Move? = null

    fun aplicarReglas(newRules: GameRules) {
        rules = newRules
        resetGame()
    }

    fun hasForcedCaptures(): Boolean {
        if (currentPlayer != CellType.BLACK || winner != null) return false
        return getAllCapturesFor(CellType.BLACK).isNotEmpty()
    }

    fun pieceHasCapture(pos: Position): Boolean {
        val piece = board[pos.row][pos.col]
        if (piece != CellType.BLACK && piece != CellType.BLACK_KING) return false
        return getCapturesFrom(pos).isNotEmpty()
    }

    fun getMovablePieces(): List<Position> {
        if (currentPlayer != CellType.BLACK || winner != null) return emptyList()
        val movable = mutableListOf<Position>()
        for (r in 0..7) {
            for (c in 0..7) {
                val cell = board[r][c]
                if (cell == CellType.BLACK || cell == CellType.BLACK_KING) {
                    if (getMovesFor(Position(r, c)).isNotEmpty()) {
                        movable.add(Position(r, c))
                    }
                }
            }
        }
        return movable
    }

    private fun createInitialBoard(): List<List<CellType>> {
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

    fun onCellClick(row: Int, col: Int) {
        if (winner != null) return
        if (currentPlayer != CellType.BLACK) return
        if (aiPhase != AiPhase.NONE) return

        val pos = Position(row, col)
        val cell = board[row][col]

        if (selectedCell != null && pos in validMoves) {
            executePlayerMove(selectedCell!!, pos)
            return
        }

        if (cell == CellType.BLACK || cell == CellType.BLACK_KING) {
            selectedCell = pos
            validMoves = getMovesFor(pos).map { it.to }
        }
    }

    private fun executePlayerMove(from: Position, to: Position) {
        val move = getMovesFor(from).find { it.to == to } ?: return

        val newBoard = board.map { it.toMutableList() }.toMutableList()
        val piece = newBoard[from.row][from.col]
        newBoard[from.row][from.col] = CellType.EMPTY

        for (cap in move.captures) {
            newBoard[cap.row][cap.col] = CellType.EMPTY
        }

        val finalPiece = promoteIfNeeded(piece, to.row)
        newBoard[to.row][to.col] = finalPiece

        board = newBoard
        selectedCell = null
        validMoves = emptyList()

        if (checkGameEnd()) return

        currentPlayer = CellType.WHITE
        startAiAnimation()
    }

    private fun startAiAnimation() {
        val allMoves = getAllMovesFor(CellType.WHITE)
        if (allMoves.isEmpty()) {
            winner = "¡Ganaste! 🏆"
            scorePlayer++
            return
        }

        val captureMoves = allMoves.filter { it.captures.isNotEmpty() }
        val move = if (captureMoves.isNotEmpty()) {
            captureMoves.maxByOrNull { it.captures.size }!!
        } else {
            allMoves.random()
        }

        pendingAiMove = move

        // Fase 1: "Pensando" → mostrar la ficha que se va a mover
        aiPhase = AiPhase.THINKING
        aiFromCell = move.from
        aiToCell = null
        aiCaptureTarget = null

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            // Fase 2: "Moviendo" → mostrar destino y ficha a comer
            aiPhase = AiPhase.MOVING
            aiToCell = move.to
            if (move.captures.isNotEmpty()) {
                aiCaptureTarget = move.captures.first()
            }

            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                // Fase 3: Ejecutar el movimiento
                executeAiMove()
            }, 700)
        }, 800)
    }

    private fun executeAiMove() {
        val move = pendingAiMove ?: return

        val newBoard = board.map { it.toMutableList() }.toMutableList()
        val piece = newBoard[move.from.row][move.from.col]
        newBoard[move.from.row][move.from.col] = CellType.EMPTY

        for (cap in move.captures) {
            newBoard[cap.row][cap.col] = CellType.EMPTY
        }

        val finalPiece = promoteIfNeeded(piece, move.to.row)
        newBoard[move.to.row][move.to.col] = finalPiece

        board = newBoard

        // Limpiar animación
        aiPhase = AiPhase.NONE
        aiFromCell = null
        aiToCell = null
        aiCaptureTarget = null
        pendingAiMove = null

        if (checkGameEnd()) return
        currentPlayer = CellType.BLACK
    }

    private fun checkGameEnd(): Boolean {
        val blackPieces = board.flatten().count { it == CellType.BLACK || it == CellType.BLACK_KING }
        val whitePieces = board.flatten().count { it == CellType.WHITE || it == CellType.WHITE_KING }

        if (whitePieces == 0) {
            winner = "¡Ganaste! 🏆"
            scorePlayer++
            return true
        }
        if (blackPieces == 0) {
            winner = "La App ganó 🤖"
            scoreApp++
            return true
        }
        if (currentPlayer == CellType.BLACK && getAllMovesFor(CellType.BLACK).isEmpty()) {
            winner = "La App ganó 🤖"
            scoreApp++
            return true
        }
        if (currentPlayer == CellType.WHITE && getAllMovesFor(CellType.WHITE).isEmpty()) {
            winner = "¡Ganaste! 🏆"
            scorePlayer++
            return true
        }
        return false
    }

    private fun getMovesFor(pos: Position): List<Move> {
        val piece = board[pos.row][pos.col]
        val allCaptures = getAllCapturesFor(getPlayerOf(piece))
        if (allCaptures.isNotEmpty()) {
            return getCapturesFrom(pos)
        }
        return getSimpleMovesFrom(pos)
    }

    private fun getPlayerOf(piece: CellType): CellType {
        return when (piece) {
            CellType.BLACK, CellType.BLACK_KING -> CellType.BLACK
            CellType.WHITE, CellType.WHITE_KING -> CellType.WHITE
            else -> CellType.EMPTY
        }
    }

    private fun getSimpleMovesFrom(pos: Position): List<Move> {
        val piece = board[pos.row][pos.col]
        val directions = getDirections(piece)
        val moves = mutableListOf<Move>()
        for (dir in directions) {
            if (isQueen(piece) && rules == GameRules.INTERNATIONAL) {
                var r = pos.row + dir.first
                var c = pos.col + dir.second
                while (r in 0..7 && c in 0..7) {
                    if (board[r][c] == CellType.EMPTY) {
                        moves.add(Move(pos, Position(r, c)))
                    } else {
                        break
                    }
                    r += dir.first
                    c += dir.second
                }
            } else {
                val r = pos.row + dir.first
                val c = pos.col + dir.second
                if (r in 0..7 && c in 0..7 && board[r][c] == CellType.EMPTY) {
                    moves.add(Move(pos, Position(r, c)))
                }
            }
        }
        return moves
    }

    private fun getCapturesFrom(pos: Position, boardState: List<List<CellType>> = board): List<Move> {
        val piece = boardState[pos.row][pos.col]
        val directions = getDirections(piece)
        val captures = mutableListOf<Move>()
        for (dir in directions) {
            if (isQueen(piece) && rules == GameRules.INTERNATIONAL) {
                var r = pos.row + dir.first
                var c = pos.col + dir.second
                var foundEnemy = false
                var enemyPos: Position? = null
                while (r in 0..7 && c in 0..7) {
                    val cell = boardState[r][c]
                    if (!foundEnemy) {
                        if (cell != CellType.EMPTY) {
                            if (isOpponent(piece, cell)) {
                                foundEnemy = true
                                enemyPos = Position(r, c)
                            } else {
                                break
                            }
                        }
                    } else {
                        if (cell == CellType.EMPTY) {
                            captures.add(Move(pos, Position(r, c), listOf(enemyPos!!)))
                        } else {
                            break
                        }
                    }
                    r += dir.first
                    c += dir.second
                }
            } else {
                val midR = pos.row + dir.first
                val midC = pos.col + dir.second
                val endR = pos.row + dir.first * 2
                val endC = pos.col + dir.second * 2
                if (endR in 0..7 && endC in 0..7) {
                    val midPiece = boardState[midR][midC]
                    val endCell = boardState[endR][endC]
                    if (midPiece != CellType.EMPTY && isOpponent(piece, midPiece) && endCell == CellType.EMPTY) {
                        captures.add(Move(pos, Position(endR, endC), listOf(Position(midR, midC))))
                    }
                }
            }
        }
        return captures
    }

    private fun isQueen(piece: CellType): Boolean {
        return piece == CellType.BLACK_KING || piece == CellType.WHITE_KING
    }

    private fun getDirections(piece: CellType): List<Pair<Int, Int>> {
        return when (piece) {
            CellType.BLACK -> listOf(-1 to -1, -1 to 1)
            CellType.WHITE -> listOf(1 to -1, 1 to 1)
            CellType.BLACK_KING, CellType.WHITE_KING -> listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1)
            else -> emptyList()
        }
    }

    private fun isOpponent(piece: CellType, other: CellType): Boolean {
        val blackPieces = setOf(CellType.BLACK, CellType.BLACK_KING)
        val whitePieces = setOf(CellType.WHITE, CellType.WHITE_KING)
        return (piece in blackPieces && other in whitePieces) || (piece in whitePieces && other in blackPieces)
    }

    private fun getAllCapturesFor(player: CellType): List<Move> {
        val pieces = mutableListOf<Position>()
        for (r in 0..7) {
            for (c in 0..7) {
                val cell = board[r][c]
                if (player == CellType.BLACK && (cell == CellType.BLACK || cell == CellType.BLACK_KING)) {
                    pieces.add(Position(r, c))
                }
                if (player == CellType.WHITE && (cell == CellType.WHITE || cell == CellType.WHITE_KING)) {
                    pieces.add(Position(r, c))
                }
            }
        }
        return pieces.flatMap { getCapturesFrom(it) }
    }

    private fun getAllMovesFor(player: CellType): List<Move> {
        val allCaptures = getAllCapturesFor(player)
        if (allCaptures.isNotEmpty()) return allCaptures
        val pieces = mutableListOf<Position>()
        for (r in 0..7) {
            for (c in 0..7) {
                val cell = board[r][c]
                if (player == CellType.BLACK && (cell == CellType.BLACK || cell == CellType.BLACK_KING)) {
                    pieces.add(Position(r, c))
                }
                if (player == CellType.WHITE && (cell == CellType.WHITE || cell == CellType.WHITE_KING)) {
                    pieces.add(Position(r, c))
                }
            }
        }
        return pieces.flatMap { getSimpleMovesFrom(it) }
    }

    private fun promoteIfNeeded(piece: CellType, row: Int): CellType {
        return when {
            piece == CellType.BLACK && row == 0 -> CellType.BLACK_KING
            piece == CellType.WHITE && row == 7 -> CellType.WHITE_KING
            else -> piece
        }
    }

    fun resetGame() {
        board = createInitialBoard()
        selectedCell = null
        validMoves = emptyList()
        currentPlayer = CellType.BLACK
        winner = null
        aiPhase = AiPhase.NONE
        aiFromCell = null
        aiToCell = null
        aiCaptureTarget = null
        pendingAiMove = null
    }
}
