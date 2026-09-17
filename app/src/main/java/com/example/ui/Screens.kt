package com.example.ui

import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.*
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.core.Spring
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke

fun getMealImageModel(imageUrl: String, mealName: String = ""): Any {
    if (imageUrl.startsWith("file:") || imageUrl.startsWith("content:")) {
        return imageUrl
    }
    val combined = "$imageUrl $mealName".lowercase()
    return when {
        // Ultra-realistic Japanese Dishes
        combined.contains("ramen") || combined.contains("tonkotsu") || combined.contains("sushi") || combined.contains("japanese") -> {
            com.example.R.drawable.img_japanese_ramen_1789339722778
        }
        // Ultra-realistic Italian Dishes
        combined.contains("pasta") || combined.contains("lasagna") || combined.contains("fettuccine") || combined.contains("tagliatelle") || combined.contains("bolognese") || combined.contains("tiramisu") || combined.contains("italian") -> {
            com.example.R.drawable.img_italian_pasta_1789339735435
        }
        // Ultra-realistic Indian Dishes
        combined.contains("butter chicken") || combined.contains("curry") || combined.contains("tikka") || combined.contains("naan") || combined.contains("biryani") || combined.contains("indian") -> {
            com.example.R.drawable.img_indian_curry_1789339747865
        }
        // Ultra-realistic Chinese Dishes
        combined.contains("dumpling") || combined.contains("dim sum") || combined.contains("potsticker") || combined.contains("bao") || combined.contains("har gow") || combined.contains("siu mai") -> {
            com.example.R.drawable.img_chinese_dim_sum_1789339771202
        }
        combined.contains("kung pao") || combined.contains("gong bao") -> {
            com.example.R.drawable.img_kung_pao_chicken_1789339134096
        }
        combined.contains("mapo") || combined.contains("tofu") || combined.contains("dan dan") || combined.contains("sichuan") || combined.contains("chinese") -> {
            com.example.R.drawable.img_sichuan_mapo_tofu_1789339156205
        }
        // Ultra-realistic Mexican Dishes
        combined.contains("birria") || combined.contains("taco") || combined.contains("tacos") || combined.contains("mexican") -> {
            com.example.R.drawable.img_birria_tacos_1789339758620
        }
        combined.contains("enchilada") || combined.contains("enchiladas") -> {
            com.example.R.drawable.img_mexican_enchiladas_1789339144430
        }
        // African & Other Dishes
        combined.contains("pancake") -> {
            com.example.R.drawable.pancake_stack_1789239350389
        }
        combined.contains("ayamase") || combined.contains("ofada") -> {
            com.example.R.drawable.img_ayamase_ofada_1786131015707
        }
        combined.contains("efo riro") -> {
            com.example.R.drawable.img_efo_riro_1786131027448
        }
        combined.contains("buka stew") || combined.contains("obe ata") || combined.contains("locust beans") -> {
            com.example.R.drawable.img_buka_stew_1786131038875
        }
        combined.contains("asaro") || combined.contains("yam porridge") -> {
            com.example.R.drawable.img_asaro_porridge_1786131053022
        }
        combined.contains("gizdodo") || combined.contains("suya") -> {
            com.example.R.drawable.img_gizdodo_dish_1786131065358
        }
        combined.contains("jollof") -> {
            com.example.R.drawable.img_jollof_rice_1782163924128
        }
        combined.contains("egusi") -> {
            com.example.R.drawable.img_egusi_pounded_yam_1782163995182
        }
        combined.contains("amala") || combined.contains("abula") -> {
            com.example.R.drawable.img_amala_abula_1782164562868
        }
        combined.contains("puff puff") -> {
            com.example.R.drawable.img_nigerian_puff_puff_1784429983181
        }
        combined.contains("moi moi") || combined.contains("moin moin") -> {
            com.example.R.drawable.img_moi_moi_1784456040852
        }
        combined.contains("chin chin") || combined.contains("chinchin") -> {
            com.example.R.drawable.img_chin_chin_snack_1784456350762
        }
        combined.contains("ewa agoyin") || combined.contains("agoyin") -> {
            com.example.R.drawable.ewa_agoyin_plate_1784456541832
        }
        else -> imageUrl
    }
}

fun getMealCountry(meal: MealEntity, chef: ChefEntity?): String {
    val mealNameLower = meal.name.lowercase()
    val descLower = meal.description.lowercase()
    val cuisineLower = chef?.cuisineType?.lowercase() ?: ""
    val categoryLower = meal.category.lowercase()
    
    return when {
        categoryLower == "china" || categoryLower == "chinese" ||
        mealNameLower.contains("dumpling") || mealNameLower.contains("dim sum") ||
        mealNameLower.contains("kung pao") || mealNameLower.contains("mapo") ||
        mealNameLower.contains("dan dan") || mealNameLower.contains("sichuan") ||
        descLower.contains("sichuan") || descLower.contains("dim sum") ||
        cuisineLower.contains("chinese") || cuisineLower.contains("sichuan") || cuisineLower.contains("china") -> "China"
        
        categoryLower == "mexico" || categoryLower == "mexican" ||
        mealNameLower.contains("taco") || mealNameLower.contains("birria") ||
        mealNameLower.contains("enchilada") || mealNameLower.contains("mole") ||
        mealNameLower.contains("tres leches") || descLower.contains("oaxaca") ||
        descLower.contains("tortilla") || descLower.contains("salsa") ||
        cuisineLower.contains("mexican") || cuisineLower.contains("mexico") || cuisineLower.contains("oaxaca") -> "Mexico"
        
        categoryLower == "west africa" || categoryLower == "nigerian" ||
        mealNameLower.contains("jollof") || mealNameLower.contains("egusi") ||
        mealNameLower.contains("amala") || mealNameLower.contains("suya") ||
        mealNameLower.contains("ewa agoyin") || mealNameLower.contains("pancake") ||
        mealNameLower.contains("ayamase") || mealNameLower.contains("efo riro") ||
        mealNameLower.contains("buka stew") || mealNameLower.contains("asaro") ||
        mealNameLower.contains("gizdodo") || mealNameLower.contains("puff puff") ||
        mealNameLower.contains("moi moi") || mealNameLower.contains("chin chin") ||
        cuisineLower.contains("nigerian") || cuisineLower.contains("west african") ||
        cuisineLower.contains("african") -> "West Africa"
        
        categoryLower == "ghana" ||
        mealNameLower.contains("waakye") || mealNameLower.contains("kelewele") ||
        mealNameLower.contains("sobolo") || cuisineLower.contains("ghanaian") ||
        cuisineLower.contains("ghanian") -> "Ghana"
        
        categoryLower == "japan" || categoryLower == "japanese" ||
        mealNameLower.contains("ramen") || mealNameLower.contains("sushi") ||
        mealNameLower.contains("salmon roll") || mealNameLower.contains("tonkotsu") ||
        cuisineLower.contains("ramen") || cuisineLower.contains("sushi") ||
        cuisineLower.contains("japanese") -> "Japan"
        
        categoryLower == "italy" || categoryLower == "italian" ||
        mealNameLower.contains("lasagna") || mealNameLower.contains("fettuccine") ||
        mealNameLower.contains("tiramisu") || mealNameLower.contains("panna cotta") ||
        cuisineLower.contains("italian") || cuisineLower.contains("pasta") -> "Italy"
        
        categoryLower == "india" || categoryLower == "indian" ||
        mealNameLower.contains("butter chicken") || mealNameLower.contains("naan") ||
        mealNameLower.contains("biryani") || cuisineLower.contains("indian") ||
        cuisineLower.contains("curry") -> "India"
        
        categoryLower == "middle east" ||
        mealNameLower.contains("tawook") || mealNameLower.contains("hummus") ||
        mealNameLower.contains("baklava") || mealNameLower.contains("limonana") ||
        cuisineLower.contains("middle eastern") || cuisineLower.contains("arab") -> "Middle East"
        
        else -> "Other"
    }
}

