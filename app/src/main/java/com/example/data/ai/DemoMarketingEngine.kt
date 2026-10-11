package com.example.data.ai

import com.example.data.model.BusinessProfile

object DemoMarketingEngine {

    fun generateContent(
        toolName: String,
        data: Map<String, String>,
        profile: BusinessProfile
    ): String {
        val store = profile.name
        val currency = profile.currency
        val pName = data["product_name"] ?: "Our Featured Collection"
        val price = data["price"] ?: "1,499"
        val features = data["features"] ?: "Premium fabric, artisanal craftsmanship, soft touch finish"
        val occasion = data["occasion"] ?: "Festive Season"

        return when (toolName) {
            "Product Description" -> """
✨ Introducing the $pName by $store ✨

Elevate your everyday style with our exclusively designed $pName. Designed with timeless elegance and meticulous attention to detail, this piece is crafted for those who value authenticity and comfort.

🌟 Key Highlights:
• $features
• Tailored fit for effortless silhouette
• Direct-from-source artisan crafting
• Easy care and enduring durability

💰 Price: $currency $price
📦 Shipping: ${profile.shippingPolicy}
🛡️ Guarantee: ${profile.returnRefundPolicy}

📲 DM us or WhatsApp ${profile.contactWhatsapp} to order yours today! Limited stock available.
            """.trimIndent()

            "Instagram Caption" -> """
Obsessed with the details? So are we. 💫

Meet our bestselling $pName — designed to turn heads and keep you feeling effortlessly chic all day long. Whether you're dressing up for a celebration or adding glamour to your day, this is your new wardrobe staple.

✨ "$features"

👉 Tap the link in our bio or DM us "$pName" to grab yours before it sells out!
💬 Drop a ❤️ in the comments if you’d style this!

#${store.replace(" ", "")} #OOTD #BoutiqueStyle #ShopSmall #HandcraftedFashion #FestiveGlam
            """.trimIndent()

            "Hashtag Suggestions" -> """
🏷️ High-Reach Trending Tags (Top Visibility):
#ShopLocal #OnlineBoutique #FestiveWear #EthnicFashion #InstaShopIndia #HandcraftedLuxury

🎯 Niche & Audience Community Tags (High Engagement):
#${profile.category.replace(" ", "").replace("&", "")} #${store.replace(" ", "")} #DesiFashionBlogger #SareeLove #ArtisanMade #D2CIndia

🔥 Buyer-Intent Conversion Tags (Sales Focused):
#OrderNow #CODavailable #DMtoOrder #WorldwideShipping #FestiveSale #AffordableLuxury
            """.trimIndent()

            "Festival Offer Message" -> """
🎉 $occasion SPECIAL CELEBRATION SALE! 🎉

Dear VIP Customer, celebrate the joyous festival of $occasion with exclusive rewards from $store!

🌟 FESTIVE PERKS:
• Flat 10% OFF on all orders above $currency 1,999!
• FREE Express Shipping across India
• Complimentary gift packaging on every order

Featured Star: $pName at just $currency $price!

Use Code: FESTIVE10 at checkout or WhatsApp us at ${profile.contactWhatsapp} with your wishlist.
Offer valid while stocks last! 🪔✨
            """.trimIndent()

            "Customer FAQ Generator" -> """
❓ Top Questions & Answers for $pName:

Q1: What is the exact material and care instructions?
A: Crafted from $features. For best longevity, gentle hand wash or dry clean recommended.

Q2: Is Cash on Delivery (COD) available for this item?
A: Yes! We provide Cash on Delivery across most pin codes with standard OTP confirmation.

Q3: What if the size doesn't fit?
A: No worries at all! We offer a friendly 7-day exchange window. Just drop us a message on ${profile.contactWhatsapp}.

Q4: How soon will I receive my parcel?
A: Orders are dispatched within 24-48 hours. Metro deliveries arrive in 2-3 days, and all other areas in 4-6 days.
            """.trimIndent()

            "Shipping & Return Reply" -> """
Hi there! Thank you for inquiring about our delivery and returns at $store.

🚚 SHIPPING:
${profile.shippingPolicy}
We partner with verified couriers (Bluedart, Delhivery, DTDC) with live SMS & WhatsApp tracking links.

🔄 RETURNS & EXCHANGES:
${profile.returnRefundPolicy}
Your peace of mind is guaranteed. If you need any assistance, our helpline is always open at ${profile.contactWhatsapp}!
            """.trimIndent()

            "Abandoned Inquiry Follow-up" -> """
Hi dear! 💕

We noticed you were eyeing our gorgeous $pName recently. We only have 2 pieces remaining in this batch, and we didn't want you to miss out!

Would you like us to hold one piece under your name for the next 24 hours? 

If you had any doubts regarding sizing, fabric, or delivery timeline, simply reply here—we're happy to share close-up video clips!

Warmly,
Team $store
            """.trimIndent()

            "Customer Objection Response" -> """
[Addressing: "Is this price justified / seems expensive"]

Hi! We completely understand wanting the best value for your hard-earned money. 

What makes our $pName truly unique is that it is not mass-produced plastic or cheap synthetic; it features $features with double-reinforced stitching and artisan finishing. Our clients regularly share that it outlasts 3 cheaper alternatives.

Plus, you get our complete guarantee: ${profile.returnRefundPolicy}. Would you like me to share real unedited photos and customer reviews so you can see the quality yourself?
            """.trimIndent()

            "Upselling & Cross-selling" -> """
✨ PERFECT PAIRINGS & COMBO DEALS ✨

Customers who loved our $pName also paired it with:
1. Matching Handcrafted Accessories (Save ₹200 when bought together!)
2. Premium Cotton Slip / Inner Liner (₹350)
3. Hand-embroidered Potli Clutch (Festive Combo)

🎁 COMBO SPECIAL: Add any second item to your cart today and unlock FREE shipping + extra 5% bundle discount!
            """.trimIndent()

            "Product Comparison" -> """
Hi! Both items are customer favorites, but here is a quick guide to help you choose what suits you best:

🔹 Option A ($pName): Best for statement elegance and special occasions. Features $features. Priced at $currency $price.
🔹 Option B (Classic Alternative): Lighter for daily casual wear with minimal embellishments.

If you are dressing for a family wedding or festive event, $pName is definitely the showstopper choice! Which vibe are you looking for?
            """.trimIndent()

            "Promotional Email Draft" -> """
Subject: Something special for your wardrobe, from $store 💌

Hi [First Name],

We have been curating something extra special in our design studio, and it has finally dropped!

Meet the $pName ($currency $price).
Crafted with $features, it is designed to bring you pure elegance and comfort.

Why you'll love it:
• Authentic artisan craft
• Direct doorstep dispatch
• 7-day hassle-free exchanges

👉 Click here to explore the full collection: [Shop Link]
Have questions? Reply directly to this email or chat with us on WhatsApp at ${profile.contactWhatsapp}.

With gratitude,
The $store Team
            """.trimIndent()

            "WhatsApp Broadcast Message" -> """
🌟 EXCLUSIVE VIP DROP: $store 🌟

Hey gorgeous! 👋

Our most-requested $pName is finally back in stock! 

🏷️ Special Price: $currency $price
💎 Highlight: $features
🚚 Dispatched in 24 hours (COD Available)

⚠️ Note: Due to artisan handcrafting, we only have 15 pieces ready for immediate shipping this week.

Reply "BOOK $pName" right here to confirm your order before stock runs out! ❤️
            """.trimIndent()

            "Customer Review Reply" -> """
[Option 1 - 5-Star Gratitude Response]
"Dear [Customer Name], thank you so much for this heartfelt 5-star review! ❤️ It brings our entire artisan team immense joy to know you loved your $pName. Looking forward to styling you again soon at $store!"

[Option 2 - Feedback / Query Resolution Response]
"Dear [Customer Name], thank you for sharing your honest feedback. We are truly sorry to hear the size didn't meet your expectations. We stand by our promise—please ping us on WhatsApp at ${profile.contactWhatsapp} and we will immediately arrange a doorstep exchange for you at zero extra charge! Your satisfaction is our priority."
            """.trimIndent()

            else -> "Generated marketing output for $toolName:\n\n$pName - $currency $price\n$features\nCrafted with pride by $store."
        }
    }
}
