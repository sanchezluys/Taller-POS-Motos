package com.example.data.models

enum class WorkshopCurrency(
    val code: String,
    val symbol: String,
    val displayName: String,
    val defaultDecimals: Boolean
) {
    USD("USD", "$", "Dólar ($ USD)", true),
    SOL("PEN", "S/", "Sol Peruano (S/)", true),
    COL("COP", "$", "Peso Colombiano ($ COP)", false),
    ARS("ARS", "$", "Peso Argentino ($ ARS)", false),
    MXN("MXN", "$", "Peso Mexicano ($ MXN)", true),
    EUR("EUR", "€", "Euro (€)", true),
    CLP("CLP", "$", "Peso Chileno ($ CLP)", false);

    companion object {
        fun fromCode(code: String): WorkshopCurrency {
            return entries.firstOrNull { it.name.equals(code, ignoreCase = true) || it.code.equals(code, ignoreCase = true) } ?: USD
        }
    }
}

enum class ThousandsSeparator(
    val label: String,
    val example: String
) {
    DOT("Punto (.)", "1.234,50"),
    COMMA("Coma (,)", "1,234.50"),
    NONE("Desactivada (Sin separador)", "1234.50")
}

data class WorkshopSettings(
    val workshopName: String = "Taller Pro Multi-Vehículos",
    val tagline: String = "Motos • Bicis • Scooters • E-Bikes",
    val address: String = "Av. Principal #123",
    val phone: String = "+1 234 567 890",
    val taxId: String = "", // RUT, NIT, RUC, CUIT, etc.
    val logoIconName: String = "WRENCH", // WRENCH, MOTO, BICI, SCOOTER, BOLT, SPEED, STORE, SHIELD
    val currency: WorkshopCurrency = WorkshopCurrency.USD,
    val customCurrencySymbol: String = "",
    val thousandsSeparator: ThousandsSeparator = ThousandsSeparator.COMMA,
    val showDecimals: Boolean = true,
    val receiptFooterNote: String = "Garantía de 30 días en mano de obra. ¡Gracias por su preferencia!",
    val isDarkMode: Boolean = true
) {
    val effectiveCurrencySymbol: String
        get() = if (customCurrencySymbol.isNotBlank()) customCurrencySymbol.trim() else currency.symbol

    fun formatPrice(amount: Double): String {
        val sym = effectiveCurrencySymbol
        val isNegative = amount < 0
        val absAmount = Math.abs(amount)

        val formattedNumber = when (thousandsSeparator) {
            ThousandsSeparator.DOT -> {
                // Dot as thousands, comma as decimal
                if (showDecimals) {
                    val whole = absAmount.toLong()
                    val fraction = Math.round((absAmount - whole) * 100).toInt()
                    val wholeFormatted = formatWithGroupSeparator(whole, '.')
                    val fracStr = String.format("%02d", fraction)
                    "$wholeFormatted,$fracStr"
                } else {
                    val whole = Math.round(absAmount)
                    formatWithGroupSeparator(whole, '.')
                }
            }
            ThousandsSeparator.COMMA -> {
                // Comma as thousands, dot as decimal
                if (showDecimals) {
                    val whole = absAmount.toLong()
                    val fraction = Math.round((absAmount - whole) * 100).toInt()
                    val wholeFormatted = formatWithGroupSeparator(whole, ',')
                    val fracStr = String.format("%02d", fraction)
                    "$wholeFormatted.$fracStr"
                } else {
                    val whole = Math.round(absAmount)
                    formatWithGroupSeparator(whole, ',')
                }
            }
            ThousandsSeparator.NONE -> {
                // No thousands separator
                if (showDecimals) {
                    String.format(java.util.Locale.US, "%.2f", absAmount)
                } else {
                    Math.round(absAmount).toString()
                }
            }
        }

        val prefix = if (isNegative) "-" else ""
        return if (sym == "€") {
            "$prefix$formattedNumber $sym"
        } else {
            "$prefix$sym $formattedNumber"
        }
    }

    private fun formatWithGroupSeparator(number: Long, separator: Char): String {
        val str = number.toString()
        val len = str.length
        if (len <= 3) return str

        val sb = StringBuilder()
        var count = 0
        for (i in len - 1 downTo 0) {
            sb.append(str[i])
            count++
            if (count % 3 == 0 && i > 0) {
                sb.append(separator)
            }
        }
        return sb.reverse().toString()
    }
}
