package com.reflect.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.LocalContext
import android.os.Build

private val LightColors = lightColorScheme(
    primary = Color(0xFF2D5A27),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF426E3E),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF4E7D57),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF6A996F),
    onSecondaryContainer = Color(0xFFFFFFFF),
    background = Color(0xFFF8F6F0),
    surface = Color(0xFFF8F6F0),
    surfaceVariant = Color(0xFFEDEBE2),
    onSurface = Color(0xFF28342A),
    onSurfaceVariant = Color(0xFF4A554A)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9ED38E),
    onPrimary = Color(0xFF0D1A0F),
    primaryContainer = Color(0xFF6AAF5C),
    onPrimaryContainer = Color(0xFF0D1A0F),
    secondary = Color(0xFFB5D1B9),
    onSecondary = Color(0xFF0D1A0F),
    secondaryContainer = Color(0xFF8EB49B),
    onSecondaryContainer = Color(0xFF0D1A0F),
    background = Color(0xFF121413),
    surface = Color(0xFF121413),
    surfaceVariant = Color(0xFF1E201F),
    onSurface = Color(0xFFE5E8E6),
    onSurfaceVariant = Color(0xFFC5CBC7)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val context = LocalContext.current
    val colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (dark) DarkColors else LightColors
    }
    MaterialTheme(colorScheme = colors, content = content)
}