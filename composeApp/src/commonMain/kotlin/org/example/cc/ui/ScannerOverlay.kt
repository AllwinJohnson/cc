package org.example.cc.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import org.example.cc.hardware.SensorEvent

/**
 * Cybernetic Viewfinder Overlay with an animated laser and tilt-reactive "floating" effect.
 */
@Composable
fun ScannerOverlay(
    modifier: Modifier = Modifier,
    sensorEvent: SensorEvent? = null
) {
    val infiniteTransition = rememberInfiniteTransition()
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        // Define the scanning rectangle (safe zone)
        // Horizontal card orientation
        val cardWidth = width * 0.85f
        val cardHeight = cardWidth * 0.63f
        
        // Anti-Gravity Floating Viewfinder Frame
        // Reacts subtly to gyroscope data (pitch and roll)
        val tiltOffsetX = (sensorEvent?.roll ?: 0f) * 20f
        val tiltOffsetY = (sensorEvent?.pitch ?: 0f) * 20f
        
        val centerX = width / 2f + tiltOffsetX
        val centerY = height / 2f + tiltOffsetY
        
        val rect = Rect(
            offset = Offset(centerX - cardWidth / 2f, centerY - cardHeight / 2f),
            size = Size(cardWidth, cardHeight)
        )

        // 1. Draw the vignette/cutout
        val fullPath = Path().apply {
            addRect(Rect(0f, 0f, width, height))
        }
        val cardPath = Path().apply {
            addRoundRect(RoundRect(rect, CornerRadius(16.dp.toPx(), 16.dp.toPx())))
        }
        val cutoutPath = Path.combine(PathOperation.Difference, fullPath, cardPath)
        drawPath(cutoutPath, color = Color.Black.copy(alpha = 0.6f))

        // 2. Draw the viewfinder frame borders
        drawRoundRect(
            color = Color.Cyan.copy(alpha = 0.8f),
            topLeft = rect.topLeft,
            size = rect.size,
            cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
            style = Stroke(width = 2.dp.toPx())
        )

        // 3. Draw the animated scanning "laser" line
        val laserY = rect.top + (rect.height * laserPosition)
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Transparent, Color.Cyan, Color.Transparent)
            ),
            start = Offset(rect.left + 10.dp.toPx(), laserY),
            end = Offset(rect.right - 10.dp.toPx(), laserY),
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
        
        // Add a subtle glow/shadow to the laser
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Cyan.copy(alpha = 0.2f), Color.Transparent),
                startY = laserY,
                endY = laserY + 20.dp.toPx()
            ),
            topLeft = Offset(rect.left, laserY),
            size = Size(rect.width, 20.dp.toPx())
        )
    }
}
