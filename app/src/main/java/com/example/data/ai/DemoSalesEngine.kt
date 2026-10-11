package com.example.data.ai

import com.example.data.model.BusinessProfile
import com.example.data.model.ProductEntity

object DemoSalesEngine {

    fun generateReply(
        customerMessage: String,
        product: ProductEntity?,
        profile: BusinessProfile,
        language: String, // "English", "Hindi (हिंदी)", "Bengali (বাংলা)"
        tone: String,     // "Friendly", "Professional", "Premium", "Casual"
        length: String    // "Short", "Standard", "Detailed"
    ): SalesReplyResult {
        val prodName = product?.name ?: "this product"
        val priceText = if (product != null) "${product.currency} ${product.price.toInt()}" else "our catalog pricing"
        val colorsText = if (!product?.colors.isNullOrBlank()) product?.colors ?: "" else "standard colors"
        val sizesText = if (!product?.availableSizes.isNullOrBlank()) product?.availableSizes ?: "" else "standard sizes"
        val stockStatus = product?.stockStatus ?: "In Stock"
        val deliveryDetails = product?.deliveryDetails?.ifBlank { profile.shippingPolicy } ?: profile.shippingPolicy
        val storeName = profile.name
        val contactNumber = profile.contactWhatsapp

        val msgLower = customerMessage.lowercase()
        val isAskingColor = msgLower.contains("color") || msgLower.contains("maroon") || msgLower.contains("colour") || msgLower.contains("shade") || msgLower.contains("লাল") || msgLower.contains("রং") || msgLower.contains("रंग")
        val isAskingPrice = msgLower.contains("price") || msgLower.contains("cost") || msgLower.contains("kitna") || msgLower.contains("rate") || msgLower.contains("দাম") || msgLower.contains("কত") || msgLower.contains("किरण")
        val isAskingCodOrShip = msgLower.contains("cod") || msgLower.contains("cash on delivery") || msgLower.contains("ship") || msgLower.contains("delivery") || msgLower.contains("ডেলিভারি") || msgLower.contains("কুরিয়ার")

        return when {
            language.contains("Hindi", ignoreCase = true) || language.contains("हिंदी") -> {
                generateHindiReply(prodName, priceText, colorsText, sizesText, stockStatus, deliveryDetails, storeName, contactNumber, isAskingColor, isAskingPrice, isAskingCodOrShip)
            }
            language.contains("Bengali", ignoreCase = true) || language.contains("বাংলা") -> {
                generateBengaliReply(prodName, priceText, colorsText, sizesText, stockStatus, deliveryDetails, storeName, contactNumber, isAskingColor, isAskingPrice, isAskingCodOrShip)
            }
            else -> {
                generateEnglishReply(prodName, priceText, colorsText, sizesText, stockStatus, deliveryDetails, storeName, contactNumber, isAskingColor, isAskingPrice, isAskingCodOrShip)
            }
        }
    }

    private fun generateEnglishReply(
        prodName: String,
        priceText: String,
        colorsText: String,
        sizesText: String,
        stockStatus: String,
        deliveryDetails: String,
        storeName: String,
        contactNumber: String,
        isAskingColor: Boolean,
        isAskingPrice: Boolean,
        isAskingCodOrShip: Boolean
    ): SalesReplyResult {
        val ready = buildString {
            append("Hello! Thank you for reaching out to $storeName. ")
            if (stockStatus == "In Stock" || stockStatus == "Low Stock") {
                append("Yes, $prodName is currently available")
                if (colorsText.isNotBlank()) append(" in $colorsText")
                append(". ")
            } else {
                append("Currently $prodName status is: $stockStatus. ")
            }
            append("The price is $priceText. ")
            if (isAskingCodOrShip) {
                append("Regarding shipping: $deliveryDetails ")
            } else {
                append("Delivery: $deliveryDetails ")
            }
            append("Sizes available: $sizesText. ")
            append("Would you like me to reserve one for you, or shall I share a quick payment link?")
        }

        val short = "Hi! Yes, $prodName is available in $colorsText at $priceText ($stockStatus). $deliveryDetails. Shall I book one for you?"

        val friendly = "Hi dear! 💕 So glad you loved our $prodName! Yes, it's available in gorgeous shades ($colorsText) for just $priceText. $deliveryDetails. Would you like me to share real customer photos or reserve your size?"

        val professional = "Dear Customer, thank you for contacting $storeName. In reference to your inquiry, $prodName is currently $stockStatus at $priceText. Available specifications: $sizesText. $deliveryDetails. Please let us know if you would like to proceed with your order."

        val premium = "Greetings from $storeName. We are delighted by your interest in our signature $prodName. Masterfully crafted and available in limited quantities ($colorsText) at $priceText. Complimentary boutique packaging and guaranteed $deliveryDetails. May our concierge assist you in securing your piece today?"

        val followUp = "Would you like to confirm your delivery address or pincode so I can check express delivery timeline for you?"

        return SalesReplyResult(
            readyToSendReply = ready,
            shortReply = short,
            friendlyReply = friendly,
            professionalReply = professional,
            premiumReply = premium,
            suggestedFollowUp = followUp,
            isDemoMode = true,
            modelUsed = "Sellora Engine (Demo / Offline Mode)",
            notice = "Demo sample response based on catalog data. Review before sending."
        )
    }

