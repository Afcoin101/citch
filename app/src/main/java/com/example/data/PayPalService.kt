package com.example.data

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

// Customer PayPal checkout order creation request
data class PayPalCreateOrderRequest(
    val amount: Double,
    val currency: String = "USD",
    val description: String,
    val buyerEmail: String = "",
    val chefId: Int = 0,
    val dishName: String = ""
)

// Response from PayPal create-order endpoint
data class PayPalCreateOrderResponse(
    val id: String? = null,
    val status: String? = null,
    val approveUrl: String? = null,
    val environment: String? = null,
    val error: String? = null
)

// Request to capture authorized PayPal order
data class PayPalCaptureOrderRequest(
    val orderId: String,
    val chefId: Int,
    val amount: Double,
    val buyerEmail: String = "",
    val chefPaypalEmail: String = ""
)

// Response from PayPal capture-order endpoint
data class PayPalCaptureOrderResponse(
    val id: String? = null,
    val orderId: String? = null,
    val status: String? = null,
    val transactionId: String? = null,
    val amount: Double? = null,
    val chefId: Int? = null,
    val chefName: String? = null,
    val chefPaypalEmail: String? = null,
    val timestamp: Long? = null,
    val message: String? = null,
    val error: String? = null
)

// Chef PayPal Payout Request
data class PayPalPayoutRequest(
    val chefId: Int,
    val chefName: String,
    val paypalEmail: String,
    val amount: Double,
    val note: String = "Chef Culinary Order Payout"
)

// Response from PayPal Payout endpoint
data class PayPalPayoutResponse(
    val batchId: String? = null,
    val status: String? = null,
    val amount: Double? = null,
    val paypalEmail: String? = null,
    val timestamp: Long? = null,
    val message: String? = null,
    val error: String? = null
)

// Item in Chef Payouts history
data class PayPalPayoutItem(
    val id: Int = 0,
    val chefId: Int = 0,
    val chefName: String = "",
    val paypalEmail: String = "",
    val amount: Double = 0.0,
    val status: String = "COMPLETED",
    val payoutBatchId: String = "",
    val note: String = "",
    val timestamp: Long = 0L
)

// Request to update chef's PayPal account email
data class PayPalUpdateChefEmailRequest(
    val chefId: Int,
    val paypalEmail: String
)

interface PayPalBackendApi {
    @POST("paypal/create-order")
    suspend fun createOrder(@Body request: PayPalCreateOrderRequest): PayPalCreateOrderResponse

    @POST("paypal/capture-order")
    suspend fun captureOrder(@Body request: PayPalCaptureOrderRequest): PayPalCaptureOrderResponse

    @POST("paypal/payout")
    suspend fun sendPayout(@Body request: PayPalPayoutRequest): PayPalPayoutResponse

    @GET("paypal/chef-payouts")
    suspend fun getChefPayouts(@Query("chefId") chefId: Int): List<PayPalPayoutItem>

    @POST("paypal/update-chef-paypal")
    suspend fun updateChefPaypalEmail(@Body request: PayPalUpdateChefEmailRequest): Map<String, Any>
}

sealed class PayPalPaymentResult {
    data class Success(
        val orderId: String,
        val transactionId: String,
        val amount: Double,
        val chefName: String,
        val buyerEmail: String
    ) : PayPalPaymentResult()

    data class Failure(
        val errorMessage: String
    ) : PayPalPaymentResult()
}

sealed class PayPalPayoutResult {
    data class Success(
        val batchId: String,
        val chefId: Int,
        val chefName: String,
        val paypalEmail: String,
        val amount: Double,
        val message: String
    ) : PayPalPayoutResult()

    data class Failure(
        val errorMessage: String
    ) : PayPalPayoutResult()
}

object PayPalClient {
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    fun getApi(baseUrl: String): PayPalBackendApi {
        val sanitizedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return Retrofit.Builder()
            .baseUrl(sanitizedUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
            .build()
            .create(PayPalBackendApi::class.java)
    }
}
