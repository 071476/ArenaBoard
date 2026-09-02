package com.ali.arenaboard.game_tictactoe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class TicTacToeViewModel : ViewModel() {

    var board by mutableStateOf(List(9) { "" })
        private set

    var currentPlayer by mutableStateOf("X")
        private set

    var winner by mutableStateOf<String?>(null)
        private set

    var scoreX by mutableIntStateOf(0)
        private set

    var scoreO by mutableIntStateOf(0)
        private set

    var level by mutableIntStateOf(1)
        private set

    var consecutiveWins by mutableIntStateOf(0)
        private set

    var gamesPlayed by mutableIntStateOf(0)
        private set

    var showAdOption by mutableStateOf(false)
        private set

    private val winPatterns = listOf(
        listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
        listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
        listOf(0, 4, 8), listOf(2, 4, 6)
    )

    fun onCellClick(index: Int) {
        if (board[index].isNotEmpty() || winner != null) return

        val newBoard = board.toMutableList()
        newBoard[index] = currentPlayer
        board = newBoard

        if (checkWinner(currentPlayer)) {
            winner = currentPlayer
            gamesPlayed++
            if (currentPlayer == "X") {
                scoreX++
                consecutiveWins++
                if (consecutiveWins >= 2 && level < 3) {
                    level++
                    consecutiveWins = 0
                }
            } else {
                scoreO++
                consecutiveWins = 0
            }
            showAdOption = gamesPlayed % 3 == 0
        } else if (board.all { it.isNotEmpty() }) {
            winner = "Empate"
            gamesPlayed++
            showAdOption = gamesPlayed % 3 == 0
        } else {
            currentPlayer = "O"
            aiMove()
        }
    }

    private fun aiMove() {
        val emptyCells = board.mapIndexedNotNull { index, value ->
            if (value.isEmpty()) index else null
        }
        if (emptyCells.isEmpty() || winner != null) return

        val move = when (level) {
            1 -> aiLevel1(emptyCells)
            2 -> aiLevel2(emptyCells)
            else -> aiLevel3()
        }

        val newBoard = board.toMutableList()
        newBoard[move] = "O"
        board = newBoard

        if (checkWinner("O")) {
            winner = "O"
            scoreO++
            consecutiveWins = 0
            gamesPlayed++
            showAdOption = gamesPlayed % 3 == 0
        } else if (board.all { it.isNotEmpty() }) {
            winner = "Empate"
            gamesPlayed++
            showAdOption = gamesPlayed % 3 == 0
        } else {
            currentPlayer = "X"
        }
    }

    private fun aiLevel1(emptyCells: List<Int>): Int {
        return emptyCells.random()
    }

    private fun aiLevel2(emptyCells: List<Int>): Int {
        val winMove = findWinningMove("O")
        if (winMove != null) return winMove

        val blockMove = findWinningMove("X")
        if (blockMove != null) return blockMove

        if (board[4].isEmpty()) return 4

        val corners = listOf(0, 2, 6, 8).filter { board[it].isEmpty() }
        if (corners.isNotEmpty()) return corners.random()

        return emptyCells.random()
    }

    private fun aiLevel3(): Int {
        var bestScore = Int.MIN_VALUE
        var bestMove = -1

        for (i in board.indices) {
            if (board[i].isEmpty()) {
                val newBoard = board.toMutableList()
                newBoard[i] = "O"
                val score = minimax(newBoard, false)
                if (score > bestScore) {
                    bestScore = score
                    bestMove = i
                }
            }
        }
        return bestMove
    }

    private fun minimax(boardState: List<String>, isMaximizing: Boolean): Int {
        if (checkWinnerOnBoard(boardState, "O")) return 10
        if (checkWinnerOnBoard(boardState, "X")) return -10
        if (boardState.all { it.isNotEmpty() }) return 0

        if (isMaximizing) {
            var best = Int.MIN_VALUE
            for (i in boardState.indices) {
                if (boardState[i].isEmpty()) {
                    val newBoard = boardState.toMutableList()
                    newBoard[i] = "O"
                    best = maxOf(best, minimax(newBoard, false))
                }
            }
            return best
        } else {
            var best = Int.MAX_VALUE
            for (i in boardState.indices) {
                if (boardState[i].isEmpty()) {
                    val newBoard = boardState.toMutableList()
                    newBoard[i] = "X"
                    best = minOf(best, minimax(newBoard, true))
                }
            }
            return best
        }
    }

    private fun findWinningMove(player: String): Int? {
        for (pattern in winPatterns) {
            val values = pattern.map { board[it] }
            val playerCount = values.count { it == player }
            val emptyCount = values.count { it.isEmpty() }
            if (playerCount == 2 && emptyCount == 1) {
                return pattern[values.indexOf("")]
            }
        }
        return null
    }

    private fun checkWinner(player: String): Boolean {
        return winPatterns.any { pattern ->
            pattern.all { board[it] == player }
        }
    }

    private fun checkWinnerOnBoard(boardState: List<String>, player: String): Boolean {
        return winPatterns.any { pattern ->
            pattern.all { boardState[it] == player }
        }
    }

    fun resetGame() {
        board = List(9) { "" }
        currentPlayer = "X"
        winner = null
        showAdOption = false
    }
}
