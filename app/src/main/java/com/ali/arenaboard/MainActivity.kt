package com.ali.arenaboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ali.arenaboard.game_checkers.CheckersScreen
import com.ali.arenaboard.game_tictactoe.TicTacToeOnlineScreen
import com.ali.arenaboard.game_tictactoe.TicTacToeScreen
import com.ali.arenaboard.ui.theme.ArenaBoardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArenaBoardTheme {
                ArenaNavigation()
            }
        }
    }
}

@Composable
fun ArenaNavigation() {
    val navController = rememberNavController()
    var onlineRoomCode by remember { mutableStateOf("") }
    var onlinePlayerId by remember { mutableStateOf("") }

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                onTicTacToe = { navController.navigate("tictactoe_mode") },
                onCheckers = { navController.navigate("checkers_mode") }
            )
        }

        composable("tictactoe_mode") {
            ModeSelectionScreen(
                gameName = "GATO",
                onBack = { navController.popBackStack() },
                onVsApp = { navController.navigate("tictactoe_game") },
                onOnline = { navController.navigate("tictactoe_online_lobby") }
            )
        }

        composable("tictactoe_game") {
            TicTacToeScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("tictactoe_online_lobby") {
            OnlineLobbyScreen(
                gameType = "GATO",
                onBack = { navController.popBackStack() },
                onRoomCreated = { code, playerId ->
                    onlineRoomCode = code
                    onlinePlayerId = playerId
                    navController.navigate("tictactoe_waiting")
                },
                onRoomJoined = { code, playerId ->
                    onlineRoomCode = code
                    onlinePlayerId = playerId
                    navController.navigate("tictactoe_online_game")
                }
            )
        }

        composable("tictactoe_waiting") {
            WaitingRoomScreen(
                roomCode = onlineRoomCode,
                gameType = "GATO",
                playerId = onlinePlayerId,
                onGameStart = {
                    navController.navigate("tictactoe_online_game") {
                        popUpTo("tictactoe_waiting") { inclusive = true }
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }

        composable("tictactoe_online_game") {
            TicTacToeOnlineScreen(
                roomCode = onlineRoomCode,
                playerId = onlinePlayerId,
                onBack = { navController.popBackStack() }
            )
        }

        composable("checkers_mode") {
            ModeSelectionScreen(
                gameName = "DAMAS",
                onBack = { navController.popBackStack() },
                onVsApp = { navController.navigate("checkers_game") },
                onOnline = { }
            )
        }

        composable("checkers_game") {
            CheckersScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
