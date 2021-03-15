package com.balvarezazocar.ciensonetosdeamor.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200,
    surface = Color.White,
    onPrimary = Color.White,
    background = Color.White
)

private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200,
    surface = Color.LightGray,
    onPrimary = Color.White,
    background = Color.White
    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
     /*       primary: Color = Color(0xFFBB86FC),
    primaryVariant: Color = Color(0xFF3700B3),
secondary: Color = Color(0xFF03DAC6),
secondaryVariant: Color = secondary,
background: Color = Color(0xFF121212),
surface: Color = Color(0xFF121212),
error: Color = Color(0xFFCF6679),
onPrimary: Color = Color.Black,
onSecondary: Color = Color.Black,
onBackground: Color = Color.White,
onSurface: Color = Color.White,
onError: Color = Color.Black
): Colors = Colors(
primary,
primaryVariant,
secondary,
secondaryVariant,
background,
surface,
error,
onPrimary,
onSecondary,
onBackground,
onSurface,
onError,
false*/
)

@Composable
fun CienSonetosDeAmorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}