package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CitchDarkCoral,
    onPrimary = Color(0xFF4A1000),
    primaryContainer = Color(0xFF701E06),
    onPrimaryContainer = Color(0xFFFFDBCF),
    
    secondary = CitchDarkTeal,
    onSecondary = Color(0xFF003730),
    secondaryContainer = Color(0xFF005047),
    onSecondaryContainer = Color(0xFF86F8E8),
    
    tertiary = CitchDarkAmber,
    onTertiary = Color(0xFF442D00),
    tertiaryContainer = Color(0xFF624300),
    onTertiaryContainer = Color(0xFFFFDF9E),
    
    background = CitchDarkBg,
    onBackground = CitchDarkText,
    
    surface = CitchDarkSurface,
    onSurface = CitchDarkText,
    surfaceVariant = CitchDarkSurfaceVariant,
    onSurfaceVariant = CitchDarkMutedText,
    
    outline = CitchDarkBorder,
    outlineVariant = Color(0xFF352B26),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val LightColorScheme = lightColorScheme(
    primary = CitchCoralPrimary,
    onPrimary = Color.White,
    primaryContainer = CitchCoralContainer,
    onPrimaryContainer = CitchCoralContainerOn,
    
    secondary = CitchForestGreen,
    onSecondary = Color.White,
    secondaryContainer = CitchMintBadge,
    onSecondaryContainer = CitchMintBadgeText,
    
    tertiary = CitchGold,
    onTertiary = Color(0xFF332000),
    tertiaryContainer = Color(0xFFFFF3D6),
    onTertiaryContainer = Color(0xFF4D3200),
    
    background = CitchCreamBg,
    onBackground = CitchEspresso,
    
    surface = Color.White,
    onSurface = CitchEspresso,
    surfaceVariant = CitchWarmSurfaceVariant,
    onSurfaceVariant = CitchCocoa,
    
    outline = CitchCardBorder,
    outlineVariant = Color(0xFFE5DDD3),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Enforce our custom vibrant culinary color scheme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
