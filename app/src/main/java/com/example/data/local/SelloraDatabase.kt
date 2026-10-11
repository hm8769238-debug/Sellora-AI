package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ActivityLogEntity
import com.example.data.model.BusinessProfile
import com.example.data.model.LeadEntity
import com.example.data.model.ProductEntity
import com.example.data.model.TemplateEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BusinessProfile::class,
        ProductEntity::class,
        LeadEntity::class,
        TemplateEntity::class,
        ActivityLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SelloraDatabase : RoomDatabase() {
    abstract fun businessProfileDao(): BusinessProfileDao
    abstract fun productDao(): ProductDao
    abstract fun leadDao(): LeadDao
    abstract fun templateDao(): TemplateDao
    abstract fun activityDao(): ActivityDao

    companion object {
        @Volatile
        private var INSTANCE: SelloraDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SelloraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SelloraDatabase::class.java,
                    "sellora_ai_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: SelloraDatabase) {
            // 1. Initial Business Profile
            database.businessProfileDao().saveProfile(
                BusinessProfile(
                    id = 1,
                    name = "Vogue Elegance Boutique",
                    category = "Online Boutique",
                    country = "India",
                    currency = "₹ INR",
                    preferredLanguage = "English",
                    brandTone = "Friendly & Warm",
                    shippingPolicy = "Free shipping across India on orders above ₹999. Standard delivery takes 3 to 5 business days.",
                    returnRefundPolicy = "7-day hassle-free exchange/return policy for unused items with tags attached. Please take a parcel opening video.",
                    contactWhatsapp = "+91 98765 43210",
                    contactInstagram = "@vogue_elegance_in",
                    contactEmail = "hello@vogueelegance.shop",
                    description = "Premium ethnic wear, designer sarees, and festive partywear curated for discerning women.",
                    isSetupCompleted = false
                )
            )

            // 2. Starter realistic products
            val sampleProducts = listOf(
                ProductEntity(
                    name = "Royal Pure Silk Banarasi Saree",
                    category = "Saree & Ethnic Wear",
                    price = 2499.0,
                    currency = "₹ INR",
                    availableSizes = "Free Size (5.5m + 0.8m Blouse)",
                    colors = "Maroon, Royal Blue, Emerald Green, Wine",
                    stockStatus = "In Stock",
                    deliveryDetails = "Dispatched within 24 hrs. Metro 2-3 days, rest of India 4-5 days. COD available.",
                    faqs = "Dry clean only. Unstitched pure brocade blouse piece included. Genuine woven zari work.",
                    description = "Handwoven Banarasi silk saree with intricate golden zari floral jaal work. Includes matching unstitched blouse piece.",
                    sku = "VE-BAN-001"
                ),
                ProductEntity(
                    name = "Kundan Choker Necklace & Earring Set",
                    category = "Jewellery Business",
                    price = 1250.0,
                    currency = "₹ INR",
                    availableSizes = "Adjustable Dori",
                    colors = "Ruby Red, Mint Green, White Pearl",
                    stockStatus = "In Stock",
                    deliveryDetails = "Shipped in bubble protective hard case. 3-4 days delivery.",
                    faqs = "Keep away from perfume and direct moisture. Wipe with soft cloth after use. Hypoallergenic brass base.",
                    description = "Handcrafted artisan Kundan choker embellished with faux pearl drops and matching statement jhumkas.",
                    sku = "VE-JW-KUN02"
                ),
                ProductEntity(
                    name = "Handcrafted Vegan Leather Tote Bag",
                    category = "Bags & Accessories",
                    price = 1899.0,
                    currency = "₹ INR",
                    availableSizes = "Medium (35cm x 28cm x 12cm)",
                    colors = "Tan Brown, Classic Black, Olive Green",
                    stockStatus = "Low Stock",
                    deliveryDetails = "Standard delivery in 3-5 days. Express available.",
                    faqs = "Fits 13-inch laptop easily. Has 2 inner slip pockets and 1 zipper compartment. Water-resistant wipeable lining.",
                    description = "Durable cruelty-free vegan leather tote with reinforced shoulder straps and premium antique brass hardware.",
                    sku = "VE-BAG-TOT03"
                ),
                ProductEntity(
                    name = "Embroidered Georgette Anarkali Kurta Set",
                    category = "Online Boutique",
                    price = 3299.0,
                    currency = "₹ INR",
                    availableSizes = "S (36), M (38), L (40), XL (42), XXL (44)",
                    colors = "Dusty Rose, Powder Blue, Ivory White",
                    stockStatus = "In Stock",
                    deliveryDetails = "Dispatches in 2 days. 3-5 business days transit.",
                    faqs = "Comes with matching pant and chiffon dupatta. Butter crepe soft inner lining included throughout.",
                    description = "Flared 3-piece designer Anarkali suit set with Lucknowi Chikankari inspired thread and sequin embroidery.",
                    sku = "VE-KUR-ANAR04"
                )
            )
            database.productDao().insertProducts(sampleProducts)

            // 3. Pre-loaded Quick Reply Templates
            val sampleTemplates = listOf(
                TemplateEntity(
                    title = "Product Availability",
                    category = "Availability",
                    content = "Hello! Yes, {product_name} is in stock and ready to ship. We have it available in {colors} for just {price}. Would you like to reserve yours before stock runs out?",
                    isDefault = true
                ),
                TemplateEntity(
                    title = "Price Inquiry",
                    category = "Pricing",
                    content = "Hi dear! {product_name} is priced at {price} with taxes included. Quality is guaranteed with premium finishing. Shall I share more close-up photos?",
                    isDefault = true
                ),
                TemplateEntity(
                    title = "Size & Color Inquiry",
                    category = "Size/Color",
                    content = "Hi! This piece is available in sizes {sizes} and shades {colors}. Please let me know your usual bust/waist measurement so I can recommend the perfect fit!",
                    isDefault = true
                ),
                TemplateEntity(
                    title = "Shipping & Courier Charges",
                    category = "Shipping",
                    content = "Hello! We offer FREE shipping on prepaid orders across India! For Cash on Delivery (COD), there is a nominal fee of ₹60. What is your delivery pin code?",
                    isDefault = true
                ),
                TemplateEntity(
                    title = "Delivery Timeline",
                    category = "Delivery",
                    content = "Orders are dispatched within 24 to 48 hours. Metro cities receive delivery in 2-3 business days, and other locations in 4-6 days. You will receive live tracking as soon as it ships!",
                    isDefault = true
                ),
                TemplateEntity(
                    title = "Payment Methods (UPI/COD)",
                    category = "Payment",
                    content = "We accept Google Pay, PhonePe, Paytm, all UPI IDs, Debit/Credit Cards, and Net Banking. COD (Cash on Delivery) is also available with advance verification. How would you prefer to pay?",
                    isDefault = true
                ),
                TemplateEntity(
                    title = "Return & Exchange Policy",
                    category = "Return",
                    content = "We have an easy 7-day exchange policy for size or defects. Just make sure the original tags and packaging are intact, and keep a quick unboxing video handy. Your satisfaction is our priority!",
                    isDefault = true
                ),
                TemplateEntity(
                    title = "Refund Procedure",
                    category = "Refund",
                    content = "Once the returned parcel reaches our studio and passes our quality check, your refund will be processed directly to your UPI/bank account within 24-48 hours.",
                    isDefault = true
                ),
                TemplateEntity(
                    title = "Order Status Inquiry",
                    category = "OrderStatus",
                    content = "Could you please share your order number or registered phone number? I will immediately check the live dispatch status with our courier partner and update you!",
                    isDefault = true
                ),
                TemplateEntity(
                    title = "Discount & Bulk Offer",
                    category = "Discount",
                    content = "Our pricing is already kept at fair direct-artisan rates, but if you order 2 or more items today, we'd love to give you an exclusive 10% combo discount! Let me know which items you're choosing.",
                    isDefault = true
                ),
                TemplateEntity(
                    title = "Order Thank You Message",
                    category = "ThankYou",
                    content = "Thank you so much for shopping with us at {business_name}! Your support means the world to our small business. We will send you the courier tracking link once dispatched! ❤️",
                    isDefault = true
                ),
                TemplateEntity(
                    title = "Friendly Follow-Up",
                    category = "FollowUp",
                    content = "Hi dear! Just checking in to see if you had any questions regarding {product_name}? We only have a few pieces left in stock this week. Happy to help you with booking!",
                    isDefault = true
                )
            )
            database.templateDao().insertTemplates(sampleTemplates)

            // 4. Starter Sample Leads
            val sampleLeads = listOf(
                LeadEntity(
                    customerName = "Pooja Sharma",
                    contactInfo = "+91 98201 11223 (WhatsApp)",
                    interestedProduct = "Royal Pure Silk Banarasi Saree",
                    inquiryNotes = "Asked about maroon color availability and COD to Delhi.",
                    leadStatus = "Interested",
                    nextFollowUpDate = "Tomorrow 11 AM",
                    notes = "Preferred maroon for cousin's wedding next month."
                ),
                LeadEntity(
                    customerName = "Ananya Roy",
                    contactInfo = "@ananya_fashionista (IG)",
                    interestedProduct = "Kundan Choker Necklace",
                    inquiryNotes = "Inquired if earrings can be customized with green beads.",
                    leadStatus = "Follow-up Needed",
                    nextFollowUpDate = "In 2 days",
                    notes = "Waiting for payday this weekend."
                )
            )
            sampleLeads.forEach { database.leadDao().insertLead(it) }

            // 5. Initial Activity
            database.activityDao().insertActivity(
                ActivityLogEntity(
                    type = "SYSTEM",
                    title = "Sellora AI Initialized",
                    details = "Business store database and starter templates loaded successfully.",
                    isDemo = true
                )
            )
        }
    }
}
