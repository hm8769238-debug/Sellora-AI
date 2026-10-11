package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quick_templates")
data class TemplateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // "Availability", "Pricing", "Size/Color", "Shipping", "Delivery", "Payment", "Return", "Refund", "OrderStatus", "Discount", "ThankYou", "FollowUp", "Custom"
    val content: String,
    val isDefault: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
