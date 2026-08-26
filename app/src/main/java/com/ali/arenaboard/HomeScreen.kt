package com.ali.arenaboard

import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ali.arenaboard.ui.theme.*

@Composable
fun HomeScreen(
    onTicTacToe: () -> Unit,
    onCheckers: () -> Unit
) {
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
            Spacer(modifier = Modifier.height(60.dp))

            // Título
            Text(
                text = "ARENA BOARD",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = BlueNeon,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Elige tu batalla",
                fontSize = 16.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Tarjeta Gato
            GameCard(
                emoji = "❌⭕",
                title = "GATO",
                subtitle = "3 en raya",
                gradientColors = listOf(
                    Color(0xFF003545),
                    Color(0xFF00688A)
                ),
                accentColor = BlueNeon,
                onClick = onTicTacToe
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Tarjeta Damas
            GameCard(
                emoji = "⚫⚪",
                title = "DAMAS",
                subtitle = "Clásicas",
                gradientColors = listOf(
                    Color(0xFF3D0033),
                    Color(0xFF7A0066)
                ),
                accentColor = PinkNeon,
                onClick = onCheckers
            )

            Spacer(modifier = Modifier.weight(1f))

            // Ranking
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .clickable { }
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏆  Ranking",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Gold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun GameCard(
    emoji: String,
    title: String,
    subtitle: String,
    gradientColors: List<Color>,
    accentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(gradientColors))
            .clickable { onClick() }
            .padding(28.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                fontSize = 40.sp
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = accentColor,
                    letterSpacing = 3.sp
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = TextMuted
                )
            }
        }
    }
}
