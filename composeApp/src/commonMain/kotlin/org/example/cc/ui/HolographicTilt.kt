package org.example.cc.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import org.example.cc.hardware.SensorEvent
import kotlin.math.PI

/**
 * Applies a 3D tilt effect and a dynamic specular highlight based on device orientation.
 */
fun Modifier.holographicTilt(event: SensorEvent): Modifier = this
    .graphicsLayer {
        // Convert radians to degrees for rotationX and rotationY
        // Note: We swap pitch and roll for natural-feeling tilt
        rotationX = -(event.pitch * 180f / PI.toFloat()) * 0.5f
        rotationY = (event.roll * 180f / PI.toFloat()) * 0.5f
        
        // Increase camera distance to reduce fisheye distortion and improve the "floating" feel
        cameraDistance = 12f * density
    }
    .drawWithContent {
        drawContent()
        
        // Dynamic Specular Highlight simulation
        // The gradient shifts its offset based on the pitch and roll
        val highlightOffset = Offset(
            x = size.width * (0.5f + event.roll * 1.5f),
            y = size.height * (0.5f + event.pitch * 1.5f)
        )
        
        val brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.35f),
                Color.White.copy(alpha = 0.1f),
                Color.Transparent
            ),
            center = highlightOffset,
            radius = size.maxDimension * 0.8f
        )
        
        drawRect(
            brush = brush,
            blendMode = BlendMode.Overlay
        )
    }
