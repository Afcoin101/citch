package com.example.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

data class RecognizedLocation(
    val cityName: String,
    val countryCode: String,
    val countryName: String,
    val fullDisplayName: String,
    val latitude: Double,
    val longitude: Double,
    val currencyCode: String,
    val currencySymbol: String
)

object LocationHelper {

    /**
     * Resolves recognized location info from explicit coordinates using Android Geocoder.
     */
    suspend fun reverseGeocode(context: Context, latitude: Double, longitude: Double): RecognizedLocation? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    suspendCancellableCoroutine { cont ->
                        geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
                            override fun onGeocode(addresses: MutableList<Address>) {
                                val addr = addresses.firstOrNull()
                                if (cont.isActive) {
                                    if (addr != null) {
                                        cont.resume(addressToRecognizedLocation(addr, latitude, longitude))
                                    } else {
                                        cont.resume(null)
                                    }
                                }
                            }
                            override fun onError(errorMessage: String?) {
                                if (cont.isActive) {
                                    cont.resume(null)
                                }
                            }
                        })
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val list = geocoder.getFromLocation(latitude, longitude, 1)
                    val addr = list?.firstOrNull()
                    if (addr != null) {
                        addressToRecognizedLocation(addr, latitude, longitude)
                    } else null
                }
            } catch (e: Throwable) {
                null
            }
        }
    }

    private fun addressToRecognizedLocation(addr: Address, latitude: Double, longitude: Double): RecognizedLocation {
        val city = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: "Unknown City"
        val countryCode = addr.countryCode?.uppercase() ?: "US"
        val countryName = addr.countryName ?: "United States"
        val subLocality = addr.subLocality ?: ""
        
        val displayName = if (subLocality.isNotBlank()) {
            "$subLocality, $city"
        } else {
            "$city, $countryName"
        }

        val currencySymbol = CurrencyHelper.getCurrencySymbolForCountry(countryCode)
        val currencyCode = CurrencyHelper.getCurrencyCodeForCountry(countryCode)

        return RecognizedLocation(
            cityName = city,
            countryCode = countryCode,
            countryName = countryName,
            fullDisplayName = displayName,
            latitude = latitude,
            longitude = longitude,
            currencyCode = currencyCode,
            currencySymbol = currencySymbol
        )
    }

    /**
     * Attempts to acquire current device GPS / Network location using Google Play Services FusedLocationProviderClient.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentDeviceLocation(context: Context): Location? {
        return suspendCancellableCoroutine { cont ->
            try {
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                val cts = CancellationTokenSource()
                var hasResumed = false

                fun safeResume(loc: Location?) {
                    if (!hasResumed && cont.isActive) {
                        hasResumed = true
                        cont.resume(loc)
                    }
                }

                fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                    .addOnSuccessListener { loc: Location? ->
                        if (loc != null) {
                            safeResume(loc)
                        } else {
                            // Fallback to last known location
                            fusedClient.lastLocation
                                .addOnSuccessListener { lastLoc: Location? ->
                                    safeResume(lastLoc)
                                }
                                .addOnFailureListener {
                                    safeResume(null)
                                }
                        }
                    }
                    .addOnFailureListener {
                        // Fallback to last location
                        fusedClient.lastLocation
                            .addOnSuccessListener { lastLoc: Location? ->
                                safeResume(lastLoc)
                            }
                            .addOnFailureListener {
                                safeResume(null)
                            }
                    }

                cont.invokeOnCancellation {
                    cts.cancel()
                }
            } catch (e: Throwable) {
                if (cont.isActive) {
                    cont.resume(null)
                }
            }
        }
    }
}
