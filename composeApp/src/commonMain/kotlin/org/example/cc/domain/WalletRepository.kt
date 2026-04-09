package org.example.cc.domain

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.cc.database.WalletDatabase
import org.example.cc.database.SavedCardEntity

class WalletRepository(private val database: WalletDatabase) {
    private val queries = database.walletQueries

    fun getAllCards(): Flow<List<CreditCard>> {
        return queries.getAllCards()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toDomain() } }
    }

    fun insertCard(card: CreditCard) {
        queries.insertCard(
            id = card.id,
            cardNumber = card.cardNumber,
            cardholderName = card.cardholderName,
            expiryDate = card.expiryDate,
            cvv = card.cvv,
            bankName = card.bankName,
            network = card.network.name,
            type = card.type.name,
            accentColorHex = card.accentColorHex,
            isDetailsOnBack = if (card.isDetailsOnBack) 1L else 0L
        )
    }

    fun deleteCard(id: String) {
        queries.deleteCard(id)
    }
}

fun SavedCardEntity.toDomain(): CreditCard {
    return CreditCard(
        id = id,
        cardNumber = cardNumber,
        cardholderName = cardholderName,
        expiryDate = expiryDate,
        cvv = cvv,
        bankName = bankName,
        network = CardNetwork.valueOf(network),
        type = CardType.valueOf(type),
        accentColorHex = accentColorHex,
        isDetailsOnBack = isDetailsOnBack == 1L
    )
}