    private fun generateHindiReply(
        prodName: String,
        priceText: String,
        colorsText: String,
        sizesText: String,
        stockStatus: String,
        deliveryDetails: String,
        storeName: String,
        contactNumber: String,
        isAskingColor: Boolean,
        isAskingPrice: Boolean,
        isAskingCodOrShip: Boolean
    ): SalesReplyResult {
        val ready = "नमस्ते जी! $storeName में संपर्क करने के लिए धन्यवाद। जी हाँ, $prodName अभी उपलब्ध है ($colorsText में) और इसका मूल्य केवल $priceText है। डिलीवरी विवरण: $deliveryDetails। साइज: $sizesText। क्या मैं आपके लिए यह पीस बुक कर दूँ या पेमेंट लिंक भेजूँ?"
        val short = "नमस्ते! जी हाँ, $prodName ($colorsText) उपलब्ध है, मूल्य $priceText। $deliveryDetails। क्या आपका ऑर्डर बुक कर दें?"
        val friendly = "हेलो जी! 💕 बहुत खुशी हुई कि आपको हमारा $prodName पसंद आया! यह $colorsText में उपलब्ध है, सिर्फ $priceText में। $deliveryDetails। क्या आप आज ही इसे अपने लिए बुक करना चाहेंगी?"
        val professional = "नमस्कार, $storeName में आपका स्वागत है। आपकी पूछताछ के अनुसार $prodName ($stockStatus) का मूल्य $priceText है। उपलब्ध शेड्स: $colorsText। $deliveryDetails। कृपया ऑर्डर पुष्टि के लिए सूचित करें।"
        val premium = "प्रणाम! $storeName के एक्सक्लूसिव कलेक्शन में आपकी रुचि सराहनीय है। हमारा सिग्नेचर $prodName ($colorsText) लिमिटेड स्टॉक में $priceText पर उपलब्ध है। $deliveryDetails। क्या हम आपके लिए आज ही विशेष पार्सल तैयार करें?"
        val followUp = "कृपया अपना डिलीवरी पिनकोड बताएं ताकि हम सटीक डिलीवरी समय चेक कर सकें?"

        return SalesReplyResult(
            readyToSendReply = ready,
            shortReply = short,
            friendlyReply = friendly,
            professionalReply = professional,
            premiumReply = premium,
            suggestedFollowUp = followUp,
            isDemoMode = true,
            modelUsed = "Sellora Engine (Hindi Demo Mode)",
            notice = "AI ড্রাফট/ड्राफ्ट — গ্রাহককে পাঠানোর আগে যাচাই করুন।"
        )
    }

    private fun generateBengaliReply(
        prodName: String,
        priceText: String,
        colorsText: String,
        sizesText: String,
        stockStatus: String,
        deliveryDetails: String,
        storeName: String,
        contactNumber: String,
        isAskingColor: Boolean,
        isAskingPrice: Boolean,
        isAskingCodOrShip: Boolean
    ): SalesReplyResult {
        val ready = "নমস্কার! $storeName-এ আপনাকে স্বাগতম। হ্যাঁ, $prodName বর্তমানে এভেইলেবল আছে ($colorsText রঙে)। এটার দাম মাত্র $priceText। ডেলিভারি সংক্রান্ত তথ্য: $deliveryDetails। সাইজ: $sizesText। আপনি কি এটি বুক করতে চান, নাকি পেমেন্ট লিংক শেয়ার করবো?"
        val short = "নমস্কার! হ্যাঁ, $prodName ($colorsText) স্টকে আছে, মূল্য $priceText। $deliveryDetails। বুকিং কনফার্ম করতে চান?"
        val friendly = "হ্যালো ডিয়ার! 💕 আমাদের $prodName পছন্দ করার জন্য অনেক ধন্যবাদ! খুব সুন্দর শেডগুলোতে ($colorsText) পাওয়া যাচ্ছে, দাম মাত্র $priceText। $deliveryDetails। আপনার জন্য কি একটা পিস সরিয়ে রাখবো?"
        val professional = "শ্রদ্ধেয় ক্রেতা, $storeName-এ যোগাযোগের জন্য ধন্যবাদ। আপনার অনুসন্ধান অনুযায়ী $prodName স্টকে আছে, দাম $priceText। উপলব্ধ সাইজ: $sizesText। $deliveryDetails। অর্ডার নিশ্চিত করতে অনুগ্রহ করে জানান।"
        val premium = "নমস্কার! $storeName-এর স্পেশাল কালেকশনে আগ্রহ দেখানোর জন্য ধন্যবাদ। আমাদের এক্সক্লুসিভ $prodName লিমিটেড কোয়ান্টিটিতে মাত্র $priceText মূল্যে উপলব্ধ। $deliveryDetails। আপনার অর্ডারটি কি আমরা আজই কনফার্ম করবো?"
        val followUp = "আপনার এলাকার পিনকোড বা ডেলিভারি ঠিকানা দিলে আমরা দ্রুততম ডেলিভারি টাইম জানিয়ে দিতে পারবো।"

        return SalesReplyResult(
            readyToSendReply = ready,
            shortReply = short,
            friendlyReply = friendly,
            professionalReply = professional,
            premiumReply = premium,
            suggestedFollowUp = followUp,
            isDemoMode = true,
            modelUsed = "Sellora Engine (Bengali Demo Mode)",
            notice = "ডেমো ড্রাফট — গ্রাহককে পাঠানোর পূর্বে তথ্য মিলিয়ে নিন।"
        )
    }
}
