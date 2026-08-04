package com.example.fianancetracker.core.ui.theme

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.fianancetracker.core.domain.settings.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

)

private const val THEME_ANIM_MS = 350

@Composable
fun FiananceTrackerTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val target = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = target.animated(),
        typography = Typography,
        content = content
    )
}

@Composable
private fun ColorScheme.animated(): ColorScheme {
    @Composable
    fun Color.anim(): Color =
        animateColorAsState(this, tween(THEME_ANIM_MS), label = "themeColor").value

    return copy(
        primary = primary.anim(),
        onPrimary = onPrimary.anim(),
        primaryContainer = primaryContainer.anim(),
        onPrimaryContainer = onPrimaryContainer.anim(),
        inversePrimary = inversePrimary.anim(),
        secondary = secondary.anim(),
        onSecondary = onSecondary.anim(),
        secondaryContainer = secondaryContainer.anim(),
        onSecondaryContainer = onSecondaryContainer.anim(),
        tertiary = tertiary.anim(),
        onTertiary = onTertiary.anim(),
        tertiaryContainer = tertiaryContainer.anim(),
        onTertiaryContainer = onTertiaryContainer.anim(),
        background = background.anim(),
        onBackground = onBackground.anim(),
        surface = surface.anim(),
        onSurface = onSurface.anim(),
        surfaceVariant = surfaceVariant.anim(),
        onSurfaceVariant = onSurfaceVariant.anim(),
        surfaceTint = surfaceTint.anim(),
        inverseSurface = inverseSurface.anim(),
        inverseOnSurface = inverseOnSurface.anim(),
        error = error.anim(),
        onError = onError.anim(),
        errorContainer = errorContainer.anim(),
        onErrorContainer = onErrorContainer.anim(),
        outline = outline.anim(),
        outlineVariant = outlineVariant.anim(),
        scrim = scrim.anim(),
        surfaceBright = surfaceBright.anim(),
        surfaceDim = surfaceDim.anim(),
        surfaceContainer = surfaceContainer.anim(),
        surfaceContainerHigh = surfaceContainerHigh.anim(),
        surfaceContainerHighest = surfaceContainerHighest.anim(),
        surfaceContainerLow = surfaceContainerLow.anim(),
        surfaceContainerLowest = surfaceContainerLowest.anim(),
    )
}
