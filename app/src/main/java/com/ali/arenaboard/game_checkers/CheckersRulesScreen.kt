package com.ali.arenaboard.game_checkers

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
fun CheckersRulesScreen(
    onBack: () -> Unit,
    onAmerican: () -> Unit,
    onInternational: () -> Unit
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
                Text(
                    text = "DAMAS",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = PinkNeon,
                    letterSpacing = 3.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Elige las reglas",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = White
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Americanas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF003D1A), Color(0xFF006B2E))))
                    .clickable { onAmerican() }
                    .padding(28.dp)
            ) {
                Column {
                    Text(
                        text = "🇺🇸  Americanas (8×8)",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Green,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "• Tablero 8×8",
                        fontSize = 14.sp,
                        color = White
                    )
                    Text(
                        text = "• Reina mueve 1 casilla en cualquier diagonal",
                        fontSize = 14.sp,
                        color = White
                    )
                    Text(
                        text = "• Reina puede comer hacia atrás",
                        fontSize = 14.sp,
                        color = White
                    )
                    Text(
                        text = "• Captura brincando 2 casillas",
                        fontSize = 14.sp,
                        color = White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Internacionales
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF3D0033), Color(0xFF7A0066))))
                    .clickable { onInternational() }
                    .padding(28.dp)
            ) {
                Column {
                    Text(
                        text = "🌍  Internacionales (8×8)",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = PinkNeon,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "• Tablero 8×8",
                        fontSize = 14.sp,
                        color = White
                    )
                    Text(
                        text = "• Reina VUELA varios casillas en diagonal",
                        fontSize = 14.sp,
                        color = White
                    )
                    Text(
                        text = "• Reina come desde lejos",
                        fontSize = 14.sp,
                        color = White
                    )
                    Text(
                        text = "• Peones igual que americanas",
                        fontSize = 14.sp,
                        color = White
                    )
                }
            }
        }
    }
}
