package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chefs")
data class ChefEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val rating: Float,
    val address: String,
    val cuisineType: String,
    val phone: String,
    val bio: String,
    val youtubeChannelUrl: String,
    val youtubeChannelName: String,
    val avatarUrl: String,
    val latitude: Double,
    val longitude: Double,
    val followersCount: Int = 114,
    val paypalEmail: String = "",
    val isSponsored: Boolean = false,
    val isChefOfTheWeek: Boolean = false,
    val isProTier: Boolean = false,
    val commissionRate: Double = 0.15,
    val sponsoredUntil: Long = 0L
)

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chefId: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String,
    val isAvailable: Boolean = true,
    val tutorialVideoUrl: String = ""
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mealId: Int,
    val mealName: String,
    val chefId: Int,
    val chefName: String,
    val quantity: Int,
    val totalAmount: Double,
    val buyerName: String,
    val buyerAddress: String,
    val buyerPhone: String,
    val status: String, // "Pending", "Preparing", "Out for Delivery", "Delivered"
    val step: Int, // 0 to 3
    val paymentId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val subtotal: Double = totalAmount,
    val platformFee: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val discountAmount: Double = 0.0,
    val chefEarnings: Double = totalAmount - platformFee
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chefId: Int,
    val mealId: Int, // 0 if general review for the chef
    val reviewerName: String,
    val rating: Int,
    val comment: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chefId: Int,
    val sender: String, // "User" or "Chef"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chef_payouts")
data class ChefPayoutEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chefId: Int,
    val chefName: String,
    val paypalEmail: String,
    val amount: Double,
    val status: String = "COMPLETED", // "COMPLETED", "PROCESSING", "FAILED"
    val payoutBatchId: String,
    val note: String = "PayPal Instant Payout",
    val timestamp: Long = System.currentTimeMillis()
)


