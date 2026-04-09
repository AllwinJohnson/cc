package org.example.cc.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.example.cc.domain.CardNetwork
import org.example.cc.domain.CardType
import org.example.cc.domain.CreditCard
import org.example.cc.hardware.HardwareSensorEngine
import org.example.cc.domain.WalletRepository
import org.koin.compose.koinInject
import kotlin.random.Random
import kotlin.random.nextInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    repository: WalletRepository = koinInject(),
    sensorEngine: HardwareSensorEngine = koinInject()
) {
    val cards by repository.getAllCards().collectAsState(initial = emptyList())
    val sensorEvent by sensorEngine.sensorEvents.collectAsState()
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        sensorEngine.startListening()
        onDispose {
            sensorEngine.stopListening()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Holographic Wallet") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                scope.launch {
                    val rnd = Random(Clock.System.now().toEpochMilliseconds())
                    val dummyCard = CreditCard(
                        id = rnd.nextInt().toString(),
                        cardNumber = "4111 1111 1111 111${rnd.nextInt(0..9)}",
                        cardholderName = "John Doe",
                        expiryDate = "12/30",
                        cvv = "123",
                        bankName = "Test Bank",
                        network = CardNetwork.VISA,
                        type = CardType.CREDIT,
                        accentColorHex = "#ff0000",
                        isDetailsOnBack = false
                    )
                    repository.insertCard(dummyCard)
                }
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Card")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cards) { card ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .holographicTilt(sensorEvent)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Card: ${card.cardNumber}", style = MaterialTheme.typography.titleMedium)
                        Text("Bank: ${card.bankName}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