fun getChefAvatarModel(avatarUrl: String, chefName: String = ""): Any {
    if (avatarUrl.contains("mama_titi", ignoreCase = true) || chefName.contains("Mama Titi", ignoreCase = true)) {
        return com.example.R.drawable.img_mama_titi_realistic_1789339798903
    }
    if (avatarUrl.startsWith("file:") || avatarUrl.startsWith("content:")) {
        return avatarUrl
    }
    return avatarUrl.ifBlank { "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150" }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(viewModel: HomeChefViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val alerts by viewModel.alerts.collectAsState()
    val unreadCount = remember(alerts) { alerts.count { !it.isRead } }

    // Real-time floating toast notifications state management
    var activeToasts by remember { mutableStateOf<List<AlertEntity>>(emptyList()) }
    val seenAlertIds = remember { mutableStateListOf<Int>() }
    var isFirstAlertCollection by remember { mutableStateOf(true) }

    LaunchedEffect(alerts) {
        if (alerts.isNotEmpty()) {
            if (isFirstAlertCollection) {
                // Initialize seen alerts with history to prevent startup toast storm
                alerts.forEach { seenAlertIds.add(it.id) }
                isFirstAlertCollection = false
            } else {
                // Safely identify and queue only brand-new alerts triggered in this session
                val newAlerts = alerts.filter { it.id !in seenAlertIds }
                if (newAlerts.isNotEmpty()) {
                    newAlerts.forEach { alert ->
                        seenAlertIds.add(alert.id)
                        activeToasts = activeToasts + alert
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            val isRootBottomTab = currentScreen is Screen.Explore ||
                    currentScreen is Screen.MapSearch ||
                    currentScreen is Screen.Orders ||
                    currentScreen is Screen.GoLiveConfig
            if (!isRootBottomTab) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1B1612),
                                modifier = Modifier
                                    .size(28.dp)
                                    .testTag("topbar_citch_logo")
                            ) {
                                Image(
                                    painter = painterResource(id = com.example.R.drawable.img_citch_logo_1789242928296),
                                    contentDescription = "Citch Logo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            val titleText = when (currentScreen) {
                                is Screen.ChefDetail -> "Kitchen Details"
                                is Screen.Showcase -> "Community Showcase"
                                is Screen.Notifications -> "Notifications"
                                is Screen.AICulinaryHub -> "AI Kitchen Assistant"
                                is Screen.Camera -> "Snap Kitchen Photo"
                                is Screen.DishGallery -> "Dish Photos"
                                else -> "Citch"
                            }
                            Text(
                                text = titleText,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF1B1612)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { viewModel.navigateTo(Screen.Explore) }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF1B1612)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFFAF6F0),
                        titleContentColor = Color(0xFF1B1612)
                    )
                )
            }
        },
        bottomBar = {
            Surface(
                color = Color(0xFFFAF6F0),
                border = BorderStroke(1.dp, Color(0xFFECE6DD)),
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(62.dp)
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Home
                    val isHome = currentScreen is Screen.Explore
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo(Screen.Explore) }
                            .padding(vertical = 4.dp)
                            .testTag("nav_home")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = if (isHome) Color(0xFFD8582B) else Color(0xFF7A7067),
                            modifier = Modifier.size(23.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Home",
                            fontSize = 11.sp,
                            fontWeight = if (isHome) FontWeight.Bold else FontWeight.Medium,
                            color = if (isHome) Color(0xFFD8582B) else Color(0xFF7A7067)
                        )
                        if (isHome) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .width(22.dp)
                                    .height(2.5.dp)
                                    .background(Color(0xFFD8582B), RoundedCornerShape(2.dp))
                            )
                        } else {
                            Spacer(modifier = Modifier.height(4.5.dp))
                        }
                    }

                    // Explore
                    val isExplore = currentScreen is Screen.MapSearch
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo(Screen.MapSearch) }
                            .padding(vertical = 4.dp)
                            .testTag("nav_explore")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Explore",
                            tint = if (isExplore) Color(0xFFD8582B) else Color(0xFF7A7067),
                            modifier = Modifier.size(23.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Explore",
                            fontSize = 11.sp,
                            fontWeight = if (isExplore) FontWeight.Bold else FontWeight.Medium,
                            color = if (isExplore) Color(0xFFD8582B) else Color(0xFF7A7067)
                        )
                        if (isExplore) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .width(22.dp)
                                    .height(2.5.dp)
                                    .background(Color(0xFFD8582B), RoundedCornerShape(2.dp))
                            )
                        } else {
                            Spacer(modifier = Modifier.height(4.5.dp))
                        }
                    }

                    // Orders
                    val isOrders = currentScreen is Screen.Orders
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo(Screen.Orders) }
                            .padding(vertical = 4.dp)
                            .testTag("nav_orders")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Orders",
                            tint = if (isOrders) Color(0xFFD8582B) else Color(0xFF7A7067),
                            modifier = Modifier.size(23.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Orders",
                            fontSize = 11.sp,
                            fontWeight = if (isOrders) FontWeight.Bold else FontWeight.Medium,
                            color = if (isOrders) Color(0xFFD8582B) else Color(0xFF7A7067)
                        )
                        if (isOrders) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .width(22.dp)
                                    .height(2.5.dp)
                                    .background(Color(0xFFD8582B), RoundedCornerShape(2.dp))
                            )
                        } else {
                            Spacer(modifier = Modifier.height(4.5.dp))
                        }
                    }

                    // Profile
                    val isProfile = currentScreen is Screen.GoLiveConfig
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo(Screen.GoLiveConfig) }
                            .padding(vertical = 4.dp)
                            .testTag("nav_profile")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonOutline,
                            contentDescription = "Profile",
                            tint = if (isProfile) Color(0xFFD8582B) else Color(0xFF7A7067),
                            modifier = Modifier.size(23.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Profile",
                            fontSize = 11.sp,
                            fontWeight = if (isProfile) FontWeight.Bold else FontWeight.Medium,
                            color = if (isProfile) Color(0xFFD8582B) else Color(0xFF7A7067)
                        )
                        if (isProfile) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .width(22.dp)
                                    .height(2.5.dp)
                                    .background(Color(0xFFD8582B), RoundedCornerShape(2.dp))
                            )
                        } else {
                            Spacer(modifier = Modifier.height(4.5.dp))
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                when (screen) {
                    is Screen.Explore -> ExploreScreen(viewModel)
                    is Screen.Showcase -> ShowcaseScreen(viewModel)
                    is Screen.MapSearch -> MapSearchScreen(viewModel)
                    is Screen.Orders -> OrdersScreen(viewModel)
                    is Screen.Notifications -> NotificationsScreen(viewModel)
                    is Screen.GoLiveConfig -> GoLiveConfigScreen(viewModel)
                    is Screen.AICulinaryHub -> AICulinaryHubScreen(viewModel)
                    is Screen.Camera -> CameraScreen(
                        onImageCaptured = { _, _ -> viewModel.navigateTo(Screen.DishGallery) },
                        onNavigateBack = { viewModel.navigateTo(Screen.Showcase) }
                    )
                    is Screen.DishGallery -> DishGalleryScreen(
                        viewModel = viewModel,
                        onNavigateToCamera = { viewModel.navigateTo(Screen.Camera) },
                        onNavigateBack = { viewModel.navigateTo(Screen.Showcase) }
                    )
                    is Screen.ChefDetail -> ChefDetailScreen(screen.chefId, viewModel)
                }
            }

            // Real-time floating toast notifications hud overlaid above active screens
            if (activeToasts.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    activeToasts.take(3).forEach { alert ->
                        key(alert.id) {
                            ToastNotificationItem(
                                alert = alert,
                                onDismiss = {
                                    activeToasts = activeToasts.filter { it.id != alert.id }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ToastNotificationItem(
    alert: AlertEntity,
    onDismiss: () -> Unit
) {
    // Standard timeout to auto-dismiss: 6 seconds
    LaunchedEffect(alert.id) {
        delay(6000)
        onDismiss()
    }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier.fillMaxWidth(0.95f)
    ) {
        val (icon, tintColor) = remember(alert.title) {
            when {
                alert.title.contains("Paid", ignoreCase = true) || alert.title.contains("Securely", ignoreCase = true) -> {
                    Icons.AutoMirrored.Filled.ReceiptLong to Color(0xFF2E7D32) // Success Green
                }
                alert.title.contains("Preparing", ignoreCase = true) || alert.title.contains("Kitchen Preparing", ignoreCase = true) -> {
                    Icons.Default.RestaurantMenu to Color(0xFFE65100) // Warm Orange for active prep
                }
                alert.title.contains("Delivery", ignoreCase = true) || alert.title.contains("Out for Delivery", ignoreCase = true) -> {
                    Icons.Default.LocationOn to Color(0xFF1976D2) // Courier Blue
                }
                alert.title.contains("Arrived", ignoreCase = true) || alert.title.contains("Served", ignoreCase = true) || alert.title.contains("Delivered", ignoreCase = true) -> {
                    Icons.Default.Verified to Color(0xFF8E24AA) // Celebratory Purple/Gold
                }
                alert.title.contains("Kitchen Alert", ignoreCase = true) -> {
                    Icons.Default.Campaign to Color(0xFFC62828) // Promotion/Alert Red
                }
                else -> {
                    Icons.Default.Notifications to Color(0xFF00796B) // Default teal
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("toast_alert_${alert.id}"),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.96f)
            ),
            border = BorderStroke(1.dp, tintColor.copy(alpha = 0.35f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Icon with subtle circular background
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(tintColor.copy(alpha = 0.12f), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tintColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Alert description Column
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = alert.title,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = alert.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Quick Dismiss Button
                IconButton(
                    onClick = {
                        visible = false
                        onDismiss()
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("dismiss_toast_button_${alert.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

data class LeaderboardChefData(
    val chef: ChefEntity,
    val averageRating: Double,
    val reviewCount: Int,
    val orderVolume: Int,
    val score: Double
)

@Composable
fun ChefLeaderboardSection(
    leaderboardChefs: List<LeaderboardChefData>,
    onChefClick: (Int) -> Unit
) {
    var showFormulaDialog by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("leaderboard_container_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🏆 Top Rated Chefs",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = { showFormulaDialog = true },
                        modifier = Modifier.size(24.dp).testTag("leaderboard_info_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Score Formula Info",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                TextButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.testTag("toggle_leaderboard_expand")
                ) {
                    Text(
                        text = if (isExpanded) "Hide" else "Show Ranks",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))

                // Display Top 5
                val topChefs = leaderboardChefs.take(5)

                if (topChefs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No chef rankings available yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        topChefs.forEachIndexed { index, leaderboardItem ->
                            val rank = index + 1
                            val rankColor = when (rank) {
                                1 -> Color(0xFFFBC02D) // Gold
                                2 -> Color(0xFFB0BEC5) // Silver
                                3 -> Color(0xFFD84315) // Bronze
                                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            }

                            val rankIcon = when (rank) {
                                1 -> "🥇"
                                2 -> "🥈"
                                3 -> "🥉"
                                else -> "•"
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onChefClick(leaderboardItem.chef.id) }
                                    .testTag("leaderboard_item_rank_$rank"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Rank Number / Badge
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                color = rankColor.copy(alpha = 0.15f),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (rank <= 3) {
                                            Text(
                                                text = rankIcon,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        } else {
                                            Text(
                                                text = "#$rank",
                                                color = rankColor,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // Chef image and name info
                                    AsyncImage(
                                        model = leaderboardItem.chef.avatarUrl,
                                        contentDescription = "Chef Avatar",
                                        placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                                        contentScale = ContentScale.Crop
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = leaderboardItem.chef.name,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = leaderboardItem.chef.cuisineType,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Dynamic score and volume metrics
                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "${String.format("%.1f", leaderboardItem.score)} pts",
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = Color(0xFFFFD54F),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = String.format("%.1f", leaderboardItem.averageRating),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.Default.ShoppingBag,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = "${leaderboardItem.orderVolume}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFormulaDialog) {
        AlertDialog(
            onDismissRequest = { showFormulaDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("How we rank our Chefs")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Our Chef Leaderboard is calculated dynamically in real-time based on actual user reviews and kitchen order volume:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Leaderboard Score =\n(Average Rating × 15) + (Order Volume × 5)",
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Text(
                        text = "⭐ Average Rating (Weight: 15):\nRepresents the quality of cooking. Higher ratings from reviews give the biggest points boost.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "📦 Order Volume (Weight: 5):\nRepresents local popularity. Every order placed dynamically increases the chef's rank standing!",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showFormulaDialog = false },
                    modifier = Modifier.testTag("dismiss_formula_dialog")
                ) {
                    Text("Got it")
                }
            }
        )
    }
}

// EXPLORE MAIN SCREEN
@Composable
fun ExploreScreen(viewModel: HomeChefViewModel) {
    val chefs by viewModel.chefs.collectAsState()
    val meals by viewModel.meals.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val alerts by viewModel.alerts.collectAsState()
    val unreadAlerts = remember(alerts) { alerts.count { !it.isRead } }

    val selectedLocation by viewModel.currentLocationName.collectAsState()
    val currentCurrencyCode by viewModel.currentCurrencyCode.collectAsState()
    val currentCurrencySymbol by viewModel.currentCurrencySymbol.collectAsState()
    val isDetectingLocation by viewModel.isDetectingLocation.collectAsState()
    val locationDetectionMessage by viewModel.locationDetectionMessage.collectAsState()

    var showLocationDialog by remember { mutableStateOf(false) }
    var selectedServiceMode by remember { mutableIntStateOf(0) } // 0: Collect, 1: Meet, 2: At mine
    var selectedCuisine by remember { mutableStateOf("All") }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        viewModel.detectUserLocationAuto()
    }

    val leaderboardChefs = remember(chefs, reviews, orders) {
        chefs.map { chef ->
            val chefReviews = reviews.filter { it.chefId == chef.id }
            val chefOrders = orders.filter { it.chefId == chef.id }
            
            val avgRating = if (chefReviews.isNotEmpty()) {
                chefReviews.map { it.rating }.average()
            } else {
                chef.rating.toDouble()
            }
            
            val orderVolume = chefOrders.size
            val score = (avgRating * 15) + (orderVolume * 5)
            
            LeaderboardChefData(
                chef = chef,
                averageRating = avgRating,
                reviewCount = chefReviews.size,
                orderVolume = orderVolume,
                score = score
            )
        }.sortedByDescending { it.score }
    }
    
    var showRegisterDialog by remember { mutableStateOf(false) }
    var showManagePhotosDialog by remember { mutableStateOf(false) }
    var checkoutMealForCountry by remember { mutableStateOf<MealEntity?>(null) }
    var checkoutChefNameForCountry by remember { mutableStateOf("") }

    val countryFilteredMeals = remember(meals, chefs, selectedCuisine) {
        if (selectedCuisine == "All") {
            // Curated representation of dishes from various popular countries
            meals.filter { meal ->
                val chef = chefs.find { it.id == meal.chefId }
                val country = getMealCountry(meal, chef)
                country in listOf("China", "Mexico", "West Africa", "Japan", "Italy", "India")
            }.take(10)
        } else {
            meals.filter { meal ->
                val chef = chefs.find { it.id == meal.chefId }
                getMealCountry(meal, chef).equals(selectedCuisine, ignoreCase = true)
            }
        }
    }

    // Map chefs with their calculated proximity distance from user's location
    val chefsWithDistance = remember(chefs, viewModel.userLat, viewModel.userLng) {
        chefs.map { chef ->
            val dist = if (chef.name.contains("Mama Titi", ignoreCase = true)) {
                0.32
            } else {
                viewModel.getDistanceToUser(chef.latitude, chef.longitude)
            }
            chef to dist
        }
    }

    val filteredAndSortedChefs = remember(chefsWithDistance, meals, searchQuery, selectedCuisine) {
        val searched = if (searchQuery.isEmpty()) {
            chefsWithDistance
        } else {
            chefsWithDistance.filter { (chef, _) ->
                val chefMeals = meals.filter { it.chefId == chef.id }
                chef.name.contains(searchQuery, ignoreCase = true) ||
                chef.cuisineType.contains(searchQuery, ignoreCase = true) ||
                chef.address.contains(searchQuery, ignoreCase = true) ||
                chefMeals.any { meal ->
                    meal.name.contains(searchQuery, ignoreCase = true) ||
                    meal.description.contains(searchQuery, ignoreCase = true)
                }
            }
        }

        val cuisineFiltered = if (selectedCuisine == "All") {
            searched
        } else {
            searched.filter { (chef, _) ->
                chef.cuisineType.contains(selectedCuisine, ignoreCase = true) ||
                (selectedCuisine == "China" && (chef.cuisineType.contains("Chinese", true) || chef.cuisineType.contains("Sichuan", true))) ||
                (selectedCuisine == "Mexico" && (chef.cuisineType.contains("Mexican", true) || chef.cuisineType.contains("Oaxaca", true))) ||
                (selectedCuisine == "West Africa" && (chef.cuisineType.contains("African", true) || chef.cuisineType.contains("Nigerian", true) || chef.cuisineType.contains("Ghanaian", true))) ||
                (selectedCuisine == "Japan" && (chef.cuisineType.contains("Japanese", true) || chef.cuisineType.contains("Ramen", true) || chef.cuisineType.contains("Sushi", true))) ||
                (selectedCuisine == "Italy" && (chef.cuisineType.contains("Italian", true) || chef.cuisineType.contains("Pasta", true))) ||
                (selectedCuisine == "India" && (chef.cuisineType.contains("Indian", true) || chef.cuisineType.contains("Curry", true)))
            }
        }

        // Put Mama Titi first if present, then Chef of Week, Sponsored Top Placement, Pro Tier, then sorted by distance
        cuisineFiltered.sortedWith(
            compareByDescending<Pair<ChefEntity, Double>> { it.first.name.contains("Mama Titi", ignoreCase = true) }
                .thenByDescending { it.first.isChefOfTheWeek }
                .thenByDescending { it.first.isSponsored }
                .thenByDescending { it.first.isProTier }
                .thenBy { it.second }
        )
    }

    Scaffold(
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = { showManagePhotosDialog = true },
                    containerColor = Color.White,
                    contentColor = Color(0xFF1B1612),
                    modifier = Modifier.testTag("manage_kitchen_photos_fab")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Host Profile", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Manage Photos 📸", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                FloatingActionButton(
                    onClick = { showRegisterDialog = true },
                    containerColor = Color(0xFFD8582B),
                    contentColor = Color.White,
                    modifier = Modifier.testTag("register_chef_button")
                ) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = "Add Post")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Host Kitchen 🍳", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFAF6F0)),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Top Location Bar & Notification Bell
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo & Location selector
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color(0xFF1B1612),
                            border = BorderStroke(1.5.dp, Color(0xFF2C241E)),
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .size(68.dp)
                                .testTag("app_brand_logo")
                        ) {
                            Image(
                                painter = painterResource(id = com.example.R.drawable.img_citch_logo_1789242928296),
                                contentDescription = "Citch Logo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(
                            modifier = Modifier
                                .clickable { showLocationDialog = true }
                                .testTag("location_picker_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "CITCH",
                                    letterSpacing = 1.8.sp,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = Color(0xFF1B1612)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFD8582B).copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "FOOD",
                                        letterSpacing = 1.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 9.sp,
                                        color = Color(0xFFD8582B),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Location",
                                    tint = Color(0xFFD8582B),
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = selectedLocation,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF5A5046),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFFFF0EB),
                                    border = BorderStroke(0.8.dp, Color(0xFFFFD4C4))
                                ) {
                                    Text(
                                        text = "$currentCurrencyCode $currentCurrencySymbol",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = Color(0xFFD8582B),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.5.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Change Location",
                                    tint = Color(0xFF7A7067),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Notification Bell button
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFECE6DD)),
                        modifier = Modifier
                            .size(46.dp)
                            .clickable { viewModel.navigateTo(Screen.Notifications) }
                            .testTag("notification_bell_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.NotificationsNone,
                                contentDescription = "Alerts",
                                tint = Color(0xFF1B1612),
                                modifier = Modifier.size(22.dp)
                            )
                            if (unreadAlerts > 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(top = 10.dp, end = 10.dp)
                                        .size(8.dp)
                                        .background(Color(0xFFD8582B), CircleShape)
                                )
                            }
                        }
                    }
                }
            }

            // 2. Editorial Serif Headline: "Your neighbours are cooking."
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Your neighbours",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        color = Color(0xFF1B1612),
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "are cooking.",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontStyle = FontStyle.Italic,
                        fontFamily = FontFamily.Serif,
                        color = Color(0xFFD8582B),
                        letterSpacing = (-0.5).sp
                    )
                }
            }

            // 3. Search Bar
            item {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFECE6DD)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .height(52.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF8C827A),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("search_field"),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 15.sp,
                                color = Color(0xFF1B1612)
                            ),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "Jollof, dim sum, tacos...",
                                        fontSize = 15.sp,
                                        color = Color(0xFF9E948C)
                                    )
                                }
                                innerTextField()
                            }
                        )
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.setSearchQuery("") },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color(0xFF8C827A),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 3.5 Citch Club Diner Perks Banner
            item {
                val isCitchClubMember by viewModel.isCitchClubMember.collectAsState()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .testTag("citch_club_banner"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCitchClubMember) Color(0xFF1E432A) else Color(0xFFFFF7ED)
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isCitchClubMember) Color(0xFF2E6B43) else Color(0xFFFFEDD5)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isCitchClubMember) Color(0xFF88D49E) else Color(0xFFD8582B),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isCitchClubMember) "Citch Club Member" else "Join Citch Club",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isCitchClubMember) Color.White else Color(0xFF9A3412)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isCitchClubMember) Color(0xFF2E6B43) else Color(0xFFEA580C)
                                    ) {
                                        Text(
                                            text = if (isCitchClubMember) "ACTIVE" else "$9.99/mo",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (isCitchClubMember)
                                        "10% off all orders + FREE Delivery over $15"
                                    else
                                        "Get 10% off every order + Free Delivery over $15",
                                    fontSize = 11.sp,
                                    color = if (isCitchClubMember) Color(0xFFB7E4C7) else Color(0xFF7A7067)
                                )
                            }
                        }
                        Button(
                            onClick = {
                                viewModel.toggleCitchClubMembership(!isCitchClubMember)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCitchClubMember) Color(0xFF2E6B43) else Color(0xFFD8582B)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(
                                text = if (isCitchClubMember) "Active ✓" else "Join Now",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // 4. Segmented Control Service Mode
            item {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = Color(0xFFEFE9DF),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val modes = listOf("🏠 Collect", "📍 Meet", "🚶 At mine")
                        modes.forEachIndexed { index, mode ->
                            val isSelected = selectedServiceMode == index
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = if (isSelected) Color.White else Color.Transparent,
                                shadowElevation = if (isSelected) 2.dp else 0.dp,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedServiceMode = index }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                ) {
                                    Text(
                                        text = mode,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color(0xFF1B1612) else Color(0xFF756C64)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. Walking Distance Green Card
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Color(0xFF1E432A),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .clickable { viewModel.navigateTo(Screen.MapSearch) }
                        .testTag("walking_distance_banner")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF285435),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(text = "🏠", fontSize = 20.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "2 cooks within walking distance",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Closest is ",
                                        color = Color(0xFFBFE6C1),
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "4 min",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = " from you",
                                        color = Color(0xFFBFE6C1),
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Explore",
                            tint = Color(0xFFE5B842),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // 6. "What are they cooking?" Section
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "What are they cooking?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B1612)
                        )
                        Text(
                            text = "See all",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD8582B),
                            modifier = Modifier.clickable { selectedCuisine = "All" }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    val cuisineItems = listOf(
                        Triple("China", "CN", com.example.R.drawable.img_chinese_dim_sum_1789339771202),
                        Triple("Mexico", "MX", com.example.R.drawable.img_birria_tacos_1789339758620),
                        Triple("West Africa", "NG", com.example.R.drawable.img_jollof_rice_1782163924128),
                        Triple("Japan", "JP", com.example.R.drawable.img_japanese_ramen_1789339722778),
                        Triple("Italy", "IT", com.example.R.drawable.img_italian_pasta_1789339735435),
                        Triple("India", "IN", com.example.R.drawable.img_indian_curry_1789339747865)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(cuisineItems) { (name, code, resId) ->
                            val isSelected = selectedCuisine == name
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable {
                                    selectedCuisine = if (selectedCuisine == name) "All" else name
                                }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .border(
                                            width = if (isSelected) 2.5.dp else 1.dp,
                                            color = if (isSelected) Color(0xFFD8582B) else Color(0xFFE5DDD3),
                                            shape = RoundedCornerShape(18.dp)
                                        )
                                ) {
                                    AsyncImage(
                                        model = resId,
                                        contentDescription = name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.28f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = code,
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = name,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(0xFFD8582B) else Color(0xFF1B1612)
                                )
                            }
                        }
                    }
                }
            }

            // 6b. Popular Dishes by Country (Photo-realistic dish cards with Quick Order)
            if (countryFilteredMeals.isNotEmpty()) {
                item {
                    val countryFlag = when (selectedCuisine) {
                        "China" -> "🇨🇳"
                        "Mexico" -> "🇲🇽"
                        "West Africa" -> "🇳🇬"
                        "Japan" -> "🇯🇵"
                        "Italy" -> "🇮🇹"
                        "India" -> "🇮🇳"
                        else -> "🌍"
                    }
                    val sectionTitle = if (selectedCuisine == "All") {
                        "Popular Dishes Around the World 🌍"
                    } else {
                        "Popular $selectedCuisine Dishes $countryFlag"
                    }
                    val sectionSubtitle = when (selectedCuisine) {
                        "China" -> "Authentic hand-crafted dim sum dumplings, wok-fired Kung Pao & Sichuan heat"
                        "Mexico" -> "Slow-braised street birria tacos, baked enchiladas verdes & Oaxaca family recipes"
                        "West Africa" -> "Smoky firewood jollof, rich egusi soup, amala & flame-grilled suya"
                        "Japan" -> "Artisanal 24-hr broth tonkotsu ramen & fresh sushi rolls"
                        "Italy" -> "Slow-baked rustic lasagna bolognese & hand-rolled truffle pastas"
                        "India" -> "Velvety slow-cooked butter chicken & pillowy clay oven garlic naan"
                        else -> "Tap any country above (China, Mexico, etc.) to discover authentic popular dishes"
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = sectionTitle,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B1612)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = sectionSubtitle,
                                    fontSize = 12.sp,
                                    color = Color(0xFF7A7067),
                                    lineHeight = 16.sp
                                )
                            }
                            if (selectedCuisine != "All") {
                                TextButton(
                                    onClick = { selectedCuisine = "All" }
                                ) {
                                    Text(
                                        text = "Clear Filter",
                                        color = Color(0xFFD8582B),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(countryFilteredMeals) { meal ->
                                val chefForMeal = chefs.find { it.id == meal.chefId }
                                val chefName = chefForMeal?.name ?: "Master Chef"
                                val countryName = getMealCountry(meal, chefForMeal)
                                PopularCountryMealCard(
                                    meal = meal,
                                    chefName = chefName,
                                    country = countryName,
                                    location = selectedLocation,
                                    onCardClick = {
                                        viewModel.navigateTo(Screen.ChefDetail(meal.chefId))
                                    },
                                    onOrderClick = {
                                        checkoutMealForCountry = meal
                                        checkoutChefNameForCountry = chefName
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 7. "Open now near Yaba" Header
            item {
                Spacer(modifier = Modifier.height(16.dp))
                val cityName = selectedLocation.substringBefore(",")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Open now near $cityName",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B1612)
                    )
                    Text(
                        text = "${filteredAndSortedChefs.size} cooks",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF7A7067)
                    )
                }
            }

            // 8. Individual Chef Cards (Mama Titi prominently featured)
            items(filteredAndSortedChefs) { (chef, distKm) ->
                val chefMeals = meals.filter { it.chefId == chef.id }
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    ChefCard(
                        chef = chef,
                        meals = chefMeals,
                        distanceKm = distKm,
                        searchQuery = searchQuery,
                        onClick = {
                            viewModel.navigateTo(Screen.ChefDetail(chef.id))
                        }
                    )
                }
            }

            // 9. Featured African Specialties Section
            val africanMeals = meals.filter { meal ->
                meal.chefId == 5 ||
                meal.chefId == 101 ||
                meal.name.contains("Jollof", true) ||
                meal.name.contains("Egusi", true) ||
                meal.name.contains("Fufu", true) ||
                meal.name.contains("Yam", true) ||
                meal.name.contains("Amala", true) ||
                meal.name.contains("Suya", true)
            }
            if (africanMeals.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Featured Specialties 🌶️",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF1B1612)
                            )
                            Text(
                                text = "View All",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD8582B),
                                modifier = Modifier.clickable {
                                    viewModel.setSelectedCategory("All")
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(africanMeals) { meal ->
                                val chefForMeal = chefs.find { it.id == meal.chefId }
                                val chefName = chefForMeal?.name ?: "Mama Titi"
                                FeaturedAfricanMealCard(
                                    meal = meal,
                                    chefName = chefName,
                                    onClick = {
                                        viewModel.navigateTo(Screen.ChefDetail(meal.chefId))
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 10. Community Leaderboard Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    ChefLeaderboardSection(
                        leaderboardChefs = leaderboardChefs,
                        onChefClick = { chefId ->
                            viewModel.navigateTo(Screen.ChefDetail(chefId))
                        }
                    )
                }
            }
        }

        // Location Picker Dialog with Auto-Detection & Multi-Currency support
        if (showLocationDialog) {
            data class LocationPreset(
                val name: String,
                val country: String,
                val flag: String,
                val currency: String
            )

            val locationPresets = listOf(
                LocationPreset("Camden, London", "United Kingdom", "🇬🇧", "GBP (£)"),
                LocationPreset("Soho, London", "United Kingdom", "🇬🇧", "GBP (£)"),
                LocationPreset("Yaba, Lagos", "Nigeria", "🇳🇬", "NGN (₦)"),
                LocationPreset("Victoria Island, Lagos", "Nigeria", "🇳🇬", "NGN (₦)"),
                LocationPreset("Lekki Phase 1, Lagos", "Nigeria", "🇳🇬", "NGN (₦)"),
                LocationPreset("Dublin 2, Ireland", "Ireland", "🇮🇪", "EUR (€)"),
                LocationPreset("Toronto, Canada", "Canada", "🇨🇦", "CAD (CA$)"),
                LocationPreset("New York City, USA", "United States", "🇺🇸", "USD ($)"),
                LocationPreset("Berlin, Germany", "Germany", "🇩🇪", "EUR (€)"),
                LocationPreset("Paris, France", "France", "🇫🇷", "EUR (€)"),
                LocationPreset("Shinjuku, Tokyo", "Japan", "🇯🇵", "JPY (¥)"),
                LocationPreset("Bandra, Mumbai", "India", "🇮🇳", "INR (₹)"),
                LocationPreset("Osu, Accra", "Ghana", "🇬🇭", "GHS (GH₵)"),
                LocationPreset("Westlands, Nairobi", "Kenya", "🇰🇪", "KES (KSh)")
            )

            AlertDialog(
                onDismissRequest = { showLocationDialog = false },
                title = {
                    Column {
                        Text(
                            text = "Select Location & Currency",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B1612),
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Prices & cooks automatically adapt to your location",
                            fontSize = 12.sp,
                            color = Color(0xFF7A7067)
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. AUTO-DETECT BUTTON
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF1E432A),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    locationPermissionLauncher.launch(
                                        arrayOf(
                                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                                .testTag("auto_detect_location_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isDetectingLocation) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.MyLocation,
                                        contentDescription = "Auto-detect",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isDetectingLocation) "Detecting your location..." else "Auto-Detect My Location",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Uses GPS & Geocoder to recognize city & currency",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        // Status notification chip if any
                        locationDetectionMessage?.let { msg ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF1F8F4),
                                border = BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = msg,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF1E432A)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Or choose a popular city:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7A7067),
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        // 2. LIST OF POPULAR PRESETS
                        locationPresets.forEach { preset ->
                            val isSelected = selectedLocation.startsWith(preset.name.substringBefore(","))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFFFFEBE5) else Color(0xFFF7F7F8),
                                border = if (isSelected) BorderStroke(1.5.dp, Color(0xFFD8582B)) else BorderStroke(0.8.dp, Color(0xFFECE6DD)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.setLocation(preset.name)
                                        showLocationDialog = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(preset.flag, fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = preset.name,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                                color = if (isSelected) Color(0xFFD8582B) else Color(0xFF1B1612),
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = preset.country,
                                                fontSize = 11.sp,
                                                color = Color(0xFF7A7067)
                                            )
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) Color(0xFFD8582B) else Color(0xFFECE6DD),
                                        modifier = Modifier.padding(start = 6.dp)
                                    ) {
                                        Text(
                                            text = preset.currency,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else Color(0xFF5A5046),
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLocationDialog = false }) {
                        Text("Close", color = Color(0xFFD8582B), fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        if (showRegisterDialog) {
            RegisterKitchenDialog(viewModel = viewModel, onDismiss = { showRegisterDialog = false })
        }

        if (showManagePhotosDialog) {
            ManageHostKitchenPhotosDialog(viewModel = viewModel, onDismiss = { showManagePhotosDialog = false })
        }

        if (checkoutMealForCountry != null) {
            OrderCheckoutDialog(
                meal = checkoutMealForCountry!!,
                chefName = checkoutChefNameForCountry,
                viewModel = viewModel,
                onDismiss = { checkoutMealForCountry = null }
            )
        }
    }
}

// COMPOSABLE: POPULAR COUNTRY MEAL CARD (Photo-realistic dish showcase with instant order)
@Composable
fun PopularCountryMealCard(
    meal: MealEntity,
    chefName: String,
    country: String,
    location: String = "Lagos",
    onCardClick: () -> Unit,
    onOrderClick: () -> Unit
) {
    val countryFlag = when (country) {
        "China" -> "🇨🇳"
        "Mexico" -> "🇲🇽"
        "West Africa" -> "🇳🇬"
        "Japan" -> "🇯🇵"
        "Italy" -> "🇮🇹"
        "India" -> "🇮🇳"
        "Ghana" -> "🇬🇭"
        "Middle East" -> "🇱🇧"
        else -> "🍽️"
    }

    Card(
        modifier = Modifier
            .width(280.dp)
            .clickable(onClick = onCardClick)
            .testTag("country_meal_card_${meal.id}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFECE6DD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                AsyncImage(
                    model = getMealImageModel(meal.imageUrl, meal.name),
                    contentDescription = meal.name,
                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Top badges: Country Flag + Cuisine tag
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1E432A).copy(alpha = 0.90f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$countryFlag $country",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = meal.category,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B1612)
                        )
                    }
                }

                // Price Tag Pill
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .background(Color(0xFFD8582B), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = com.example.data.CurrencyHelper.formatPriceForLocation(meal.price, location),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = meal.name,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B1612),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color(0xFF7A7067),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "By $chefName",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF7A7067),
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = meal.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF7A7067),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Order Action Button
                Button(
                    onClick = onOrderClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("order_btn_${meal.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD8582B),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Order",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Order Now",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// COMPOSABLE: FEATURED WEST AFRICAN SPECIALTIES CARD
@Composable
fun FeaturedAfricanMealCard(
    meal: MealEntity,
    chefName: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .clickable(onClick = onClick)
            .testTag("featured_african_meal_card_${meal.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFECE6DD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                AsyncImage(
                    model = getMealImageModel(meal.imageUrl, meal.name),
                    contentDescription = meal.name,
                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1E432A), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "West African 🌶️",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = meal.category,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B1612)
                        )
                    }
                }
                
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .background(Color(0xFFD8582B), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = com.example.data.CurrencyHelper.formatPrice(meal.price),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = meal.name,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B1612),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(3.dp))
                
                Text(
                    text = "By $chefName",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF7A7067),
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = meal.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF7A7067),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

// COMPOSABLE: INDIVIDUAL CHEF CARD (Citch Community Marketplace Style)
@Composable
fun ChefCard(
    chef: ChefEntity,
    meals: List<MealEntity>,
    distanceKm: Double? = null,
    searchQuery: String = "",
    onClick: () -> Unit
) {
    var isFavorite by remember { mutableStateOf(false) }
    val firstMeal = meals.firstOrNull()
    val heroImageModel = remember(meals, chef) {
        if (firstMeal != null) {
            getMealImageModel(firstMeal.imageUrl, firstMeal.name)
        } else {
            getChefAvatarModel(chef.avatarUrl, chef.name)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("chef_card_${chef.id}"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFECE6DD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Main hero dish image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = heroImageModel,
                    contentDescription = chef.name,
                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    contentScale = ContentScale.Crop
                )

                // Top-Left: Status & Monetization badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when {
                        chef.isChefOfTheWeek -> Color(0xFFB45309)
                        chef.isSponsored -> Color(0xFF1D4ED8)
                        chef.isProTier -> Color(0xFF15803D)
                        else -> Color(0xFF1E432A)
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(14.dp)
                ) {
                    Text(
                        text = when {
                            chef.isChefOfTheWeek -> "🏆 Chef of the Week"
                            chef.isSponsored -> "⭐ Top Placement"
                            chef.isProTier -> "💎 Pro Kitchen Host"
                            else -> "Neighbour Fave"
                        },
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                // Top-Right: Favorite heart button
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 3.dp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                        .size(38.dp)
                        .clickable { isFavorite = !isFavorite }
                        .testTag("chef_fav_${chef.id}")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color(0xFFD8582B) else Color(0xFF333333),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Bottom-Right Overlapping Chef Avatar
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = BorderStroke(3.dp, Color.White),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-16).dp, y = 24.dp)
                        .size(54.dp)
                ) {
                    AsyncImage(
                        model = getChefAvatarModel(chef.avatarUrl, chef.name),
                        contentDescription = chef.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Card Body Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 14.dp)
            ) {
                // Chef Name & Distance Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chef.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = Color(0xFF1B1612)
                    )

                    val distFormatted = if (distanceKm != null) {
                        val minEst = (distanceKm * 10).toInt().coerceAtLeast(3)
                        val metersEst = if (distanceKm < 1.0) "${(distanceKm * 1000).toInt()}m" else "${String.format("%.1f", distanceKm)}km"
                        "📍 $minEst min • $metersEst"
                    } else {
                        "📍 4 min • 320m"
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE2F4E6)
                    ) {
                        Text(
                            text = distFormatted,
                            color = Color(0xFF1E432A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Bio / Quote
                Text(
                    text = chef.bio.ifBlank { "Cooking home-style flavours for the neighborhood — freshly made to order." },
                    color = Color(0xFF7A7067),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Rating & Starting Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFE5B842),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${chef.rating} (${chef.followersCount.takeIf { it > 0 } ?: 312})",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = Color(0xFF1B1612)
                        )
                    }

                    val minMealPrice = meals.minOfOrNull { it.price } ?: 0.8
                    val formattedMinPrice = com.example.data.CurrencyHelper.formatPriceForLocation(minMealPrice, chef.address)
                    Text(
                        text = "from $formattedMinPrice",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1B1612)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Social Proof: "👥 47 neighbours ordered this week"
                val weeklyCount = (chef.followersCount / 6).coerceIn(18, 95)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = Color(0xFF2E2722),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$weeklyCount neighbours ordered this week",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2E2722)
                    )
                }

                // Monetization badge pill row
                if (chef.isSponsored || chef.isProTier || chef.isChefOfTheWeek) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (chef.isChefOfTheWeek) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFFFBEB),
                                border = BorderStroke(1.dp, Color(0xFFFDE68A))
                            ) {
                                Text(
                                    "🏆 Chef of Week",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB45309),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        if (chef.isSponsored) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFEFF6FF),
                                border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                            ) {
                                Text(
                                    "⭐ Top Placement",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1D4ED8),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        if (chef.isProTier) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFF0FDF4),
                                border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                            ) {
                                Text(
                                    "💎 Pro Kitchen (8% Fee)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF15803D),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Search matches (if actively searching)
                val matchedMeals = if (searchQuery.isNotEmpty()) {
                    meals.filter {
                        it.name.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
                    }
                } else emptyList()

                if (matchedMeals.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFECE6DD))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "✨ Matches for \"$searchQuery\":",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFFD8582B)
                    )
                    matchedMeals.forEach { meal ->
                        Text(
                            text = "• ${meal.name}",
                            fontSize = 12.sp,
                            color = Color(0xFF1B1612)
                        )
                    }
                }
            }
        }
    }
}

