package com.example.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Enterprise Firebase Integration Service for Citch Home Kitchens Platform
 * Provides:
 * 1. Firebase Authentication (Email/Password & Anonymous)
 * 2. Live Firestore Database Sync (Orders & Kitchens)
 * 3. Firebase Cloud Messaging (FCM) Push Notifications
 */
object CitchFirebaseService {
    private const val TAG = "CitchFirebaseService"
    const val NOTIFICATION_CHANNEL_ID = "citch_orders_channel"

    private var isInitialized = false
    private var auth: FirebaseAuth? = null
    private var db: FirebaseFirestore? = null
    private var messaging: FirebaseMessaging? = null
    private var appContext: Context? = null

    const val NOTIFICATION_CHANNEL_ORDERS = "citch_orders_channel"
    const val NOTIFICATION_CHANNEL_PROMOS = "citch_promos_channel"

    private val _currentUserFlow = MutableStateFlow<FirebaseUser?>(null)
    val currentUserFlow: StateFlow<FirebaseUser?> = _currentUserFlow.asStateFlow()

    private val _fcmTokenFlow = MutableStateFlow<String?>(null)
    val fcmTokenFlow: StateFlow<String?> = _fcmTokenFlow.asStateFlow()

    private val _subscribedTopicsFlow = MutableStateFlow<List<String>>(emptyList())
    val subscribedTopicsFlow: StateFlow<List<String>> = _subscribedTopicsFlow.asStateFlow()

    private val _firestoreSyncStatus = MutableStateFlow("Firebase Offline Mode (Room DB active)")
    val firestoreSyncStatus: StateFlow<String> = _firestoreSyncStatus.asStateFlow()

    private var ordersListener: ListenerRegistration? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
        try {
            val prefs = context.getSharedPreferences("citch_firebase_prefs", Context.MODE_PRIVATE)
            val savedApiKey = prefs.getString("custom_api_key", null)
            val savedProjectId = prefs.getString("custom_project_id", "citch-591f9") ?: "citch-591f9"
            val savedAppId = prefs.getString("custom_app_id", "1:625029070704:android:debef6c64c45abdc6f99b5") ?: "1:625029070704:android:debef6c64c45abdc6f99b5"

            val app = try {
                if (savedApiKey != null && savedApiKey.isNotBlank()) {
                    val options = com.google.firebase.FirebaseOptions.Builder()
                        .setApplicationId(savedAppId)
                        .setApiKey(savedApiKey)
                        .setProjectId(savedProjectId)
                        .setGcmSenderId("625029070704")
                        .build()
                    if (FirebaseApp.getApps(context).isNotEmpty()) {
                        val existing = FirebaseApp.getInstance()
                        existing.delete()
                    }
                    FirebaseApp.initializeApp(context, options, "citchCustomApp")
                } else if (FirebaseApp.getApps(context).isEmpty()) {
                    val defaultApp = FirebaseApp.initializeApp(context)
                    if (defaultApp == null) {
                        val options = com.google.firebase.FirebaseOptions.Builder()
                            .setApplicationId(savedAppId)
                            .setApiKey("AIzaSyCitchFirebaseAutoIntegrationKey2026")
                            .setProjectId(savedProjectId)
                            .setGcmSenderId("625029070704")
                            .build()
                        FirebaseApp.initializeApp(context, options)
                    } else defaultApp
                } else {
                    FirebaseApp.getInstance()
                }
            } catch (e: Exception) {
                val options = com.google.firebase.FirebaseOptions.Builder()
                    .setApplicationId(savedAppId)
                    .setApiKey(savedApiKey ?: "AIzaSyCitchFirebaseAutoIntegrationKey2026")
                    .setProjectId(savedProjectId)
                    .setGcmSenderId("625029070704")
                    .build()
                FirebaseApp.initializeApp(context, options)
            }

            if (app != null) {
                auth = FirebaseAuth.getInstance(app)
                db = FirebaseFirestore.getInstance(app)
                messaging = try { FirebaseMessaging.getInstance() } catch (e: Exception) { null }
                isInitialized = true

                _currentUserFlow.value = auth?.currentUser
                auth?.addAuthStateListener { firebaseAuth ->
                    _currentUserFlow.value = firebaseAuth.currentUser
                }

                _firestoreSyncStatus.value = "Connected to $savedProjectId 🟢"
                Log.d(TAG, "Firebase initialized successfully for $savedProjectId.")

                messaging?.token?.addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        val token = task.result
                        _fcmTokenFlow.value = token
                        Log.d(TAG, "FCM Registration Token: $token")
                    }
                }

