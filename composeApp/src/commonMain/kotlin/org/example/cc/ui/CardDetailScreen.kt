package org.example.cc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import org.example.cc.domain.CreditCard
import org.example.cc.hardware.SensorEvent

import org.example.cc.ui.platform.CCBackHandler

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardDetailScreen(
    card: CreditCard,
    sensorEvent: SensorEvent,
    onBack: () -> Unit,
    onSaveNotes: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Hardware Back Handling
    CCBackHandler {
        onBack()
    }

    var notes by remember { mutableStateOf(card.notes) }

    // Auto-save logic with debounce
    LaunchedEffect(notes) {
        if (notes == card.notes) return@LaunchedEffect
        delay(500)
        onSaveNotes(notes)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SiltAndStone.Background)
            .padding(top = 16.dp)
    ) {
        // Custom Back Button in Silt & Stone style
        IconButton(
            onClick = onBack,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SiltAndStone.Primary)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Hero Card Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            HolographicCard(
                card = card,
                sensorEvent = sensorEvent,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Notes Section
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(SiltAndStone.SurfaceContainer)
                .padding(24.dp)
        ) {
            Text(
                text = "NOTES",
                style = SiltAndStone.Typography().displaySmall,
                color = SiltAndStone.Primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxSize(),
                placeholder = { 
                    Text("Add secret notes, banking codes, or reminders...", 
                    color = SiltAndStone.Primary.copy(alpha = 0.4f)) 
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SiltAndStone.Primary,
                    unfocusedBorderColor = SiltAndStone.OutlineVariant,
                    cursorColor = SiltAndStone.Primary,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                textStyle = SiltAndStone.Typography().bodyLarge
            )
        }
    }
}
