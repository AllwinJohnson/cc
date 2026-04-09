package org.example.cc.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import cc.composeapp.generated.resources.Res
import cc.composeapp.generated.resources.SpaceGrotesk
import org.jetbrains.compose.resources.Font

object SiltAndStone {
    val Primary = Color(0xFF636560)
    val OnPrimary = Color(0xFFFFFFFF)
    val PrimaryContainer = Color(0xFFE8E9E2)
    val OnPrimaryContainer = Color(0xFF545651)
    
    val Secondary = Color(0xFF5C6854)
    val OnSecondary = Color(0xFFFFFFFF)
    val SecondaryContainer = Color(0xFFDAE7CE)
    
    val Tertiary = Color(0xFF925255)
    val OnTertiary = Color(0xFFFFFFFF)
    val TertiaryContainer = Color(0xFFF9A9AC)
    
    val Background = Color(0xFFFFFBFF)
    val OnBackground = Color(0xFF3A3927)
    
    val Surface = Color(0xFFFFFBFF)
    val OnSurface = Color(0xFF3A3927)
    val SurfaceContainer = Color(0xFFF9F5D7) // The "Stone" tone
    val SurfaceContainerHigh = Color(0xFFF3EFD0)
    val SurfaceContainerHighest = Color(0xFFEDE9C9)
    val SurfaceContainerLow = Color(0xFFFEFADF)
    
    val Outline = Color(0xFF83816C)
    val OutlineVariant = Color(0xFFBDBAA3).copy(alpha = 0.15f) // The "Ghost Border"
    
    // Geometry
    val CardCornerRadius = 20 // User specified 20dp
    val AspectRatio = 1.586f // Credit card ratio

    @Composable
    fun SpaceGroteskFamily() = FontFamily(
        Font(Res.font.SpaceGrotesk, FontWeight.Normal),
        Font(Res.font.SpaceGrotesk, FontWeight.Bold),
        Font(Res.font.SpaceGrotesk, FontWeight.Medium)
    )

    @Composable
    fun Typography() = Typography(
        displayLarge = TextStyle(
            fontFamily = SpaceGroteskFamily(),
            fontWeight = FontWeight.Bold,
            fontSize = 57.sp,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp
        ),
        headlineMedium = TextStyle(
            fontFamily = SpaceGroteskFamily(),
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = SpaceGroteskFamily(),
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        displaySmall = TextStyle(
            fontFamily = SpaceGroteskFamily(),
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            lineHeight = 44.sp,
            letterSpacing = 0.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        )
    )
}
