package com.example.data.repository

import com.example.data.local.SelloraDatabase
import com.example.data.model.ActivityLogEntity
import com.example.data.model.BusinessProfile
import com.example.data.model.LeadEntity
import com.example.data.model.ProductEntity
import com.example.data.model.TemplateEntity
import kotlinx.coroutines.flow.Flow

class SelloraRepository(private val database: SelloraDatabase) {
    // Profile
    fun getProfile(): Flow<BusinessProfile?> = database.businessProfileDao().getProfile()
    suspend fun getProfileSync(): BusinessProfile? = database.businessProfileDao().getProfileSync()
    suspend fun saveProfile(profile: BusinessProfile) = database.businessProfileDao().saveProfile(profile)

    // Products
    fun getAllProducts(): Flow<List<ProductEntity>> = database.productDao().getAllProducts()
    suspend fun getAllProductsSync(): List<ProductEntity> = database.productDao().getAllProductsSync()
    suspend fun getProductById(id: Long): ProductEntity? = database.productDao().getProductById(id)
    fun searchProducts(query: String): Flow<List<ProductEntity>> = database.productDao().searchProducts(query)
    suspend fun insertProduct(product: ProductEntity): Long = database.productDao().insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = database.productDao().updateProduct(product)
    suspend fun deleteProduct(id: Long) = database.productDao().deleteById(id)
    suspend fun insertProducts(products: List<ProductEntity>) = database.productDao().insertProducts(products)

    // Leads
    fun getAllLeads(): Flow<List<LeadEntity>> = database.leadDao().getAllLeads()
    suspend fun getAllLeadsSync(): List<LeadEntity> = database.leadDao().getAllLeadsSync()
    fun searchLeads(query: String): Flow<List<LeadEntity>> = database.leadDao().searchLeads(query)
    fun getLeadsByStatus(status: String): Flow<List<LeadEntity>> = database.leadDao().getLeadsByStatus(status)
    suspend fun insertLead(lead: LeadEntity): Long = database.leadDao().insertLead(lead)
    suspend fun updateLead(lead: LeadEntity) = database.leadDao().updateLead(lead)
    suspend fun deleteLead(id: Long) = database.leadDao().deleteById(id)

    // Templates
    fun getAllTemplates(): Flow<List<TemplateEntity>> = database.templateDao().getAllTemplates()
    fun getTemplatesByCategory(category: String): Flow<List<TemplateEntity>> = database.templateDao().getTemplatesByCategory(category)
    suspend fun insertTemplate(template: TemplateEntity): Long = database.templateDao().insertTemplate(template)
    suspend fun updateTemplate(template: TemplateEntity) = database.templateDao().updateTemplate(template)
    suspend fun deleteTemplate(id: Long) = database.templateDao().deleteById(id)

    // Activities
    fun getRecentActivities(): Flow<List<ActivityLogEntity>> = database.activityDao().getRecentActivities()
    suspend fun logActivity(type: String, title: String, details: String, isDemo: Boolean = false) {
        database.activityDao().insertActivity(
            ActivityLogEntity(
                type = type,
                title = title,
                details = details,
                isDemo = isDemo
            )
        )
    }

    // CSV Utilities
    fun exportProductsCsv(products: List<ProductEntity>): String {
        val sb = StringBuilder()
        sb.append("SKU,Name,Category,Price,Currency,Available Sizes,Colors,Stock Status,Delivery Details,FAQs,Description\n")
        products.forEach { p ->
            sb.append("\"${escapeCsv(p.sku)}\",")
            sb.append("\"${escapeCsv(p.name)}\",")
            sb.append("\"${escapeCsv(p.category)}\",")
            sb.append("${p.price},")
            sb.append("\"${escapeCsv(p.currency)}\",")
            sb.append("\"${escapeCsv(p.availableSizes)}\",")
            sb.append("\"${escapeCsv(p.colors)}\",")
            sb.append("\"${escapeCsv(p.stockStatus)}\",")
            sb.append("\"${escapeCsv(p.deliveryDetails)}\",")
            sb.append("\"${escapeCsv(p.faqs)}\",")
            sb.append("\"${escapeCsv(p.description)}\"\n")
        }
        return sb.toString()
    }

    fun exportLeadsCsv(leads: List<LeadEntity>): String {
        val sb = StringBuilder()
        sb.append("Customer Name,Contact Info,Interested Product,Status,Next Follow-up,Inquiry Notes,General Notes\n")
        leads.forEach { l ->
            sb.append("\"${escapeCsv(l.customerName)}\",")
            sb.append("\"${escapeCsv(l.contactInfo)}\",")
            sb.append("\"${escapeCsv(l.interestedProduct)}\",")
            sb.append("\"${escapeCsv(l.leadStatus)}\",")
            sb.append("\"${escapeCsv(l.nextFollowUpDate)}\",")
            sb.append("\"${escapeCsv(l.inquiryNotes)}\",")
            sb.append("\"${escapeCsv(l.notes)}\"\n")
        }
        return sb.toString()
    }

    fun parseProductsCsv(csvContent: String): List<ProductEntity> {
        val lines = csvContent.lines().filter { it.isNotBlank() }
        if (lines.size <= 1) return emptyList()

        val parsed = mutableListOf<ProductEntity>()
        // Skip header
        for (i in 1 until lines.size) {
            val line = lines[i]
            val tokens = parseCsvLine(line)
            if (tokens.size >= 2) {
                val sku = tokens.getOrNull(0) ?: ""
                val name = tokens.getOrNull(1) ?: "Imported Item"
                val category = tokens.getOrNull(2) ?: "General"
                val price = tokens.getOrNull(3)?.toDoubleOrNull() ?: 999.0
                val currency = tokens.getOrNull(4) ?: "₹ INR"
                val sizes = tokens.getOrNull(5) ?: "Standard"
                val colors = tokens.getOrNull(6) ?: ""
                val stock = tokens.getOrNull(7) ?: "In Stock"
                val delivery = tokens.getOrNull(8) ?: "3-5 days delivery"
                val faqs = tokens.getOrNull(9) ?: ""
                val desc = tokens.getOrNull(10) ?: ""

                parsed.add(
                    ProductEntity(
                        name = name,
                        category = category,
                        price = price,
                        currency = currency,
                        availableSizes = sizes,
                        colors = colors,
                        stockStatus = stock,
                        deliveryDetails = delivery,
                        faqs = faqs,
                        description = desc,
                        sku = sku
                    )
                )
            }
        }
        return parsed
    }

    private fun escapeCsv(value: String): String {
        return value.replace("\"", "\"\"")
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val cur = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length && line[i + 1] == '\"') {
                    cur.append('\"')
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            } else if (c == ',' && !inQuotes) {
                result.add(cur.toString().trim())
                cur.clear()
            } else {
                cur.append(c)
            }
            i++
        }
        result.add(cur.toString().trim())
        return result
    }
}