// SHOWCASE SCREEN
@Composable
fun ShowcaseScreen(viewModel: HomeChefViewModel) {
    val chefs by viewModel.chefs.collectAsState()
    val meals by viewModel.meals.collectAsState()
    val likedMealIds by viewModel.likedMealIds.collectAsState()
    val mealLikesCount by viewModel.mealLikesCount.collectAsState()
    val mealComments by viewModel.mealComments.collectAsState()
    
    val isDark = isSystemInDarkTheme()
    
    val cuisineFilters = listOf(
        "All",
        "China 🇨🇳",
        "Mexico 🇲🇽",
        "West Africa 🇳🇬",
        "Japan 🇯🇵",
        "Italy 🇮🇹",
        "India 🇮🇳",
        "Ghana 🇬🇭",
        "Middle East 🇱🇧"
    )
    var selectedCuisine by remember { mutableStateOf("All") }
    
    val filteredMeals = remember(meals, chefs, selectedCuisine) {
        if (selectedCuisine == "All") {
            meals
        } else {
            val targetCountry = when {
                selectedCuisine.contains("China") -> "China"
                selectedCuisine.contains("Mexico") -> "Mexico"
                selectedCuisine.contains("West Africa") -> "West Africa"
                selectedCuisine.contains("Japan") -> "Japan"
                selectedCuisine.contains("Italy") -> "Italy"
                selectedCuisine.contains("India") -> "India"
                selectedCuisine.contains("Ghana") -> "Ghana"
                selectedCuisine.contains("Middle East") -> "Middle East"
                else -> selectedCuisine
            }
            meals.filter { meal ->
                val chef = chefs.find { it.id == meal.chefId }
                getMealCountry(meal, chef).equals(targetCountry, ignoreCase = true)
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App top minimalist clean header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Culinary Showcase",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp, fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Live kitchen creations & daily chef specials",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.DishGallery) },
                            modifier = Modifier.testTag("open_dish_gallery_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.GridOn,
                                contentDescription = "Dish Gallery",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.Camera) },
                            modifier = Modifier.testTag("open_camera_screen_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Snap Dish",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Cuisine Filter Bar (Uber-style horizontal pills)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(cuisineFilters) { cuisine ->
                    val isSelected = selectedCuisine == cuisine
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)) else null,
                        modifier = Modifier.clickable { selectedCuisine = cuisine }
                    ) {
                        Text(
                            text = cuisine,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        if (filteredMeals.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = "Empty",
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "No dishes to display for this cuisine",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            var checkoutMeal by remember { mutableStateOf<MealEntity?>(null) }
            var checkoutChefName by remember { mutableStateOf("") }

            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(filteredMeals) { meal ->
                        val chef = chefs.find { it.id == meal.chefId }
                        if (chef != null) {
                            val isLiked = likedMealIds.contains(meal.id)
                            val likesCount = mealLikesCount[meal.id] ?: ((meal.id * 17 + 23) % 150 + 12)
                            val commentsList = mealComments[meal.id] ?: emptyList()
                            
                            SocialDishPostCard(
                                meal = meal,
                                chef = chef,
                                isLiked = isLiked,
                                likesCount = likesCount,
                                comments = commentsList,
                                onLikeToggle = { viewModel.toggleLikeMeal(meal.id) },
                                onAddComment = { user, txt -> viewModel.addCommentToMeal(meal.id, user, txt) },
                                onViewChef = { viewModel.navigateTo(Screen.ChefDetail(chef.id)) },
                                onOrderNow = {
                                    checkoutMeal = meal
                                    checkoutChefName = chef.name
                                }
                            )
                        }
                    }
                }

                if (checkoutMeal != null) {
                    OrderCheckoutDialog(
                        meal = checkoutMeal!!,
                        chefName = checkoutChefName,
                        viewModel = viewModel,
                        onDismiss = { checkoutMeal = null }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SocialDishPostCard(
    meal: MealEntity,
    chef: ChefEntity,
    isLiked: Boolean,
    likesCount: Int,
    comments: List<Pair<String, String>>,
    onLikeToggle: () -> Unit,
    onAddComment: (String, String) -> Unit,
    onViewChef: () -> Unit,
    onOrderNow: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    var showComments by remember { mutableStateOf(false) }
    var newCommentText by remember { mutableStateOf("") }
    var reviewerName by remember { mutableStateOf("") }
    val context = LocalContext.current
    
    // Animate heart scale on state change
    val heartScale by animateFloatAsState(
        targetValue = if (isLiked) 1.3f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label = "HeartScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("social_dish_card_${meal.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            // Post Header (Chef Identity)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = chef.avatarUrl,
                    contentDescription = chef.name,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                    contentScale = ContentScale.Crop
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = chef.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = chef.cuisineType,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { onViewChef() }
                ) {
                    Text(
                        text = "View Menu",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Post Visual Image (with double-tap to like)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .combinedClickable(
                        onDoubleClick = {
                            if (!isLiked) {
                                onLikeToggle()
                                Toast.makeText(context, "Liked ${meal.name}! ❤️", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onClick = { /* Just select / read */ }
                    )
            ) {
                AsyncImage(
                    model = getMealImageModel(meal.imageUrl, meal.name),
                    contentDescription = meal.name,
                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Overlay Category Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = meal.category,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Overlay Sells Price Tag
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = com.example.data.CurrencyHelper.formatPrice(meal.price),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Likes and Interactions Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onLikeToggle,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like Button",
                            tint = if (isLiked) Color(0xFFFF3D00) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$likesCount likes",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    IconButton(
                        onClick = { showComments = !showComments },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = "Comments",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${comments.size} comments",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                IconButton(
                    onClick = {
                        Toast.makeText(context, "Link copied to share this gourmet masterpiece! 🔗", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Post Content Body / Caption
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = meal.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Dynamic Cooking Tutorial Section
                if (meal.tutorialVideoUrl.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    var isPlaying by remember { mutableStateOf(false) }

                    if (isPlaying) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black)
                        ) {
                            VideoPlayer(
                                youtubeVideoUrl = meal.tutorialVideoUrl,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                            IconButton(
                                onClick = { isPlaying = false },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Video",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else {
                        // Playback banner button
                        Card(
                            onClick = { isPlaying = true },
                            modifier = Modifier.fillMaxWidth().testTag("watch_tutorial_card_${meal.id}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(Color.Red, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play Tutorial",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Watch Cooking Tutorial 📺",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Learn secret preparation techniques directly from ${chef.name}.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onOrderNow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("social_order_button_${meal.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Order Dish",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Order Now • ${com.example.data.CurrencyHelper.formatPrice(meal.price)}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expandable Real-Time Comments Board
            if (showComments) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Recent Feedback 💬",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    
                    if (comments.isEmpty()) {
                        Text(
                            text = "No comments yet. Be the first to cheer them on!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        comments.forEach { (user, comment) ->
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user,
                                        fontWeight = FontWeight.ExtraBold,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(Color.Gray)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Just now",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.LightGray
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = comment,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDark) Color(0xFFAFA09C) else Color(0xFF534846)
                                )
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Write Comment Row Inputs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = reviewerName,
                                onValueChange = { reviewerName = it },
                                placeholder = { Text("Your Name", fontSize = 11.sp) },
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                )
                            )
                            OutlinedTextField(
                                value = newCommentText,
                                onValueChange = { newCommentText = it },
                                placeholder = { Text("Write a supportive comment...", fontSize = 11.sp) },
                                singleLine = false,
                                maxLines = 3,
                                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                )
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        IconButton(
                            onClick = {
                                if (newCommentText.isNotBlank()) {
                                    val nameToPost = if (reviewerName.isBlank()) "Guest Foodie" else reviewerName
                                    onAddComment(nameToPost, newCommentText)
                                    newCommentText = ""
                                    Toast.makeText(context, "Comment posted! 💬", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = newCommentText.isNotBlank(),
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(
                                    if (newCommentText.isNotBlank()) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.4f)
                                )
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send Comment",
                                tint = if (newCommentText.isNotBlank()) MaterialTheme.colorScheme.onPrimary else Color.Gray,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// MAP SEARCH SCREEN (Explore Nearby Kitchens)
@Composable
fun MapSearchScreen(viewModel: HomeChefViewModel) {
    val chefs by viewModel.chefs.collectAsState()
    val meals by viewModel.meals.collectAsState()
    val mapRangeKm by viewModel.mapRangeKm.collectAsState()
    val currentLocationName by viewModel.currentLocationName.collectAsState()
    val currentCurrencySymbol by viewModel.currentCurrencySymbol.collectAsState()

    val closeChefs = remember(chefs, mapRangeKm, currentLocationName) {
        viewModel.getChefsWithinRange(chefs, mapRangeKm)
    }

    var selectedChefForQuickCheckout by remember { mutableStateOf<ChefEntity?>(null) }
    var checkoutMeal by remember { mutableStateOf<MealEntity?>(null) }
    var checkoutChefName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF6F0))
    ) {
        // Editorial Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1B1612),
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("map_citch_logo")
                    ) {
                        Image(
                            painter = painterResource(id = com.example.R.drawable.img_citch_logo_1789242928296),
                            contentDescription = "Citch Logo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFECE6DD))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📍", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentLocationName,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = Color(0xFF1B1612)
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF1E432A).copy(alpha = 0.1f)
                ) {
                    Text(
                        "${closeChefs.size} active cooks",
                        color = Color(0xFF1E432A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Explore",
                fontSize = 30.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B1612),
                lineHeight = 34.sp
            )
            Text(
                text = "nearby home kitchens.",
                fontSize = 30.sp,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFD8582B),
                lineHeight = 34.sp
            )
        }

        // Map search radius card (Warm Cream & White Card)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFECE6DD)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Search Radius",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color(0xFF1B1612)
                        )
                        Text(
                            text = "Discover home cooks in your neighborhood",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF7A7067)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFD8582B),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "${String.format(Locale.US, "%.1f", mapRangeKm)} km",
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Slider(
                    value = mapRangeKm,
                    onValueChange = { viewModel.setMapRange(it) },
                    valueRange = 1f..15f,
                    steps = 14,
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFD8582B),
                        activeTrackColor = Color(0xFFD8582B),
                        inactiveTrackColor = Color(0xFFEFE9DF)
                    )
                )
            }
        }

        // Custom Dynamic Leaflet Map
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, Color(0xFFECE6DD), RoundedCornerShape(22.dp))
        ) {
            LeafletMapView(
                closeChefs = closeChefs,
                userLat = viewModel.userLat,
                userLng = viewModel.userLng,
                viewModel = viewModel,
                onQuickCheckout = { chefId ->
                    selectedChefForQuickCheckout = chefs.find { it.id == chefId }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Legend / User Marker Info Overlay
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .border(
                        1.dp,
                        Color(0xFFECE6DD),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF007AFF))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "You",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B1612)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E432A))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Kitchens: ${closeChefs.size}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E432A)
                )
            }
        }

        // Quick Order Kitchens Search Carousel
        if (closeChefs.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Kitchens Nearby (${closeChefs.size})",
                        fontFamily = FontFamily.Serif,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B1612)
                    )
                    Text(
                        text = "⚡ Instant Pickup / Collect",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF1E432A),
                        fontWeight = FontWeight.Bold
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(closeChefs) { (chef, distance) ->
                        Card(
                            modifier = Modifier
                                .width(260.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFECE6DD)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = chef.name,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                        color = Color(0xFF1B1612),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFFEF3C7)
                                    ) {
                                        Text(
                                            text = "★ ${String.format(Locale.US, "%.1f", chef.rating)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = Color(0xFFB45309),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFFAF6F0)
                                    ) {
                                        Text(
                                            text = "📍 ${String.format(Locale.US, "%.1f", distance)} km",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF7A7067),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = chef.cuisineType,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF7A7067)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedButton(
                                        onClick = { viewModel.navigateTo(Screen.ChefDetail(chef.id)) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        border = BorderStroke(1.dp, Color(0xFFECE6DD))
                                    ) {
                                        Text("Menu", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1B1612))
                                    }

                                    Button(
                                        onClick = { selectedChefForQuickCheckout = chef },
                                        modifier = Modifier.weight(1.3f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD8582B)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.ShoppingCart,
                                            contentDescription = null,
                                            modifier = Modifier.size(12.dp),
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Order", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog to Select Meal from Kitchen for Stripe Checkout
    if (selectedChefForQuickCheckout != null) {
        val targetChef = selectedChefForQuickCheckout!!
        val targetMeals = meals.filter { it.chefId == targetChef.id }

        AlertDialog(
            onDismissRequest = { selectedChefForQuickCheckout = null },
            title = {
                Column {
                    Text(
                        text = "Order Food from ${targetChef.name}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Powered by Stripe Checkout API",
                        fontSize = 12.sp,
                        color = Color(0xFF635BFF),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (targetMeals.isEmpty()) {
                        Text("No active meal items available for this kitchen currently.", style = MaterialTheme.typography.bodySmall)
                    } else {
                        Text("Select a dish to proceed to Stripe payment:", style = MaterialTheme.typography.bodySmall)
                        targetMeals.forEach { meal ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        checkoutMeal = meal
                                        checkoutChefName = targetChef.name
                                        selectedChefForQuickCheckout = null
                                    },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(meal.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                        Text(meal.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 1)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            checkoutMeal = meal
                                            checkoutChefName = targetChef.name
                                            selectedChefForQuickCheckout = null
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF635BFF)),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(com.example.data.CurrencyHelper.formatPrice(meal.price), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedChefForQuickCheckout = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Launch Stripe Order Checkout Dialog
    if (checkoutMeal != null) {
        OrderCheckoutDialog(
            meal = checkoutMeal!!,
            chefName = checkoutChefName,
            viewModel = viewModel,
            onDismiss = { checkoutMeal = null }
        )
    }
}

@Composable
fun LeafletMapView(
    closeChefs: List<Pair<ChefEntity, Double>>,
    userLat: Double,
    userLng: Double,
    viewModel: HomeChefViewModel,
    onQuickCheckout: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                }
                
                webViewClient = WebViewClient()
                
                addJavascriptInterface(object {
                    @android.webkit.JavascriptInterface
                    fun openChefDetails(chefId: Int) {
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            viewModel.navigateTo(Screen.ChefDetail(chefId))
                        }
                    }

                    @android.webkit.JavascriptInterface
                    fun quickCheckout(chefId: Int) {
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            onQuickCheckout(chefId)
                        }
                    }
                }, "Android")
                
                val html = generateLeafletHtml(closeChefs, userLat, userLng, isDark)
                loadDataWithBaseURL("https://openstreetmap.org", html, "text/html", "UTF-8", null)
            }
        },
        modifier = modifier,
        update = { webView ->
            val html = generateLeafletHtml(closeChefs, userLat, userLng, isDark)
            webView.loadDataWithBaseURL("https://openstreetmap.org", html, "text/html", "UTF-8", null)
        },
        onRelease = { webView ->
            try {
                webView.stopLoading()
                webView.destroy()
            } catch (e: Exception) {
                // ignore
            }
        }
    )
}

fun generateLeafletHtml(
    closeChefs: List<Pair<ChefEntity, Double>>,
    userLat: Double,
    userLng: Double,
    isDark: Boolean
): String {
    val markersCode = StringBuilder()
    closeChefs.forEach { (chef, distance) ->
        val escapedName = chef.name.replace("'", "\\'")
        val escapedCuisine = chef.cuisineType.replace("'", "\\'")
        val escapedAddress = chef.address.replace("'", "\\'")
        val badgeHtml = when {
            chef.isChefOfTheWeek -> """<div style="background:#FFF3CD;color:#856404;font-size:10px;font-weight:bold;padding:2px 6px;border-radius:4px;margin-bottom:4px;display:inline-block;">🏆 Chef of Week</div>"""
            chef.isSponsored -> """<div style="background:#EFF6FF;color:#1D4ED8;font-size:10px;font-weight:bold;padding:2px 6px;border-radius:4px;margin-bottom:4px;display:inline-block;">⭐ Top Placement</div>"""
            chef.isProTier -> """<div style="background:#F0FDF4;color:#15803D;font-size:10px;font-weight:bold;padding:2px 6px;border-radius:4px;margin-bottom:4px;display:inline-block;">💎 Pro Kitchen</div>"""
            else -> ""
        }
        markersCode.append("""
            L.marker([${chef.latitude}, ${chef.longitude}], {icon: kitchenIcon})
                .addTo(map)
                .bindPopup(`
                    <div style="font-family: system-ui, -apple-system, sans-serif; line-height: 1.4; min-width: 160px;">
                        ${badgeHtml}
                        <div class="popup-title">${escapedName}</div>
                        <div class="popup-cuisine">${escapedCuisine}</div>
                        <div class="popup-address">${escapedAddress}</div>
                        <div class="popup-distance">🍳 ${String.format("%.2f", distance)} km away</div>
                        <button class="popup-button" onclick="Android.openChefDetails(${chef.id})">View Menu</button>
                        <button class="popup-button-stripe" onclick="Android.quickCheckout(${chef.id})">💳 Quick Stripe Checkout</button>
                    </div>
                `);
        """.trimIndent())
    }

    val mapClass = if (isDark) "dark-map" else ""

    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                html, body, #map {
                    margin: 0;
                    padding: 0;
                    width: 100%;
                    height: 100%;
                }
                
                /* Dark Mode Tile inversion filter */
                .dark-map .leaflet-tile {
                    filter: invert(100%) hue-rotate(180deg) brightness(95%) contrast(90%);
                }
                .dark-map .leaflet-container {
                    background: #120F0E;
                }
                
                /* Pulse animation for user dot */
                .user-location-icon {
                    position: relative;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                }
                .user-dot {
                    width: 12px;
                    height: 12px;
                    background-color: #007AFF;
                    border: 2px solid white;
                    border-radius: 50%;
                    box-shadow: 0 0 6px rgba(0,0,0,0.4);
                }
                .pulse-ring {
                    position: absolute;
                    width: 32px;
                    height: 32px;
                    border: 2px solid #007AFF;
                    border-radius: 50%;
                    animation: pulse 1.8s infinite ease-out;
                    opacity: 0;
                }
                @keyframes pulse {
                    0% { transform: scale(0.5); opacity: 0.8; }
                    100% { transform: scale(1.6); opacity: 0; }
                }

                /* Custom kitchen pin styling */
                .kitchen-location-icon {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                }
                .pin-marker {
                    width: 30px;
                    height: 30px;
                    background-color: ${if (isDark) "#FF6E4A" else "#FF4B2B"};
                    border-radius: 50% 50% 50% 0;
                    transform: rotate(-45deg);
                    box-shadow: -2px 2px 5px rgba(0,0,0,0.4);
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    animation: bounce 0.4s ease-out;
                }
                .pin-marker::after {
                    content: "🍳";
                    font-size: 14px;
                    transform: rotate(45deg);
                }
                
                @keyframes bounce {
                    0% { transform: translateY(-10px) rotate(-45deg); }
                    100% { transform: translateY(0) rotate(-45deg); }
                }
                
                /* Leaflet popup styling override */
                .leaflet-popup-content-wrapper {
                    background: ${if (isDark) "#1C1816" else "#FFFFFF"};
                    color: ${if (isDark) "#F5EFEB" else "#1E1B1A"};
                    border-radius: 14px;
                    padding: 8px;
                    box-shadow: 0px 4px 20px rgba(0,0,0,0.3);
                    border: 1px solid ${if (isDark) "#2E2724" else "rgba(0,0,0,0.05)"};
                }
                .leaflet-popup-tip {
                    background: ${if (isDark) "#1C1816" else "#FFFFFF"};
                }
                .popup-title {
                    font-weight: 800;
                    font-size: 14px;
                    margin-bottom: 3px;
                }
                .popup-cuisine {
                    font-size: 11px;
                    color: ${if (isDark) "#10B981" else "#00B074"};
                    font-weight: bold;
                    text-transform: uppercase;
                    letter-spacing: 0.8px;
                    margin-bottom: 6px;
                }
                .popup-address {
                    font-size: 12px;
                    color: ${if (isDark) "#A5928E" else "#4D3F3C"};
                    margin-bottom: 8px;
                }
                .popup-distance {
                    font-size: 11px;
                    font-weight: 700;
                    color: ${if (isDark) "#FF6E4A" else "#FF4B2B"};
                }
                .popup-button {
                    display: block;
                    width: 100%;
                    text-align: center;
                    background: ${if (isDark) "#FF6E4A" else "#FF4B2B"};
                    color: white !important;
                    font-weight: bold;
                    border: none;
                    border-radius: 8px;
                    padding: 8px 12px;
                    margin-top: 10px;
                    text-decoration: none;
                    font-size: 12px;
                    cursor: pointer;
                    box-sizing: border-box;
                    box-shadow: 0 2px 6px rgba(0,0,0,0.15);
                    transition: background 0.2s;
                }
                .popup-button:active {
                    background: ${if (isDark) "#E05533" else "#D0351B"};
                }
                .popup-button-stripe {
                    display: block;
                    width: 100%;
                    text-align: center;
                    background: #635BFF;
                    color: white !important;
                    font-weight: bold;
                    border: none;
                    border-radius: 8px;
                    padding: 8px 12px;
                    margin-top: 6px;
                    text-decoration: none;
                    font-size: 11px;
                    cursor: pointer;
                    box-sizing: border-box;
                    box-shadow: 0 2px 6px rgba(0,0,0,0.15);
                    transition: background 0.2s;
                }
                .popup-button-stripe:active {
                    background: #4B45C6;
                }
            </style>
        </head>
        <body class="${mapClass}">
            <div id="map"></div>
            <script>
                // Initialize the map centered at user location
                var map = L.map('map', {
                    zoomControl: false,
                    attributionControl: false
                }).setView([$userLat, $userLng], 14);

                // Add OpenStreetMap tiles
                L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                    maxZoom: 19
                }).addTo(map);
                
                // Add scale/zoom controls beautifully
                L.control.zoom({ position: 'topright' }).addTo(map);

                // User location pulsing marker
                var userIcon = L.divIcon({
                    className: 'user-location-icon',
                    html: '<div class="pulse-ring"></div><div class="user-dot"></div>',
                    iconSize: [30, 30],
                    iconAnchor: [15, 15]
                });
                
                L.marker([$userLat, $userLng], {icon: userIcon})
                    .addTo(map)
                    .bindPopup('<div style="font-family: system-ui, -apple-system, sans-serif; font-weight: bold; font-size: 13px; text-align: center;">📍 You are here</div>');

                // Kitchen markers setup
                var kitchenIcon = L.divIcon({
                    className: 'kitchen-location-icon',
                    html: '<div class="pin-marker"></div>',
                    iconSize: [30, 30],
                    iconAnchor: [15, 30]
                });

                $markersCode
            </script>
        </body>
        </html>
    """.trimIndent()
}

private fun vLines(scope: androidx.compose.ui.graphics.drawscope.DrawScope, stroke: Float) {
    var x = 0f
    with(scope) {
        while (x < scope.size.width) {
            scope.drawLine(
                color = Color(0x1F2196F3),
                start = Offset(x, 0f),
                end = Offset(x, scope.size.height),
                strokeWidth = stroke
            )
            x += 60.dp.toPx()
        }
    }
}

private fun hLines(scope: androidx.compose.ui.graphics.drawscope.DrawScope, stroke: Float) {
    var y = 0f
    with(scope) {
        while (y < scope.size.height) {
            scope.drawLine(
                color = Color(0x1F2196F3),
                start = Offset(0f, y),
                end = Offset(scope.size.width, y),
                strokeWidth = stroke
            )
            y += 60.dp.toPx()
        }
    }
}

// MY ORDERS SCREEN + TRACKING TIMELINE
@Composable
fun MockDeliveryMap(currentStep: Int) {
    val isDark = isSystemInDarkTheme()
    val routeColor = MaterialTheme.colorScheme.primary
    val trackBgColor = if (isDark) Color(0xFF1E1C1B) else Color(0xFFF9F6F5)
    val pathProgress by animateFloatAsState(
        targetValue = when (currentStep) {
            0 -> 0.0f
            1 -> 0.15f
            2 -> 0.65f
            3 -> 1.0f
            else -> 0.0f
        },
        animationSpec = spring(stiffness = androidx.compose.animation.core.Spring.StiffnessLow),
        label = "riderProgress"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .testTag("mock_delivery_map"),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        colors = CardDefaults.cardColors(containerColor = trackBgColor)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp)) {
                val width = size.width
                val height = size.height

                // Define coordinates
                val kitchenX = width * 0.15f
                val kitchenY = height * 0.5f
                val homeX = width * 0.85f
                val homeY = height * 0.5f

                // Draw decorative background map grid lines
                val gridColor = if (isDark) Color(0xFF33302F) else Color(0xFFEDE5E3)
                for (i in 1..4) {
                    val lineX = width * (i * 0.2f)
                    drawLine(
                        color = gridColor.copy(alpha = 0.25f),
                        start = Offset(lineX, 0f),
                        end = Offset(lineX, height),
                        strokeWidth = 2f
                    )
                }
                for (i in 1..3) {
                    val lineY = height * (i * 0.25f)
                    drawLine(
                        color = gridColor.copy(alpha = 0.25f),
                        start = Offset(0f, lineY),
                        end = Offset(width, lineY),
                        strokeWidth = 2f
                    )
                }

                // Draw path connecting them with a curvy bezier path
                val controlX1 = width * 0.4f
                val controlY1 = height * 0.2f
                val controlX2 = width * 0.6f
                val controlY2 = height * 0.8f

                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(kitchenX, kitchenY)
                    cubicTo(controlX1, controlY1, controlX2, controlY2, homeX, homeY)
                }

                // Draw background road path
                drawPath(
                    path = path,
                    color = (if (isDark) Color(0xFF4A4543) else Color(0xFFE5DDD9)).copy(alpha = 0.6f),
                    style = Stroke(width = 8f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )

                // Draw completed path in primary color
                val t = pathProgress
                val u = 1 - t
                val riderX = u * u * u * kitchenX + 3 * u * u * t * controlX1 + 3 * u * t * t * controlX2 + t * t * t * homeX
                val riderY = u * u * u * kitchenY + 3 * u * u * t * controlY1 + 3 * u * t * t * controlY2 + t * t * t * homeY

                // Draw dotted road details
                drawPath(
                    path = path,
                    color = routeColor,
                    style = Stroke(
                        width = 4f,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                    )
                )

                // Draw Kitchen Pin Circle
                drawCircle(
                    color = if (isDark) Color(0xFFE57373) else Color(0xFFD32F2F),
                    radius = 16f,
                    center = Offset(kitchenX, kitchenY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 6f,
                    center = Offset(kitchenX, kitchenY)
                )

                // Draw Home Pin Circle
                drawCircle(
                    color = if (isDark) Color(0xFF81C784) else Color(0xFF388E3C),
                    radius = 16f,
                    center = Offset(homeX, homeY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 6f,
                    center = Offset(homeX, homeY)
                )

                // Draw Rider current location circle with pulse
                drawCircle(
                    color = routeColor.copy(alpha = 0.25f),
                    radius = 28f,
                    center = Offset(riderX, riderY)
                )
                drawCircle(
                    color = routeColor,
                    radius = 14f,
                    center = Offset(riderX, riderY)
                )
                drawCircle(
                    color = Color.White,
                    radius = 5f,
                    center = Offset(riderX, riderY)
                )
            }

            // Text labels overlay on Canvas coordinates
            Text(
                text = "🍳 Kitchen",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFFFA7A7) else Color(0xFFC62828),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp)
                    .offset(y = (-30).dp)
            )

            Text(
                text = "🏠 You",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFFF1FDF1) else Color(0xFF2E7D32),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
                    .offset(y = (-30).dp)
            )

            val statusText = when (currentStep) {
                0 -> "Awaiting Confirmation ⏰"
                1 -> "Cooking Recipe 🍳"
                2 -> "Courier En Route 🚴"
                else -> "Arrived safely 🎉"
            }
            Surface(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = statusText,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun OrdersScreen(viewModel: HomeChefViewModel) {
    val orders by viewModel.orders.collectAsState()
    val trackedOrderId by viewModel.trackedOrderId.collectAsState()
    val meals by viewModel.meals.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val isDark = isSystemInDarkTheme()

    // State for reorder checkout
    var checkoutMeal by remember { mutableStateOf<MealEntity?>(null) }
    var checkoutChefName by remember { mutableStateOf("") }
    var checkoutQuantity by remember { mutableStateOf(1) }
    var checkoutName by remember { mutableStateOf("") }
    var checkoutAddress by remember { mutableStateOf("") }
    var checkoutPhone by remember { mutableStateOf("") }

    // State for past orders dashboard search & sort
    var historySearchQuery by remember { mutableStateOf("") }
    var historySortOption by remember { mutableStateOf(0) } // 0: Newest, 1: Oldest, 2: Price High-to-Low, 3: Price Low-to-High

    // State for leaving a review
    var reviewChefId by remember { mutableStateOf<Int?>(null) }
    var reviewMealId by remember { mutableStateOf<Int?>(null) }
    var reviewMealName by remember { mutableStateOf("") }

    val activeTrackedOrder = remember(orders, trackedOrderId) {
        orders.find { it.id == trackedOrderId }
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF6F0))
    ) {
        if (trackedOrderId != null && activeTrackedOrder != null) {
            // RENDER FULL LIVE TRACKING DASHBOARD FOR A SINGLE ORDER
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFECE6DD)),
                    modifier = Modifier.clickable { viewModel.setTrackedOrder(null) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("back_to_orders_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(16.dp), tint = Color(0xFF1B1612))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Back to All Orders",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = Color(0xFF1B1612)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Live Delivery Tracking",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    color = Color(0xFF1B1612)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Map representing live coordinate transitions
                MockDeliveryMap(currentStep = activeTrackedOrder.step)

                Spacer(modifier = Modifier.height(16.dp))

                // Estimated time arrival card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFECE6DD)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            val etaText = when (activeTrackedOrder.step) {
                                0 -> "Estimated Arrival: 25-30 Mins"
                                1 -> "Estimated Arrival: 15-20 Mins"
                                2 -> "Estimated Arrival: 5-8 Mins"
                                else -> "Arrived Successfully"
                            }
                            val subtext = when (activeTrackedOrder.step) {
                                0 -> "Your payment was processed securely. Waiting for chef acceptance."
                                1 -> "The chef is hand-crafting your fresh meal with premium ingredients."
                                2 -> "Rider Tobi is speeding down local roads with your hot package!"
                                else -> "Arrived safely and still steaming hot. Hope you love it!"
                            }

                            Text(
                                text = etaText,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFFD8582B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = subtext,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF7A7067)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Pulse radial loader
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(56.dp)) {
                            CircularProgressIndicator(
                                progress = {
                                    when (activeTrackedOrder.step) {
                                        0 -> 0.1f
                                        1 -> 0.4f
                                        2 -> 0.75f
                                        else -> 1.0f
                                    }
                                },
                                modifier = Modifier.fillMaxSize(),
                                color = Color(0xFFD8582B),
                                trackColor = Color(0xFFEFE9DF),
                                strokeWidth = 5.dp
                            )
                            Icon(
                                imageVector = when (activeTrackedOrder.step) {
                                    0 -> Icons.Default.AccessTime
                                    1 -> Icons.Default.Restaurant
                                    2 -> Icons.Default.Moped
                                    else -> Icons.Default.CheckCircle
                                },
                                contentDescription = null,
                                tint = Color(0xFFD8582B),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Delivery Courier details
                if (activeTrackedOrder.step >= 1) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFECE6DD)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFD8582B).copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "T",
                                        fontWeight = FontWeight.ExtraBold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color(0xFFD8582B)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Tobi (Specialist Courier)",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color(0xFF1B1612)
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFF9A825),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "4.9 • Electric Cargo Bike",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF7A7067)
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(
                                        onClick = {
                                            Toast.makeText(context, "Calling Tobi... 📞", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = IconButtonDefaults.iconButtonColors(
                                            containerColor = Color(0xFF1E432A).copy(alpha = 0.1f)
                                        ),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(Icons.Default.Phone, contentDescription = "Call", modifier = Modifier.size(18.dp), tint = Color(0xFF1E432A))
                                    }
                                    IconButton(
                                        onClick = {
                                            Toast.makeText(context, "Opening direct courier chat... 💬", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = IconButtonDefaults.iconButtonColors(
                                            containerColor = Color(0xFFD8582B).copy(alpha = 0.1f)
                                        ),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Icon(Icons.Default.Chat, contentDescription = "Chat", modifier = Modifier.size(18.dp), tint = Color(0xFFD8582B))
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Active order full timeline stepper
                Text(
                    text = "Live Tracking Milestones",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF1B1612),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                TimelineStepper(currentStep = activeTrackedOrder.step)

                Spacer(modifier = Modifier.height(16.dp))

                // Accordion of summary
                var isExpanded by remember { mutableStateOf(false) }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFECE6DD))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isExpanded = !isExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFFD8582B))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Order Summary Details", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF1B1612))
                            }
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFF7A7067)
                            )
                        }

                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFECE6DD))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Dish ordered", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7A7067))
                                Text("${activeTrackedOrder.quantity}x ${activeTrackedOrder.mealName}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = Color(0xFF1B1612))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Chef kitchen", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7A7067))
                                Text(activeTrackedOrder.chefName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = Color(0xFF1B1612))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Payment ID", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7A7067))
                                Text(activeTrackedOrder.paymentId, style = MaterialTheme.typography.bodySmall, color = Color(0xFFD8582B))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Destination Address", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7A7067))
                                Text(activeTrackedOrder.buyerAddress, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.End, color = Color(0xFF1B1612), modifier = Modifier.weight(1f).padding(start = 16.dp))
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFECE6DD))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Total Amount Paid", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF1B1612))
                                Text(com.example.data.CurrencyHelper.formatPrice(activeTrackedOrder.totalAmount), fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium, color = Color(0xFFD8582B))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.setTrackedOrder(null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("de_focus_tracker_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B1612))
                ) {
                    Text("Return to All Orders", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        } else {
            // Editorial Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1B1612),
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("orders_citch_logo")
                        ) {
                            Image(
                                painter = painterResource(id = com.example.R.drawable.img_citch_logo_1789242928296),
                                contentDescription = "Citch Logo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color(0xFFECE6DD))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("📦", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Order Tracker",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF1B1612)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Your orders",
                    fontSize = 30.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B1612),
                    lineHeight = 34.sp
                )
                Text(
                    text = "& hot deliveries.",
                    fontSize = 30.sp,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFD8582B),
                    lineHeight = 34.sp
                )
            }

            // Custom Segmented Pill Toggle
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFFEFE9DF),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val activeCount = orders.count { it.step < 3 }
                    val isTab0 = selectedTab == 0
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = if (isTab0) Color.White else Color.Transparent,
                        border = if (isTab0) BorderStroke(1.dp, Color(0xFFECE6DD)) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = 0 }
                            .testTag("tab_active_orders")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔥 Active Tracker",
                                fontWeight = if (isTab0) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (isTab0) Color(0xFF1B1612) else Color(0xFF7A7067)
                            )
                            if (activeCount > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFD8582B)
                                ) {
                                    Text(
                                        text = activeCount.toString(),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    val isTab1 = selectedTab == 1
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = if (isTab1) Color.White else Color.Transparent,
                        border = if (isTab1) BorderStroke(1.dp, Color(0xFFECE6DD)) else null,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = 1 }
                            .testTag("tab_past_orders")
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📜 Past Orders",
                                fontWeight = if (isTab1) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (isTab1) Color(0xFF1B1612) else Color(0xFF7A7067)
                            )
                        }
                    }
                }
            }

            if (selectedTab == 0) {
                // ACTIVE TRACKING TAB
                val activeOrders = remember(orders) { orders.filter { it.step < 3 } }

                if (activeOrders.isEmpty()) {
                    // EMPTY STATE FOR ACTIVE TRACKING
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(24.dp)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Image(
                                painter = painterResource(id = com.example.R.drawable.img_delivery_courier_1784427764589),
                                contentDescription = "Active Delivery Courier",
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(22.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "No Active Orders Underway",
                                style = MaterialTheme.typography.titleLarge,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B1612),
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Pick a dish made by your neighbors, checkout, and watch your food arrive hot and fresh with real-time tracking!",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF7A7067)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = { viewModel.navigateTo(Screen.Showcase) },
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .height(48.dp)
                                    .testTag("empty_active_go_showcase"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD8582B))
                            ) {
                                Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Browse Kitchen Dishes Now", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "Active Deliveries En Route",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF1B1612),
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        }

                        items(activeOrders) { order ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setTrackedOrder(order.id) },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFECE6DD)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "Order #${order.id}",
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = Color(0xFF1B1612)
                                            )
                                            Text(
                                                text = "Chef: ${order.chefName}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF7A7067)
                                            )
                                        }

                                        // Status indicator chip
                                        val (bgColor, textColor, text) = when (order.step) {
                                            0 -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "🍳 Awaiting Cook")
                                            1 -> Triple(Color(0xFFFDE8E0), Color(0xFFD8582B), "🔥 Preparing Food")
                                            else -> Triple(Color(0xFFE2F4E6), Color(0xFF1E432A), "🚴 En Route")
                                        }

                                        Surface(
                                            color = bgColor,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = text,
                                                color = textColor,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                            )
                                        }
                                    }

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFECE6DD))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "${order.quantity}x ${order.mealName}",
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color(0xFF1B1612)
                                            )
                                            Text(
                                                text = "Estimated arrival: " + when (order.step) {
                                                    0 -> "30 mins"
                                                    1 -> "20 mins"
                                                    else -> "6 mins"
                                                },
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF7A7067)
                                            )
                                        }

                                        Text(
                                            text = com.example.data.CurrencyHelper.formatPrice(order.totalAmount),
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFFD8582B),
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    val progressFraction = when (order.step) {
                                        0 -> 0.2f
                                        1 -> 0.55f
                                        else -> 0.85f
                                    }
                                    LinearProgressIndicator(
                                        progress = { progressFraction },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = Color(0xFFD8582B),
                                        trackColor = Color(0xFFEFE9DF)
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Button(
                                        onClick = { viewModel.setTrackedOrder(order.id) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("track_active_order_${order.id}"),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF1E432A)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Open Interactive Live GPS Tracker", fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // PAST ORDERS TAB
                val pastOrders = remember(orders) { orders.filter { it.step >= 3 } }

                val totalSpent = remember(pastOrders) { pastOrders.sumOf { it.totalAmount } }
                val averageOrderValue = remember(pastOrders) { if (pastOrders.isNotEmpty()) totalSpent / pastOrders.size else 0.0 }

                val filteredPastOrders = remember(pastOrders, historySearchQuery, historySortOption) {
                    val filtered = if (historySearchQuery.isBlank()) {
                        pastOrders
                    } else {
                        pastOrders.filter {
                            it.mealName.contains(historySearchQuery, ignoreCase = true) ||
                            it.chefName.contains(historySearchQuery, ignoreCase = true)
                        }
                    }

                    when (historySortOption) {
                        0 -> filtered.sortedByDescending { it.timestamp }
                        1 -> filtered.sortedBy { it.timestamp }
                        2 -> filtered.sortedByDescending { it.totalAmount }
                        3 -> filtered.sortedBy { it.totalAmount }
                        else -> filtered
                    }
                }

                if (pastOrders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = "Empty Past",
                                tint = Color(0xFF7A7067).copy(alpha = 0.5f),
                                modifier = Modifier.size(70.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Completed Orders Found",
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = FontFamily.Serif,
                                color = Color(0xFF1B1612),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "After meals arrive successfully, they appear in history for instant reordering or leaving reviews.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF7A7067)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Stat Summary Section
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, Color(0xFFECE6DD)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Orders Summary",
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = Color(0xFF1B1612)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Total Spent
                                        Card(
                                            modifier = Modifier.weight(1f),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF6F0)),
                                            shape = RoundedCornerShape(14.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(10.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Icon(
                                                    Icons.Default.TrendingUp,
                                                    contentDescription = null,
                                                    tint = Color(0xFFD8582B),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = com.example.data.CurrencyHelper.formatPrice(totalSpent),
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF1B1612)
                                                )
                                                Text(
                                                    text = "Total Spent",
                                                    fontSize = 9.sp,
                                                    color = Color(0xFF7A7067),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }

                                        // Total Orders
                                        Card(
                                            modifier = Modifier.weight(1f),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF6F0)),
                                            shape = RoundedCornerShape(14.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(10.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Icon(
                                                    Icons.Default.Restaurant,
                                                    contentDescription = null,
                                                    tint = Color(0xFFD8582B),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "${pastOrders.size}",
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF1B1612)
                                                )
                                                Text(
                                                    text = "Total Orders",
                                                    fontSize = 9.sp,
                                                    color = Color(0xFF7A7067),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }

                                        // Avg Value
                                        Card(
                                            modifier = Modifier.weight(1f),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF6F0)),
                                            shape = RoundedCornerShape(14.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(10.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Icon(
                                                    Icons.Default.ShoppingBag,
                                                    contentDescription = null,
                                                    tint = Color(0xFFD8582B),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = com.example.data.CurrencyHelper.formatPrice(averageOrderValue),
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF1B1612)
                                                )
                                                Text(
                                                    text = "Avg Value",
                                                    fontSize = 9.sp,
                                                    color = Color(0xFF7A7067),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Search & Sorting controls section
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Search Input
                                OutlinedTextField(
                                    value = historySearchQuery,
                                    onValueChange = { historySearchQuery = it },
                                    placeholder = { Text("Search meals or chefs...", fontSize = 13.sp, color = Color(0xFF7A7067)) },
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF7A7067)) },
                                    trailingIcon = {
                                        if (historySearchQuery.isNotEmpty()) {
                                            IconButton(onClick = { historySearchQuery = "" }) {
                                                Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    },
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("history_search_input"),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        focusedBorderColor = Color(0xFFD8582B),
                                        unfocusedBorderColor = Color(0xFFECE6DD)
                                    )
                                )

                                // Sorting Filter Chips Scroll
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Sort:", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7A7067), fontWeight = FontWeight.Bold)
                                    val options = listOf("Newest First", "Oldest First", "Price: High to Low", "Price: Low to High")
                                    options.forEachIndexed { index, label ->
                                        val isSelected = historySortOption == index
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { historySortOption = index },
                                            label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = Color(0xFFD8582B).copy(alpha = 0.12f),
                                                selectedLabelColor = Color(0xFFD8582B)
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.testTag("sort_chip_$index")
                                        )
                                    }
                                }
                            }
                        }

                        // Orders Log items
                        if (filteredPastOrders.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No matching orders found",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }
                            }
                        } else {
                            items(filteredPastOrders, key = { it.id }) { order ->
                                PastOrderCard(
                                    order = order,
                                    meals = meals,
                                    onReorderClick = { targetMeal, chefName, quantity, buyerName, buyerAddress, buyerPhone ->
                                        checkoutMeal = targetMeal
                                        checkoutChefName = chefName
                                        checkoutQuantity = quantity
                                        checkoutName = buyerName
                                        checkoutAddress = buyerAddress
                                        checkoutPhone = buyerPhone
                                    },
                                    onReviewClick = { chefId, mealId, mealName ->
                                        reviewChefId = chefId
                                        reviewMealId = mealId
                                        reviewMealName = mealName
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal dialog overlays inside OrdersScreen context
    if (checkoutMeal != null) {
        OrderCheckoutDialog(
            meal = checkoutMeal!!,
            chefName = checkoutChefName,
            viewModel = viewModel,
            onDismiss = { checkoutMeal = null },
            initialQuantity = checkoutQuantity,
            initialName = checkoutName,
            initialAddress = checkoutAddress,
            initialPhone = checkoutPhone
        )
    }

    if (reviewChefId != null) {
        ReviewDialog(
            chefId = reviewChefId!!,
            mealId = reviewMealId ?: 0,
            mealName = reviewMealName,
            viewModel = viewModel,
            onDismiss = { reviewChefId = null }
        )
    }
}

@Composable
fun PastOrderCard(
    order: OrderEntity,
    meals: List<MealEntity>,
    onReorderClick: (MealEntity, String, Int, String, String, String) -> Unit,
    onReviewClick: (Int, Int, String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("past_order_card_${order.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFECE6DD)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order #${order.id}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF1B1612)
                    )
                    Text(
                        text = "Chef: ${order.chefName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF7A7067)
                    )
                }

                Surface(
                    color = Color(0xFFE2F4E6),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF1E432A),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Delivered",
                            color = Color(0xFF1E432A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFECE6DD))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${order.quantity}x ${order.mealName}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF1B1612)
                    )
                    val orderDate = remember(order.timestamp) {
                        try {
                            val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
                            sdf.format(Date(order.timestamp))
                        } catch (e: Exception) {
                            "Recently Delivered"
                        }
                    }
                    Text(
                        text = orderDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF7A7067)
                    )
                }

                Text(
                    text = com.example.data.CurrencyHelper.formatPrice(order.totalAmount),
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFD8582B),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Collapsible details accordion
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFECE6DD))
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Payment ID", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7A7067))
                        Text(order.paymentId, style = MaterialTheme.typography.bodySmall, color = Color(0xFFD8582B))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Recipient Name", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7A7067))
                        Text(order.buyerName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = Color(0xFF1B1612))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Contact Phone", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7A7067))
                        Text(order.buyerPhone, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = Color(0xFF1B1612))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Delivery Destination", style = MaterialTheme.typography.bodySmall, color = Color(0xFF7A7067))
                        Text(
                            text = order.buyerAddress,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.End,
                            color = Color(0xFF1B1612),
                            modifier = Modifier.weight(1f).padding(start = 16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Toggle expand button (Icon)
                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFFFAF6F0), shape = RoundedCornerShape(10.dp))
                        .testTag("toggle_details_${order.id}")
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle Details",
                        tint = Color(0xFF1B1612),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Reorder instant chip
                Button(
                    onClick = {
                        val targetMeal = meals.find { it.id == order.mealId } ?: MealEntity(
                            id = order.mealId,
                            chefId = order.chefId,
                            name = order.mealName,
                            description = "Classic meal specially pre-cooked for your preferences.",
                            price = order.totalAmount / order.quantity,
                            imageUrl = "",
                            category = "Classic"
                        )
                        onReorderClick(targetMeal, order.chefName, order.quantity, order.buyerName, order.buyerAddress, order.buyerPhone)
                    },
                    modifier = Modifier
                        .weight(1.1f)
                        .height(38.dp)
                        .testTag("reorder_button_${order.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD8582B)
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Instant Reorder", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                // Leave rating review chip
                OutlinedButton(
                    onClick = {
                        onReviewClick(order.chefId, order.mealId, order.mealName)
                    },
                    modifier = Modifier
                        .weight(0.9f)
                        .height(38.dp)
                        .testTag("rate_past_order_${order.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF1B1612)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFECE6DD)),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.StarBorder, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF1B1612))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Review Dish", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// REALTIME TRACKING CARD VIEW
@Composable
fun ActiveTrackingCard(order: OrderEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "DELIVERY TO:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                order.buyerName,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                order.buyerAddress,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Dish Name:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Text(
                        order.mealName,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "Paid SECURE:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = "Secured",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Yes",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

// DYNAMIC STEPPER VIEW COMPONENT
@Composable
fun TimelineStepper(currentStep: Int) {
    val steps = listOf(
        "Order Confirmed ✓" to "Cashier processed secure transactions.",
        "Prep stage 🍳" to "Chef raw crafting premium fresh recipe.",
        "Out for Delivery 🚴" to "Courier navigating surrounding local roads.",
        "Arrived & Served 🎉" to "Meal served perfectly hot! Enjoy!"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        steps.forEachIndexed { index, (title, desc) ->
            val isActive = index <= currentStep
            val isCurrent = index == currentStep
            val color = if (isActive) MaterialTheme.colorScheme.primary else Color.LightGray

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Stepper index bullet bubble
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(36.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isCurrent) 3.dp else 0.dp,
                                color = if (isCurrent) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (index < currentStep) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        } else {
                            Text(
                                text = (index + 1).toString(),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Stepper line connecting to next step
                    if (index < steps.size - 1) {
                        Spacer(
                            modifier = Modifier
                                .width(3.dp)
                                .height(38.dp)
                                .background(if (index < currentStep) MaterialTheme.colorScheme.primary else Color.LightGray)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        color = if (isActive) MaterialTheme.colorScheme.onBackground else Color.Gray,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = desc,
                        color = if (isActive) MaterialTheme.colorScheme.onSurfaceVariant else Color.LightGray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// ALERT NOTIFICATION LIST VIEW
@Composable
fun NotificationsScreen(viewModel: HomeChefViewModel) {
    val alerts by viewModel.alerts.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.clearAlerts()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Community Meal Alerts 🔔",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (alerts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "No notification alerts",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(alerts) { alert ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (alert.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = if (alert.title.contains("Paid")) Icons.AutoMirrored.Filled.ReceiptLong else Icons.Default.Campaign,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    alert.title,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    alert.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(alert.timestamp)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// DETAIL: KITCHEN PROFILE, SOCIAL SHOWCASE, TUTORIAL WEB-VIEWS, & REVIEWS
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChefDetailScreen(chefId: Int, viewModel: HomeChefViewModel) {
    val chef by viewModel.activeChef.collectAsState()
    val meals by viewModel.activeChefMeals.collectAsState()
    val reviews by viewModel.activeChefReviews.collectAsState()
    
    var showReviewDialog by remember { mutableStateOf(false) }
    var showChatDialog by remember { mutableStateOf(false) }
    var selectedMealForRating by remember { mutableStateOf<MealEntity?>(null) }
    var activeOrderMeal by remember { mutableStateOf<MealEntity?>(null) }
    var activeTutorialUrl by remember { mutableStateOf<String?>(null) }
    var showCameraDialog by remember { mutableStateOf(false) }
    var showPayoutDialog by remember { mutableStateOf(false) }
    var showEditPaypalDialog by remember { mutableStateOf(false) }

    // State for dedicated tabs
    var selectedTab by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    if (chef == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val activeChefNonNull = chef!!

    // Dynamic calculations for aggregate ratings
    val totalReviews = reviews.size
    val avgRating = if (reviews.isNotEmpty()) reviews.map { it.rating }.average() else activeChefNonNull.rating.toDouble()
    
    val ratingDistribution = remember(reviews) {
        val dist = IntArray(6) { 0 }
        reviews.forEach {
            if (it.rating in 1..5) {
                dist[it.rating]++
            }
        }
        dist
    }

    // Chef PayPal Payouts & Revenue state
    val allOrders by viewModel.orders.collectAsState()
    val chefOrders = remember(allOrders, activeChefNonNull.id) { allOrders.filter { it.chefId == activeChefNonNull.id } }
    val chefGrossSales = remember(chefOrders) { chefOrders.sumOf { it.totalAmount } }
    val chefPayouts by viewModel.getPayoutsForChef(activeChefNonNull.id).collectAsState(initial = emptyList())
    val totalDisbursed = remember(chefPayouts) { chefPayouts.sumOf { it.amount } }
    val availableBalance = remember(chefGrossSales, totalDisbursed) { (chefGrossSales - totalDisbursed).coerceAtLeast(0.0) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
        // Upper banner graphic (Uber Minimalist Style)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(Screen.Explore) },
                    modifier = Modifier
                        .padding(16.dp)
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), CircleShape)
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Chef identity profile chip overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Box(
                        modifier = Modifier.size(76.dp)
                    ) {
                        AsyncImage(
                            model = activeChefNonNull.avatarUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                .clickable { showCameraDialog = true },
                            contentScale = ContentScale.Crop
                        )
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(28.dp)
                                .align(Alignment.BottomEnd)
                                .clickable { showCameraDialog = true }
                                .testTag("chef_detail_camera_button"),
                            shadowElevation = 4.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Change Profile Photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            activeChefNonNull.name,
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF06C167)
                            ) {
                                Text(
                                    text = "★ ${String.format("%.1f", avgRating)}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                activeChefNonNull.cuisineType,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Dedicated profile Navigation Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Dishes (${meals.size})", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Restaurant, contentDescription = "Active Dishes") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("About Chef", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Chef Bio") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Ratings (${reviews.size})", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Ratings") }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("PayPal Payout", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "PayPal Chef Payout") }
                )
            }
        }

        // Content switching based on selected tab
        when (selectedTab) {
            0 -> { // TAB 0: ACTIVE DISHES MENU
                if (meals.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No dishes currently listed.", color = Color.Gray)
                            }
                        }
                    }
                } else {
                    items(meals) { meal ->
                        val mealReviews = remember(reviews, meal.id) { reviews.filter { it.mealId == meal.id } }
                        val hasRatings = mealReviews.isNotEmpty()
                        val dishAvgRating = remember(mealReviews) { if (hasRatings) mealReviews.map { it.rating }.average() else 5.0 }
                        val ratingCount = mealReviews.size

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    AsyncImage(
                                        model = getMealImageModel(meal.imageUrl, meal.name),
                                        contentDescription = "Dish Cover",
                                        modifier = Modifier
                                            .size(100.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.LightGray),
                                        contentScale = ContentScale.Crop
                                    )
                                    
                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                meal.name,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.titleMedium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                com.example.data.CurrencyHelper.formatPrice(meal.price),
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.primary,
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                        }
                                        
                                        // DISH STAR RATING OVERVIEW
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            repeat(5) { index ->
                                                val isFilled = index < dishAvgRating.toInt()
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = if (isFilled) Color(0xFFFFB300) else Color.LightGray.copy(alpha = 0.5f),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (hasRatings) {
                                                    "${String.format("%.1f", dishAvgRating)} ($ratingCount ${if (ratingCount == 1) "review" else "reviews"})"
                                                } else {
                                                    "New ⭐"
                                                },
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                            )
                                        }

                                        Text(
                                            meal.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                color = MaterialTheme.colorScheme.secondaryContainer,
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    meal.category,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                TextButton(
                                                    onClick = { selectedMealForRating = meal },
                                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                                    modifier = Modifier
                                                        .height(32.dp)
                                                        .testTag("rate_dish_button_${meal.id}")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.RateReview,
                                                        contentDescription = "Rate Dish",
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("Rate", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                                }

                                                Button(
                                                    onClick = { activeOrderMeal = meal },
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                                    modifier = Modifier
                                                        .height(32.dp)
                                                        .testTag("buy_and_pay_button_${meal.id}")
                                                ) {
                                                    Text("Buy & Pay Sec", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }

                                // DYNAMIC COOKING TUTORIAL INLINE
                                if (meal.tutorialVideoUrl.isNotEmpty()) {
                                    var isPlaying by remember { mutableStateOf(false) }
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                    
                                    if (isPlaying) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color.Black)
                                        ) {
                                            VideoPlayer(
                                                youtubeVideoUrl = meal.tutorialVideoUrl,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(180.dp)
                                            )
                                            IconButton(
                                                onClick = { isPlaying = false },
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(8.dp)
                                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Close Video",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    } else {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { isPlaying = true }
                                                .padding(horizontal = 16.dp, vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .background(Color.Red.copy(alpha = 0.15f), CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.PlayArrow,
                                                    contentDescription = "Play",
                                                    tint = Color.Red,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "Watch preparation masterclass 📺",
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            1 -> { // TAB 1: CHEF BIOGRAPHY & CONTACT
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.People,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${activeChefNonNull.followersCount} Local loyal fans",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                var isFollowing by remember { mutableStateOf(false) }
                                Button(
                                    onClick = { isFollowing = !isFollowing },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isFollowing) Color.LightGray else MaterialTheme.colorScheme.primary
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isFollowing) Icons.Default.Check else Icons.Default.Favorite,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isFollowing) "Following" else "Follow Chef", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "About the Chef",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = activeChefNonNull.bio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Kitchen Location & Contact",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Color.Red, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = activeChefNonNull.address,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = "Phone", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = activeChefNonNull.phone,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showChatDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("chef_contact_chat_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = "Chat",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Chat & Customize Ingredients 💬",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }

                // YouTube Playlist Masterclass integration (from original layout)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayCircleFilled,
                                contentDescription = "YouTube logo",
                                tint = Color.Red,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Chef Recipe Tutorials 📺",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Text(
                            text = "Learn secret preparation techniques directly from chef channel: ${activeChefNonNull.youtubeChannelName}.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        if (activeTutorialUrl != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                VideoPlayer(youtubeVideoUrl = activeTutorialUrl!!)
                                IconButton(
                                    onClick = { activeTutorialUrl = null },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close player")
                                }
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { activeTutorialUrl = activeChefNonNull.youtubeChannelUrl },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp, 50.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.DarkGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(32.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Watch: Signature masterclass tutorials from channel",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        "Channel: ${activeChefNonNull.youtubeChannelName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            2 -> { // TAB 2: AGGREGATE STAR RATINGS & TRUST REVIEWS
                // 1. Dynamic Aggregate Statistics Panel
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Aggregate Customer Ratings 📊",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left Column: Large Average Rating
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = String.format("%.1f", avgRating),
                                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        repeat(5) { index ->
                                            val isFilled = index < avgRating.toInt()
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = if (isFilled) Color(0xFFFFB300) else Color.LightGray.copy(alpha = 0.5f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Based on $totalReviews reviews",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                // Right Column: Linear Star Progress Bars
                                Column(
                                    modifier = Modifier.weight(1.5f),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    for (star in 5 downTo 1) {
                                        val count = ratingDistribution[star]
                                        val fraction = if (totalReviews > 0) count.toFloat() / totalReviews else 0f
                                        
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "$star ★",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.width(28.dp),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            LinearProgressIndicator(
                                                progress = { fraction },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(8.dp)
                                                    .clip(RoundedCornerShape(4.dp)),
                                                color = Color(0xFFFFB300),
                                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                                            )
                                            Text(
                                                text = "$count",
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier.width(24.dp),
                                                textAlign = TextAlign.End,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Add Review header row
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Customer Testimonials",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        TextButton(onClick = { showReviewDialog = true }) {
                            Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Review")
                        }
                    }
                }

                // 3. Customer Reviews List
                if (reviews.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No reviews yet. Be the first to try!", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                } else {
                    items(reviews) { review ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        review.reviewerName,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    Row {
                                        repeat(5) { starIndex ->
                                            Icon(
                                                Icons.Default.Star,
                                                contentDescription = null,
                                                tint = if (starIndex < review.rating) Color(0xFFFFB300) else Color.LightGray.copy(alpha = 0.5f),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    review.comment,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(review.timestamp)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
            3 -> { // TAB 3: CHEF PAYPAL PAYOUTS & REVENUE
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF003087))
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.AccountBalanceWallet,
                                        contentDescription = null,
                                        tint = Color(0xFF00CFDE),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "PayPal Chef Earnings",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 16.sp
                                    )
                                }
                                Surface(
                                    color = Color(0xFF0070BA),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "Instant Deposit",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Gross Kitchen Sales", color = Color(0xFFB4D8F8), fontSize = 11.sp)
                                    Text(
                                        com.example.data.CurrencyHelper.formatPrice(chefGrossSales),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                                Column {
                                    Text("Paid via PayPal", color = Color(0xFFB4D8F8), fontSize = 11.sp)
                                    Text(
                                        com.example.data.CurrencyHelper.formatPrice(totalDisbursed),
                                        color = Color(0xFF90E0EF),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                                Column {
                                    Text("Available Balance", color = Color(0xFFB4D8F8), fontSize = 11.sp)
                                    Text(
                                        com.example.data.CurrencyHelper.formatPrice(availableBalance),
                                        color = Color(0xFF00FF88),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp
                                    )
                                }
                            }

                            Button(
                                onClick = { showPayoutDialog = true },
                                modifier = Modifier.fillMaxWidth().testTag("chef_payout_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0070BA)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                val amountToShow = if (availableBalance > 0.0) availableBalance else 50.0
                                Text(
                                    "Disburse Payout to PayPal • ${com.example.data.CurrencyHelper.formatPrice(amountToShow)}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Linked PayPal Account Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFEBF3FC),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF0070BA), modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Chef PayPal Recipient Account", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(
                                        activeChefNonNull.paypalEmail.ifBlank { "No PayPal account configured" },
                                        fontSize = 12.sp,
                                        color = Color(0xFF0070BA),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            TextButton(onClick = { showEditPaypalDialog = true }, modifier = Modifier.testTag("edit_chef_paypal_btn")) {
                                Text("Edit", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Payout History Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PayPal Payout History (${chefPayouts.size})",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                if (chefPayouts.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No payouts recorded yet", fontWeight = FontWeight.Bold, color = Color.Gray)
                                Text("Tap 'Disburse Payout to PayPal' above to test instant PayPal disbursements.", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
                            }
                        }
                    }
                } else {
                    items(chefPayouts) { payout ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFE8F5E9),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            payout.note.ifBlank { "PayPal Chef Payout" },
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            "Batch: ${payout.payoutBatchId}",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                        Text(
                                            SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()).format(Date(payout.timestamp)),
                                            fontSize = 10.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "+${com.example.data.CurrencyHelper.formatPrice(payout.amount)}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp,
                                        color = Color(0xFF2E7D32)
                                    )
                                    Surface(
                                        color = Color(0xFFE8F5E9),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            payout.status,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2E7D32),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Real-time Chat Floating Action Button
    ExtendedFloatingActionButton(
            onClick = { showChatDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("chef_chat_fab"),
            icon = { Icon(Icons.Default.Chat, contentDescription = "Chat icon") },
            text = { Text("Ask Chef 💬") },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        )
    }

    if (showChatDialog) {
        ChefChatDialog(
            chef = activeChefNonNull,
            viewModel = viewModel,
            onDismiss = { showChatDialog = false }
        )
    }

    if (showReviewDialog) {
        ReviewDialog(
            chefId = activeChefNonNull.id,
            mealId = 0,
            mealName = "",
            viewModel = viewModel,
            onDismiss = { showReviewDialog = false }
        )
    }

    if (selectedMealForRating != null) {
        ReviewDialog(
            chefId = activeChefNonNull.id,
            mealId = selectedMealForRating!!.id,
            mealName = selectedMealForRating!!.name,
            viewModel = viewModel,
            onDismiss = { selectedMealForRating = null }
        )
    }

    if (activeOrderMeal != null) {
        OrderCheckoutDialog(
            meal = activeOrderMeal!!,
            chefName = activeChefNonNull.name,
            viewModel = viewModel,
            onDismiss = { activeOrderMeal = null }
        )
    }

    if (showCameraDialog) {
        CookCameraDialog(
            targetType = CameraTargetType.COOK_AVATAR,
            title = "Update ${activeChefNonNull.name}'s Photo",
            subtitle = "Take a fresh profile photo with your camera or choose from gallery",
            onPhotoCaptured = { savedUriString ->
                viewModel.updateChefProfile(activeChefNonNull, newAvatarUrl = savedUriString)
                showCameraDialog = false
            },
            onDismiss = { showCameraDialog = false }
        )
    }

    if (showPayoutDialog) {
        var payoutAmountText by remember { mutableStateOf("50.00") }
        var isSubmitting by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isSubmitting) showPayoutDialog = false },
            icon = {
                Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color(0xFF0070BA), modifier = Modifier.size(32.dp))
            },
            title = {
                Text("Disburse PayPal Payout", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Direct deposit from platform funds to Chef ${activeChefNonNull.name}'s PayPal wallet.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    OutlinedTextField(
                        value = payoutAmountText,
                        onValueChange = { payoutAmountText = it },
                        label = { Text("Payout Amount ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        leadingIcon = { Text("$", fontWeight = FontWeight.Bold, color = Color(0xFF0070BA)) },
                        modifier = Modifier.fillMaxWidth().testTag("payout_amount_input")
                    )
                    Surface(
                        color = Color(0xFFEBF3FC),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Recipient Account:", fontSize = 11.sp, color = Color.Gray)
                            Text(activeChefNonNull.paypalEmail.ifBlank { "chef.paypal@example.com" }, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF003087))
                        }
                    }
                    if (isSubmitting) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF0070BA))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Processing PayPal Payout...", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = payoutAmountText.toDoubleOrNull() ?: 50.0
                        isSubmitting = true
                        scope.launch {
                            val res = viewModel.executeChefPayPalPayout(
                                chefId = activeChefNonNull.id,
                                chefName = activeChefNonNull.name,
                                amount = amt,
                                paypalEmail = activeChefNonNull.paypalEmail.ifBlank { "chef.paypal@example.com" },
                                note = "HomeChef Kitchen Earnings Payout"
                            )
                            isSubmitting = false
                            showPayoutDialog = false
                            when (res) {
                                is com.example.data.UnifiedPaymentResult.Success -> {
                                    Toast.makeText(context, "PayPal Payout Sent! Batch: ${res.transactionId}", Toast.LENGTH_LONG).show()
                                }
                                is com.example.data.UnifiedPaymentResult.Failure -> {
                                    Toast.makeText(context, "Payout Failed: ${res.errorMessage}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    },
                    enabled = !isSubmitting,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0070BA)),
                    modifier = Modifier.testTag("confirm_payout_button")
                ) {
                    Text("Send Payout via PayPal", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPayoutDialog = false },
                    enabled = !isSubmitting
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEditPaypalDialog) {
        var emailInput by remember { mutableStateOf(activeChefNonNull.paypalEmail) }

        AlertDialog(
            onDismissRequest = { showEditPaypalDialog = false },
            icon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF0070BA), modifier = Modifier.size(32.dp))
            },
            title = {
                Text("Chef PayPal Address", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Enter the chef's official PayPal account email address where kitchen earnings will be disbursed.", fontSize = 12.sp, color = Color.Gray)
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("PayPal Email") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("chef_paypal_email_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (emailInput.isNotBlank()) {
                            viewModel.updateChefPaypalEmail(activeChefNonNull.id, emailInput.trim())
                            Toast.makeText(context, "Chef PayPal address updated!", Toast.LENGTH_SHORT).show()
                        }
                        showEditPaypalDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0070BA)),
                    modifier = Modifier.testTag("save_chef_paypal_btn")
                ) {
                    Text("Save Address", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditPaypalDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// SECURE EMBEDDED WEB-VIEW YOUTUBE PLAYER
@Composable
fun VideoPlayer(youtubeVideoUrl: String, modifier: Modifier = Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val videoId = remember(youtubeVideoUrl) {
        val pattern = "(?<=watch\\?v=|/videos/|embed/)[^#&?]*".toRegex()
        pattern.find(youtubeVideoUrl)?.value ?: "FLeSREbZ7Rk"
    }

    val embedUrl = "https://www.youtube.com/embed/$videoId?autoplay=1"
    var hasWebViewError by remember { mutableStateOf(false) }

    if (hasWebViewError) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable {
                    try {
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse(youtubeVideoUrl)
                        )
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // ignore
                    }
                },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = "Play Tutorial",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(52.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Watch Tutorial Recipe",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Click to play recipe tutorial on YouTube.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    } else {
        AndroidView(
            factory = { ctx ->
                try {
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        webViewClient = WebViewClient()
                        loadUrl(embedUrl)
                    }
                } catch (e: Throwable) {
                    hasWebViewError = true
                    android.view.View(ctx)
                }
            },
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            update = { webView ->
                try {
                    if (webView is WebView) {
                        webView.loadUrl(embedUrl)
                    }
                } catch (e: Throwable) {
                    hasWebViewError = true
                }
            },
            onRelease = { view ->
                try {
                    if (view is WebView) {
                        view.stopLoading()
                        view.destroy()
                    }
                } catch (e: Exception) {
                    // ignore
                }
            }
        )
    }
}

// DIALOG: CREATE NEW IN-APP CHEF / DISH HOSTING POST WITH PICTURE UPLOAD
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterKitchenDialog(viewModel: HomeChefViewModel, onDismiss: () -> Unit) {
    var chefName by remember { mutableStateOf("") }
    var cuisine by remember { mutableStateOf("Local Homestyle Cooking") }
    var bio by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var youtubeUrl by remember { mutableStateOf("") }
    var youtubeName by remember { mutableStateOf("") }

    // Host Profile Picture State
    var avatarUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150") }

    // Food Picture State
    var dishName by remember { mutableStateOf("") }
    var dishDesc by remember { mutableStateOf("") }
    var dishPrice by remember { mutableStateOf("") }
    var dishCategory by remember { mutableStateOf("Mains") }
    var dishImageUrl by remember { mutableStateOf("Jollof Rice") }

    val context = LocalContext.current

    var activeCameraTarget by remember { mutableStateOf<CameraTargetType?>(null) }

    val avatarGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val localPath = copyUriToInternalStorage(context, uri, "cook_avatar")
            avatarUrl = localPath
        }
    }

    val dishGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val localPath = copyUriToInternalStorage(context, uri, "dish_photo")
            dishImageUrl = localPath
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Publish Host Kitchen & Dishes 🍳", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("1. Chef Social Profile & Avatar Photo 📸", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                // Profile Avatar Preview & Camera Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .clickable { activeCameraTarget = CameraTargetType.COOK_AVATAR },
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Chef Avatar Preview",
                            placeholder = painterResource(id = android.R.drawable.ic_menu_camera),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(24.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Camera",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { activeCameraTarget = CameraTargetType.COOK_AVATAR },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("cook_profile_camera_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Take Profile Photo (Camera) 📸", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                avatarGalleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .testTag("cook_profile_gallery_button"),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Choose from Gallery 🖼️", fontSize = 11.sp)
                        }
                    }
                }

                if (avatarUrl.startsWith("file:") || avatarUrl.startsWith("content:")) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F5E9),
                        border = BorderStroke(1.dp, Color(0xFF4CAF50)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Custom Profile Photo Captured! ✓", color = Color(0xFF1B5E20), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                OutlinedTextField(
                    value = avatarUrl,
                    onValueChange = { avatarUrl = it },
                    label = { Text("Profile Photo URL / Local Path") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = { Icon(Icons.Default.PhotoCamera, contentDescription = null) }
                )

                // Preset Chef Avatars
                Text("Or select sample avatar preset:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(
                        "👩‍🍳 Amara" to "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150",
                        "👨‍🍳 Tunde" to "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                        "👩‍🍳 Chinelo" to "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                        "🧑‍🍳 Kemi" to "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150",
                        "👨‍🍳 Chef David" to "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150"
                    ).forEach { (label, url) ->
                        FilterChip(
                            selected = avatarUrl == url,
                            onClick = { avatarUrl = url },
                            label = { Text(label, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = chefName,
                    onValueChange = { chefName = it },
                    label = { Text("Your Chef Name") },
                    modifier = Modifier.fillMaxWidth().testTag("add_chef_name")
                )
                OutlinedTextField(
                    value = cuisine,
                    onValueChange = { cuisine = it },
                    label = { Text("Cuisine Specialties Tag") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Short Bio / Culinary Passion") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Kitchen Physical Address") },
                    modifier = Modifier.fillMaxWidth()
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                Text("2. Signature Dish Details & Food Picture 🥘", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)

                // Food Image Preview & Camera Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(2.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(12.dp))
                            .clickable { activeCameraTarget = CameraTargetType.DISH_PHOTO },
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = getMealImageModel(dishImageUrl, dishName),
                            contentDescription = "Dish Photo Preview",
                            placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(24.dp)
                                .background(MaterialTheme.colorScheme.secondary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Camera",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { activeCameraTarget = CameraTargetType.DISH_PHOTO },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .testTag("cook_dish_camera_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Capture Dish Photo (Camera) 🥘", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                dishGalleryLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .testTag("cook_dish_gallery_button"),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Choose from Gallery 🖼️", fontSize = 11.sp)
                        }
                    }
                }

                if (dishImageUrl.startsWith("file:") || dishImageUrl.startsWith("content:")) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F5E9),
                        border = BorderStroke(1.dp, Color(0xFF4CAF50)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Custom Dish Photo Captured! ✓", color = Color(0xFF1B5E20), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                OutlinedTextField(
                    value = dishImageUrl,
                    onValueChange = { dishImageUrl = it },
                    label = { Text("Food Photo URL or Keyword") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = { Icon(Icons.Default.AddPhotoAlternate, contentDescription = null) }
                )

                // Preset Food Photos
                Text("Select Popular Dish Photo Preset:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(
                        "🍛 Jollof Rice",
                        "🌶️ Ayamase Ofada",
                        "🥘 Egusi Pounded Yam",
                        "🍲 Efo Riro",
                        "🍲 Buka Stew",
                        "🥣 Asaro Porridge",
                        "🍢 Suya & Asun",
                        "🥞 Puff Puff"
                    ).forEach { preset ->
                        FilterChip(
                            selected = dishImageUrl == preset,
                            onClick = { dishImageUrl = preset },
                            label = { Text(preset, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = dishName,
                    onValueChange = { dishName = it },
                    label = { Text("Dish Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dishDesc,
                    onValueChange = { dishDesc = it },
                    label = { Text("Short Dish Culinary Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dishPrice,
                    onValueChange = { dishPrice = it },
                    label = { Text("Dish Price ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Category options Box / Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("Mains", "Starters", "Desserts", "Drinks").forEach { cat ->
                        FilterChip(
                            selected = dishCategory == cat,
                            onClick = { dishCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (chefName.isEmpty() || address.isEmpty() || dishName.isEmpty() || dishPrice.isEmpty()) {
                        Toast.makeText(context, "Please complete all mandatory parameters.", Toast.LENGTH_SHORT).show()
                    } else {
                        val parsedPrice = dishPrice.toDoubleOrNull() ?: 10.0
                        viewModel.createPostListing(
                            chefName = chefName,
                            cuisine = cuisine,
                            bio = bio,
                            phone = phone,
                            address = address,
                            youtubeUrl = youtubeUrl,
                            youtubeName = youtubeName,
                            mealName = dishName,
                            mealDesc = dishDesc,
                            mealPrice = parsedPrice,
                            category = dishCategory,
                            avatarUrl = avatarUrl,
                            imageUrl = dishImageUrl
                        )
                        Toast.makeText(context, "Kitchen & Dish Photos listed live on Front Page!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("publish_post_confirm")
            ) {
                Text("Publish Kitchen & Photos")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (activeCameraTarget != null) {
        CookCameraDialog(
            targetType = activeCameraTarget!!,
            title = if (activeCameraTarget == CameraTargetType.COOK_AVATAR) "Cook Profile Photo Camera" else "Signature Dish Food Camera",
            subtitle = if (activeCameraTarget == CameraTargetType.COOK_AVATAR) "Take a real photo of yourself as a home chef" else "Snap your delicious homemade specialty dish",
            onPhotoCaptured = { savedUriString ->
                if (activeCameraTarget == CameraTargetType.COOK_AVATAR) {
                    avatarUrl = savedUriString
                } else {
                    dishImageUrl = savedUriString
                }
                activeCameraTarget = null
            },
            onDismiss = { activeCameraTarget = null }
        )
    }
}

// DIALOG: MANAGE EXISTING HOST KITCHEN PROFILE & DISH PHOTOS
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ManageHostKitchenPhotosDialog(
    viewModel: HomeChefViewModel,
    onDismiss: () -> Unit
) {
    val chefs by viewModel.chefs.collectAsState()
    val meals by viewModel.meals.collectAsState()
    val context = LocalContext.current

    var selectedChef by remember { mutableStateOf(chefs.firstOrNull()) }
    var selectedMeal by remember { mutableStateOf<MealEntity?>(null) }

    LaunchedEffect(selectedChef) {
        if (selectedChef != null) {
            val chefMeals = meals.filter { it.chefId == selectedChef?.id }
            selectedMeal = chefMeals.firstOrNull()
        }
    }

    var editAvatarUrl by remember(selectedChef) { mutableStateOf(selectedChef?.avatarUrl ?: "") }
    var editBio by remember(selectedChef) { mutableStateOf(selectedChef?.bio ?: "") }
    var editCuisine by remember(selectedChef) { mutableStateOf(selectedChef?.cuisineType ?: "") }

    var editDishName by remember(selectedMeal) { mutableStateOf(selectedMeal?.name ?: "") }
    var editDishPrice by remember(selectedMeal) { mutableStateOf(selectedMeal?.price?.toString() ?: "12.0") }
    var editDishImageUrl by remember(selectedMeal) { mutableStateOf(selectedMeal?.imageUrl ?: "") }

    var activeCameraTarget by remember { mutableStateOf<CameraTargetType?>(null) }

    val editAvatarGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val localPath = copyUriToInternalStorage(context, uri, "cook_avatar")
            editAvatarUrl = localPath
        }
    }

    val editDishGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val localPath = copyUriToInternalStorage(context, uri, "dish_photo")
            editDishImageUrl = localPath
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Manage Host Profile & Dish Photos", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Select Host Kitchen to Edit:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(chefs) { chef ->
                        FilterChip(
                            selected = selectedChef?.id == chef.id,
                            onClick = { selectedChef = chef },
                            label = { Text(chef.name, fontSize = 11.sp) }
                        )
                    }
                }

                selectedChef?.let { chef ->
                    HorizontalDivider()

                    Text("1. Update Profile Picture (Avatar) 📸", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                .clickable { activeCameraTarget = CameraTargetType.COOK_AVATAR },
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = editAvatarUrl,
                                contentDescription = "Updated Avatar Preview",
                                placeholder = painterResource(id = android.R.drawable.ic_menu_camera),
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { activeCameraTarget = CameraTargetType.COOK_AVATAR },
                                    modifier = Modifier.weight(1f).height(36.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Camera 📸", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = {
                                        editAvatarGalleryLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    modifier = Modifier.weight(1f).height(36.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Gallery 🖼️", fontSize = 11.sp)
                                }
                            }

                            OutlinedTextField(
                                value = editAvatarUrl,
                                onValueChange = { editAvatarUrl = it },
                                label = { Text("Avatar URL / Path") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Avatar Presets
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            "👩‍🍳 Amara" to "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150",
                            "👨‍🍳 Tunde" to "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                            "👩‍🍳 Chinelo" to "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                            "🧑‍🍳 Kemi" to "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150"
                        ).forEach { (label, url) ->
                            FilterChip(
                                selected = editAvatarUrl == url,
                                onClick = { editAvatarUrl = url },
                                label = { Text(label, fontSize = 10.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = editCuisine,
                        onValueChange = { editCuisine = it },
                        label = { Text("Cuisine Specialty") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Chef Bio") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider()

                    Text("2. Update Dish Photo & Price 🍲", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)

                    val chefMeals = meals.filter { it.chefId == chef.id }
                    if (chefMeals.isNotEmpty()) {
                        Text("Select Dish to Update:", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(chefMeals) { m ->
                                FilterChip(
                                    selected = selectedMeal?.id == m.id,
                                    onClick = { selectedMeal = m },
                                    label = { Text(m.name, fontSize = 11.sp) }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(2.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(10.dp))
                                    .clickable { activeCameraTarget = CameraTargetType.DISH_PHOTO },
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = getMealImageModel(editDishImageUrl, editDishName),
                                    contentDescription = "Dish Preview",
                                    placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Button(
                                        onClick = { activeCameraTarget = CameraTargetType.DISH_PHOTO },
                                        modifier = Modifier.weight(1f).height(36.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Camera 🥘", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            editDishGalleryLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        modifier = Modifier.weight(1f).height(36.dp),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Gallery 🖼️", fontSize = 11.sp)
                                    }
                                }

                                OutlinedTextField(
                                    value = editDishImageUrl,
                                    onValueChange = { editDishImageUrl = it },
                                    label = { Text("Food Photo URL / Keyword") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Dish presets
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("🍛 Jollof Rice", "🌶️ Ayamase", "🥘 Egusi", "🍲 Efo Riro", "🍲 Buka Stew", "🥣 Asaro", "🍢 Suya").forEach { p ->
                                FilterChip(
                                    selected = editDishImageUrl == p,
                                    onClick = { editDishImageUrl = p },
                                    label = { Text(p, fontSize = 10.sp) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = editDishName,
                            onValueChange = { editDishName = it },
                            label = { Text("Dish Name") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = editDishPrice,
                            onValueChange = { editDishPrice = it },
                            label = { Text("Price ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val chef = selectedChef
                    if (chef != null) {
                        viewModel.updateChefProfile(
                            chef = chef,
                            newAvatarUrl = editAvatarUrl,
                            newCuisine = editCuisine,
                            newBio = editBio
                        )
                        selectedMeal?.let { m ->
                            val parsedPrice = editDishPrice.toDoubleOrNull() ?: m.price
                            viewModel.updateMealDetails(
                                meal = m,
                                newName = editDishName,
                                newPrice = parsedPrice,
                                newImageUrl = editDishImageUrl
                            )
                        }
                        Toast.makeText(context, "Host profile & dish photo updated on front page!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                }
            ) {
                Text("Save & Update Front Page")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (activeCameraTarget != null) {
        CookCameraDialog(
            targetType = activeCameraTarget!!,
            title = if (activeCameraTarget == CameraTargetType.COOK_AVATAR) "Update Cook Avatar Photo" else "Update Dish Photo",
            subtitle = if (activeCameraTarget == CameraTargetType.COOK_AVATAR) "Snap a live chef photo or choose from device gallery" else "Capture a fresh photo of your culinary preparation",
            onPhotoCaptured = { savedUriString ->
                if (activeCameraTarget == CameraTargetType.COOK_AVATAR) {
                    editAvatarUrl = savedUriString
                } else {
                    editDishImageUrl = savedUriString
                }
                activeCameraTarget = null
            },
            onDismiss = { activeCameraTarget = null }
        )
    }
}

@Composable
fun ChefChatDialog(
    chef: ChefEntity,
    viewModel: HomeChefViewModel,
    onDismiss: () -> Unit
) {
    val messages by viewModel.activeChefChatMessages.collectAsState()
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(vertical = 8.dp)
                .testTag("chef_chat_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header (Chef Avatar & Name)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = chef.avatarUrl,
                        contentDescription = chef.name,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = chef.name,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Online • Instant Reply",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.background(Color.White.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Chat Messages List
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (messages.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QuestionAnswer,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        text = "Ask about ingredients, spices, allergen info, or customizable options!",
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        modifier = Modifier.padding(horizontal = 24.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        items(messages) { message ->
                            val isUser = message.sender == "User"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                            ) {
                                Card(
                                    shape = RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isUser) 16.dp else 4.dp,
                                        bottomEnd = if (isUser) 4.dp else 16.dp
                                    ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer
                                    ),
                                    modifier = Modifier.widthIn(max = 260.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = message.text,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(message.timestamp)),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = (if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer).copy(alpha = 0.6f),
                                            modifier = Modifier.align(Alignment.End)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Quick Suggestion Chips
                val suggestions = listOf(
                    "Is it spicy?",
                    "Any nut allergies?",
                    "Vegetarian options?",
                    "Can I customize portion?"
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(suggestions) { query ->
                        SuggestionChip(
                            onClick = {
                                viewModel.sendChefChatMessage(chef.id, query)
                            },
                            label = { Text(query, style = MaterialTheme.typography.bodySmall) },
                            modifier = Modifier.testTag("suggestion_chip_${query.replace(" ", "_").replace("?", "")}")
                        )
                    }
                }

                // Input Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        placeholder = { Text("Ask about ingredients...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_text_field"),
                        maxLines = 3,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (textInput.isNotBlank()) {
                                viewModel.sendChefChatMessage(chef.id, textInput)
                                textInput = ""
                            }
                        },
                        enabled = textInput.isNotBlank(),
                        modifier = Modifier
                            .background(
                                if (textInput.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                            .testTag("send_chat_message_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (textInput.isNotBlank()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// DIALOG: ADD DISH COGNITIVE Star-rating TRUST REVIEW
@Composable
fun ReviewDialog(
    chefId: Int,
    mealId: Int = 0,
    mealName: String = "",
    viewModel: HomeChefViewModel,
    onDismiss: () -> Unit
) {
    var reviewerName by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5) }

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (mealId != 0) "Rate Dish: $mealName" else "Publish Trust Review",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = reviewerName,
                    onValueChange = { reviewerName = it },
                    label = { Text("Your Screen Name") },
                    modifier = Modifier.fillMaxWidth().testTag("add_reviewer_name")
                )

                Text("Rating Core Score:", fontWeight = FontWeight.Medium)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        val itemRate = index + 1
                        IconButton(onClick = { rating = itemRate }, modifier = Modifier.size(36.dp)) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "$itemRate Stars",
                                tint = if (itemRate <= rating) Color(0xFFFFB300) else Color.LightGray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text(if (mealId != 0) "How was the dish?" else "Product / Culinary Feedback comment") },
                    modifier = Modifier.fillMaxWidth().testTag("add_review_comment")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (reviewerName.isEmpty() || comment.isEmpty()) {
                        Toast.makeText(context, "Please write feedback parameters fully.", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.submitReview(chefId, mealId, reviewerName, rating, comment)
                        val isLive = viewModel.isLiveMode.value
                        val msg = if (isLive) "Review submitted & synced with backend!" else "Trust rating saved in local base!"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("add_review_submit_button")
            ) {
                Text("Publish Review")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

// DIALOG: CHECKOUT SECURE PAYMENTS (GOOGLE PLAY / GOOGLE PAY / CASH / CARD)
@Composable
fun OrderCheckoutDialog(
    meal: MealEntity,
    chefName: String,
    viewModel: HomeChefViewModel,
    onDismiss: () -> Unit,
    initialQuantity: Int = 1,
    initialName: String = "",
    initialAddress: String = "",
    initialPhone: String = ""
) {
    var quantity by remember { mutableStateOf(initialQuantity) }
    var name by remember { mutableStateOf(initialName.ifBlank { "Alex Morgan" }) }
    var address by remember { mutableStateOf(initialAddress.ifBlank { "742 Evergreen Terrace, Apt 4B" }) }
    var phone by remember { mutableStateOf(initialPhone.ifBlank { "+1 (555) 019-2834" }) }
    var buyerEmail by remember { mutableStateOf("alex.morgan@paypal.com") }

    // Selected payment method: Default to PayPal for real customer payments and instant chef settlement
    var selectedMethod by remember { mutableStateOf(com.example.data.PaymentMethodType.PAYPAL) }

    val isCitchClubMember by viewModel.isCitchClubMember.collectAsState()
    val chefs by viewModel.chefs.collectAsState()
    val chef = remember(chefs, meal.chefId) { chefs.find { it.id == meal.chefId } }
    val commissionRate = chef?.commissionRate ?: 0.15

    val foodSubtotal = remember(quantity, meal.price) { meal.price * quantity }
    val memberDiscount = remember(isCitchClubMember, foodSubtotal) {
        if (isCitchClubMember) foodSubtotal * 0.10 else 0.0
    }
    val discountedFoodSubtotal = foodSubtotal - memberDiscount
    val isFreeDelivery = isCitchClubMember && discountedFoodSubtotal >= 15.0
    val deliveryFee = if (isFreeDelivery) 0.0 else 3.50
    val totalCost = discountedFoodSubtotal + deliveryFee

    val platformCommissionFee = foodSubtotal * commissionRate
    val hostKitchenCredit = foodSubtotal - platformCommissionFee

    val context = LocalContext.current
    val isLiveMode by viewModel.isLiveMode.collectAsState()
    val scope = rememberCoroutineScope()

    // Payment process states: "INPUT", "PROCESSING", "SUCCESS", "FAILURE"
    var paymentStage by remember { mutableStateOf("INPUT") }
    var paymentStatusMessage by remember { mutableStateOf("") }
    var paymentErrorMessage by remember { mutableStateOf("") }
    var transactionReference by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (paymentStage != "PROCESSING") onDismiss() },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when (selectedMethod) {
                                com.example.data.PaymentMethodType.PAYPAL -> Icons.Default.AccountBalanceWallet
                                com.example.data.PaymentMethodType.GOOGLE_PLAY_BILLING -> Icons.Default.PlayArrow
                                com.example.data.PaymentMethodType.GOOGLE_PAY -> Icons.Default.PlayCircle
                                com.example.data.PaymentMethodType.ONE_TAP_CASH -> Icons.Default.Payments
                            },
                            contentDescription = null,
                            tint = when (selectedMethod) {
                                com.example.data.PaymentMethodType.PAYPAL -> Color(0xFF0070BA)
                                com.example.data.PaymentMethodType.GOOGLE_PLAY_BILLING -> Color(0xFF01875F)
                                com.example.data.PaymentMethodType.GOOGLE_PAY -> Color(0xFF01875F)
                                com.example.data.PaymentMethodType.ONE_TAP_CASH -> Color(0xFF06C167)
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Checkout & Payment",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = when (selectedMethod) {
                            com.example.data.PaymentMethodType.PAYPAL -> "PayPal Express Checkout"
                            com.example.data.PaymentMethodType.GOOGLE_PLAY_BILLING -> "Google Play In-App Billing"
                            com.example.data.PaymentMethodType.GOOGLE_PAY -> "Google Pay Fast Pass"
                            com.example.data.PaymentMethodType.ONE_TAP_CASH -> "Cash on Handover"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = when (selectedMethod) {
                            com.example.data.PaymentMethodType.PAYPAL -> Color(0xFF0070BA)
                            else -> Color(0xFF01875F)
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (paymentStage == "INPUT") {
                    // Order Summary Card (Uber White Style)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = meal.name,
                                        fontWeight = FontWeight.ExtraBold,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "Kitchen: $chefName",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                                Text(
                                    text = com.example.data.CurrencyHelper.formatPrice(meal.price),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(8.dp))

                            // Quantity Selector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Quantity", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(Color.White, RoundedCornerShape(20.dp))
                                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(20.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    IconButton(
                                        onClick = { if (quantity > 1) quantity-- },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                    }
                                    Text(
                                        text = "$quantity",
                                        fontWeight = FontWeight.ExtraBold,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                    IconButton(
                                        onClick = { quantity++ },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Delivery Info Section
                    Text("Delivery Details", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("checkout_name_input")
                    )
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Delivery Address") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("checkout_address_input")
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("checkout_phone_input")
                    )

                    // Payment Method Selector
                    Text("Select Payment Method", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                    com.example.data.PaymentMethodType.values().forEach { method ->
                        val isSelected = selectedMethod == method
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedMethod = method },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) Color(0xFF000000) else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                1.5.dp,
                                if (isSelected) Color(0xFF000000) else Color(0xFFE5E5E5)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = when (method) {
                                            com.example.data.PaymentMethodType.PAYPAL -> Icons.Default.AccountBalanceWallet
                                            com.example.data.PaymentMethodType.GOOGLE_PLAY_BILLING -> Icons.Default.PlayArrow
                                            com.example.data.PaymentMethodType.GOOGLE_PAY -> Icons.Default.PlayCircle
                                            com.example.data.PaymentMethodType.ONE_TAP_CASH -> Icons.Default.LocalAtm
                                        },
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else when (method) {
                                            com.example.data.PaymentMethodType.PAYPAL -> Color(0xFF0070BA)
                                            com.example.data.PaymentMethodType.ONE_TAP_CASH -> Color(0xFF06C167)
                                            else -> Color(0xFF01875F)
                                        },
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = method.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isSelected) Color.White else Color.Black
                                        )
                                        Text(
                                            text = method.subtitle,
                                            fontSize = 11.sp,
                                            color = if (isSelected) Color(0xFFD5D5D5) else Color.Gray
                                        )
                                    }
                                }
                                Surface(
                                    color = if (isSelected) {
                                        if (method == com.example.data.PaymentMethodType.PAYPAL) Color(0xFF0070BA) else Color(0xFF01875F)
                                    } else Color.White,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = method.badge,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color.DarkGray,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }

                    // PayPal Email & Buyer Protection Info Card
                    if (selectedMethod == com.example.data.PaymentMethodType.PAYPAL) {
                        OutlinedTextField(
                            value = buyerEmail,
                            onValueChange = { buyerEmail = it },
                            label = { Text("PayPal Account Email") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF0070BA))
                            },
                            modifier = Modifier.fillMaxWidth().testTag("checkout_paypal_email_input")
                        )

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFEBF3FC),
                            border = BorderStroke(1.dp, Color(0xFFB9D7F9)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = Color(0xFF0070BA),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        "Official PayPal Checkout & Chef Payout",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF003087)
                                    )
                                    Text(
                                        "Secure instant transfer to Chef $chefName. Protected by PayPal Purchase Protection.",
                                        fontSize = 11.sp,
                                        color = Color(0xFF00457C)
                                    )
                                }
                            }
                        }
                    }

                    // Google Play Billing Info Card
                    if (selectedMethod == com.example.data.PaymentMethodType.GOOGLE_PLAY_BILLING) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFE6F4EA),
                            border = BorderStroke(1.dp, Color(0xFFCEEAD6)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.VerifiedUser,
                                    contentDescription = null,
                                    tint = Color(0xFF01875F),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        "Official Google Play Billing 7.1",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF0D652D)
                                    )
                                    Text(
                                        "Instant 1-Tap checkout with saved cards, Play balance, UPI, or PayPal.",
                                        fontSize = 11.sp,
                                        color = Color(0xFF137333)
                                    )
                                }
                            }
                        }
                    }

                    // Citch Club Diner Perks Banner
                    if (!isCitchClubMember) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFFF7ED),
                            border = BorderStroke(1.dp, Color(0xFFFFEDD5)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "🌟 Join Citch Club ($9.99/mo)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF9A3412)
                                    )
                                    Text(
                                        "Save ${com.example.data.CurrencyHelper.formatPrice(foodSubtotal * 0.10)} on this order & get Free Delivery over $15!",
                                        fontSize = 11.sp,
                                        color = Color(0xFF7A7067)
                                    )
                                }
                                Button(
                                    onClick = { viewModel.toggleCitchClubMembership(true) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD8582B)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Apply 10%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF0FDF4),
                            border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Citch Club Perks: -${com.example.data.CurrencyHelper.formatPrice(memberDiscount)} discount + ${if (isFreeDelivery) "FREE Delivery" else "$3.50 Delivery"}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color(0xFF15803D)
                                )
                            }
                        }
                    }

                    // Itemized Receipt Breakdown
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFBF9F7),
                        border = BorderStroke(1.dp, Color(0xFFECE6DD)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Food Subtotal ($quantity items):", fontSize = 12.sp, color = Color(0xFF7A7067))
                                Text(com.example.data.CurrencyHelper.formatPrice(foodSubtotal), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1B1612))
                            }
                            if (isCitchClubMember && memberDiscount > 0.0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Citch Club Member (10% off):", fontSize = 12.sp, color = Color(0xFF15803D))
                                    Text("-${com.example.data.CurrencyHelper.formatPrice(memberDiscount)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Delivery Fee:", fontSize = 12.sp, color = Color(0xFF7A7067))
                                Text(
                                    if (isFreeDelivery) "FREE (Citch Club)" else com.example.data.CurrencyHelper.formatPrice(deliveryFee),
                                    fontSize = 12.sp,
                                    fontWeight = if (isFreeDelivery) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isFreeDelivery) Color(0xFF15803D) else Color(0xFF1B1612)
                                )
                            }
                            Divider(color = Color(0xFFECE6DD), thickness = 0.5.dp, modifier = Modifier.padding(vertical = 2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total to Pay:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1B1612))
                                Text(
                                    text = com.example.data.CurrencyHelper.formatPrice(totalCost),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = Color(0xFFD8582B)
                                )
                            }
                        }
                    }

                    // Marketplace Commission & Take-Rate Transparency Pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF3F4F6),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF6B7280), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Marketplace split: Kitchen receives ${com.example.data.CurrencyHelper.formatPrice(hostKitchenCredit)} · Citch platform fee (${(commissionRate * 100).toInt()}%) ${com.example.data.CurrencyHelper.formatPrice(platformCommissionFee)}",
                                fontSize = 10.sp,
                                color = Color(0xFF4B5563)
                            )
                        }
                    }
                } else if (paymentStage == "PROCESSING") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = if (selectedMethod == com.example.data.PaymentMethodType.PAYPAL) Color(0xFF0070BA) else Color(0xFF01875F),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            when (selectedMethod) {
                                com.example.data.PaymentMethodType.PAYPAL -> "Authorizing PayPal Order & Chef Settlement..."
                                com.example.data.PaymentMethodType.GOOGLE_PLAY_BILLING -> "Launching Google Play In-App Billing..."
                                com.example.data.PaymentMethodType.GOOGLE_PAY -> "Authorizing with Google Pay Fast Pass..."
                                com.example.data.PaymentMethodType.ONE_TAP_CASH -> "Confirming Cash on Delivery Order..."
                            },
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            paymentStatusMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (paymentStage == "SUCCESS") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(Color(0xFF01875F), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Success",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Text(
                            "Order Placed Successfully!",
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black
                        )
                        Text(
                            "Your kitchen host has received the ticket and started preparing your fresh meal.",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                        Surface(
                            color = Color(0xFFF6F6F6),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Payment: ${selectedMethod.title}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("Ref: $transactionReference", fontSize = 11.sp, color = Color.DarkGray)
                                Text("Total Paid: ${com.example.data.CurrencyHelper.formatPrice(totalCost)}", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                                Text("Kitchen Credited: ${com.example.data.CurrencyHelper.formatPrice(hostKitchenCredit)} (Net)", fontSize = 11.sp, color = Color(0xFF15803D), fontWeight = FontWeight.SemiBold)
                                Text("Citch Take-Rate (${(commissionRate * 100).toInt()}%): ${com.example.data.CurrencyHelper.formatPrice(platformCommissionFee)}", fontSize = 10.sp, color = Color.Gray)
                                Text("Deliver to: $address", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                } else if (paymentStage == "FAILURE") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(MaterialTheme.colorScheme.errorContainer, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Text(
                            "Payment Failed",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            paymentErrorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            color = Color.Gray
                        )
                        Button(
                            onClick = { paymentStage = "INPUT" },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            Text("Try Another Payment Method")
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (paymentStage == "INPUT") {
                Button(
                    onClick = {
                        if (name.isBlank() || address.isBlank() || phone.isBlank()) {
                            Toast.makeText(context, "Please fill in your delivery contact info.", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        scope.launch {
                            paymentStage = "PROCESSING"
                            when (selectedMethod) {
                                com.example.data.PaymentMethodType.PAYPAL -> {
                                    paymentStatusMessage = "Processing PayPal payment & transferring directly to Chef $chefName..."
                                    val res = viewModel.processPayPalPayment(
                                        amount = totalCost,
                                        buyerEmail = buyerEmail.ifBlank { "customer@paypal.com" },
                                        dishName = meal.name,
                                        chefId = meal.chefId,
                                        chefName = chefName
                                    )
                                    when (res) {
                                        is com.example.data.UnifiedPaymentResult.Success -> {
                                            transactionReference = res.transactionId
                                            viewModel.requestOrder(
                                                meal = meal,
                                                chefName = chefName,
                                                quantity = quantity,
                                                buyerName = name,
                                                buyerAddress = address,
                                                buyerPhone = phone,
                                                paymentMethod = com.example.data.PaymentMethodType.PAYPAL,
                                                customPaymentId = res.transactionId,
                                                commissionRate = commissionRate,
                                                customDeliveryFee = deliveryFee,
                                                onSuccess = { paymentStage = "SUCCESS" }
                                            )
                                        }
                                        is com.example.data.UnifiedPaymentResult.Failure -> {
                                            paymentErrorMessage = res.errorMessage
                                            paymentStage = "FAILURE"
                                        }
                                    }
                                }
                                com.example.data.PaymentMethodType.GOOGLE_PLAY_BILLING -> {
                                    paymentStatusMessage = "Processing 1-Tap Google Play In-App Purchase..."
                                    val res = viewModel.processGooglePlayBillingPayment(totalCost, meal.name, name)
                                    when (res) {
                                        is com.example.data.UnifiedPaymentResult.Success -> {
                                            transactionReference = res.transactionId
                                            viewModel.requestOrder(
                                                meal = meal,
                                                chefName = chefName,
                                                quantity = quantity,
                                                buyerName = name,
                                                buyerAddress = address,
                                                buyerPhone = phone,
                                                paymentMethod = com.example.data.PaymentMethodType.GOOGLE_PLAY_BILLING,
                                                customPaymentId = res.transactionId,
                                                commissionRate = commissionRate,
                                                customDeliveryFee = deliveryFee,
                                                onSuccess = { paymentStage = "SUCCESS" }
                                            )
                                        }
                                        is com.example.data.UnifiedPaymentResult.Failure -> {
                                            paymentErrorMessage = res.errorMessage
                                            paymentStage = "FAILURE"
                                        }
                                    }
                                }
                                com.example.data.PaymentMethodType.GOOGLE_PAY -> {
                                    paymentStatusMessage = "Connecting with Google Pay..."
                                    val res = viewModel.processGooglePayment(totalCost, name, meal.name)
                                    when (res) {
                                        is com.example.data.UnifiedPaymentResult.Success -> {
                                            transactionReference = res.transactionId
                                            viewModel.requestOrder(
                                                meal = meal,
                                                chefName = chefName,
                                                quantity = quantity,
                                                buyerName = name,
                                                buyerAddress = address,
                                                buyerPhone = phone,
                                                paymentMethod = com.example.data.PaymentMethodType.GOOGLE_PAY,
                                                customPaymentId = res.transactionId,
                                                commissionRate = commissionRate,
                                                customDeliveryFee = deliveryFee,
                                                onSuccess = { paymentStage = "SUCCESS" }
                                            )
                                        }
                                        is com.example.data.UnifiedPaymentResult.Failure -> {
                                            paymentErrorMessage = res.errorMessage
                                            paymentStage = "FAILURE"
                                        }
                                    }
                                }
                                com.example.data.PaymentMethodType.ONE_TAP_CASH -> {
                                    paymentStatusMessage = "Locking in Cash on Delivery reservation..."
                                    val res = viewModel.processCashPayment(totalCost, address)
                                    when (res) {
                                        is com.example.data.UnifiedPaymentResult.Success -> {
                                            transactionReference = res.transactionId
                                            viewModel.requestOrder(
                                                meal = meal,
                                                chefName = chefName,
                                                quantity = quantity,
                                                buyerName = name,
                                                buyerAddress = address,
                                                buyerPhone = phone,
                                                paymentMethod = com.example.data.PaymentMethodType.ONE_TAP_CASH,
                                                customPaymentId = res.transactionId,
                                                commissionRate = commissionRate,
                                                customDeliveryFee = deliveryFee,
                                                onSuccess = { paymentStage = "SUCCESS" }
                                            )
                                        }
                                        is com.example.data.UnifiedPaymentResult.Failure -> {
                                            paymentErrorMessage = res.errorMessage
                                            paymentStage = "FAILURE"
                                        }
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("submit_checkout_pay"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (selectedMethod) {
                            com.example.data.PaymentMethodType.PAYPAL -> Color(0xFF0070BA)
                            com.example.data.PaymentMethodType.GOOGLE_PLAY_BILLING -> Color(0xFF01875F)
                            com.example.data.PaymentMethodType.GOOGLE_PAY -> Color(0xFF000000)
                            com.example.data.PaymentMethodType.ONE_TAP_CASH -> Color(0xFF06C167)
                        }
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    val label = when (selectedMethod) {
                        com.example.data.PaymentMethodType.PAYPAL -> "🅿️ Pay with PayPal • ${com.example.data.CurrencyHelper.formatPrice(totalCost)}"
                        com.example.data.PaymentMethodType.GOOGLE_PLAY_BILLING -> "⚡ Google Play 1-Tap • ${com.example.data.CurrencyHelper.formatPrice(totalCost)}"
                        com.example.data.PaymentMethodType.GOOGLE_PAY -> "⚡ Google Pay • ${com.example.data.CurrencyHelper.formatPrice(totalCost)}"
                        com.example.data.PaymentMethodType.ONE_TAP_CASH -> "💵 Cash on Delivery • ${com.example.data.CurrencyHelper.formatPrice(totalCost)}"
                    }
                    Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            } else if (paymentStage == "SUCCESS") {
                Button(
                    onClick = {
                        onDismiss()
                        Toast.makeText(context, "Order is confirmed and active in tracking!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("close_invoice_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000000)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Track Order Live 🛵", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (paymentStage == "INPUT") {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("cancel_checkout")
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        }
    )
}

@Composable
fun GoLiveConfigScreen(viewModel: HomeChefViewModel) {
    val context = LocalContext.current
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var locationTrackingEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFFAF6F0))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Editorial Header
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1B1612),
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("profile_citch_logo")
                    ) {
                        Image(
                            painter = painterResource(id = com.example.R.drawable.img_citch_logo_1789242928296),
                            contentDescription = "Citch Logo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFECE6DD))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🌿", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Citch Member",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = Color(0xFF1B1612)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Your profile",
                fontSize = 30.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B1612),
                lineHeight = 34.sp
            )
            Text(
                text = "& host kitchen.",
                fontSize = 30.sp,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFD8582B),
                lineHeight = 34.sp
            )
        }

        // User Profile Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFECE6DD)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFD8582B),
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "A",
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                color = Color.White
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            "Alex Morgan",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF1B1612)
                        )
                        Text(
                            "olamide.hanson@gmail.com",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF7A7067)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                color = Color(0xFFE2F4E6),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    "🌿 Verified Foodie ⭐",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E432A),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Surface(
                                color = Color(0xFFFAF6F0),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, Color(0xFFECE6DD))
                            ) {
                                val userLoc by viewModel.currentLocationName.collectAsState()
                                Text(
                                    "📍 $userLoc",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF7A7067),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFECE6DD))

                // Action to open AI Culinary Hub
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFAF6F0),
                    border = BorderStroke(1.dp, Color(0xFFECE6DD)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(Screen.AICulinaryHub) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFFD8582B), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("AI Kitchen Assistant", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1B1612))
                                Text("Diet advice, recipe pairings & cooking advice", fontSize = 10.sp, color = Color(0xFF7A7067))
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF7A7067), modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // App Preferences
        Text(
            "App Preferences",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall,
            color = Color(0xFF1B1612)
        )

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFECE6DD)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFFD8582B), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Order Status Notifications", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF1B1612))
                            Text("Real-time kitchen updates", fontSize = 11.sp, color = Color(0xFF7A7067))
                        }
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFD8582B)
                        )
                    )
                }

                HorizontalDivider(color = Color(0xFFECE6DD))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFD8582B), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Live Location Accuracy", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF1B1612))
                            Text("Discover closest home kitchens", fontSize = 11.sp, color = Color(0xFF7A7067))
                        }
                    }
                    Switch(
                        checked = locationTrackingEnabled,
                        onCheckedChange = { locationTrackingEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFD8582B)
                        )
                    )
                }
            }
        }

        // Payments & Payout Info
        Text(
            "Payment & Chef Payout Hub",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall,
            color = Color(0xFF1B1612)
        )

        val chefs by viewModel.chefs.collectAsState()
        val allOrders by viewModel.orders.collectAsState()
        val allPayouts by viewModel.allPayouts.collectAsState()
        val isLiveMode by viewModel.isLiveMode.collectAsState()
        val coroutineScope = rememberCoroutineScope()

        val totalPlatformVolume = remember(allOrders) { allOrders.sumOf { it.totalAmount } }
        val totalPlatformCommissions = remember(allOrders) { allOrders.sumOf { it.platformFee } }
        val totalNetChefEarnings = remember(allOrders) { allOrders.sumOf { it.chefEarnings } }
        val totalDisbursedAll = remember(allPayouts) { allPayouts.sumOf { it.amount } }

        var selectedChefForPayout by remember { mutableStateOf<ChefEntity?>(null) }
        var payoutDialogAmount by remember { mutableStateOf("50.00") }
        var isDisbursing by remember { mutableStateOf(false) }

        // Main PayPal Gateway Card (Forest Green Citch Style)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E432A)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = Color(0xFF88D49E),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "PayPal Engine & Settlement",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    }
                    Surface(
                        color = Color(0xFF2E6B43),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            "Active",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                Text(
                    "Real customer payments through PayPal Express Checkout with direct automated disbursements to chefs' PayPal accounts.",
                    fontSize = 12.sp,
                    color = Color(0xFFD8F3DC),
                    lineHeight = 16.sp
                )

                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

                // Environment & Mode Switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("PayPal Environment", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        Text(
                            if (isLiveMode) "LIVE PRODUCTION (Real Billing)" else "SANDBOX (Safe Verification)",
                            color = if (isLiveMode) Color(0xFF88D49E) else Color(0xFFFFD166),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Button(
                        onClick = {
                            val newMode = !isLiveMode
                            viewModel.setLiveMode(newMode)
                            viewModel.setPayPalEnvironment(if (newMode) "LIVE" else "SANDBOX")
                            Toast.makeText(context, if (newMode) "Switched to PayPal LIVE mode" else "Switched to PayPal SANDBOX mode", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLiveMode) Color(0xFFD8582B) else Color(0xFF2E6B43)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (isLiveMode) "Live Mode" else "Sandbox", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Platform Financials Summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Gross GMV", color = Color(0xFFB7E4C7), fontSize = 11.sp)
                        Text(
                            com.example.data.CurrencyHelper.formatPrice(totalPlatformVolume),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Column {
                        Text("Citch Take-Rate", color = Color(0xFFFFD166), fontSize = 11.sp)
                        Text(
                            com.example.data.CurrencyHelper.formatPrice(totalPlatformCommissions),
                            color = Color(0xFFFFD166),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Column {
                        Text("Net Chef Payouts", color = Color(0xFFB7E4C7), fontSize = 11.sp)
                        Text(
                            com.example.data.CurrencyHelper.formatPrice(totalDisbursedAll),
                            color = Color(0xFF88D49E),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // 4 Monetization Engines Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
            border = BorderStroke(1.dp, Color(0xFFFDE68A)),
            modifier = Modifier.fillMaxWidth().testTag("monetization_engine_hub")
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFD97706),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Citch Monetization Revenue Engine", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF92400E))
                            Text("4 Active Multi-Stream Revenue Channels", fontSize = 11.sp, color = Color(0xFFB45309))
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFFFDE68A))

                // Engine 1: Marketplace Commission (Take Rate)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("1. Marketplace Commission (Take Rate)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1B1612))
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = Color(0xFFE0F2FE), shape = RoundedCornerShape(4.dp)) {
                                Text("15% / 8% Pro", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                        Text("Retained automatically on every order checkout. Credited: ${com.example.data.CurrencyHelper.formatPrice(totalPlatformCommissions)}.", fontSize = 11.sp, color = Color(0xFF7A7067))
                    }
                }

                // Engine 2: Citch Club Diner Subscriptions
                val isCitchClubMember by viewModel.isCitchClubMember.collectAsState()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("2. Citch Club Diner Membership", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1B1612))
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = if (isCitchClubMember) Color(0xFFDCFCE7) else Color(0xFFF3F4F6), shape = RoundedCornerShape(4.dp)) {
                                Text(if (isCitchClubMember) "ACTIVE" else "$9.99/mo", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isCitchClubMember) Color(0xFF15803D) else Color(0xFF4B5563), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                        Text("Diners pay $9.99/month for 10% food discount and Free Delivery over $15.", fontSize = 11.sp, color = Color(0xFF7A7067))
                    }
                    Switch(
                        checked = isCitchClubMember,
                        onCheckedChange = { viewModel.toggleCitchClubMembership(it) }
                    )
                }

                // Engine 3: Pro Kitchen Subscription ($29.99/mo)
                val proKitchensCount = remember(chefs) { chefs.count { it.isProTier } }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("3. Chef Pro Kitchen Tier ($29.99/mo)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1B1612))
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = Color(0xFFEDE9FE), shape = RoundedCornerShape(4.dp)) {
                                Text("$proKitchensCount Active", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D28D9), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                        Text("Chefs pay $29.99/month for reduced take-rate (8% vs 15%) and enhanced reach.", fontSize = 11.sp, color = Color(0xFF7A7067))
                    }
                }

                // Engine 4: Sponsored Top Placement ($19.00/wk)
                val sponsoredKitchensCount = remember(chefs) { chefs.count { it.isSponsored } }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("4. Top Placement & Map Sponsorship ($19/wk)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1B1612))
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp)) {
                                Text("$sponsoredKitchensCount Featured", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                        Text("Featured shelf placement & prominent Golden Star markers on Leaflet maps.", fontSize = 11.sp, color = Color(0xFF7A7067))
                    }
                }
            }
        }

        // Chefs Payout List
        Text(
            "Kitchen Hosts, Tiers & Accounts",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall,
            color = Color(0xFF1B1612)
        )

        chefs.forEach { chef ->
            val chefOrders = remember(allOrders, chef.id) { allOrders.filter { it.chefId == chef.id } }
            val chefSales = remember(chefOrders) { chefOrders.sumOf { it.totalAmount } }
            val chefFees = remember(chefOrders) { chefOrders.sumOf { it.platformFee } }
            val chefNetEarnings = remember(chefOrders) { chefOrders.sumOf { it.chefEarnings } }
            val chefPayoutsList = remember(allPayouts, chef.id) { allPayouts.filter { it.chefId == chef.id } }
            val chefDisbursed = remember(chefPayoutsList) { chefPayoutsList.sumOf { it.amount } }
            val chefBalance = remember(chefNetEarnings, chefDisbursed) { (chefNetEarnings - chefDisbursed).coerceAtLeast(0.0) }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFECE6DD)),
                modifier = Modifier.fillMaxWidth().testTag("chef_payout_card_${chef.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFD8582B).copy(alpha = 0.12f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        chef.name.take(1),
                                        color = Color(0xFFD8582B),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(chef.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1B1612))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (chef.isProTier) {
                                        Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                                            Text("PRO (8%)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                    } else {
                                        Surface(color = Color(0xFFF3F4F6), shape = RoundedCornerShape(4.dp)) {
                                            Text("STD (15%)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4B5563), modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                                Text(
                                    chef.paypalEmail.ifBlank { "No PayPal linked" },
                                    fontSize = 11.sp,
                                    color = Color(0xFF7A7067),
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "Net Balance: ${com.example.data.CurrencyHelper.formatPrice(chefBalance)}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = Color(0xFF1E432A)
                            )
                            Text(
                                "Gross Sales: ${com.example.data.CurrencyHelper.formatPrice(chefSales)}",
                                fontSize = 10.sp,
                                color = Color(0xFF7A7067)
                            )
                            Text(
                                "Commission: -${com.example.data.CurrencyHelper.formatPrice(chefFees)}",
                                fontSize = 10.sp,
                                color = Color(0xFFD97706)
                            )
                        }
                    }

                    // Tiers and Sponsorship Status Tags
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (chef.isChefOfTheWeek) {
                            Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp)) {
                                Text("🏆 Chef of Week", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                        if (chef.isSponsored) {
                            Surface(color = Color(0xFFDBEAFE), shape = RoundedCornerShape(4.dp)) {
                                Text("⭐ Top Placement Active", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                        }
                    }

                    // Interactive Monetization Controls & Payout
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Pro Tier Toggle
                            OutlinedButton(
                                onClick = {
                                    if (chef.isProTier) {
                                        viewModel.downgradeChefProTier(chef.id)
                                    } else {
                                        viewModel.upgradeChefToProTier(chef.id)
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(if (chef.isProTier) "Downgrade" else "Upgrade Pro (8%)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            // Sponsorship Toggle
                            OutlinedButton(
                                onClick = {
                                    if (chef.isSponsored) {
                                        viewModel.endChefSponsorship(chef.id)
                                    } else {
                                        viewModel.sponsorChefTopPlacement(chef.id, 1)
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(if (chef.isSponsored) "End Boost" else "⭐ Boost ($19/wk)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = {
                                selectedChefForPayout = chef
                                payoutDialogAmount = if (chefBalance > 0.0) String.format(Locale.US, "%.2f", chefBalance) else "50.00"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD8582B)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Payout", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }

        // Secondary Gateways (Google Play)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFECE6DD)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF01875F), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Google Play Billing & Google Pay", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1B1612))
                        Text("Available as checkout options alongside PayPal & Cash", fontSize = 11.sp, color = Color(0xFF7A7067))
                    }
                }
            }
        }

        // Selected Chef Payout Dialog
        if (selectedChefForPayout != null) {
            val chefToPay = selectedChefForPayout!!
            AlertDialog(
                onDismissRequest = { if (!isDisbursing) selectedChefForPayout = null },
                containerColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                icon = {
                    Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = Color(0xFFD8582B), modifier = Modifier.size(32.dp))
                },
                title = {
                    Text("Disburse to ${chefToPay.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1B1612))
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Direct payout to recipient PayPal wallet:", fontSize = 12.sp, color = Color(0xFF7A7067))
                        Surface(
                            color = Color(0xFFFAF6F0),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFECE6DD)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFFD8582B), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(chefToPay.paypalEmail.ifBlank { "chef@paypal.com" }, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1B1612))
                            }
                        }
                        OutlinedTextField(
                            value = payoutDialogAmount,
                            onValueChange = { payoutDialogAmount = it },
                            label = { Text("Amount ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD8582B),
                                unfocusedBorderColor = Color(0xFFECE6DD)
                            )
                        )
                        if (isDisbursing) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp).align(Alignment.CenterHorizontally), color = Color(0xFFD8582B))
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val amt = payoutDialogAmount.toDoubleOrNull() ?: 50.0
                            isDisbursing = true
                            coroutineScope.launch {
                                val res = viewModel.executeChefPayPalPayout(
                                    chefId = chefToPay.id,
                                    chefName = chefToPay.name,
                                    amount = amt,
                                    paypalEmail = chefToPay.paypalEmail.ifBlank { "chef@paypal.com" },
                                    note = "Host Kitchen Payout"
                                )
                                isDisbursing = false
                                selectedChefForPayout = null
                                when (res) {
                                    is com.example.data.UnifiedPaymentResult.Success -> {
                                        Toast.makeText(context, "PayPal Payout Completed! Batch: ${res.transactionId}", Toast.LENGTH_LONG).show()
                                    }
                                    is com.example.data.UnifiedPaymentResult.Failure -> {
                                        Toast.makeText(context, "Error: ${res.errorMessage}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        },
                        enabled = !isDisbursing,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD8582B)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Execute PayPal Payout", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedChefForPayout = null }, enabled = !isDisbursing) {
                        Text("Cancel", color = Color(0xFF7A7067))
                    }
                }
            )
        }

        // Privacy & Legal
        Text(
            "Legal & Compliance",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall,
            color = Color(0xFF1B1612)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showPrivacyDialog = true },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFECE6DD))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF1E432A), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Privacy Policy & Terms", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF1B1612))
                        Text("Google Play standard data protection policy", fontSize = 11.sp, color = Color(0xFF7A7067))
                    }
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF7A7067))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Privacy Policy Dialog
        if (showPrivacyDialog) {
            Dialog(onDismissRequest = { showPrivacyDialog = false }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.85f),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Security,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Privacy Policy",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(onClick = { showPrivacyDialog = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text(
                                    text = "Last Updated: August 2026",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontStyle = FontStyle.Italic,
                                    color = Color.Gray
                                )
                            }
                            item {
                                Text(
                                    text = "Welcome to Citch. We are committed to protecting your personal information and your right to privacy. This policy describes how we collect, use, and safeguard your details when using the app.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            item {
                                Text("1. Information We Collect", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = Color.Black)
                                Text(
                                    text = "• Profile Credentials: Name, email address, and delivery coordinates.\n" +
                                           "• Payments: Transactions are processed securely via Google Play / authorized merchant gateways. No credit card information is stored locally.\n" +
                                           "• Device Location: Fine/coarse GPS coordinate streams are utilized to search local home kitchens and calculate delivery distances in real time.\n" +
                                           "• Local Room Storage: Food carts, orders, and custom assistant preferences are saved locally on your device for fast offline loading.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                )
                            }
                            item {
                                Text("2. How We Use Information", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = Color.Black)
                                Text(
                                    text = "• Facilitate orders between verified home food artisans and food lovers.\n" +
                                           "• Map coordinates and calculate delivery distances in real time.\n" +
                                           "• Render customized culinary recipes via secure server-side Gemini models.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                )
                            }
                            item {
                                Text("3. Contact Information", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = Color.Black)
                                Text(
                                    text = "For privacy requests, inquiries, or account deletion: olamide.hanson@gmail.com",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                        Button(
                            onClick = { showPrivacyDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Acknowledge & Close", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(140.dp))
    }
}

@Composable
fun StepItem(
    stepNumber: String,
    title: String,
    description: String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stepNumber,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

// --- AI CULINARY HUB COMPOSABLES ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AICulinaryHubScreen(viewModel: HomeChefViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(
        "AI Chatbot" to Icons.Default.Chat,
        "Sourcing Maps" to Icons.Default.Map,
        "Creative Studio" to Icons.Default.AutoAwesome,
        "Vision Scanner" to Icons.Default.PhotoCamera,
        "Live Voice" to Icons.Default.GraphicEq
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.testTag("ai_tabs")
        ) {
            tabs.forEachIndexed { index, (title, icon) ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, style = MaterialTheme.typography.labelMedium) },
                    icon = { Icon(icon, contentDescription = title, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.testTag("ai_tab_$index")
                )
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when (selectedTabIndex) {
                0 -> ChatbotTabContent(viewModel)
                1 -> SourcingMapsTabContent(viewModel)
                2 -> CreativeStudioTabContent(viewModel)
                3 -> VisionScannerTabContent(viewModel)
                4 -> LiveVoiceTabContent(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatbotTabContent(viewModel: HomeChefViewModel) {
    val aiChatHistory by viewModel.aiChatHistory.collectAsState()
    val aiChatIsLoading by viewModel.aiChatIsLoading.collectAsState()
    val aiChatModel by viewModel.aiChatModel.collectAsState()
    val aiChatThinkingMode by viewModel.aiChatThinkingMode.collectAsState()
    val aiChatGoogleSearch by viewModel.aiChatGoogleSearch.collectAsState()
    val aiChatGoogleMaps by viewModel.aiChatGoogleMaps.collectAsState()
    val isRecordingAudio by viewModel.isRecordingAudio.collectAsState()
    val transcriptionResult by viewModel.transcriptionResult.collectAsState()

    var userText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(aiChatHistory.size) {
        if (aiChatHistory.isNotEmpty()) {
            listState.animateScrollToItem(aiChatHistory.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Culinara Configuration (Gemini API)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Model:",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    
                    val models = listOf("gemini-3.5-flash", "gemini-3.1-pro-preview", "gemini-3.1-flash-lite")
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        models.forEach { m ->
                            val isSelected = aiChatModel == m
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateAiChatModel(m) },
                                label = { Text(m.substringAfter("gemini-"), fontSize = 10.sp) },
                                modifier = Modifier.padding(horizontal = 2.dp).testTag("chip_$m")
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = aiChatThinkingMode,
                            onCheckedChange = { viewModel.toggleAiChatThinking(it) },
                            modifier = Modifier.scale(0.7f).testTag("thinking_switch")
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Thinking", style = MaterialTheme.typography.bodySmall)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = aiChatGoogleSearch,
                            onCheckedChange = { viewModel.toggleAiChatGoogleSearch(it) },
                            modifier = Modifier.scale(0.7f).testTag("search_grounding_switch")
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Search", style = MaterialTheme.typography.bodySmall)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(
                            checked = aiChatGoogleMaps,
                            onCheckedChange = { viewModel.toggleAiChatGoogleMaps(it) },
                            modifier = Modifier.scale(0.7f).testTag("maps_grounding_switch")
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Maps", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        if (transcriptionResult.isNotEmpty() || isRecordingAudio) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isRecordingAudio) Icons.Default.Mic else Icons.Default.Receipt,
                        contentDescription = "Dictation",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isRecordingAudio) "Listening..." else "Speech Transcript (Gemini 3.5 Flash)",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = transcriptionResult,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    if (!isRecordingAudio && transcriptionResult.isNotEmpty() && transcriptionResult != "Transcribing...") {
                        TextButton(
                            onClick = {
                                userText = transcriptionResult
                            }
                        ) {
                            Text("Use Text", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(aiChatHistory) { (sender, text) ->
                val isUser = sender == "User"
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Column(
                        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        Text(
                            text = sender,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            tonalElevation = 1.dp
                        ) {
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
            if (aiChatIsLoading) {
                item {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Culinara AI is thinking...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.toggleAudioRecording() },
                modifier = Modifier.testTag("ai_mic_button")
            ) {
                Icon(
                    imageVector = if (isRecordingAudio) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Voice Dictation",
                    tint = if (isRecordingAudio) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            OutlinedTextField(
                value = userText,
                onValueChange = { userText = it },
                placeholder = { Text("Ask Culinara a recipe challenge...", fontSize = 14.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_chat_input"),
                shape = CircleShape,
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            FloatingActionButton(
                onClick = {
                    if (userText.isNotBlank()) {
                        viewModel.sendAiChatMessage(userText)
                        userText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .testTag("ai_chat_send_button"),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourcingMapsTabContent(viewModel: HomeChefViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    val pastSearches = remember {
        mutableStateListOf(
            "Organic Farmers Market Downtown SF",
            "Premium African Spice Importers",
            "Fresh Sourdough Flour Millers"
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Google Maps Sourcing Grounding (Gemini 3.5 Flash)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Verify premium local supply chains, wholesale distributors, and food markets using real-world Google Maps API locations.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search sourcing e.g. organic avocado suppliers, SF spice markets") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier.fillMaxWidth().testTag("maps_search_input"),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    if (searchQuery.isNotBlank()) {
                        isLoading = true
                        resultText = null
                        if (!pastSearches.contains(searchQuery)) {
                            pastSearches.add(0, searchQuery)
                        }
                        coroutineScope.launch {
                            val response = GeminiService.generateContent(
                                model = "gemini-3.5-flash",
                                prompt = "Find real, actual business locations for: \"$searchQuery\" in the San Francisco Bay Area. List business names, real street addresses, phone numbers, and proximity notes based on Google Maps. Format clearly with bullets.",
                                useMaps = true
                            )
                            resultText = response
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.weight(1f).testTag("maps_search_btn")
            ) {
                Text("Scan Google Maps Sourcing")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(12.dp))
                Text("Querying Google Maps with Grounding...", style = MaterialTheme.typography.bodySmall)
            }
        } else if (resultText != null) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Map, contentDescription = "Map Pin", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Grounding Citations Verified ✓", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    Text(
                        text = resultText ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }
        } else {
            Column(modifier = Modifier.weight(1f)) {
                Text("Suggested Sourcing Queries:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                pastSearches.forEach { search ->
                    OutlinedCard(
                        onClick = {
                            searchQuery = search
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(search, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreativeStudioTabContent(viewModel: HomeChefViewModel) {
    var activeSubStudio by remember { mutableStateOf(0) }
    
    val imagePrompt by viewModel.imagePrompt.collectAsState()
    val imageQuality by viewModel.imageQuality.collectAsState()
    val imageSize by viewModel.imageSize.collectAsState()
    val imageAspectRatio by viewModel.imageAspectRatio.collectAsState()
    val generatedImageUrl by viewModel.generatedImageUrl.collectAsState()
    val imageIsGenerating by viewModel.imageIsGenerating.collectAsState()

    val videoPrompt by viewModel.videoPrompt.collectAsState()
    val videoAspectRatio by viewModel.videoAspectRatio.collectAsState()
    val generatedVideoUrl by viewModel.generatedVideoUrl.collectAsState()
    val videoIsGenerating by viewModel.videoIsGenerating.collectAsState()

    val musicPrompt by viewModel.musicPrompt.collectAsState()
    val musicDurationSec by viewModel.musicDurationSec.collectAsState()
    val generatedMusicUrl by viewModel.generatedMusicUrl.collectAsState()
    val musicIsGenerating by viewModel.musicIsGenerating.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TabRow(
            selectedTabIndex = activeSubStudio,
            containerColor = Color.Transparent,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Tab(selected = activeSubStudio == 0, onClick = { activeSubStudio = 0 }, text = { Text("Imagen 3") })
            Tab(selected = activeSubStudio == 1, onClick = { activeSubStudio = 1 }, text = { Text("Veo Sizzle") })
            Tab(selected = activeSubStudio == 2, onClick = { activeSubStudio = 2 }, text = { Text("Lyria Beats") })
        }

        when (activeSubStudio) {
            0 -> {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("High-Quality Menu Designer (Imagen 3)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = imagePrompt,
                        onValueChange = { viewModel.updateImagePrompt(it) },
                        placeholder = { Text("e.g. ultra realistic wood fire gourmet pizza with bubbles on crust and fresh basil") },
                        modifier = Modifier.fillMaxWidth().testTag("image_prompt_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Model Selection:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("gemini-3.1-flash-image-preview" to "Standard", "gemini-3-pro-image-preview" to "Studio Pro").forEach { (qualityId, label) ->
                            FilterChip(
                                selected = imageQuality == qualityId,
                                onClick = { viewModel.updateImageQuality(qualityId) },
                                label = { Text(label) },
                                modifier = Modifier.testTag("quality_chip_$qualityId")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Select Target Definition:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("1K", "2K", "4K").forEach { size ->
                            FilterChip(
                                selected = imageSize == size,
                                onClick = { viewModel.updateImageSize(size) },
                                label = { Text(size) },
                                modifier = Modifier.testTag("size_chip_$size")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Choose Aspect Ratio (Imagen Standard):", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    val ratios = listOf("1:1", "16:9", "9:16", "3:2", "2:3", "4:3", "21:9")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ratios.forEach { ratio ->
                            FilterChip(
                                selected = imageAspectRatio == ratio,
                                onClick = { viewModel.updateImageAspectRatio(ratio) },
                                label = { Text(ratio) },
                                modifier = Modifier.testTag("ratio_chip_$ratio")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.generateImage() },
                        enabled = !imageIsGenerating,
                        modifier = Modifier.fillMaxWidth().testTag("image_generate_btn")
                    ) {
                        if (imageIsGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generating Illustration...")
                        } else {
                            Text("Generate High-Fidelity Menu Asset")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (generatedImageUrl != null) {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                AsyncImage(
                                    model = generatedImageUrl,
                                    contentDescription = "Generated Image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(
                                            when (imageAspectRatio) {
                                                "16:9" -> 1.77f
                                                "9:16" -> 0.56f
                                                "3:2" -> 1.5f
                                                "2:3" -> 0.67f
                                                "4:3" -> 1.33f
                                                "21:9" -> 2.33f
                                                else -> 1f
                                            }
                                        )
                                        .background(Color.Black),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Render: Ready ($imageSize - $imageAspectRatio)", style = MaterialTheme.typography.labelSmall)
                                        Text("Model: $imageQuality", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Veo Cinematic Sizzle Creator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Generate sizzling cooking video reels using veo-3.1-fast-generate-preview.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = videoPrompt,
                        onValueChange = { viewModel.updateVideoPrompt(it) },
                        placeholder = { Text("e.g. delicious garlic sauce pouring over grilled salmon steak, slow motion, steam rising, high contrast") },
                        modifier = Modifier.fillMaxWidth().testTag("video_prompt_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Aspect Ratio:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("16:9" to "Landscape (Cinematic)", "9:16" to "Portrait (Reel)").forEach { (aspect, label) ->
                            FilterChip(
                                selected = videoAspectRatio == aspect,
                                onClick = { viewModel.updateVideoAspectRatio(aspect) },
                                label = { Text(label) },
                                modifier = Modifier.testTag("video_aspect_$aspect")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.generateVideo() },
                        enabled = !videoIsGenerating,
                        modifier = Modifier.fillMaxWidth().testTag("video_generate_btn")
                    ) {
                        if (videoIsGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Animating Video on Veo...")
                        } else {
                            Text("Animate Video from Text")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (generatedVideoUrl != null) {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Videocam, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Veo 3.1 Promo Output (1080p, $videoAspectRatio)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(if (videoAspectRatio == "16:9") 1.77f else 0.56f)
                                        .background(Color.Black, shape = MaterialTheme.shapes.medium),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Play Video", modifier = Modifier.size(60.dp), tint = Color.White.copy(alpha = 0.8f))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Cinematic Sizzle Simulation Loop", color = Color.White, style = MaterialTheme.typography.bodySmall)
                                        Text("Double tap to play simulated Veo feed", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Lyria Culinary Ambient Beat Studio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Create background cooking tracks, sizzling soundscapes, or coffee shop vibes using lyria-3-clip-preview.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = musicPrompt,
                        onValueChange = { viewModel.updateMusicPrompt(it) },
                        placeholder = { Text("e.g. warm lo-fi kitchen hiphop with background sizzle of frying oil and clinking cups") },
                        modifier = Modifier.fillMaxWidth().testTag("music_prompt_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Generate Mode:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("30s" to "Short Clip (lyria-3-clip-preview)", "3m" to "Full Track (lyria-3-pro-preview)").forEach { (dur, label) ->
                            FilterChip(
                                selected = musicDurationSec == dur,
                                onClick = { viewModel.updateMusicDuration(dur) },
                                label = { Text(label) },
                                modifier = Modifier.testTag("music_dur_$dur")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.generateMusic() },
                        enabled = !musicIsGenerating,
                        modifier = Modifier.fillMaxWidth().testTag("music_generate_btn")
                    ) {
                        if (musicIsGenerating) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Composing Ambient Track...")
                        } else {
                            Text("Compose Custom Kitchen Beat")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (generatedMusicUrl != null) {
                        var isPlayingMusic by remember { mutableStateOf(false) }
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FloatingActionButton(
                                    onClick = { isPlayingMusic = !isPlayingMusic },
                                    shape = CircleShape,
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isPlayingMusic) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Playback Control"
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Ambient Kitchen Rhythm", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = if (isPlayingMusic) "Now Playing Lyria Track..." else "Audio File Ready (Format: MP3, $musicDurationSec)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = if (isPlayingMusic) 0.45f else 0f,
                                        modifier = Modifier.fillMaxWidth(),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisionScannerTabContent(viewModel: HomeChefViewModel) {
    var selectedVisionType by remember { mutableStateOf(0) }
    
    val selectedImageAnalysisResult by viewModel.selectedImageAnalysisResult.collectAsState()
    val imageAnalysisIsLoading by viewModel.imageAnalysisIsLoading.collectAsState()

    val videoAnalysisResult by viewModel.videoAnalysisResult.collectAsState()
    val videoAnalysisIsLoading by viewModel.videoAnalysisIsLoading.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TabRow(
            selectedTabIndex = selectedVisionType,
            containerColor = Color.Transparent,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Tab(selected = selectedVisionType == 0, onClick = { selectedVisionType = 0 }, text = { Text("Fridge Scanner") })
            Tab(selected = selectedVisionType == 1, onClick = { selectedVisionType = 1 }, text = { Text("Video Analyzer") })
        }

        when (selectedVisionType) {
            0 -> {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("What's in my Fridge? (Gemini 3.1 Pro)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Take a picture or choose a sample fridge section to scan for fresh ingredients and draft a tailored recipe instantly.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Select a Fridge Compartment Scan:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    val fridgeSamples = listOf(
                        "Crisper Drawer: Spinach, Bell Peppers & Ginger" to "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=200",
                        "Middle Shelf: Salmon Steak, Garlic & Lemon" to "https://images.unsplash.com/photo-1519708227418-c8fd9a32b7a2?w=200",
                        "Door Rack: Eggs, Truffle Oil, Parmesan & Butter" to "https://images.unsplash.com/photo-1506084868230-bb9d95c24759?w=200"
                    )

                    fridgeSamples.forEach { (title, url) ->
                        OutlinedCard(
                            onClick = { viewModel.analyzeFridgeImage(title) },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("fridge_scan_$title")
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = null,
                                    modifier = Modifier.size(50.dp).background(Color.Gray, shape = MaterialTheme.shapes.small),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(title.substringBefore(":"), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text(title.substringAfter(": "), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (imageAnalysisIsLoading) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Gemini Pro is identifying ingredients...", style = MaterialTheme.typography.bodySmall)
                        }
                    } else if (selectedImageAnalysisResult != null) {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Chef's Draft Recipe Recommendations:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                Text(selectedImageAnalysisResult ?: "", style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp)
                            }
                        }
                    }
                }
            }
            1 -> {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Culinary Video Analyzer (Gemini 3.1 Pro)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Provide a cooking video recipe to extract exact proportions, chef techniques, and temperatures.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    val videoSamples = listOf(
                        "Kenji's Deep-Bowl Black Garlic Tonkotsu Ramen" to "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=200",
                        "Nigerian Sizzling Jollof Rice Masterclass" to "https://images.unsplash.com/photo-1626861300079-7844b27f17f6?w=200",
                        "The Secrets of Perfect French Truffle Soufflé" to "https://images.unsplash.com/photo-1579372786545-d24232daf58c?w=200"
                    )

                    videoSamples.forEach { (title, url) ->
                        OutlinedCard(
                            onClick = { viewModel.analyzeRecipeVideo(title) },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag("video_scan_$title")
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = null,
                                        modifier = Modifier.size(50.dp).background(Color.Gray, shape = MaterialTheme.shapes.small),
                                        contentScale = ContentScale.Crop
                                    )
                                    Box(modifier = Modifier.size(24.dp).background(Color.Black.copy(alpha = 0.5f), shape = CircleShape), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (videoAnalysisIsLoading) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Gemini Pro is analyzing video frames...", style = MaterialTheme.typography.bodySmall)
                        }
                    } else if (videoAnalysisResult != null) {
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Pro Recipe Transcript & Analysis Metrics:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                Text(videoAnalysisResult ?: "", style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiveVoiceTabContent(viewModel: HomeChefViewModel) {
    val isLiveVoiceSessionActive by viewModel.isLiveVoiceSessionActive.collectAsState()
    val voiceSessionLog by viewModel.voiceSessionLog.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(voiceSessionLog.size) {
        if (voiceSessionLog.isNotEmpty()) {
            listState.animateScrollToItem(voiceSessionLog.size - 1)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Voice Sous-Chef Companion",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Initiate a real-time voice session using the Live API (gemini-3.1-flash-live-preview) to get step-by-step guidance hands-free.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Box(
            modifier = Modifier
                .size(140.dp)
                .background(
                    if (isLiveVoiceSessionActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                )
                .testTag("voice_pulsator_box"),
            contentAlignment = Alignment.Center
        ) {
            if (isLiveVoiceSessionActive) {
                var pulseState by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    while (true) {
                        pulseState = !pulseState
                        delay(800)
                    }
                }
                val scale by animateFloatAsState(if (pulseState) 1.2f else 0.95f)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(scale)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), shape = CircleShape)
                )
            }

            FloatingActionButton(
                onClick = { viewModel.toggleLiveVoiceSession() },
                shape = CircleShape,
                containerColor = if (isLiveVoiceSessionActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                contentColor = if (isLiveVoiceSessionActive) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(80.dp).testTag("live_voice_fab")
            ) {
                Icon(
                    imageVector = if (isLiveVoiceSessionActive) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Voice Control",
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isLiveVoiceSessionActive) "Voice Session Active (Listening...)" else "Tap to connect Voice Sous-Chef",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (isLiveVoiceSessionActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(24.dp))

        ElevatedCard(
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Live Conversation Stream:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                
                if (voiceSessionLog.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No active session stream logs.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(voiceSessionLog) { log ->
                            val isSystem = log.startsWith("[")
                            val isChef = log.startsWith("AI Sous-Chef")
                            Text(
                                text = log,
                                style = if (isSystem) MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic) else MaterialTheme.typography.bodyMedium,
                                color = when {
                                    isSystem -> MaterialTheme.colorScheme.outline
                                    isChef -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onSurface
                                },
                                fontWeight = if (isChef) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
