package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "business_profile")
data class BusinessProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "My Boutique Store",
    val category: String = "Online Boutique",
    val country: String = "India",
    val currency: String = "₹ INR",
    val preferredLanguage: String = "English",
    val brandTone: String = "Friendly & Warm",
    val shippingPolicy: String = "Standard delivery in 3-5 business days across India. Express delivery available on request. Free shipping above ₹999.",
    val returnRefundPolicy: String = "Easy 7-day exchange or refund for unused items with original tags. Unboxing video mandatory for damage claims.",
    val contactWhatsapp: String = "+91 98765 43210",
    val contactInstagram: String = "@myboutique_official",
    val contactEmail: String = "support@myboutique.com",
    val description: String = "Curated ethnic wear, designer jewellery, and handcrafted apparel crafted with love.",
    val isSetupCompleted: Boolean = false
)

object BusinessCategories {
    val ALL = listOf(
        "Instagram Clothing Store",
        "Online Boutique",
        "Jewellery Business",
        "Cosmetics & Skincare",
        "Saree & Ethnic Wear",
        "Handmade Products",
        "Shoes & Footwear",
        "Bags & Accessories",
        "Home Decor",
        "Small E-commerce",
        "Facebook Marketplace",
        "Instagram Reseller",
        "Small D2C Brand",
        "WhatsApp Business Seller",
        "Online Gift Shop"
    )
}
