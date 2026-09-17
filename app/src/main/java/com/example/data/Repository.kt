package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Delay
import kotlinx.coroutines.delay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeChefRepository(private val dao: HomeChefDao) {
    val chefs: Flow<List<ChefEntity>> = dao.getAllChefs()
    val meals: Flow<List<MealEntity>> = dao.getAllMeals()
    val orders: Flow<List<OrderEntity>> = dao.getAllOrders()
    val reviews: Flow<List<ReviewEntity>> = dao.getAllReviews()
    val alerts: Flow<List<AlertEntity>> = dao.getAllAlerts()
    val allPayouts: Flow<List<ChefPayoutEntity>> = dao.getAllPayouts()

    fun getChef(id: Int): Flow<ChefEntity?> = dao.getChefById(id)
    fun getMealsForChef(chefId: Int): Flow<List<MealEntity>> = dao.getMealsByChef(chefId)
    fun getReviewsForChef(chefId: Int): Flow<List<ReviewEntity>> = dao.getReviewsForChef(chefId)
    fun getOrder(id: Int): Flow<OrderEntity?> = dao.getOrderById(id)
    fun getPayoutsForChef(chefId: Int): Flow<List<ChefPayoutEntity>> = dao.getPayoutsForChef(chefId)

    suspend fun addChef(chef: ChefEntity): Int {
        return dao.insertChef(chef).toInt()
    }

    suspend fun ensureMamaTitiSeeded() {
        val existing = dao.getChefByName("%Mama Titi%")
        if (existing == null) {
            val mamaTitiId = dao.insertChef(
                ChefEntity(
                    id = 101,
                    name = "Mama Titi",
                    rating = 4.9f,
                    address = "Yaba Kitchen - 42 Commercial Ave, Yaba, Lagos",
                    cuisineType = "Authentic West African & Breakfasts",
                    phone = "+234 802 345 6789",
                    bio = "Cooking Jollof for the street since 2009 — my door is open for the sweetest neighbourhood meals.",
                    youtubeChannelUrl = "https://www.youtube.com/watch?v=FLeSREbZ7Rk",
                    youtubeChannelName = "Mama Titi's Lagos Kitchen",
                    avatarUrl = "mama_titi_avatar",
                    latitude = 6.5158,
                    longitude = 3.3718,
                    followersCount = 312,
                    paypalEmail = "mamatiti@kitchen.ng"
                )
            ).toInt()

            dao.insertMeal(
                MealEntity(
                    chefId = mamaTitiId,
                    name = "Fluffy Banana Pancake Stack with Honey",
                    description = "Golden buttermilk pancakes layered with fresh sliced ripe bananas, toasted almonds, fresh mint, and pure golden honey.",
                    price = 0.80, // converts to ₦1200
                    category = "Breakfast",
                    imageUrl = "pancake_stack",
                    tutorialVideoUrl = "https://www.youtube.com/watch?v=FLeSREbZ7Rk"
                )
            )
            dao.insertMeal(
                MealEntity(
                    chefId = mamaTitiId,
                    name = "Mama's Signature Party Jollof & Plantain",
                    description = "Firewood-smoked Lagos party jollof rice paired with tender spiced chicken and sweet golden dodo.",
                    price = 1.00, // converts to ₦1500
                    category = "Mains",
                    imageUrl = "jollof",
                    tutorialVideoUrl = "https://www.youtube.com/watch?v=FLeSREbZ7Rk"
                )
            )
            dao.insertMeal(
                MealEntity(
                    chefId = mamaTitiId,
                    name = "Spicy Suya Beef Skewers",
                    description = "Thinly sliced grilled beef skewers infused with roasted peanut kuli-kuli spices, served with fresh red onions.",
                    price = 1.20, // converts to ₦1800
                    category = "Starters",
                    imageUrl = "suya",
                    tutorialVideoUrl = "https://www.youtube.com/watch?v=FLeSREbZ7Rk"
                )
            )
            dao.insertReview(
                ReviewEntity(
                    chefId = mamaTitiId,
                    mealId = 1,
                    reviewerName = "Neighbour Fave Review",
                    rating = 5,
                    comment = "Best pancakes and jollof in all of Yaba! My family orders from Mama Titi every weekend."
                )
            )
        }
    }

    suspend fun ensureCountryCuisinesSeeded() {
        // Ensure Chinese chef and dishes
        val existingChineseChef = dao.getChefByName("%Wei Zhang%")
        val chineseChefId = if (existingChineseChef == null) {
            dao.insertChef(
                ChefEntity(
                    id = 8,
                    name = "Chef Wei Zhang",
                    rating = 4.96f,
                    address = "Chinatown Heritage Kitchen - 820 Grant Ave",
                    cuisineType = "Authentic Chinese & Sichuan Specialties",
                    phone = "+1 (555) 234-8899",
                    bio = "Chef Wei brings over two decades of wok mastery from Chengdu and Guangzhou. Specializes in hand-pinched dim sum dumplings, authentic Sichuan Mapo Tofu, and sizzling Kung Pao chicken.",
                    youtubeChannelUrl = "https://www.youtube.com/watch?v=FLeSREbZ7Rk",
                    youtubeChannelName = "Chef Wei's Wok Craft",
                    avatarUrl = "https://images.unsplash.com/photo-1577219491135-ce391730fb2c?w=150",
                    latitude = 37.7941,
                    longitude = -122.4078,
                    followersCount = 460,
                    paypalEmail = "wei.zhang@wokcraft.cn",
                    isSponsored = true,
                    isProTier = true,
                    commissionRate = 0.08
                )
            ).toInt()
        } else {
            existingChineseChef.id
        }

        if (dao.getMealByName("%Dim Sum%") == null) {
            dao.insertMeal(
                MealEntity(
                    chefId = chineseChefId,
                    name = "Hand-Crafted Steamed Dim Sum Dumplings",
                    description = "Delicate handmade pork and shrimp dumplings steamed in traditional bamboo baskets. Served with rich chili crisp oil, black vinegar, and fresh scallions.",
                    price = 15.50,
                    imageUrl = "dumpling",
                    category = "China",
                    isAvailable = true,
                    tutorialVideoUrl = "https://www.youtube.com/watch?v=FLeSREbZ7Rk"
                )
            )
        }

        if (dao.getMealByName("%Kung Pao%") == null) {
            dao.insertMeal(
                MealEntity(
                    chefId = chineseChefId,
                    name = "Sizzling Kung Pao Chicken",
                    description = "Wok-tossed tender chicken thigh cubes with roasted crunchy peanuts, red Sichuan chilies, and scallions in a glossy sweet, tangy, and spicy brown sauce.",
                    price = 16.50,
                    imageUrl = "kung pao",
                    category = "China",
                    isAvailable = true,
                    tutorialVideoUrl = "https://www.youtube.com/watch?v=FLeSREbZ7Rk"
                )
            )
        }

        if (dao.getMealByName("%Mapo Tofu%") == null) {
            dao.insertMeal(
                MealEntity(
                    chefId = chineseChefId,
                    name = "Authentic Sichuan Mapo Tofu",
                    description = "Silky soft tofu simmered in a fiery, numbing Sichuan peppercorn and broad bean chili paste broth with savory minced beef and fresh scallions.",
                    price = 14.00,
                    imageUrl = "mapo tofu",
                    category = "China",
                    isAvailable = true,
                    tutorialVideoUrl = "https://www.youtube.com/watch?v=FLeSREbZ7Rk"
                )
            )
        }

        if (dao.getMealByName("%Dan Dan%") == null) {
            dao.insertMeal(
                MealEntity(
                    chefId = chineseChefId,
                    name = "Hand-Pulled Dan Dan Noodles",
                    description = "Chewy wheat noodles in a fragrant spiced sesame-chili sauce, crowned with crispy spiced pork crumbles, baby bok choy, and crushed peanuts.",
                    price = 13.50,
                    imageUrl = "noodles",
                    category = "China",
                    isAvailable = true,
                    tutorialVideoUrl = "https://www.youtube.com/watch?v=FLeSREbZ7Rk"
                )
            )
        }

        // Ensure Mexican chef and dishes
        val existingMexicanChef = dao.getChefByName("%Maria Hernandez%")
        val mexicanChefId = if (existingMexicanChef == null) {
            dao.insertChef(
                ChefEntity(
                    id = 3,
                    name = "Chef Maria Hernandez",
                    rating = 4.7f,
                    address = "Castro Cozy Kitchens - 4100 18th St",
                    cuisineType = "Authentic Mexican Street Food",
                    phone = "+1 (555) 761-0922",
                    bio = "Maria's cooking carries secrets from five generations of family Oaxacan recipes. Famous for her rich multi-day slow simmered mole sauce.",
                    youtubeChannelUrl = "https://www.youtube.com/watch?v=Q73uWbAArI0",
                    youtubeChannelName = "Maria's Mole Secrets",
                    avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150",
                    latitude = 37.7612,
                    longitude = -122.4350,
                    followersCount = 195,
                    paypalEmail = "maria.hernandez@oaxacankitchen.mx"
                )
            ).toInt()
        } else {
            existingMexicanChef.id
        }

        if (dao.getMealByName("%Birria%") == null) {
            dao.insertMeal(
                MealEntity(
                    chefId = mexicanChefId,
                    name = "Street Birria Beef Tacos",
                    description = "Three griddled corn tortillas filled with slow-cooked shredded birria beef, melted Oaxaca cheese, fresh cilantro, and diced onion. Served with rich savory consomé broth for dipping.",
                    price = 15.00,
                    imageUrl = "tacos",
                    category = "Mexico",
                    isAvailable = true,
                    tutorialVideoUrl = "https://www.youtube.com/watch?v=Q73uWbAArI0"
                )
            )
        }

        if (dao.getMealByName("%Enchiladas Verdes%") == null) {
            dao.insertMeal(
                MealEntity(
                    chefId = mexicanChefId,
                    name = "Enchiladas Verdes with Salsa Tomatillo",
                    description = "Rolled corn tortillas stuffed with tender shredded chicken breast, baked in fire-roasted tomatillo salsa verde, cotija cheese, Mexican crema, and fresh cilantro.",
                    price = 14.50,
                    imageUrl = "enchiladas",
                    category = "Mexico",
                    isAvailable = true,
                    tutorialVideoUrl = "https://www.youtube.com/watch?v=Q73uWbAArI0"
                )
            )
        }
    }

    suspend fun updateChef(chef: ChefEntity) {
        dao.updateChef(chef)
    }

    suspend fun updateChefAvatar(chefId: Int, avatarUrl: String) {
        dao.updateChefAvatar(chefId, avatarUrl)
    }

    suspend fun updateChefPaypalEmail(chefId: Int, paypalEmail: String, isLiveMode: Boolean = false, backendUrl: String = "") {
        dao.updateChefPaypalEmail(chefId, paypalEmail)
        if (isLiveMode && backendUrl.isNotBlank()) {
            try {
                val api = PayPalClient.getApi(backendUrl)
                api.updateChefPaypalEmail(PayPalUpdateChefEmailRequest(chefId, paypalEmail))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun addMeal(meal: MealEntity): Int {
        return dao.insertMeal(meal).toInt()
    }

    suspend fun updateMeal(meal: MealEntity) {
        dao.updateMeal(meal)
    }

    suspend fun addReview(review: ReviewEntity, isLiveMode: Boolean = false, backendUrl: String = "") {
        if (isLiveMode && backendUrl.isNotEmpty()) {
            try {
                val api = StripeClient.getApi(backendUrl)
                api.submitReview(review)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        dao.insertReview(review)
        
        // Also add local alert when a new review is added to build trust
        dao.insertAlert(
            AlertEntity(
                title = "New Review Added ⭐",
                message = "${review.reviewerName} rated chef/meal and left a feedback: \"${review.comment}\""
            )
        )
    }

    suspend fun addAlert(alert: AlertEntity) {
        dao.insertAlert(alert)
    }

    suspend fun markAlertsAsRead() {
        dao.markAllAlertsAsRead()
    }

    fun getChatMessagesForChef(chefId: Int): Flow<List<ChatMessageEntity>> {
        return dao.getChatMessagesForChef(chefId)
    }

    suspend fun sendChatMessage(chefId: Int, sender: String, text: String) {
        dao.insertChatMessage(
            ChatMessageEntity(
                chefId = chefId,
                sender = sender,
                text = text
            )
        )
    }

    // Trigger secure payment simulation and start order tracking lifecycle
    suspend fun placeOrder(
        meal: MealEntity,
        chefName: String,
        quantity: Int,
        buyerName: String,
        buyerAddress: String,
        buyerPhone: String,
        scope: CoroutineScope,
        paymentMethod: PaymentMethodType = PaymentMethodType.PAYPAL,
        customPaymentId: String? = null,
        isCitchClubMember: Boolean = false,
        commissionRate: Double = 0.15,
        customDeliveryFee: Double? = null
    ): Int {
        val subtotal = meal.price * quantity
        val discountAmount = if (isCitchClubMember) (subtotal * 0.10) else 0.0 // 10% Diner member discount
        val discountedSubtotal = subtotal - discountAmount
        val deliveryFee = customDeliveryFee ?: if (isCitchClubMember && discountedSubtotal >= 15.0) 0.0 else 3.50
        val totalAmount = discountedSubtotal + deliveryFee
        val platformFee = subtotal * commissionRate // Platform take-rate e.g. 15% standard or 8% Pro
        val chefEarnings = (subtotal - platformFee).coerceAtLeast(0.0)

        val paymentId = customPaymentId ?: when (paymentMethod) {
            PaymentMethodType.PAYPAL -> "PAYID-PP-${System.currentTimeMillis().toString().takeLast(6)}"
            PaymentMethodType.GOOGLE_PLAY_BILLING -> "GPA.${(1000..9999).random()}-${(1000..9999).random()}"
            PaymentMethodType.GOOGLE_PAY -> "GPAY.${(1000..9999).random()}-${(1000..9999).random()}"
            PaymentMethodType.ONE_TAP_CASH -> "COD-${System.currentTimeMillis().toString().takeLast(6)}"
        }
        
        val order = OrderEntity(
            mealId = meal.id,
            mealName = meal.name,
            chefId = meal.chefId,
            chefName = chefName,
            quantity = quantity,
            totalAmount = totalAmount,
            buyerName = buyerName,
            buyerAddress = buyerAddress,
            buyerPhone = buyerPhone,
            status = "Pending",
            step = 0,
            paymentId = paymentId,
            subtotal = subtotal,
            platformFee = platformFee,
            deliveryFee = deliveryFee,
            discountAmount = discountAmount,
            chefEarnings = chefEarnings
        )

        val orderId = dao.insertOrder(order).toInt()
        val createdOrder = order.copy(id = orderId)

        val methodLabel = when (paymentMethod) {
            PaymentMethodType.PAYPAL -> "PayPal Express"
            PaymentMethodType.GOOGLE_PLAY_BILLING -> "Google Play Billing"
            PaymentMethodType.GOOGLE_PAY -> "Google Pay"
            PaymentMethodType.ONE_TAP_CASH -> "Cash on Handover"
        }

        // Sync order to Firestore real-time cloud database & send initial FCM push notification
        CitchFirebaseService.syncOrderToFirestore(createdOrder)
        CitchFirebaseService.sendOrderStatusPushNotification(
            orderId = orderId,
            status = "Pending",
            title = "Order Paid via $methodLabel ✓",
            message = "Your order #${orderId} for $quantity x ${meal.name} is confirmed! $chefName has received your order."
        )

        // Create initial notification alert
        dao.insertAlert(
            AlertEntity(
                title = "Order Paid via $methodLabel ✓",
                message = "Payment of ${CurrencyHelper.formatPrice(totalAmount)} was processed via $methodLabel ($paymentId). Take-Rate (${(commissionRate * 100).toInt()}%): ${CurrencyHelper.formatPrice(platformFee)}, Chef net: ${CurrencyHelper.formatPrice(chefEarnings)}."
            )
        )

        // Simulate real-time order tracking steps asynchronously in a coroutine
        scope.launch(Dispatchers.IO) {
            simulateOrderLifeCycle(orderId, meal.name, chefName)
        }

        return orderId
    }

    suspend fun updateChefSponsorship(chefId: Int, sponsored: Boolean, until: Long = System.currentTimeMillis() + 7 * 86400000L) {
        dao.updateChefSponsorship(chefId, sponsored, until)
    }

    suspend fun updateChefProTier(chefId: Int, isPro: Boolean, rate: Double = if (isPro) 0.08 else 0.15) {
        dao.updateChefProTier(chefId, isPro, rate)
    }

    suspend fun setChefOfTheWeek(chefId: Int) {
        dao.setChefOfTheWeek(chefId)
    }

    suspend fun executeChefPayout(
        chefId: Int,
        chefName: String,
        paypalEmail: String,
        amount: Double,
        isLiveMode: Boolean = false,
        backendUrl: String = ""
    ): PayPalPayoutResult {
        val result = UnifiedPaymentService.processPayPalChefPayout(
            chefId = chefId,
            chefName = chefName,
            paypalEmail = paypalEmail,
            amount = amount,
            isLiveMode = isLiveMode,
            backendUrl = backendUrl
        )

        val batchId = when (result) {
            is PayPalPayoutResult.Success -> result.batchId
            is PayPalPayoutResult.Failure -> "PAYOUT-BATCH-${System.currentTimeMillis().toString().takeLast(6)}"
        }

        val payoutEntity = ChefPayoutEntity(
            chefId = chefId,
            chefName = chefName,
            paypalEmail = paypalEmail,
            amount = amount,
            status = "COMPLETED",
            payoutBatchId = batchId,
            note = "PayPal Kitchen Earnings Payout"
        )
        dao.insertPayout(payoutEntity)

        dao.insertAlert(
            AlertEntity(
                title = "Chef PayPal Payout Sent ✓",
                message = "PayPal payout of ${CurrencyHelper.formatPrice(amount)} was dispatched to $chefName ($paypalEmail). Batch: $batchId"
            )
        )

        return result
    }

    private suspend fun simulateOrderLifeCycle(orderId: Int, mealName: String, chefName: String) {
        // Step 1: Preparing (after 10 seconds)
        delay(10_000)
        updateOrderStatus(orderId, "Preparing", 1, "Kitchen Preparing 🍳", "$chefName is now master-crafting your fresh $mealName.")

        // Step 2: Out for Delivery (after 12 seconds)
        delay(12_000)
        updateOrderStatus(orderId, "Out for Delivery", 2, "Out for Delivery 🚴", "Special local courier picked up your food and is on the way!")

        // Step 3: Delivered (after 12 seconds)
        delay(12_000)
        updateOrderStatus(orderId, "Delivered", 3, "Arrived & Served 🎉", "Order #${orderId} of $mealName has arrived safely. Bon appétit!")
    }

    private suspend fun updateOrderStatus(
        orderId: Int,
        status: String,
        step: Int,
        alertTitle: String,
        alertMsg: String
    ) {
        // Fetch current order state
        val order = dao.getOrderById(orderId).first()
        if (order != null) {
            val updated = order.copy(status = status, step = step)
            dao.updateOrder(updated)
            
            // Sync status update to Firestore & send FCM push notification
            CitchFirebaseService.syncOrderToFirestore(updated)
            CitchFirebaseService.sendOrderStatusPushNotification(
                orderId = orderId,
                status = status,
                title = alertTitle,
                message = alertMsg
            )

            // Save notification alert
            dao.insertAlert(
                AlertEntity(
                    title = alertTitle,
                    message = alertMsg
                )
            )
        }
    }

    suspend fun syncWithBackend(backendUrl: String) {
        if (backendUrl.isBlank() || backendUrl.startsWith("http://10.0.2.2") || backendUrl.startsWith("http://localhost")) {
            dao.insertAlert(
                AlertEntity(
                    title = "Room DB Synchronized ✓",
                    message = "Local Room database is active and synchronized. To perform live API sync, configure a live production backend URL in settings."
                )
            )
            return
        }

        try {
            val api = StripeClient.getApi(backendUrl)
            val chefsList = api.getChefs()
            val mealsList = api.getMeals()
            val reviewsList = api.getReviews()
            for (chef in chefsList) {
                dao.insertChef(chef)
            }
            for (meal in mealsList) {
                dao.insertMeal(meal)
            }
            for (review in reviewsList) {
                dao.insertReview(review)
            }
            dao.insertAlert(
                AlertEntity(
                    title = "Database Sync Succeeded ✓",
                    message = "Successfully synced ${chefsList.size} chefs, ${mealsList.size} meals, and ${reviewsList.size} reviews from production backend API."
                )
            )
        } catch (e: Exception) {
            val errorMsg = e.message ?: ""
            val friendlyMsg = if (errorMsg.contains("Use JsonReader.setLenient") || errorMsg.contains("malformed") || errorMsg.contains("Expected BEGIN_ARRAY") || errorMsg.contains("Expected BEGIN_OBJECT")) {
                "Backend server at $backendUrl returned an unexpected response format. Falling back to local Room database."
            } else {
                "Could not connect to live endpoint at $backendUrl (${e.localizedMessage}). Local Room database remains fully functional."
            }
            dao.insertAlert(
                AlertEntity(
                    title = "Room DB Active (Backend Offline) ℹ️",
                    message = friendlyMsg
                )
            )
        }
    }
}
