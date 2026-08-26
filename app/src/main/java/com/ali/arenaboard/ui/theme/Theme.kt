package com.ali.arenaboard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ArenaColorScheme = darkColorScheme(
    primary = BlueNeon,
    secondary = PinkNeon,
    tertiary = Gold,
    background = Background,
    surface = Surface,
    onPrimary = Background,
    onSecondary = Background,
    onTertiary = Background,
    onBackground = White,
    onSurface = White
)

@Composable
fun ArenaBoardTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ArenaColorScheme,
        content = content
    )
}
