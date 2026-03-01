package com.aiflow.workflow.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF3366CC),
    secondary = Color(0xFF4D8BF5),
    tertiary = Color(0xFF1A4DA3),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF252525),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onTertiary = Color(0xFFFFFFFF),
    onBackground = Color(0xFFFFFFFF),
    onSurface = Color(0xFFFFFFFF),
    outline = Color(0xFF333333),
    outlineVariant = Color(0xFF666666),
    error = Color(0xFFFF4D4F),
    success = Color(0xFF52C41A),
    warning = Color(0xFFFAAD14),
    info = Color(0xFF1890FF)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3366CC),
    secondary = Color(0xFF4D8BF5),
    tertiary = Color(0xFF1A4DA3),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF5F5F5),
    surfaceVariant = Color(0xFFE0E0E0),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onTertiary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000),
    onSurface = Color(0xFF000000),
    outline = Color(0xFFCCCCCC),
    outlineVariant = Color(0xFF999999),
    error = Color(0xFFFF4D4F),
    success = Color(0xFF52C41A),
    warning = Color(0xFFFAAD14),
    info = Color(0xFF1890FF)
)

@Composable
fun AIWorkflowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}