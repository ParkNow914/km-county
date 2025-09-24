package com.kmcounty.ridepricing.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light color scheme
private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = OnPrimaryColor,
    primaryContainer = PrimaryContainerColor,
    onPrimaryContainer = OnPrimaryContainerColor,
    secondary = SecondaryColor,
    onSecondary = OnSecondaryColor,
    secondaryContainer = SecondaryContainerColor,
    onSecondaryContainer = OnSecondaryContainerColor,
    tertiary = TertiaryColor,
    onTertiary = OnTertiaryColor,
    tertiaryContainer = TertiaryContainerColor,
    onTertiaryContainer = OnTertiaryContainerColor,
    error = ErrorColor,
    errorContainer = ErrorContainerColor,
    onError = OnErrorColor,
    onErrorContainer = OnErrorContainerColor,
    background = BackgroundColor,
    onBackground = OnBackgroundColor,
    surface = SurfaceColor,
    onSurface = OnSurfaceColor,
    surfaceVariant = SurfaceVariantColor,
    onSurfaceVariant = OnSurfaceVariantColor,
    outline = OutlineColor,
    inverseOnSurface = InverseOnSurfaceColor,
    inverseSurface = InverseSurfaceColor,
    inversePrimary = InversePrimaryColor,
    surfaceTint = SurfaceTintColor,
    outlineVariant = OutlineVariantColor,
    scrim = ScrimColor,
)

// Dark color scheme
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimaryColor,
    onPrimary = DarkOnPrimaryColor,
    primaryContainer = DarkPrimaryContainerColor,
    onPrimaryContainer = DarkOnPrimaryContainerColor,
    secondary = DarkSecondaryColor,
    onSecondary = DarkOnSecondaryColor,
    secondaryContainer = DarkSecondaryContainerColor,
    onSecondaryContainer = DarkOnSecondaryContainerColor,
    tertiary = DarkTertiaryColor,
    onTertiary = DarkOnTertiaryColor,
    tertiaryContainer = DarkTertiaryContainerColor,
    onTertiaryContainer = DarkOnTertiaryContainerColor,
    error = DarkErrorColor,
    errorContainer = DarkErrorContainerColor,
    onError = DarkOnErrorColor,
    onErrorContainer = DarkOnErrorContainerColor,
    background = DarkBackgroundColor,
    onBackground = DarkOnBackgroundColor,
    surface = DarkSurfaceColor,
    onSurface = DarkOnSurfaceColor,
    surfaceVariant = DarkSurfaceVariantColor,
    onSurfaceVariant = DarkOnSurfaceVariantColor,
    outline = DarkOutlineColor,
    inverseOnSurface = DarkInverseOnSurfaceColor,
    inverseSurface = DarkInverseSurfaceColor,
    inversePrimary = DarkInversePrimaryColor,
    surfaceTint = DarkSurfaceTintColor,
    outlineVariant = DarkOutlineVariantColor,
    scrim = DarkScrimColor,
)

@Composable
fun RidePricingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
