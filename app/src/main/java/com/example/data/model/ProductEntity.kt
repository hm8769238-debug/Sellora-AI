package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val price: Double,
    val currency: String = "₹ INR",
    val availableSizes: String = "Free Size",
    val colors: String = "",
    val stockStatus: String = "In Stock", // "In Stock", "Low Stock", "Pre-order", "Out of Stock"
    val deliveryDetails: String = "Delivered in 3-5 days. COD available.",
    val faqs: String = "",
    val description: String = "",
    val sku: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
