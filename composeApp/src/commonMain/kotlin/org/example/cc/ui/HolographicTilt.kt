package org.example.cc.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import org.example.cc.hardware.SensorEvent

/**
 * Applies an "Anti-Gravity" 3D tilt effect and a linear sheen highlight.
 * The tilt follows the device orientation with mass-sensitive smoothing,
 * and the highlight moves opposite to the tilt to simulate real-world light reflection.
 */
fun Modifier.holographicTilt(event: SensorEvent, enabled: Boolean = true): Modifier = if (!enabled) this else this
    .graphicsLayer {
        // Use the user-recommended 15f intensity for a balanced weightless feel
        rotationX = -event.pitch * 15f
        rotationY = event.roll * 15f
        
        // High camera distance reduces fisheye distortion for a premium floating feel
        cameraDistance = 12f * density
    }
    .drawWithContent {
        drawContent()
        
        // Linear "Sheen" Gradient
        val shimmerShiftX = -event.roll * size.width * 2f
        val shimmerShiftY = -event.pitch * size.height * 2f
        
        val brush = Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.0f),
                Color.White.copy(alpha = 0.3f),
                Color.White.copy(alpha = 0.0f)
            ),
            start = Offset(shimmerShiftX, shimmerShiftY),
            end = Offset(shimmerShiftX + size.width, shimmerShiftY + size.height)
        )
        
        drawRect(
            brush = brush,
            blendMode = BlendMode.Overlay
        )
    }
