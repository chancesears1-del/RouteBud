package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

enum class AppSkin(
    val displayName: String,
    val iconEmoji: String
) {
    LIGHT("Skin: Light", "☀️"),
    DARK("Skin: Dark", "🌙"),
    SUNSET("Skin: Sunset", "🌅"),
    CYBERPUNK("Skin: Neon", "⚡")
}

// Custom Colors for Skins (High Density M3 Palette)
val LightPrimary = Color(0xFF6750A4)
val LightPrimaryContainer = Color(0xFFEADDFF)
val LightBackground = Color(0xFFFEF7FF)
val LightSurface = Color(0xFFFFFFFF)
val LightOnSurface = Color(0xFF1D1B20)
val LightBubbleSent = Color(0xFFEADDFF)
val LightBubbleReceived = Color(0xFFF3EDF7)

val DarkPrimary = Color(0xFFA8C7FA)
val DarkPrimaryContainer = Color(0xFF004A77)
val DarkBackground = Color(0xFF131314)
val DarkSurface = Color(0xFF1E1F20)
val DarkOnSurface = Color(0xFFE3E2E6)
val DarkBubbleSent = Color(0xFF004A77)
val DarkBubbleReceived = Color(0xFF282A2D)

val SunsetPrimary = Color(0xFFFFB0CD)
val SunsetPrimaryContainer = Color(0xFF64163F)
val SunsetBackground = Color(0xFF2B1625)
val SunsetSurface = Color(0xFF381C30)
val SunsetOnSurface = Color(0xFFFFDBF0)
val SunsetBubbleSent = Color(0xFF8C255B)
val SunsetBubbleReceived = Color(0xFF411D33)

val CyberpunkPrimary = Color(0xFFFFE600)
val CyberpunkPrimaryContainer = Color(0xFF3D003D)
val CyberpunkBackground = Color(0xFF09090E)
val CyberpunkSurface = Color(0xFF12121A)
val CyberpunkOnSurface = Color(0xFF00F0FF)
val CyberpunkSecondary = Color(0xFFFF007F)
val CyberpunkBubbleSent = Color(0xFFFF0055)
val CyberpunkBubbleReceived = Color(0xFF1A1A2E)

fun getSkinColorScheme(skin: AppSkin): ColorScheme {
    return when (skin) {
        AppSkin.LIGHT -> lightColorScheme(
            primary = LightPrimary,
            primaryContainer = LightPrimaryContainer,
            onPrimary = Color.White,
            onPrimaryContainer = Color(0xFF21005D),
            background = LightBackground,
            surface = LightSurface,
            onSurface = LightOnSurface,
            surfaceVariant = Color(0xFFF3EDF7),
            onSurfaceVariant = Color(0xFF49454F),
            outlineVariant = Color(0xFFE7E0EC)
        )
        AppSkin.DARK -> darkColorScheme(
            primary = DarkPrimary,
            primaryContainer = DarkPrimaryContainer,
            onPrimary = Color(0xFF003062),
            background = DarkBackground,
            surface = DarkSurface,
            onSurface = DarkOnSurface,
            surfaceVariant = Color(0xFF2B2C2E),
            onSurfaceVariant = Color(0xFFC4C6D0)
        )
        AppSkin.SUNSET -> darkColorScheme(
            primary = SunsetPrimary,
            primaryContainer = SunsetPrimaryContainer,
            onPrimary = Color(0xFF51002A),
            background = SunsetBackground,
            surface = SunsetSurface,
            onSurface = SunsetOnSurface,
            surfaceVariant = Color(0xFF43223B),
            onSurfaceVariant = Color(0xFFEEBBDD)
        )
        AppSkin.CYBERPUNK -> darkColorScheme(
            primary = CyberpunkPrimary,
            primaryContainer = CyberpunkPrimaryContainer,
            onPrimary = Color.Black,
            secondary = CyberpunkSecondary,
            background = CyberpunkBackground,
            surface = CyberpunkSurface,
            onSurface = CyberpunkOnSurface,
            surfaceVariant = Color(0xFF1F1F2E),
            onSurfaceVariant = Color(0xFFFF007F)
        )
    }
}

data class SkinChatColors(
    val bubbleSentBg: Color,
    val bubbleSentText: Color,
    val bubbleReceivedBg: Color,
    val bubbleReceivedText: Color,
    val mapGridLine: Color,
    val mapBg: Color
)

fun getSkinChatColors(skin: AppSkin): SkinChatColors {
    return when (skin) {
        AppSkin.LIGHT -> SkinChatColors(
            bubbleSentBg = LightBubbleSent,
            bubbleSentText = Color(0xFF21005D),
            bubbleReceivedBg = LightBubbleReceived,
            bubbleReceivedText = Color(0xFF1D1B20),
            mapGridLine = Color(0xFFCAC4D0),
            mapBg = Color(0xFFE7E0EC)
        )
        AppSkin.DARK -> SkinChatColors(
            bubbleSentBg = DarkBubbleSent,
            bubbleSentText = Color(0xFFC2E7FF),
            bubbleReceivedBg = DarkBubbleReceived,
            bubbleReceivedText = Color(0xFFE3E2E6),
            mapGridLine = Color(0xFF2C2D30),
            mapBg = Color(0xFF18191B)
        )
        AppSkin.SUNSET -> SkinChatColors(
            bubbleSentBg = SunsetBubbleSent,
            bubbleSentText = Color(0xFFFFDBF0),
            bubbleReceivedBg = SunsetBubbleReceived,
            bubbleReceivedText = Color(0xFFEEBBDD),
            mapGridLine = Color(0xFF5D2A4F),
            mapBg = Color(0xFF23111E)
        )
        AppSkin.CYBERPUNK -> SkinChatColors(
            bubbleSentBg = CyberpunkBubbleSent,
            bubbleSentText = Color.White,
            bubbleReceivedBg = CyberpunkBubbleReceived,
            bubbleReceivedText = Color(0xFF00F0FF),
            mapGridLine = Color(0xFF1F2B3E),
            mapBg = Color(0xFF0D0E15)
        )
    }
}
