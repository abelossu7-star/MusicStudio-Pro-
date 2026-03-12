package com.musicstudio.pro.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val LightColorPalette = lightColors(
    primary = Primary,
    primaryVariant = PrimaryVariant,
    secondary = Secondary
)

private val DarkColorPalette = darkColors(
    primary = Primary,
    primaryVariant = PrimaryVariant,
    secondary = Secondary
)

@Composable
fun MusicStudioProTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
