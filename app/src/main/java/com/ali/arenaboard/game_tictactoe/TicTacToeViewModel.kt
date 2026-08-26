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
            if (currentPlayer == "X") scoreX++ else scoreO++
        } else if (board.all { it.isNotEmpty() }) {
            winner = "Empate"
        } else {
            currentPlayer = if (currentPlayer == "X") "O" else "X"
            if (currentPlayer == "O") {
                aiMove()
            }
        }
    }

    private fun aiMove() {
        val emptyCells = board.mapIndexedNotNull { index, value ->
            if (value.isEmpty()) index else null
        }
        if (emptyCells.isEmpty() || winner != null) return

        val move = emptyCells.random()
        val newBoard = board.toMutableList()
        newBoard[move] = "O"
        board = newBoard

        if (checkWinner("O")) {
            winner = "O"
            scoreO++
        } else if (board.all { it.isNotEmpty() }) {
            winner = "Empate"
        } else {
            currentPlayer = "X"
        }
    }

    private fun checkWinner(player: String): Boolean {
        return winPatterns.any { pattern ->
            pattern.all { board[it] == player }
        }
    }

    fun resetGame() {
        board = List(9) { "" }
        currentPlayer = "X"
        winner = null
    }
}
