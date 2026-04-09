package org.example.cc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.cc.domain.CreditCard
import org.example.cc.hardware.SensorEvent

@Composable
fun HolographicCard(
    card: CreditCard,
    sensorEvent: SensorEvent,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(SiltAndStone.AspectRatio)
            .clip(RoundedCornerShape(SiltAndStone.CardCornerRadius.dp))
            .background(SiltAndStone.Primary)
            .border(
                width = 1.dp,
                color = SiltAndStone.OutlineVariant,
                shape = RoundedCornerShape(SiltAndStone.CardCornerRadius.dp)
            )
            .holographicTilt(sensorEvent)
    ) {
        // Card Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = card.bankName.uppercase(),
                    color = SiltAndStone.OnPrimary,
                    style = SiltAndStone.Typography().titleMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                
                // Representing the Chip
                Box(
                    modifier = Modifier
                        .size(40.dp, 30.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(SiltAndStone.SurfaceContainerHighest)
                )
            }

            Column {
                Text(
                    text = formatCardNumber(card.cardNumber),
                    color = SiltAndStone.OnPrimary,
                    fontFamily = SiltAndStone.SpaceGroteskFamily(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.5.sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "CARD HOLDER",
                            color = SiltAndStone.OnPrimary.copy(alpha = 0.7f),
                            style = SiltAndStone.Typography().bodySmall
                        )
                        Text(
                            text = card.cardholderName.uppercase(),
                            color = SiltAndStone.OnPrimary,
                            style = SiltAndStone.Typography().bodyMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            fontFamily = SiltAndStone.SpaceGroteskFamily()
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "EXPIRES",
                            color = SiltAndStone.OnPrimary.copy(alpha = 0.7f),
                            style = SiltAndStone.Typography().bodySmall
                        )
                        Text(
                            text = card.expiryDate,
                            color = SiltAndStone.OnPrimary,
                            style = SiltAndStone.Typography().bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun formatCardNumber(number: String): String {
    return number.chunked(4).joinToString("  ")
}
