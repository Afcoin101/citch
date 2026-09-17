package com.example.data

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object CurrencyHelper {

    var activeCountryCode: String = "GB"
    var activeLocationName: String = "Camden, London"

    fun updateActiveLocation(locationName: String, countryCode: String? = null) {
        activeLocationName = locationName
        activeCountryCode = countryCode ?: getCountryCodeFromLocationString(locationName)
    }

    /**
     * Converts base USD price into the target currency based on country code.
     * Exchange rates relative to 1.0 USD.
     */
    fun convertFromUsd(amountInUsd: Double, countryCode: String): Double {
        return when (countryCode.uppercase().trim()) {
            "GB", "UK" -> amountInUsd * 0.78
            "NG" -> amountInUsd * 1500.0
            "GH" -> amountInUsd * 15.0
            "DE", "FR", "ES", "IT", "NL", "BE", "AT", "FI", "GR", "IE", "PT" -> amountInUsd * 0.92
            "CA" -> amountInUsd * 1.36
            "AU" -> amountInUsd * 1.52
            "IN" -> amountInUsd * 83.5
            "JP" -> amountInUsd * 155.0
            "ZA" -> amountInUsd * 18.2
            "KE" -> amountInUsd * 130.0
            "CN" -> amountInUsd * 7.23
            "MX" -> amountInUsd * 19.8
            "BR" -> amountInUsd * 5.6
            "AE" -> amountInUsd * 3.67
            else -> amountInUsd
        }
    }

    /**
     * Returns currency symbol for a ISO 2-letter country code.
     */
    fun getCurrencySymbolForCountry(countryCode: String): String {
        return when (countryCode.uppercase().trim()) {
            "GB", "UK" -> "£"
            "NG" -> "₦"
            "GH" -> "GH₵"
            "DE", "FR", "ES", "IT", "NL", "BE", "AT", "FI", "GR", "IE", "PT" -> "€"
            "CA" -> "CA$"
            "AU" -> "AU$"
            "IN" -> "₹"
            "JP" -> "¥"
            "ZA" -> "R"
            "KE" -> "KSh"
            "CN" -> "¥"
            "MX" -> "Mex$"
            "BR" -> "R$"
            "AE" -> "AED"
            else -> "$"
        }
    }

    /**
     * Returns ISO 4217 Currency Code (e.g. GBP, NGN, EUR, USD).
     */
    fun getCurrencyCodeForCountry(countryCode: String): String {
        return when (countryCode.uppercase().trim()) {
            "GB", "UK" -> "GBP"
            "NG" -> "NGN"
            "GH" -> "GHS"
            "DE", "FR", "ES", "IT", "NL", "BE", "AT", "FI", "GR", "IE", "PT" -> "EUR"
            "CA" -> "CAD"
            "AU" -> "AUD"
            "IN" -> "INR"
            "JP" -> "JPY"
            "ZA" -> "ZAR"
            "KE" -> "KES"
            "CN" -> "CNY"
            "MX" -> "MXN"
            "BR" -> "BRL"
            "AE" -> "AED"
            else -> "USD"
        }
    }

    /**
     * Derives a country code from a free-form location text string
     * (e.g. "Camden, London" -> "GB", "Yaba, Lagos" -> "NG", "Dublin 2, Ireland" -> "IE").
     */
    fun getCountryCodeFromLocationString(location: String): String {
        val loc = location.lowercase()
        return when {
            loc.contains("london") || loc.contains("camden") || loc.contains("uk") ||
            loc.contains("united kingdom") || loc.contains("manchester") || loc.contains("birmingham") ||
            loc.contains("england") || loc.contains("scotland") || loc.contains("wales") -> "GB"

            loc.contains("lagos") || loc.contains("yaba") || loc.contains("ikeja") ||
            loc.contains("lekki") || loc.contains("surulere") || loc.contains("abuja") ||
            loc.contains("nigeria") -> "NG"

            loc.contains("ireland") || loc.contains("dublin") || loc.contains("cork") ||
            loc.contains("galway") -> "IE"

            loc.contains("ghana") || loc.contains("accra") || loc.contains("kumasi") -> "GH"

            loc.contains("germany") || loc.contains("berlin") || loc.contains("munich") ||
            loc.contains("frankfurt") -> "DE"

            loc.contains("france") || loc.contains("paris") || loc.contains("lyon") -> "FR"

            loc.contains("spain") || loc.contains("madrid") || loc.contains("barcelona") -> "ES"

            loc.contains("italy") || loc.contains("rome") || loc.contains("milan") -> "IT"

            loc.contains("canada") || loc.contains("toronto") || loc.contains("vancouver") ||
            loc.contains("montreal") -> "CA"

            loc.contains("australia") || loc.contains("sydney") || loc.contains("melbourne") -> "AU"

            loc.contains("india") || loc.contains("mumbai") || loc.contains("delhi") ||
            loc.contains("bangalore") -> "IN"

            loc.contains("japan") || loc.contains("tokyo") || loc.contains("osaka") ||
            loc.contains("kyoto") -> "JP"

            loc.contains("south africa") || loc.contains("johannesburg") || loc.contains("cape town") -> "ZA"

            loc.contains("kenya") || loc.contains("nairobi") -> "KE"

            loc.contains("china") || loc.contains("beijing") || loc.contains("shanghai") -> "CN"

            loc.contains("mexico") || loc.contains("mexico city") || loc.contains("cancun") -> "MX"

            loc.contains("dubai") || loc.contains("uae") || loc.contains("abu dhabi") -> "AE"

            loc.contains("usa") || loc.contains("united states") || loc.contains("new york") ||
            loc.contains("san francisco") || loc.contains("los angeles") -> "US"

            else -> {
                // If nothing matched, use device's default country locale
                val defaultCountry = Locale.getDefault().country.uppercase()
                if (defaultCountry.isNotBlank()) defaultCountry else "US"
            }
        }
    }

    /**
     * Formats price for a given location string or recognized area.
     * Converts base USD amount automatically into the country's local currency.
     */
    fun formatPriceForLocation(amountInUSD: Double, location: String): String {
        val countryCode = getCountryCodeFromLocationString(location)
        return formatPriceForCountry(amountInUSD, countryCode)
    }

    /**
     * Formats base USD amount into the target country's currency.
     */
    fun formatPriceForCountry(amountInUSD: Double, countryCode: String): String {
        val converted = convertFromUsd(amountInUSD, countryCode)
        val symbol = getCurrencySymbolForCountry(countryCode)

        return when (countryCode.uppercase().trim()) {
            "NG" -> {
                val formatter = NumberFormat.getIntegerInstance(Locale("en", "NG"))
                "$symbol${formatter.format(converted.toInt())}"
            }
            "IN" -> {
                val formatter = NumberFormat.getIntegerInstance(Locale("en", "IN"))
                "$symbol${formatter.format(converted.toInt())}"
            }
            "JP" -> {
                val formatter = NumberFormat.getIntegerInstance(Locale.JAPAN)
                "$symbol${formatter.format(converted.toInt())}"
            }
            "KE" -> {
                val formatter = NumberFormat.getIntegerInstance(Locale("en", "KE"))
                "$symbol ${formatter.format(converted.toInt())}"
            }
            "GB", "UK" -> {
                "$symbol${String.format(Locale.UK, "%.2f", converted)}"
            }
            "DE", "FR", "ES", "IT", "NL", "BE", "AT", "FI", "GR", "IE", "PT" -> {
                "$symbol${String.format(Locale.GERMANY, "%.2f", converted)}"
            }
            else -> {
                "$symbol${String.format(Locale.US, "%.2f", converted)}"
            }
        }
    }

    /**
     * Default formatPrice: uses current active recognized location from ViewModel/Helper,
     * or falls back to active country code.
     */
    fun formatPrice(amountInUSD: Double): String {
        return formatPriceForCountry(amountInUSD, activeCountryCode)
    }

    fun formatPrice(amountInUSD: Double, locale: Locale): String {
        val country = locale.country.uppercase()
        return formatPriceForCountry(amountInUSD, country.ifBlank { activeCountryCode })
    }

    fun getDetectedCountryName(locale: Locale = Locale.getDefault()): String {
        return locale.displayCountry.ifBlank { "United States" }
    }
}
