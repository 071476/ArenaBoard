package com.ali.arenaboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ali.arenaboard.ui.theme.ArenaBoardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArenaBoardTheme {
                HomeScreen(
                    onTicTacToe = { },
                    onCheckers = { }
                )
            }
        }
    }
}
