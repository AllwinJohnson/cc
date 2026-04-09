package org.example.cc.domain

object BankMatcher {
    private val bankDictionary = mapOf(
        "HDFC" to listOf("HDF-C", "HDC", "HDF C"),
        "ICICI" to listOf("1C1C", "ICI-CI", "ICIC"),
        "AMEX" to listOf("AMERICAN EXPRESS", "AM-EX", "AM EX"),
        "CHASE" to listOf("CHAS E", "CHA-SE"),
        "HSBC" to listOf("H S B C", "H-SBC")
    )

    fun match(input: String): String {
        val upperInput = input.uppercase().trim()
        
        // Exact match check
        if (bankDictionary.containsKey(upperInput)) return upperInput
        
        // Key-value check
        for ((bank, aliases) in bankDictionary) {
            if (aliases.any { alias -> upperInput.contains(alias) || alias.contains(upperInput) }) {
                return bank
            }
        }
        
        // Levi-style fuzzy fallback (simple contains)
        for (bank in bankDictionary.keys) {
            if (upperInput.contains(bank) || bank.contains(upperInput)) {
                return bank
            }
        }
        
        return input // Fallback to raw text
    }
}
