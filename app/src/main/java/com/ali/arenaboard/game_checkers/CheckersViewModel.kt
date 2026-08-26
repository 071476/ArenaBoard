package com.ali.arenaboard.game_checkers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class Position(val row: Int, val col: Int)

data class Move(val from: Position, val to: Position, val captures: List<Position> = emptyList())

enum class CellType { EMPTY, BLACK, WHITE, BLACK_KING, WHITE_KING }

class CheckersViewModel : ViewModel() {

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

    var mustCaptureFrom by mutableStateOf<Position?>(null)
        private set

    private fun createInitialBoard(): List<List<CellType>> {
        val board = MutableList(8) { MutableList(8) { CellType.EMPTY } }
        for (row in 0..2) {
            for (col in 0..7) {
                if ((row + col) % 2 == 1) {
                    board[row][col] = CellType.WHITE
                }
            }
        }
        for (row in 5..7) {
            for (col in 0..7) {
                if ((row + col) % 2 == 1) {
                    board[row][col] = CellType.BLACK
                }
            }
        }
        return board
    }

    fun onCellClick(row: Int, col: Int) {
        if (winner != null) return
        if (currentPlayer != CellType.BLACK) return

        val pos = Position(row, col)
        val cell = board[row][col]

        if (mustCaptureFrom != null && pos != mustCaptureFrom) {
            if (cell == CellType.BLACK || cell == CellType.BLACK_KING) {
                selectPiece(pos)
            }
            return
        }

        if (cell == CellType.BLACK || cell == CellType.BLACK_KING) {
            selectPiece(pos)
        } else if (selectedCell != null && pos in validMoves) {
            executeMove(selectedCell!!, pos)
        }
    }

    private fun selectPiece(pos: Position) {
        selectedCell = pos
        val moves = getValidMovesFor(pos)
        validMoves = moves.map { it.to }
    }

    private fun executeMove(from: Position, to: Position) {
        val allMoves = getValidMovesFor(from)
        val move = allMoves.find { it.to == to } ?: return

        val newBoard = board.map { it.toMutableList() }.toMutableList()
        val piece = newBoard[from.row][from.col]
        newBoard[from.row][from.col] = CellType.EMPTY

        for (cap in move.captures) {
            newBoard[cap.row][cap.col] = CellType.EMPTY
        }

        val finalPiece = when {
            piece == CellType.BLACK && to.row == 0 -> CellType.BLACK_KING
            piece == CellType.WHITE && to.row == 7 -> CellType.WHITE_KING
            else -> piece
        }
        newBoard[to.row][to.col] = finalPiece

        board = newBoard

        if (move.captures.isNotEmpty()) {
            val moreCaptures = getCapturesFrom(to)
            if (moreCaptures.isNotEmpty()) {
                selectedCell = to
                validMoves = moreCaptures.map { it.to }
                mustCaptureFrom = to
                return
            }
        }

        mustCaptureFrom = null
        selectedCell = null
        validMoves = emptyList()

        if (checkGameEnd()) return

        currentPlayer = CellType.WHITE
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            aiMove()
        }, 500)
    }

    private fun aiMove() {
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

        val newBoard = board.map { it.toMutableList() }.toMutableList()
        val piece = newBoard[move.from.row][move.from.col]
        newBoard[move.from.row][move.from.col] = CellType.EMPTY

        for (cap in move.captures) {
            newBoard[cap.row][cap.col] = CellType.EMPTY
        }

        val finalPiece = when {
            piece == CellType.WHITE && move.to.row == 7 -> CellType.WHITE_KING
            else -> piece
        }
        newBoard[move.to.row][move.to.col] = finalPiece

        var currentPos = move.to
        var remainingCaptures = getCapturesFrom(currentPos, newBoard)
        while (remainingCaptures.isNotEmpty()) {
            val nextCapture = remainingCaptures.maxByOrNull { it.captures.size }!!
            newBoard[currentPos.row][currentPos.col] = CellType.EMPTY
            for (cap in nextCapture.captures) {
                newBoard[cap.row][cap.col] = CellType.EMPTY
            }
            val promoted = if (finalPiece == CellType.WHITE && nextCapture.to.row == 7) CellType.WHITE_KING else finalPiece
            newBoard[nextCapture.to.row][nextCapture.to.col] = promoted
            currentPos = nextCapture.to
            remainingCaptures = getCapturesFrom(currentPos, newBoard)
        }

        board = newBoard

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

    private fun getValidMovesFor(pos: Position): List<Move> {
        val captures = getCapturesFrom(pos)
        if (captures.isNotEmpty()) return captures
        return getSimpleMovesFrom(pos)
    }

    private fun getSimpleMovesFrom(pos: Position): List<Move> {
        val piece = board[pos.row][pos.col]
        val directions = getDirections(piece)
        val moves = mutableListOf<Move>()
        for (dir in directions) {
            val newRow = pos.row + dir.first
            val newCol = pos.col + dir.second
            if (newRow in 0..7 && newCol in 0..7 && board[newRow][newCol] == CellType.EMPTY) {
                moves.add(Move(pos, Position(newRow, newCol)))
            }
        }
        return moves
    }

    private fun getCapturesFrom(pos: Position, boardState: List<List<CellType>> = board): List<Move> {
        val piece = boardState[pos.row][pos.col]
        val directions = getDirections(piece)
        val captures = mutableListOf<Move>()
        for (dir in directions) {
            val midRow = pos.row + dir.first
            val midCol = pos.col + dir.second
            val endRow = pos.row + dir.first * 2
            val endCol = pos.col + dir.second * 2
            if (endRow in 0..7 && endCol in 0..7) {
                val midPiece = boardState[midRow][midCol]
                val endCell = boardState[endRow][endCol]
                if (midPiece != CellType.EMPTY && isOpponent(piece, midPiece) && endCell == CellType.EMPTY) {
                    captures.add(Move(pos, Position(endRow, endCol), listOf(Position(midRow, midCol))))
                }
            }
        }
        return captures
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

    private fun getAllMovesFor(player: CellType): List<Move> {
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
        val allCaptures = pieces.flatMap { getCapturesFrom(it) }
        if (allCaptures.isNotEmpty()) return allCaptures
        return pieces.flatMap { getSimpleMovesFrom(it) }
    }

    fun resetGame() {
        board = createInitialBoard()
        selectedCell = null
        validMoves = emptyList()
        currentPlayer = CellType.BLACK
        winner = null
        mustCaptureFrom = null
    }
}
