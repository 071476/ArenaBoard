package com.ali.arenaboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                onTicTacToe = { navController.navigate("tictactoe_mode") },
                onCheckers = { }
            )
        }

        composable("tictactoe_mode") {
            ModeSelectionScreen(
                gameName = "GATO",
                onBack = { navController.popBackStack() },
                onVsApp = { navController.navigate("tictactoe_game") },
                onOnline = { }
            )
        }

        composable("tictactoe_game") {
            TicTacToeScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
