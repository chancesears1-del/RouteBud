package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun RouteBuddyTheme(
    activeSkin: AppSkin = AppSkin.LIGHT,
    content: @Composable () -> Unit
) {
    val colorScheme = getSkinColorScheme(activeSkin)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
