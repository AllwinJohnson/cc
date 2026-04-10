package org.example.cc.ui

import androidx.compose.foundation.clickable
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
        contentPadding = PaddingValues(
            top = 400.dp, // Reachable first card focus
            bottom = 400.dp, // Reachable last card focus
            start = 16.dp,
            end = 16.dp
        ),
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
                    .clickable { onCardClick(card) }
                    .onGloballyPositioned { 
                        itemCenterY = it.positionInParent().y + it.size.height / 2f 
                    }
                    // CRITICAL FIX 1: Z-Index driven by focus, not list position
                    .zIndex(focusFactor * 100f) 
                    .graphicsLayer {
                        val cleanRelative = if (containerHeight > 0) {
                            ((itemCenterY - (containerHeight / 2f)) / (containerHeight / 2f)).coerceIn(-1f, 1f)
                        } else 0f
                        
                        // Reduced tilt for subtle depth
                        rotationX = cleanRelative * -15f 
                        
                        // CRITICAL FIX 2: Zero translationY so spacedBy(-120.dp) works correctly
                        translationY = 0f 
                        
                        val scale = 0.85f + (focusFactor * 0.15f)
                        scaleX = scale
                        scaleY = scale
                        
                        alpha = 1.0f 
                        cameraDistance = 12f * density.density
                    }
            ) {
                HolographicCard(
                    card = card,
                    sensorEvent = sensorEvent,
                    isFocused = focusFactor > 0.95f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