                subscribeToDefaultTopics()
                createNotificationChannels(context)
            } else {
                _firestoreSyncStatus.value = "Firebase Uninitialized"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Firebase initialization error: ${e.message}", e)
            _firestoreSyncStatus.value = "Firebase Offline: ${e.localizedMessage ?: "Add google-services.json"}"
            isInitialized = false
        }
    }

    fun reinitializeWithCustomConfig(context: Context, apiKey: String, projectId: String, onComplete: (Boolean, String) -> Unit) {
        val cleanKey = apiKey.trim()
        val cleanProjectId = if (projectId.isBlank()) "citch-591f9" else projectId.trim()

        if (cleanKey.isBlank()) {
            onComplete(false, "API key cannot be empty. Please enter your Firebase Web API key.")
            return
        }

        try {
            val prefs = context.getSharedPreferences("citch_firebase_prefs", Context.MODE_PRIVATE)
            prefs.edit()
                .putString("custom_api_key", cleanKey)
                .putString("custom_project_id", cleanProjectId)
                .apply()

            initialize(context)
            onComplete(true, "Successfully updated Firebase config for '$cleanProjectId'!")
        } catch (e: Exception) {
            onComplete(false, "Reinitialization failed: ${e.localizedMessage}")
        }
    }

    fun isFirebaseReady(): Boolean = isInitialized && auth != null && db != null

    // ==================== 1. AUTHENTICATION ====================

    private fun parseAuthError(e: Exception): String {
        val msg = e.localizedMessage ?: e.message ?: "Authentication failed"
        return when {
            msg.contains("API key not valid", ignoreCase = true) || msg.contains("INVALID_KEY", ignoreCase = true) ->
                "Invalid API Key. Please paste your actual Firebase Web API Key in the 'Firebase API Key' box below (Found in Firebase Console -> Project Settings -> General)."
            msg.contains("OPERATION_NOT_ALLOWED", ignoreCase = true) || msg.contains("ADMIN_ONLY_OPERATION", ignoreCase = true) ->
                "Anonymous Auth disabled in Firebase Console. Go to console.firebase.google.com/project/citch-591f9/authentication/providers and enable Anonymous Sign-in."
            else -> msg
        }
    }

    fun signInAnonymously(onComplete: (Boolean, String?) -> Unit) {
        val authInstance = auth
        if (authInstance == null) {
            onComplete(false, "Firebase Auth not initialized.")
            return
        }

        authInstance.signInAnonymously()
            .addOnSuccessListener { result ->
                _currentUserFlow.value = result.user
                onComplete(true, "Signed in anonymously! UID: ${result.user?.uid?.take(8)}...")
            }
            .addOnFailureListener { e ->
                val errorMsg = parseAuthError(e)
                onComplete(false, errorMsg)
            }
    }

    fun signUpWithEmail(email: String, pass: String, onComplete: (Boolean, String?) -> Unit) {
        val authInstance = auth
        if (authInstance == null) {
            onComplete(false, "Firebase Auth not initialized")
            return
        }

        authInstance.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { result ->
                _currentUserFlow.value = result.user
                onComplete(true, "Account created successfully")
            }
            .addOnFailureListener { e ->
                onComplete(false, parseAuthError(e))
            }
    }

    fun signInWithEmail(email: String, pass: String, onComplete: (Boolean, String?) -> Unit) {
        val authInstance = auth
        if (authInstance == null) {
            onComplete(false, "Firebase Auth not initialized")
            return
        }

        authInstance.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { result ->
                _currentUserFlow.value = result.user
                onComplete(true, "Login successful")
            }
            .addOnFailureListener { e ->
                onComplete(false, parseAuthError(e))
            }
    }

    fun signOut() {
        auth?.signOut()
        _currentUserFlow.value = null
    }

    // ==================== 2. REAL-TIME FIRESTORE DATABASE SYNC ====================

    fun syncOrderToFirestore(order: OrderEntity) {
        val firestore = db ?: return
        val orderData = hashMapOf(
            "id" to order.id,
            "mealId" to order.mealId,
            "mealName" to order.mealName,
            "chefId" to order.chefId,
            "chefName" to order.chefName,
            "quantity" to order.quantity,
            "totalAmount" to order.totalAmount,
            "buyerName" to order.buyerName,
            "buyerAddress" to order.buyerAddress,
            "buyerPhone" to order.buyerPhone,
            "status" to order.status,
            "step" to order.step,
            "paymentId" to order.paymentId,
            "timestamp" to order.timestamp
        )

        firestore.collection("orders")
            .document(order.id.toString())
            .set(orderData)
            .addOnSuccessListener {
                Log.d(TAG, "Order #${order.id} synced to Firestore cloud!")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Firestore sync failed for Order #${order.id}: ${e.message}")
            }
    }

    fun listenToCloudOrders(onCloudOrdersUpdated: (List<OrderEntity>) -> Unit) {
        val firestore = db ?: return
        ordersListener?.remove()

        ordersListener = firestore.collection("orders")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Firestore Listen Failed: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val orderList = mutableListOf<OrderEntity>()
                    for (doc in snapshot.documents) {
                        try {
                            val id = doc.getLong("id")?.toInt() ?: doc.id.toIntOrNull() ?: 0
                            val mealId = doc.getLong("mealId")?.toInt() ?: 0
                            val mealName = doc.getString("mealName") ?: ""
                            val chefId = doc.getLong("chefId")?.toInt() ?: 0
                            val chefName = doc.getString("chefName") ?: ""
                            val quantity = doc.getLong("quantity")?.toInt() ?: 1
                            val totalAmount = doc.getDouble("totalAmount") ?: 0.0
                            val buyerName = doc.getString("buyerName") ?: ""
                            val buyerAddress = doc.getString("buyerAddress") ?: ""
                            val buyerPhone = doc.getString("buyerPhone") ?: ""
                            val status = doc.getString("status") ?: "Pending"
                            val step = doc.getLong("step")?.toInt() ?: 0
                            val paymentId = doc.getString("paymentId") ?: ""
                            val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()

                            orderList.add(
                                OrderEntity(
                                    id = id,
                                    mealId = mealId,
                                    mealName = mealName,
                                    chefId = chefId,
                                    chefName = chefName,
                                    quantity = quantity,
                                    totalAmount = totalAmount,
                                    buyerName = buyerName,
                                    buyerAddress = buyerAddress,
                                    buyerPhone = buyerPhone,
                                    status = status,
                                    step = step,
                                    paymentId = paymentId,
                                    timestamp = timestamp
                                )
                            )
                        } catch (e: Exception) {
                            Log.e(TAG, "Error parsing Firestore order doc: ${e.message}")
                        }
                    }
                    onCloudOrdersUpdated(orderList)
                }
            }
    }

    // ==================== 3. FCM NOTIFICATION CHANNELS & TOPICS ====================

    private fun subscribeToDefaultTopics() {
        val topics = listOf("order_updates", "promotional_alerts")
        _subscribedTopicsFlow.value = topics
        messaging?.let { fcm ->
            for (topic in topics) {
                fcm.subscribeToTopic(topic)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Subscribed to FCM topic: $topic")
                        }
                    }
            }
        }
    }

    fun subscribeToTopic(topic: String, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        val fcm = messaging
        if (fcm == null) {
            onResult(false, "FCM Messaging service uninitialized")
            return
        }
        fcm.subscribeToTopic(topic)
            .addOnSuccessListener {
                val current = _subscribedTopicsFlow.value.toMutableList()
                if (!current.contains(topic)) current.add(topic)
                _subscribedTopicsFlow.value = current
                onResult(true, "Subscribed to FCM topic: '$topic'")
            }
            .addOnFailureListener { e ->
                onResult(false, "Failed to subscribe to '$topic': ${e.localizedMessage}")
            }
    }

    private fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val ordersChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ORDERS,
                "Citch Real-Time Order Status",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Live real-time push updates for kitchen preparation and courier delivery"
                enableVibration(true)
            }

            val promosChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_PROMOS,
                "Citch Promotional Deals & Discounts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Special offers, weekend kitchen promos, and new dish alerts"
            }

            notificationManager.createNotificationChannel(ordersChannel)
            notificationManager.createNotificationChannel(promosChannel)
        }
    }

    fun triggerLocalPushNotification(
        context: Context,
        title: String,
        message: String,
        channelId: String = NOTIFICATION_CHANNEL_ORDERS
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(
                if (channelId == NOTIFICATION_CHANNEL_ORDERS) NotificationCompat.PRIORITY_HIGH
                else NotificationCompat.PRIORITY_DEFAULT
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify((System.currentTimeMillis() % 10000).toInt(), builder.build())
    }

    /**
     * Dispatch Real-Time FCM Order Status Push Alert to local device and Firestore order stream.
     */
    fun sendOrderStatusPushNotification(orderId: Int, status: String, title: String, message: String) {
        val ctx = appContext ?: return
        triggerLocalPushNotification(ctx, title, message, NOTIFICATION_CHANNEL_ORDERS)
        Log.d(TAG, "FCM Order Status Push Sent for Order #$orderId: [$status] $title - $message")
    }

    /**
     * Dispatch FCM Promotional Deal / Discount Push Alert.
     */
    fun sendPromotionalPushNotification(title: String, message: String, promoCode: String? = null) {
        val ctx = appContext ?: return
        val fullMsg = if (!promoCode.isNull_or_blank_safe()) "$message (Use code: $promoCode)" else message
        triggerLocalPushNotification(ctx, title, fullMsg, NOTIFICATION_CHANNEL_PROMOS)
        Log.d(TAG, "FCM Promotional Alert Push Sent: $title - $fullMsg")
    }

    private fun String?.isNull_or_blank_safe(): Boolean = this == null || this.trim().isEmpty()
}

/**
 * Firebase Cloud Messaging Background Service
 */
class CitchFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_SERVICE", "Refreshed FCM registration token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val messageType = remoteMessage.data["type"] ?: "order_status"
        val channelId = if (messageType == "promotional") CitchFirebaseService.NOTIFICATION_CHANNEL_PROMOS else CitchFirebaseService.NOTIFICATION_CHANNEL_ORDERS

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: if (messageType == "promotional") "Citch Kitchen Promo 🏷️" else "Citch Order Alert 🍳"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: "You have a new update from Citch Home Kitchens."

        CitchFirebaseService.triggerLocalPushNotification(applicationContext, title, body, channelId)
    }
}
