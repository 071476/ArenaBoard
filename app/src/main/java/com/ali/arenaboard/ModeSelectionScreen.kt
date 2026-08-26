package com.ali.arenaboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ali.arenaboard.ui.theme.*

@Composable
fun ModeSelectionScreen(
    gameName: String,
    onBack: () -> Unit,
    onVsApp: () -> Unit,
    onOnline: () -> Unit
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
            Spacer(modifier = Modifier.height(40.dp))

            // Botón atrás
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
                Text(
                    text = gameName,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = White,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "¿Contra quién?",
                fontSize = 22.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Tarjeta Contra la App
            ModeCard(
                emoji = "🤖",
                title = "Contra la App",
                subtitle = "Juega ahora mismo",
                gradientColors = listOf(
                    Color(0xFF003D1A),
                    Color(0xFF006B2E)
                ),
                accentColor = Green,
                onClick = onVsApp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tarjeta En línea
            ModeCard(
                emoji = "🌐",
                title = "En línea",
                subtitle = "Busca un rival",
                gradientColors = listOf(
                    Color(0xFF3D0033),
                    Color(0xFF7A0066)
                ),
                accentColor = PinkNeon,
                onClick = onOnline
            )
        }
    }
}

@Composable
fun ModeCard(
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
            .padding(32.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = emoji,
                fontSize = 44.sp
            )
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = accentColor,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = TextMuted
                )
            }
        }
    }
}
