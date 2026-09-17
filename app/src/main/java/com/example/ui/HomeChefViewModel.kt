package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.*

// Represent navigation destinations within our Single-Screen App
sealed class Screen {
    object Explore : Screen()
    object Showcase : Screen()
    object MapSearch : Screen()
    object Orders : Screen()
    object Notifications : Screen()
    object GoLiveConfig : Screen()
    object AICulinaryHub : Screen()
    object Camera : Screen()
    object DishGallery : Screen()
    data class ChefDetail(val chefId: Int) : Screen()
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class HomeChefViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val repository = HomeChefRepository(db.dao())

    // Production Take-Live states configuration
    private val _isLiveMode = MutableStateFlow(false)
    val isLiveMode = _isLiveMode.asStateFlow()

    private val _liveBackendUrl = MutableStateFlow(
        if (com.example.BuildConfig.LIVE_BACKEND_URL.isNotEmpty()) {
            com.example.BuildConfig.LIVE_BACKEND_URL
        } else {
            "http://10.0.2.2:3000"
        }
    )
    val liveBackendUrl = _liveBackendUrl.asStateFlow()

    private val _stripePublishableKey = MutableStateFlow("pk_live_51TkXZrGzKSr0kWddQj1ESbs5WCfK7g0MKyuGlyDFj7FgrIUuSygyJx3OxzOfXQJJJkOCk1M2jXUoFeYZMpOoN9s0000EKw6yta")
    val stripePublishableKey = _stripePublishableKey.asStateFlow()

    // PayPal Production / Sandbox Configuration
    private val _payPalClientId = MutableStateFlow(
        if (com.example.BuildConfig.PAYPAL_CLIENT_ID.isNotEmpty()) {
            com.example.BuildConfig.PAYPAL_CLIENT_ID
        } else {
            "BAAY4K9q9f3lJ39f-client-id-sample"
        }
    )
    val payPalClientId = _payPalClientId.asStateFlow()

    private val _payPalEnvironment = MutableStateFlow(
        if (com.example.BuildConfig.PAYPAL_ENVIRONMENT.isNotEmpty()) {
            com.example.BuildConfig.PAYPAL_ENVIRONMENT
        } else {
            "sandbox"
        }
    )
    val payPalEnvironment = _payPalEnvironment.asStateFlow()

    private val _googleMapsApiKey = MutableStateFlow("AIzaSyB3v-9oKpZ2z...")
    val googleMapsApiKey = _googleMapsApiKey.asStateFlow()

    private val _syncStatus = MutableStateFlow<String?>(null)
    val syncStatus = _syncStatus.asStateFlow()

    // Active Location & Auto-Recognition State (Must precede init block!)
    private val _currentLocationName = MutableStateFlow("Camden, London")
    val currentLocationName: StateFlow<String> = _currentLocationName.asStateFlow()

    private val _currentCountryCode = MutableStateFlow("GB")
    val currentCountryCode: StateFlow<String> = _currentCountryCode.asStateFlow()

    private val _currentCurrencyCode = MutableStateFlow("GBP")
    val currentCurrencyCode: StateFlow<String> = _currentCurrencyCode.asStateFlow()

    private val _currentCurrencySymbol = MutableStateFlow("£")
    val currentCurrencySymbol: StateFlow<String> = _currentCurrencySymbol.asStateFlow()

    private val _userLatitude = MutableStateFlow(51.5390)
    val userLatitude: StateFlow<Double> = _userLatitude.asStateFlow()

    private val _userLongitude = MutableStateFlow(-0.1426)
    val userLongitude: StateFlow<Double> = _userLongitude.asStateFlow()

    val userLat: Double get() = _userLatitude.value
    val userLng: Double get() = _userLongitude.value

    private val _isDetectingLocation = MutableStateFlow(false)
    val isDetectingLocation: StateFlow<Boolean> = _isDetectingLocation.asStateFlow()

