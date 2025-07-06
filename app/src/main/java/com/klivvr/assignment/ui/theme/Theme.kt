package com.klivvr.assignment.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val ColorScheme.titleTextColor: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) TitleTextColorDark else TitleTextColorLight

val ColorScheme.flagBackgroundColor: Color
    @Composable
    @ReadOnlyComposable
    get() =
        if (isSystemInDarkTheme()) FlagBackgroundColor else FlagBackgroundColor

val ColorScheme.cityBackgroundColor: Color
    @Composable
    @ReadOnlyComposable
    get() =
        if (isSystemInDarkTheme()) CityDarkBackgroundColor else CityLightBackgroundColor

val ColorScheme.stickyLetterStrokeColor: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) TitleTextColorLight else Color.LightGray

val ColorScheme.stickyLetterTextColor: Color
    @Composable
    @ReadOnlyComposable
    get() = if (isSystemInDarkTheme()) TitleTextColorLight else Color.Gray


// DATA CLASS FOR CUSTOM COLORS
@Immutable
data class ExtendedColors(
    val searchBarUnfocusedBackground: Color,
    val searchBarFocusedBackground: Color,
    val searchBarFocusedContent: Color,
    val searchBarUnfocusedContent: Color
)

// DEFINE THE COLOR SETS FOR LIGHT AND DARK THEMES
private val lightExtendedColors = ExtendedColors(
    searchBarUnfocusedBackground = SearchBarUnfocusedBackgroundLight,
    searchBarFocusedBackground = SearchBarFocusedBackgroundLight,
    searchBarFocusedContent = SearchBarFocusedContentLight,
    searchBarUnfocusedContent = SearchBarUnfocusedContentLight
)

private val darkExtendedColors = ExtendedColors(
    searchBarUnfocusedBackground = SearchBarUnfocusedBackgroundDark,
    searchBarFocusedBackground = SearchBarFocusedBackgroundDark,
    searchBarFocusedContent = SearchBarFocusedContentDark,
    searchBarUnfocusedContent = SearchBarUnfocusedContentDark
)

// THE COMPOSITION LOCAL
private val LocalExtendedColors = staticCompositionLocalOf { lightExtendedColors }

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    background = DarkBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    outline = DarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    background = LightBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightOnSurfaceVariant,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    outline = LightOutline
)

@Composable
fun KlivvrAssignmentTheme(
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

    // CHOOSE THE CORRECT SET OF EXTENDED COLORS
    val extendedColors = if (darkTheme) darkExtendedColors else lightExtendedColors

    // ... (Your existing SideEffect block for status bar color)

    // PROVIDE THE COLORS TO THE COMPOSITION
    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }

}

object KlivvrTheme {
    val extendedColors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current
}