package org.example.cc.domain

enum class CardNetwork {
    VISA, MASTERCARD, AMEX, RUPAY, DISCOVER, OTHER
}

enum class CardType {
    CREDIT, DEBIT
}

data class CreditCard(
    val id: String,
    val cardNumber: String,
    val cardholderName: String,
    val expiryDate: String,
    val cvv: String,
    val bankName: String,
    val network: CardNetwork,
    val type: CardType,
    val accentColorHex: String,
    val isDetailsOnBack: Boolean
)
