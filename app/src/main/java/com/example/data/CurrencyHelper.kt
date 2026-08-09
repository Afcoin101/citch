package com.example.data

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyHelper {

    /**
     * Converts base USD price into local price based on device country/locale,
     * and formats it with local currency symbol.
     */
    fun formatPrice(amountInUSD: Double, locale: Locale = Locale.getDefault()): String {
        val country = locale.country.uppercase()
        val language = locale.language.lowercase()

        return when {
            country == "NG" || language == "yo" || language == "ig" || language == "ha" -> {
                // Nigeria Naira (approx 1 USD = 1,500 NGN)
                val ngnAmount = amountInUSD * 1500.0
                val formatter = NumberFormat.getIntegerInstance(Locale("en", "NG"))
                "₦${formatter.format(ngnAmount.toInt())}"
            }
            country == "GH" -> {
                // Ghana Cedi
                val ghsAmount = amountInUSD * 15.0
                "GH₵${String.format("%.2f", ghsAmount)}"
            }
            country == "GB" || country == "UK" -> {
                // British Pound
                val gbpAmount = amountInUSD * 0.78
                "£${String.format("%.2f", gbpAmount)}"
            }
            country in listOf("DE", "FR", "ES", "IT", "NL", "BE", "AT", "FI", "GR", "IE", "PT") -> {
                // Eurozone
                val eurAmount = amountInUSD * 0.92
                "€${String.format("%.2f", eurAmount)}"
            }
            country == "CA" -> {
                // Canadian Dollar
                val cadAmount = amountInUSD * 1.36
                "CA$${String.format("%.2f", cadAmount)}"
            }
            country == "AU" -> {
                // Australian Dollar
                val audAmount = amountInUSD * 1.52
                "AU$${String.format("%.2f", audAmount)}"
            }
            country == "IN" -> {
                // Indian Rupee
                val inrAmount = amountInUSD * 83.5
                val formatter = NumberFormat.getIntegerInstance(Locale("en", "IN"))
                "₹${formatter.format(inrAmount.toInt())}"
            }
            country == "JP" -> {
                // Japanese Yen
                val jpyAmount = amountInUSD * 155.0
                val formatter = NumberFormat.getIntegerInstance(Locale.JAPAN)
                "¥${formatter.format(jpyAmount.toInt())}"
            }
            country == "ZA" -> {
                // South African Rand
                val zarAmount = amountInUSD * 18.2
                "R${String.format("%.2f", zarAmount)}"
            }
            country == "KE" -> {
                // Kenyan Shilling
                val kesAmount = amountInUSD * 130.0
                val formatter = NumberFormat.getIntegerInstance(Locale("en", "KE"))
                "KSh ${formatter.format(kesAmount.toInt())}"
            }
            else -> {
                try {
                    val currency = Currency.getInstance(locale)
                    if (currency.currencyCode == "USD") {
                        "$${String.format("%.2f", amountInUSD)}"
                    } else {
                        val symbol = currency.getSymbol(locale)
                        "$symbol${String.format("%.2f", amountInUSD)}"
                    }
                } catch (e: Exception) {
                    "$${String.format("%.2f", amountInUSD)}"
                }
            }
        }
    }

    /**
     * Returns country code name for displaying location context
     */
    fun getDetectedCountryName(locale: Locale = Locale.getDefault()): String {
        return locale.displayCountry.ifBlank { "United States" }
    }
}