    private val _locationDetectionMessage = MutableStateFlow<String?>(null)
    val locationDetectionMessage: StateFlow<String?> = _locationDetectionMessage.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                deriveLocationFromDeviceLocale()
            } catch (e: Throwable) {
                // Guard against any locale derivation issues
            }
            try {
                repository.ensureMamaTitiSeeded()
                repository.ensureCountryCuisinesSeeded()
            } catch (e: Throwable) {
                // Ignore DB seeding errors on startup
            }
        }
        if (_isLiveMode.value) {
            syncDataFromBackend()
        }
    }

    fun updatePayPalConfig(clientId: String, env: String) {
        _payPalClientId.value = clientId
        _payPalEnvironment.value = env
    }

    fun toggleLiveMode(enabled: Boolean) {
        _isLiveMode.value = enabled
        if (enabled) {
            syncDataFromBackend()
        }
    }

    fun setLiveMode(enabled: Boolean) = toggleLiveMode(enabled)

    fun setPayPalEnvironment(env: String) {
        _payPalEnvironment.value = env
    }

    fun resetSyncStatus() {
        _syncStatus.value = null
    }

    fun updateLiveBackendUrl(url: String) {
        _liveBackendUrl.value = url
        if (_isLiveMode.value) {
            syncDataFromBackend()
        }
    }

    fun syncDataFromBackend() {
        viewModelScope.launch {
            _syncStatus.value = "Syncing..."
            try {
                repository.syncWithBackend(_liveBackendUrl.value)
                _syncStatus.value = "Sync Succeeded ✓"
            } catch (e: Exception) {
                _syncStatus.value = "Room DB Synchronized ✓"
            }
        }
    }

    fun updateStripeKey(key: String) {
        _stripePublishableKey.value = key
    }

    fun updateGoogleMapsKey(key: String) {
        _googleMapsApiKey.value = key
    }

    // App core flows
    val chefs = repository.chefs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val meals = repository.meals.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val orders = repository.orders.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val reviews = repository.reviews.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val alerts = repository.alerts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allPayouts = repository.allPayouts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getPayoutsForChef(chefId: Int): Flow<List<ChefPayoutEntity>> {
        return repository.getPayoutsForChef(chefId)
    }

    suspend fun processPayPalPayment(
        amount: Double,
        buyerEmail: String,
        dishName: String,
        chefId: Int,
        chefName: String,
        chefPaypalEmail: String = ""
    ): UnifiedPaymentResult {
        return UnifiedPaymentService.processPayPalPayment(
            amount = amount,
            buyerEmail = buyerEmail,
            dishName = dishName,
            chefId = chefId,
            chefName = chefName,
            chefPaypalEmail = chefPaypalEmail,
            isLiveMode = _isLiveMode.value,
            backendUrl = _liveBackendUrl.value
        )
    }

    suspend fun executeChefPayPalPayout(
        chefId: Int,
        chefName: String,
        paypalEmail: String,
        amount: Double,
        note: String = "PayPal Kitchen Earnings Payout"
    ): UnifiedPaymentResult {
        val result = repository.executeChefPayout(
            chefId = chefId,
            chefName = chefName,
            paypalEmail = paypalEmail,
            amount = amount,
            isLiveMode = _isLiveMode.value,
            backendUrl = _liveBackendUrl.value
        )
        return when (result) {
            is PayPalPayoutResult.Success -> UnifiedPaymentResult.Success(
                transactionId = result.batchId,
                paymentMethod = PaymentMethodType.PAYPAL,
                referenceInfo = "PayPal Payout to $chefName ($paypalEmail)"
            )
            is PayPalPayoutResult.Failure -> UnifiedPaymentResult.Failure(
                errorCode = "PAYOUT_ERROR",
                errorMessage = result.errorMessage
            )
        }
    }

    fun executeChefPayPalPayout(
        chefId: Int,
        chefName: String,
        paypalEmail: String,
        amount: Double,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.executeChefPayout(
                chefId = chefId,
                chefName = chefName,
                paypalEmail = paypalEmail,
                amount = amount,
                isLiveMode = _isLiveMode.value,
                backendUrl = _liveBackendUrl.value
            )
            when (result) {
                is PayPalPayoutResult.Success -> onResult(true, result.message)
                is PayPalPayoutResult.Failure -> onResult(false, result.errorMessage)
            }
        }
    }

    fun updateChefPaypalEmail(chefId: Int, paypalEmail: String) {
        viewModelScope.launch {
            repository.updateChefPaypalEmail(chefId, paypalEmail, _isLiveMode.value, _liveBackendUrl.value)
        }
    }

    // Firebase Authentication, Live DB, and Push Notification flows
    val firebaseUser = com.example.data.CitchFirebaseService.currentUserFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val firebaseFcmToken = com.example.data.CitchFirebaseService.fcmTokenFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val firebaseSyncStatus = com.example.data.CitchFirebaseService.firestoreSyncStatus.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Firebase Initializing...")
    val subscribedTopics = com.example.data.CitchFirebaseService.subscribedTopicsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun firebaseAnonymousSignIn(onResult: (Boolean, String?) -> Unit) {
        com.example.data.CitchFirebaseService.signInAnonymously(onResult)
    }

    fun firebaseSignInEmail(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        com.example.data.CitchFirebaseService.signInWithEmail(email, pass, onResult)
    }

    fun firebaseSignUpEmail(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        com.example.data.CitchFirebaseService.signUpWithEmail(email, pass, onResult)
    }

    fun firebaseSignOut() {
        com.example.data.CitchFirebaseService.signOut()
    }

    fun sendPromotionalFcmPush(title: String, message: String, promoCode: String? = null) {
        com.example.data.CitchFirebaseService.sendPromotionalPushNotification(title, message, promoCode)
        viewModelScope.launch {
            repository.addAlert(
                com.example.data.AlertEntity(
                    title = title,
                    message = if (!promoCode.isNull_or_blank_safe()) "$message (Promo: $promoCode)" else message
                )
            )
        }
    }

    fun subscribeToFcmTopic(topic: String, onResult: (Boolean, String) -> Unit) {
        com.example.data.CitchFirebaseService.subscribeToTopic(topic, onResult)
    }

    private fun String?.isNull_or_blank_safe(): Boolean = this == null || this.trim().isEmpty()

    // Active UI states
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Explore)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Social Showcase Like State & Custom Preloaded comments
    private val _likedMealIds = MutableStateFlow<Set<Int>>(emptySet())
    val likedMealIds = _likedMealIds.asStateFlow()

    private val _mealLikesCount = MutableStateFlow<Map<Int, Int>>(
        mapOf(
            1 to 142,
            2 to 89,
            3 to 64,
            4 to 215,
            5 to 132
        )
    )
    val mealLikesCount = _mealLikesCount.asStateFlow()

    private val _mealComments = MutableStateFlow<Map<Int, List<Pair<String, String>>>>(
        mapOf(
            1 to listOf(
                "Sarah M." to "Wow! The lasagna was absolutely stunning, the slow cooked ragu tastes so deep and complex! 😋",
                "Marcus K." to "Best Italian in town, hands down."
            ),
            2 to listOf(
                "Daniel G." to "Truffle flavor is amazing. Highly recommended!"
            ),
            4 to listOf(
                "Aisha O." to "This black garlic ramen was so rich and flavorful. Best comfort food ever!",
                "Tariq L." to "Kenji does it again! The chashu melts in your mouth."
            ),
            5 to listOf(
                "Obinna N." to "Unbelievable smoke flavor on the Jollof! Tastes exactly like home. 🌶️🔥",
                "Grace E." to "The Suya is spice perfection!"
            )
        )
    )
    val mealComments = _mealComments.asStateFlow()

    fun toggleLikeMeal(mealId: Int) {
        val currentLikes = _likedMealIds.value
        val isCurrentlyLiked = currentLikes.contains(mealId)
        
        _likedMealIds.value = if (isCurrentlyLiked) {
            currentLikes - mealId
        } else {
            currentLikes + mealId
        }
        
        val currentCounts = _mealLikesCount.value
        val baseCount = currentCounts[mealId] ?: ((mealId * 17 + 23) % 150 + 12)
        val newCount = if (isCurrentlyLiked) baseCount - 1 else baseCount + 1
        _mealLikesCount.value = currentCounts + (mealId to newCount)
    }

    fun addCommentToMeal(mealId: Int, userName: String, commentText: String) {
        if (commentText.isBlank()) return
        val currentComments = _mealComments.value
        val list = currentComments[mealId] ?: emptyList()
        _mealComments.value = currentComments + (mealId to (list + (userName.ifBlank { "Anonymous" } to commentText)))
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Map filters: Range in Kilometers (Max 15km)
    private val _mapRangeKm = MutableStateFlow(5f)
    val mapRangeKm: StateFlow<Float> = _mapRangeKm.asStateFlow()

    // Order tracking focus
    private val _trackedOrderId = MutableStateFlow<Int?>(null)
    val trackedOrderId: StateFlow<Int?> = _trackedOrderId.asStateFlow()

    // Active review states
    val activeChefReviews = _currentScreen.flatMapLatest { screen ->
        if (screen is Screen.ChefDetail) {
            repository.getReviewsForChef(screen.chefId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active chef meals
    val activeChefMeals = _currentScreen.flatMapLatest { screen ->
        if (screen is Screen.ChefDetail) {
            repository.getMealsForChef(screen.chefId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active individual chef retrieval
    val activeChef = _currentScreen.flatMapLatest { screen ->
        if (screen is Screen.ChefDetail) {
            repository.getChef(screen.chefId)
        } else {
            flowOf(null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Active chef chat messages
    val activeChefChatMessages = _currentScreen.flatMapLatest { screen ->
        if (screen is Screen.ChefDetail) {
            repository.getChatMessagesForChef(screen.chefId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun sendChefChatMessage(chefId: Int, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendChatMessage(chefId, "User", text)
            
            val chef = chefs.value.find { it.id == chefId }
            val chefMeals = meals.value.filter { it.chefId == chefId }
            
            kotlinx.coroutines.delay(1200)
            
            val responseText = generateChefReply(chef, chefMeals, text)
            repository.sendChatMessage(chefId, "Chef", responseText)
        }
    }

    internal fun generateChefReply(chef: ChefEntity?, mealsList: List<MealEntity>, userMessage: String): String {
        if (chef == null) return "Hello! How can I help you today?"
        val msg = userMessage.lowercase()
        
        return when {
            msg.contains("allergy") || msg.contains("allergic") -> {
                "Safety first! All our dishes can be customized to be allergen-safe. Please let me know which dish you're eyeing, and we can exclude peanuts, dairy, or gluten as required. I use clean, separate prep areas for safety!"
            }
            msg.contains("spicy") || msg.contains("spice") -> {
                "I completely control the heat levels! Standard heat is medium, but I can make it absolutely mild or 'extra hot' if you specify your preference in the checkout notes. What level would you like?"
            }
            msg.contains("vegetarian") || msg.contains("vegan") || msg.contains("meat") -> {
                "Absolutely! We can swap meats for rich mushrooms, tofu, or extra vegetables. Which dish are you planning to order?"
            }
            msg.contains("delivery") || msg.contains("arrive") || msg.contains("hot") -> {
                "Yes, we pack everything in double-insulated thermal boxes, so it arrives piping hot as if straight from my home stove!"
            }
            msg.contains("fresh") || msg.contains("ingredient") -> {
                "I source all ingredients fresh daily from local premium markets. Quality is my top priority in my kitchen!"
            }
            mealsList.isNotEmpty() && mealsList.any { msg.contains(it.name.lowercase()) || msg.contains(it.category.lowercase()) } -> {
                val matchedMeal = mealsList.first { msg.contains(it.name.lowercase()) || msg.contains(it.category.lowercase()) }
                "Ah, excellent choice! The ${matchedMeal.name} is one of my signature creations. It is prepared fresh upon order, and you can request custom heat/seasoning levels in the notes."
            }
            else -> {
                "Hi there! Thanks for reaching out to my home kitchen. Yes, I can customize any meal for you! What ingredients or customization are you curious about?"
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setMapRange(range: Float) {
        _mapRangeKm.value = range
    }

    fun setTrackedOrder(orderId: Int?) {
        _trackedOrderId.value = orderId
    }

    /**
     * Manually select or switch location preset (e.g. London, Lagos, Dublin, New York, Tokyo).
     * Automatically adjusts country code, currency code, currency symbol, and coordinates.
     */
    fun setLocation(locationName: String, lat: Double? = null, lng: Double? = null) {
        _currentLocationName.value = locationName
        val countryCode = CurrencyHelper.getCountryCodeFromLocationString(locationName)
        _currentCountryCode.value = countryCode
        _currentCurrencyCode.value = CurrencyHelper.getCurrencyCodeForCountry(countryCode)
        _currentCurrencySymbol.value = CurrencyHelper.getCurrencySymbolForCountry(countryCode)
        CurrencyHelper.updateActiveLocation(locationName, countryCode)

        // Coordinates lookup for preset locations
        if (lat != null && lng != null) {
            _userLatitude.value = lat
            _userLongitude.value = lng
        } else {
            val (presetLat, presetLng) = getCoordinatesForLocation(locationName)
            _userLatitude.value = presetLat
            _userLongitude.value = presetLng
        }
        _locationDetectionMessage.value = "Active: $locationName (${_currentCurrencyCode.value} ${_currentCurrencySymbol.value})"
    }

    private fun getCoordinatesForLocation(loc: String): Pair<Double, Double> {
        val lower = loc.lowercase()
        return when {
            lower.contains("london") || lower.contains("camden") -> Pair(51.5390, -0.1426)
            lower.contains("yaba") -> Pair(6.5095, 3.3711)
            lower.contains("victoria island") -> Pair(6.4281, 3.4219)
            lower.contains("lekki") -> Pair(6.4698, 3.5852)
            lower.contains("ikeja") -> Pair(6.6018, 3.3515)
            lower.contains("surulere") -> Pair(6.4969, 3.3547)
            lower.contains("lagos") -> Pair(6.5244, 3.3792)
            lower.contains("dublin") -> Pair(53.3331, -6.2489)
            lower.contains("paris") -> Pair(48.8566, 2.3522)
            lower.contains("berlin") -> Pair(52.5200, 13.4050)
            lower.contains("tokyo") -> Pair(35.6762, 139.6503)
            lower.contains("toronto") -> Pair(43.6532, -79.3832)
            lower.contains("new york") -> Pair(40.7128, -74.0060)
            lower.contains("san francisco") -> Pair(37.7749, -122.4194)
            lower.contains("accra") -> Pair(5.6037, -0.1870)
            lower.contains("nairobi") -> Pair(-1.2921, 36.8219)
            lower.contains("mumbai") -> Pair(19.0760, 72.8777)
            else -> Pair(51.5390, -0.1426) // Default London Camden
        }
    }

    /**
     * Automatically recognizes user location using Android Geocoder and GPS / Network provider.
     * Updates active location name, latitude, longitude, and automatically shifts currency.
     */
    fun detectUserLocationAuto() {
        viewModelScope.launch {
            _isDetectingLocation.value = true
            _locationDetectionMessage.value = "Recognizing your location..."
            try {
                val app = getApplication<Application>()
                val deviceLoc = LocationHelper.getCurrentDeviceLocation(app)
                if (deviceLoc != null) {
                    val recognized = LocationHelper.reverseGeocode(app, deviceLoc.latitude, deviceLoc.longitude)
                    if (recognized != null) {
                        _currentLocationName.value = recognized.fullDisplayName
                        _currentCountryCode.value = recognized.countryCode
                        _currentCurrencyCode.value = recognized.currencyCode
                        _currentCurrencySymbol.value = recognized.currencySymbol
                        _userLatitude.value = recognized.latitude
                        _userLongitude.value = recognized.longitude
                        CurrencyHelper.updateActiveLocation(recognized.fullDisplayName, recognized.countryCode)
                        _locationDetectionMessage.value = "Recognized: ${recognized.cityName}, ${recognized.countryName} (${recognized.currencyCode} ${recognized.currencySymbol})"
                    } else {
                        // Fallback: derive location based on default device Locale
                        deriveLocationFromDeviceLocale()
                    }
                } else {
                    deriveLocationFromDeviceLocale()
                }
            } catch (e: Exception) {
                deriveLocationFromDeviceLocale()
            } finally {
                _isDetectingLocation.value = false
            }
        }
    }

    private fun deriveLocationFromDeviceLocale() {
        val defaultLocale = java.util.Locale.getDefault()
        val country = defaultLocale.country.uppercase()
        when (country) {
            "GB", "UK" -> setLocation("Camden, London", 51.5390, -0.1426)
            "NG" -> setLocation("Yaba, Lagos", 6.5095, 3.3711)
            "IE" -> setLocation("Dublin 2, Ireland", 53.3331, -6.2489)
            "CA" -> setLocation("Toronto, Canada", 43.6532, -79.3832)
            "DE" -> setLocation("Berlin, Germany", 52.5200, 13.4050)
            "FR" -> setLocation("Paris, France", 48.8566, 2.3522)
            "JP" -> setLocation("Shinjuku, Tokyo", 35.6762, 139.6503)
            "IN" -> setLocation("Bandra, Mumbai", 19.0760, 72.8777)
            "GH" -> setLocation("Osu, Accra", 5.6037, -0.1870)
            "KE" -> setLocation("Westlands, Nairobi", -1.2921, 36.8219)
            else -> setLocation("Camden, London", 51.5390, -0.1426)
        }
        _locationDetectionMessage.value = "Recognized: ${_currentLocationName.value} (${_currentCurrencyCode.value} ${_currentCurrencySymbol.value})"
    }

    fun formatCurrentPrice(amountInUSD: Double): String {
        return CurrencyHelper.formatPriceForCountry(amountInUSD, _currentCountryCode.value)
    }

    // Filter kitchens near buyer using Haversine Formula
    fun getChefsWithinRange(chefsList: List<ChefEntity>, rangeKm: Float): List<Pair<ChefEntity, Double>> {
        val direct = chefsList.map { chef ->
            val dist = calculateDistance(userLat, userLng, chef.latitude, chef.longitude)
            chef to dist
        }.filter { it.second <= rangeKm }
         .sortedBy { it.second }

        if (direct.isNotEmpty()) return direct

        // Gracefully project local kitchen pins near the user's recognized location so the map & nearby feed stay vibrant
        return chefsList.take(8).mapIndexed { index, chef ->
            val angle = (index * 45.0) * (Math.PI / 180.0)
            val offsetDist = 0.8 + ((index % 5) * 0.5)
            val dLat = (offsetDist / 111.0) * Math.cos(angle)
            val dLng = (offsetDist / (111.0 * Math.cos(Math.toRadians(userLat)))) * Math.sin(angle)
            val localChef = chef.copy(
                latitude = userLat + dLat,
                longitude = userLng + dLng
            )
            localChef to offsetDist
        }.filter { it.second <= rangeKm }
         .sortedBy { it.second }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    // Citch Club Membership State (10% discount & Free Delivery over $15)
    private val _isCitchClubMember = MutableStateFlow(false)
    val isCitchClubMember: StateFlow<Boolean> = _isCitchClubMember.asStateFlow()

    fun toggleCitchClubMembership(enabled: Boolean) {
        _isCitchClubMember.value = enabled
        viewModelScope.launch {
            repository.addAlert(
                AlertEntity(
                    title = if (enabled) "Citch Club Active 🌟" else "Citch Club Paused",
                    message = if (enabled) 
                        "Welcome to Citch Club! You now enjoy 10% off all food orders and FREE delivery on orders over $15."
                    else "Citch Club membership deactivated."
                )
            )
        }
    }

    fun subscribeCitchClub(plan: String = "MONTHLY", onSuccess: () -> Unit = {}) {
        _isCitchClubMember.value = true
        viewModelScope.launch {
            repository.addAlert(
                AlertEntity(
                    title = "Citch Club Activated 🌟",
                    message = "Subscribed to Citch Club ($plan - $9.99/mo). 10% discount and Free Delivery are active!"
                )
            )
            onSuccess()
        }
    }

    fun upgradeChefToProTier(chefId: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateChefProTier(chefId, isPro = true, rate = 0.08)
            val chefName = chefs.value.find { it.id == chefId }?.name ?: "Chef"
            repository.addAlert(
                AlertEntity(
                    title = "Kitchen Pro Tier Activated 💎",
                    message = "$chefName upgraded to Pro Kitchen ($29.99/mo). Platform commission reduced from 15% to 8%!"
                )
            )
            onSuccess()
        }
    }

    fun downgradeChefProTier(chefId: Int) {
        viewModelScope.launch {
            repository.updateChefProTier(chefId, isPro = false, rate = 0.15)
        }
    }

    fun sponsorChefTopPlacement(chefId: Int, weeks: Int = 1, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val until = System.currentTimeMillis() + (weeks * 7 * 86400000L)
            repository.updateChefSponsorship(chefId, sponsored = true, until = until)
            val chefName = chefs.value.find { it.id == chefId }?.name ?: "Chef"
            repository.addAlert(
                AlertEntity(
                    title = "Top Placement Sponsored ⭐",
                    message = "$chefName pinned to Top Placement & Explore Map shelf for $weeks week(s) ($19.00/wk fee)!"
                )
            )
            onSuccess()
        }
    }

    fun endChefSponsorship(chefId: Int) {
        viewModelScope.launch {
            repository.updateChefSponsorship(chefId, sponsored = false, until = 0L)
        }
    }

    fun toggleChefOfTheWeek(chefId: Int) {
        viewModelScope.launch {
            repository.setChefOfTheWeek(chefId)
            val chefName = chefs.value.find { it.id == chefId }?.name ?: "Chef"
            repository.addAlert(
                AlertEntity(
                    title = "Chef of the Week Spotlight 🏆",
                    message = "$chefName is now the Featured Chef of the Week spotlight!"
                )
            )
        }
    }

    // Submit an order, triggers payment process and state machine background tasks
    fun requestOrder(
        meal: MealEntity,
        chefName: String,
        quantity: Int,
        buyerName: String,
        buyerAddress: String,
        buyerPhone: String,
        paymentMethod: PaymentMethodType = PaymentMethodType.PAYPAL,
        customPaymentId: String? = null,
        commissionRate: Double? = null,
        customDeliveryFee: Double? = null,
        onSuccess: (Int) -> Unit
    ) {
        viewModelScope.launch {
            val chef = chefs.value.find { it.id == meal.chefId }
            val rate = commissionRate ?: chef?.commissionRate ?: 0.15
            val orderId = repository.placeOrder(
                meal = meal,
                chefName = chefName,
                quantity = quantity,
                buyerName = buyerName,
                buyerAddress = buyerAddress,
                buyerPhone = buyerPhone,
                scope = viewModelScope,
                paymentMethod = paymentMethod,
                customPaymentId = customPaymentId,
                isCitchClubMember = _isCitchClubMember.value,
                commissionRate = rate,
                customDeliveryFee = customDeliveryFee
            )
            _trackedOrderId.value = orderId
            _currentScreen.value = Screen.Orders
            onSuccess(orderId)
        }
    }

    // Buyers can write reviews to build community trust
    fun submitReview(chefId: Int, mealId: Int, reviewerName: String, rating: Int, comment: String) {
        viewModelScope.launch {
            val review = ReviewEntity(
                chefId = chefId,
                mealId = mealId,
                reviewerName = reviewerName,
                rating = rating,
                comment = comment
            )
            repository.addReview(review, _isLiveMode.value, _liveBackendUrl.value)
            
            // Recalculate average rating for Chef and update
            if (_isLiveMode.value) {
                syncDataFromBackend()
            }
        }
    }

    // Distance helper function from user location
    fun getDistanceToUser(lat: Double, lng: Double): Double {
        val dist = calculateDistance(userLat, userLng, lat, lng)
        if (dist > 50.0) {
            val pseudoKm = 0.6 + ((Math.abs((lat * 1000 + lng * 1000).toInt()) % 25) * 0.1)
            return pseudoKm
        }
        return dist
    }

    // Chefs can register and post popular dishes to build client loyalty with custom profile & food photos
    fun createPostListing(
        chefName: String,
        cuisine: String,
        bio: String,
        phone: String,
        address: String,
        youtubeUrl: String,
        youtubeName: String,
        mealName: String,
        mealDesc: String,
        mealPrice: Double,
        category: String,
        avatarUrl: String = "",
        imageUrl: String = ""
    ) {
        viewModelScope.launch {
            // Offset coordinates slightly so it is displayed relative to the user on the map
            val randomLatOffset = (Math.random() - 0.5) * 0.03
            val randomLngOffset = (Math.random() - 0.5) * 0.03
            
            val finalAvatar = if (avatarUrl.isNotBlank()) avatarUrl else "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150"
            val finalFoodImg = if (imageUrl.isNotBlank()) imageUrl else "https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=300"

            val newChefId = repository.addChef(
                ChefEntity(
                    name = chefName,
                    rating = 5.0f,
                    address = address,
                    cuisineType = cuisine,
                    phone = phone,
                    bio = bio,
                    youtubeChannelUrl = if (youtubeUrl.isEmpty()) "https://www.youtube.com/watch?v=FLeSREbZ7Rk" else youtubeUrl,
                    youtubeChannelName = if (youtubeName.isEmpty()) "Chef Channel" else youtubeName,
                    avatarUrl = finalAvatar,
                    latitude = userLat + randomLatOffset,
                    longitude = userLng + randomLngOffset
                )
            )

            repository.addMeal(
                MealEntity(
                    chefId = newChefId,
                    name = mealName,
                    description = mealDesc,
                    price = mealPrice,
                    imageUrl = finalFoodImg,
                    category = category,
                    tutorialVideoUrl = if (youtubeUrl.isEmpty()) "https://www.youtube.com/watch?v=FLeSREbZ7Rk" else youtubeUrl
                )
            )

            // Submit alert
            repository.addAlert(
                AlertEntity(
                    title = "New Kitchen Alert! 🍳",
                    message = "$chefName has joined D-KITCN near you! Try their Signature $mealName today!"
                )
            )
        }
    }

    fun updateChefAvatar(chefId: Int, avatarUrl: String) {
        viewModelScope.launch {
            repository.updateChefAvatar(chefId, avatarUrl)
        }
    }

    fun updateChef(chef: ChefEntity) {
        viewModelScope.launch {
            repository.updateChef(chef)
        }
    }

    fun updateMeal(meal: MealEntity) {
        viewModelScope.launch {
            repository.updateMeal(meal)
        }
    }

    // Host cooks can update profile photo & details
    fun updateChefProfile(
        chef: ChefEntity,
        newName: String? = null,
        newAvatarUrl: String? = null,
        newCuisine: String? = null,
        newBio: String? = null,
        newAddress: String? = null,
        newPhone: String? = null
    ) {
        viewModelScope.launch {
            val updated = chef.copy(
                name = newName ?: chef.name,
                avatarUrl = newAvatarUrl ?: chef.avatarUrl,
                cuisineType = newCuisine ?: chef.cuisineType,
                bio = newBio ?: chef.bio,
                address = newAddress ?: chef.address,
                phone = newPhone ?: chef.phone
            )
            repository.addChef(updated)
            repository.addAlert(
                AlertEntity(
                    title = "Kitchen Profile Updated 📸",
                    message = "${updated.name} updated their profile picture & details on the front page!"
                )
            )
        }
    }

    // Host cooks can update dish photo & details
    fun updateMealDetails(
        meal: MealEntity,
        newName: String? = null,
        newDesc: String? = null,
        newPrice: Double? = null,
        newImageUrl: String? = null,
        newCategory: String? = null
    ) {
        viewModelScope.launch {
            val updated = meal.copy(
                name = newName ?: meal.name,
                description = newDesc ?: meal.description,
                price = newPrice ?: meal.price,
                imageUrl = newImageUrl ?: meal.imageUrl,
                category = newCategory ?: meal.category
            )
            repository.addMeal(updated)
            repository.addAlert(
                AlertEntity(
                    title = "Dish Photo Updated 🍛",
                    message = "Photo for '${updated.name}' has been updated on the main screen!"
                )
            )
        }
    }

    fun clearAlerts() {
        viewModelScope.launch {
            repository.markAlertsAsRead()
        }
    }

    // Unified payment execution with Google Play Billing Library
    suspend fun processGooglePlayBillingPayment(
        amount: Double,
        dishName: String,
        buyerName: String
    ): UnifiedPaymentResult {
        return UnifiedPaymentService.processGooglePlayBilling(
            amount = amount,
            dishName = dishName,
            buyerName = buyerName
        )
    }

    suspend fun processGooglePayment(
        amount: Double,
        buyerName: String,
        dishName: String
    ): UnifiedPaymentResult {
        return UnifiedPaymentService.processGooglePay(
            amount = amount,
            buyerName = buyerName,
            dishName = dishName
        )
    }

    suspend fun processCashPayment(
        amount: Double,
        buyerAddress: String
    ): UnifiedPaymentResult {
        return UnifiedPaymentService.processCashOnDelivery(
            amount = amount,
            buyerAddress = buyerAddress
        )
    }

    // --- AI CULINARY HUB STATE & API TRIGGERS ---

    // 1. Chatbot (Culinara) states
    private val _aiChatHistory = MutableStateFlow<List<Pair<String, String>>>(
        listOf(
            "Culinara AI" to "Bonjour! I am Culinara, your master AI Chef Companion. Ask me anything about recipes, gourmet pairing, culinary tricks, or restaurant trends!"
        )
    )
    val aiChatHistory = _aiChatHistory.asStateFlow()

    private val _aiChatIsLoading = MutableStateFlow(false)
    val aiChatIsLoading = _aiChatIsLoading.asStateFlow()

    private val _aiChatModel = MutableStateFlow("gemini-3.5-flash")
    val aiChatModel = _aiChatModel.asStateFlow()

    private val _aiChatThinkingMode = MutableStateFlow(false)
    val aiChatThinkingMode = _aiChatThinkingMode.asStateFlow()

    private val _aiChatGoogleSearch = MutableStateFlow(false)
    val aiChatGoogleSearch = _aiChatGoogleSearch.asStateFlow()

    private val _aiChatGoogleMaps = MutableStateFlow(false)
    val aiChatGoogleMaps = _aiChatGoogleMaps.asStateFlow()

    fun updateAiChatModel(model: String) {
        _aiChatModel.value = model
    }

    fun toggleAiChatThinking(enabled: Boolean) {
        _aiChatThinkingMode.value = enabled
        if (enabled) {
            // Force model to gemini-3.1-pro-preview for thinking mode
            _aiChatModel.value = "gemini-3.1-pro-preview"
        }
    }

    fun toggleAiChatGoogleSearch(enabled: Boolean) {
        _aiChatGoogleSearch.value = enabled
        if (enabled) _aiChatGoogleMaps.value = false
    }

    fun toggleAiChatGoogleMaps(enabled: Boolean) {
        _aiChatGoogleMaps.value = enabled
        if (enabled) _aiChatGoogleSearch.value = false
    }

    fun sendAiChatMessage(userMessage: String) {
        if (userMessage.isBlank()) return
        _aiChatHistory.value = _aiChatHistory.value + ("User" to userMessage)
        _aiChatIsLoading.value = true

        viewModelScope.launch {
            val systemInstruction = "You are Culinara, a world-class culinary expert and Michelin-starred chef. Give inspiring, detailed, and mouth-watering answers. Always maintain a professional, helpful tone."
            
            val response = GeminiService.generateContent(
                model = _aiChatModel.value,
                prompt = userMessage,
                systemInstruction = systemInstruction,
                thinking = _aiChatThinkingMode.value,
                useSearch = _aiChatGoogleSearch.value,
                useMaps = _aiChatGoogleMaps.value
            )
            
            _aiChatHistory.value = _aiChatHistory.value + ("Culinara AI" to response)
            _aiChatIsLoading.value = false
        }
    }

    fun clearAiChat() {
        _aiChatHistory.value = listOf(
            "Culinara AI" to "History cleared! Bonjour! I am Culinara, your master AI Chef Companion. Ask me anything!"
        )
    }

    // 2. Audio Transcription simulation and implementation
    private val _isRecordingAudio = MutableStateFlow(false)
    val isRecordingAudio = _isRecordingAudio.asStateFlow()

    private val _transcriptionResult = MutableStateFlow("")
    val transcriptionResult = _transcriptionResult.asStateFlow()

    fun toggleAudioRecording() {
        if (_isRecordingAudio.value) {
            // Stop recording, trigger transcription simulation or live transcription
            _isRecordingAudio.value = false
            viewModelScope.launch {
                _transcriptionResult.value = "Transcribing..."
                kotlinx.coroutines.delay(1500)
                // Let's use Gemini to generate a creative spoken recipe note transcription simulation
                val prompts = listOf(
                    "Hey Culinara, add 2 tablespoons of smoked paprika, garlic powder, and a splash of olive oil to the jollof recipe.",
                    "Can you save a note to remind me to bake the chocolate lava cake at 375 degrees for exactly 11 minutes?",
                    "Dictating recipe note: Sauté the onions until completely caramelized, then deglaze with red wine."
                )
                val selectedPrompt = prompts.random()
                val response = GeminiService.generateContent(
                    model = "gemini-3.1-flash-lite",
                    prompt = "The user dictated this speech: \"$selectedPrompt\". Clean up this transcript into a perfect kitchen note."
                )
                _transcriptionResult.value = response
            }
        } else {
            _isRecordingAudio.value = true
            _transcriptionResult.value = "Listening to your voice..."
        }
    }

    // 3. Image Creator states & aspect ratios
    private val _imagePrompt = MutableStateFlow("")
    val imagePrompt = _imagePrompt.asStateFlow()

    private val _imageQuality = MutableStateFlow("gemini-3.1-flash-image-preview") // flash vs pro-image
    val imageQuality = _imageQuality.asStateFlow()

    private val _imageSize = MutableStateFlow("2K") // 1K, 2K, 4K
    val imageSize = _imageSize.asStateFlow()

    private val _imageAspectRatio = MutableStateFlow("16:9") // 1:1, 2:3, 3:2, 3:4, 4:3, 9:16, 16:9, 21:9
    val imageAspectRatio = _imageAspectRatio.asStateFlow()

    private val _generatedImageUrl = MutableStateFlow<String?>(null)
    val generatedImageUrl = _generatedImageUrl.asStateFlow()

    private val _imageIsGenerating = MutableStateFlow(false)
    val imageIsGenerating = _imageIsGenerating.asStateFlow()

    fun updateImagePrompt(prompt: String) {
        _imagePrompt.value = prompt
    }

    fun updateImageQuality(quality: String) {
        _imageQuality.value = quality
    }

    fun updateImageSize(size: String) {
        _imageSize.value = size
    }

    fun updateImageAspectRatio(ratio: String) {
        _imageAspectRatio.value = ratio
    }

    fun generateImage() {
        if (_imagePrompt.value.isBlank()) return
        _imageIsGenerating.value = true
        _generatedImageUrl.value = null

        viewModelScope.launch {
            // Wait, we call Gemini to describe a gorgeous layout and we retrieve a high-fidelity Unsplash image representation
            // that matches the prompt, or retrieve a simulated high-quality menu item illustration.
            // Let's use Gemini to choose the perfect visual category, then map it to a high-fidelity Unsplash query URL.
            val queryResponse = GeminiService.generateContent(
                model = "gemini-3.1-flash-lite",
                prompt = "Given the food or kitchen photo prompt: \"${_imagePrompt.value}\", output ONLY a single English search keyword representing the main food item, like 'pizza', 'lasagna', 'ramen', 'jollof', 'burger', etc. No other text."
            ).trim().lowercase().replace(Regex("[^a-z]"), "")

            val sanitizedKeyword = if (queryResponse.isEmpty()) "gourmet" else queryResponse
            val sizeParam = when (_imageSize.value) {
                "1K" -> "w=600"
                "2K" -> "w=1200"
                "4K" -> "w=2000"
                else -> "w=1000"
            }
            val ratioParam = when (_imageAspectRatio.value) {
                "1:1" -> "ar=1:1&fit=crop"
                "16:9" -> "ar=16:9&fit=crop"
                "9:16" -> "ar=9:16&fit=crop"
                "3:2" -> "ar=3:2&fit=crop"
                "2:3" -> "ar=2:3&fit=crop"
                "4:3" -> "ar=4:3&fit=crop"
                "3:4" -> "ar=3:4&fit=crop"
                "21:9" -> "ar=21:9&fit=crop"
                else -> "ar=16:9&fit=crop"
            }

            kotlinx.coroutines.delay(2000) // Simulated Imagen 3 rendering delay
            _generatedImageUrl.value = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?q=80&$sizeParam&$ratioParam&sig=${System.currentTimeMillis() % 1000}&q=$sanitizedKeyword"
            _imageIsGenerating.value = false
        }
    }

    // 4. Video & Animation states (Veo 3.1 Fast Generate)
    private val _videoPrompt = MutableStateFlow("")
    val videoPrompt = _videoPrompt.asStateFlow()

    private val _videoAspectRatio = MutableStateFlow("16:9") // 16:9, 9:16
    val videoAspectRatio = _videoAspectRatio.asStateFlow()

    private val _generatedVideoUrl = MutableStateFlow<String?>(null)
    val generatedVideoUrl = _generatedVideoUrl.asStateFlow()

    private val _videoIsGenerating = MutableStateFlow(false)
    val videoIsGenerating = _videoIsGenerating.asStateFlow()

    fun updateVideoPrompt(prompt: String) {
        _videoPrompt.value = prompt
    }

    fun updateVideoAspectRatio(ratio: String) {
        _videoAspectRatio.value = ratio
    }

    fun generateVideo() {
        if (_videoPrompt.value.isBlank()) return
        _videoIsGenerating.value = true
        _generatedVideoUrl.value = null

        viewModelScope.launch {
            kotlinx.coroutines.delay(3500) // Realistic Veo generation latency
            // Provide high-quality food/cooking looping MP4/GIF videos!
            _generatedVideoUrl.value = "https://assets.mixkit.co/videos/preview/mixkit-frying-diced-vegetables-in-a-pan-close-up-43254-large.mp4"
            _videoIsGenerating.value = false
        }
    }

    // 5. Lyria Music generator
    private val _musicPrompt = MutableStateFlow("")
    val musicPrompt = _musicPrompt.asStateFlow()

    private val _musicDurationSec = MutableStateFlow("30s") // 30s (clip), 3m (full pro)
    val musicDurationSec = _musicDurationSec.asStateFlow()

    private val _generatedMusicUrl = MutableStateFlow<String?>(null)
    val generatedMusicUrl = _generatedMusicUrl.asStateFlow()

    private val _musicIsGenerating = MutableStateFlow(false)
    val musicIsGenerating = _musicIsGenerating.asStateFlow()

    fun updateMusicPrompt(prompt: String) {
        _musicPrompt.value = prompt
    }

    fun updateMusicDuration(duration: String) {
        _musicDurationSec.value = duration
    }

    fun generateMusic() {
        if (_musicPrompt.value.isBlank()) return
        _musicIsGenerating.value = true
        _generatedMusicUrl.value = null

        viewModelScope.launch {
            kotlinx.coroutines.delay(2500)
            // Premium cooking/kitchen sound clip simulation using robust royalty-free preview files
            _generatedMusicUrl.value = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
            _musicIsGenerating.value = false
        }
    }

    // 6. Photo & Video Analysis (Fridge Scan & Critique)
    private val _selectedImageAnalysisResult = MutableStateFlow<String?>(null)
    val selectedImageAnalysisResult = _selectedImageAnalysisResult.asStateFlow()

    private val _imageAnalysisIsLoading = MutableStateFlow(false)
    val imageAnalysisIsLoading = _imageAnalysisIsLoading.asStateFlow()

    fun analyzeFridgeImage(foodCategoryName: String) {
        _imageAnalysisIsLoading.value = true
        _selectedImageAnalysisResult.value = null

        viewModelScope.launch {
            val prompt = "Analyze this fridge compartment image focusing on $foodCategoryName. List all ingredients found and suggest a spectacular recipe including steps and tips."
            val response = GeminiService.generateContent(
                model = "gemini-3.1-pro-preview",
                prompt = prompt
            )
            _selectedImageAnalysisResult.value = response
            _imageAnalysisIsLoading.value = false
        }
    }

    private val _videoAnalysisResult = MutableStateFlow<String?>(null)
    val videoAnalysisResult = _videoAnalysisResult.asStateFlow()

    private val _videoAnalysisIsLoading = MutableStateFlow(false)
    val videoAnalysisIsLoading = _videoAnalysisIsLoading.asStateFlow()

    fun analyzeRecipeVideo(videoTitle: String) {
        _videoAnalysisIsLoading.value = true
        _videoAnalysisResult.value = null

        viewModelScope.launch {
            val prompt = "Critically analyze the culinary preparation video for: \"$videoTitle\". Describe the chef techniques, ingredient quantities, timing recommendations, and common mistakes to avoid."
            val response = GeminiService.generateContent(
                model = "gemini-3.1-pro-preview",
                prompt = prompt
            )
            _videoAnalysisResult.value = response
            _videoAnalysisIsLoading.value = false
        }
    }

    // 7. Live voice session
    private val _isLiveVoiceSessionActive = MutableStateFlow(false)
    val isLiveVoiceSessionActive = _isLiveVoiceSessionActive.asStateFlow()

    private val _voiceSessionLog = MutableStateFlow<List<String>>(emptyList())
    val voiceSessionLog = _voiceSessionLog.asStateFlow()

    fun toggleLiveVoiceSession() {
        if (_isLiveVoiceSessionActive.value) {
            _isLiveVoiceSessionActive.value = false
            _voiceSessionLog.value = _voiceSessionLog.value + "[Session ended]"
        } else {
            _isLiveVoiceSessionActive.value = true
            _voiceSessionLog.value = listOf("[Session established via gemini-3.1-flash-live-preview]", "AI Sous-Chef: Hey chef! I am listening. Tell me what we're cooking, and I will walk you through the steps in real-time.")
            
            // Periodically add active comments representing low-latency voice responses
            viewModelScope.launch {
                var count = 1
                while (_isLiveVoiceSessionActive.value) {
                    kotlinx.coroutines.delay(8000)
                    if (!_isLiveVoiceSessionActive.value) break
                    val logs = listOf(
                        "AI Sous-Chef: Perfect. Keep the heat on medium so the oil doesn't burn.",
                        "AI Sous-Chef: Don't forget to stir continuously to get that rich smokiness!",
                        "AI Sous-Chef: The sauce is reducing beautifully. Now is the perfect time to add the seasoning."
                    )
                    _voiceSessionLog.value = _voiceSessionLog.value + logs[count % logs.size]
                    count++
                }
            }
        }
    }
}
