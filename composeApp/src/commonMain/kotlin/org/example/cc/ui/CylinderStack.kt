package org.example.cc.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.example.cc.domain.CreditCard
import org.example.cc.hardware.SensorEvent
import kotlin.math.abs

@Composable
fun CylinderStack(
    cards: List<CreditCard>,
    sensorEvent: SensorEvent,
    onCardClick: (CreditCard) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    
    var containerHeight by remember { mutableStateOf(0f) }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { 
                containerHeight = it.size.height.toFloat() 
            },
        contentPadding = PaddingValues(vertical = 100.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy((-120).dp) // Overlap cards
    ) {
        itemsIndexed(cards) { index, card ->
            var itemCenterY by remember { mutableStateOf(0f) }
            
            // Calculate focus factor (0.0 to 1.0)
            // 1.0 = center of screen, 0.0 = edge of screen
            val linearFocusFactor = if (containerHeight > 0) {
                val center = containerHeight / 2f
                val distance = abs(center - itemCenterY)
                (1f - (distance / center)).coerceIn(0f, 1f)
            } else 1f
            
            // Squared focus factor for snappier center "hit"
            val focusFactor = linearFocusFactor * linearFocusFactor

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { 
                        itemCenterY = it.positionInParent().y + it.size.height / 2f 
                    }
                    .zIndex(focusFactor) // Center card is on top
                    .graphicsLayer {
                        // Position Transformations
                        // Scale 1.1f at center to 0.85f at edges
                        val scale = 0.85f + (focusFactor * 0.25f)
                        scaleX = scale
                        scaleY = scale
                        
                        // RotationX simulates the cylinder curve
                        // Calculate relative distance from center (-1.0 to 1.0)
                        val relativePosition = if (containerHeight > 0) {
                            (itemCenterY - (containerHeight / 2f)) / (containerHeight / 2f)
                        } else 0f
                        
                        rotationX = relativePosition * -40f // -20 to 20 approx
                        
                        // Alpha fades out distant items
                        alpha = 0.6f + (focusFactor * 0.4f)
                        
                        cameraDistance = 8f * density.density
                    }
            ) {
                HolographicCard(
                    card = card,
                    sensorEvent = sensorEvent,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
