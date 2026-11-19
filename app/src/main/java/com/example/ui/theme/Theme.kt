package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = AntiqueGold,
    onPrimary = MidnightGreen,
    primaryContainer = DarkGoldContainer,
    onPrimaryContainer = SoftGoldContainer,
    secondary = SoftEmerald,
    onSecondary = WarmIvory,
    secondaryContainer = ElevatedPine,
    onSecondaryContainer = WarmIvory,
    tertiary = AntiqueGold,
    onTertiary = MidnightGreen,
    background = MidnightGreen,
    onBackground = WarmIvory,
    surface = DeepPineSurface,
    onSurface = WarmIvory,
    surfaceVariant = ElevatedPine,
    onSurfaceVariant = Color(0xFFC5D0CA),
    outline = DarkSageOutline,
    error = StatusRejected,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = HeritageGreen,
    onPrimary = WarmIvory,
    primaryContainer = SoftGoldContainer,
    onPrimaryContainer = HeritageGreen,
    secondary = BronzeGold,
    onSecondary = Color.White,
    secondaryContainer = WarmSand,
    onSecondaryContainer = SlateInk,
    tertiary = AntiqueGold,
    onTertiary = MidnightGreen,
    background = WarmIvory,
    onBackground = SlateInk,
    surface = ClubCardIvory,
    onSurface = SlateInk,
    surfaceVariant = WarmSand,
    onSurfaceVariant = MutedMoss,
    outline = SageOutline,
    error = StatusRejected,
    onError = Color.White
)

val ClubShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun GoanGymkhanaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = ClubShapes,
        content = content
    )
}
