package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiSalesService
import com.example.data.ai.SalesReplyResult
import com.example.data.local.SelloraDatabase
import com.example.data.model.ActivityLogEntity
import com.example.data.model.BusinessProfile
import com.example.data.model.LeadEntity
import com.example.data.model.ProductEntity
import com.example.data.model.TemplateEntity
import com.example.data.repository.SelloraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class SelloraScreen(val route: String, val title: String) {
    object Dashboard : SelloraScreen("dashboard", "Dashboard")
    object Wizard : SelloraScreen("wizard", "Setup Wizard")
    object Catalog : SelloraScreen("catalog", "Product Catalog")
    object ReplyGenerator : SelloraScreen("generator", "AI Sales Reply")
    object AITools : SelloraScreen("ai_tools", "AI Tools Hub")
    object Leads : SelloraScreen("leads", "Customer Leads")
    object Templates : SelloraScreen("templates", "Quick Replies")
    object PackageInfo : SelloraScreen("package_info", "Product Package & Setup")
    object Settings : SelloraScreen("settings", "Store Settings")
}

class SelloraViewModel(application: Application) : AndroidViewModel(application) {

    private val database = SelloraDatabase.getDatabase(application, viewModelScope)
    val repository = SelloraRepository(database)
    val aiService = GeminiSalesService()

    private val _currentScreen = MutableStateFlow<SelloraScreen>(SelloraScreen.Dashboard)
    val currentScreen: StateFlow<SelloraScreen> = _currentScreen.asStateFlow()

    private val _isDemoModeActive = MutableStateFlow(false)
    val isDemoModeActive: StateFlow<Boolean> = _isDemoModeActive.asStateFlow()

    // Screen navigation stack
    private val screenHistory = mutableListOf<SelloraScreen>(SelloraScreen.Dashboard)

    val profile: StateFlow<BusinessProfile?> = repository.getProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allProducts: StateFlow<List<ProductEntity>> = repository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLeads: StateFlow<List<LeadEntity>> = repository.getAllLeads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTemplates: StateFlow<List<TemplateEntity>> = repository.getAllTemplates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentActivities: StateFlow<List<ActivityLogEntity>> = repository.getRecentActivities()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter states
    val productSearchQuery = MutableStateFlow("")
    val productStockFilter = MutableStateFlow("All")

    val leadSearchQuery = MutableStateFlow("")
    val leadStatusFilter = MutableStateFlow("All")

    val templateCategoryFilter = MutableStateFlow("All")

    // AI Generator states
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _salesReplyResult = MutableStateFlow<SalesReplyResult?>(null)
    val salesReplyResult: StateFlow<SalesReplyResult?> = _salesReplyResult.asStateFlow()

    val generatorCustomerMessage = MutableStateFlow("Is this saree available in maroon, and what is the price?")
    val generatorSelectedProductId = MutableStateFlow<Long?>(null)
    val generatorLanguage = MutableStateFlow("English")
    val generatorTone = MutableStateFlow("Friendly & Warm")
    val generatorLength = MutableStateFlow("Standard")

    // Marketing Tools states
    val activeMarketingTool = MutableStateFlow("Product Description")
    val marketingToolInputs = MutableStateFlow<Map<String, String>>(emptyMap())
    val marketingToolResult = MutableStateFlow("")
    val isMarketingGenerating = MutableStateFlow(false)

    // User feedback
    val userNotice = MutableStateFlow<String?>(null)

    init {
        // Automatically set demo mode to true if Gemini API key is not configured
        _isDemoModeActive.value = !aiService.isApiKeyConfigured()
    }

    fun navigateTo(screen: SelloraScreen) {
        if (_currentScreen.value != screen) {
            screenHistory.add(screen)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenHistory.size > 1) {
            screenHistory.removeAt(screenHistory.lastIndex)
            _currentScreen.value = screenHistory.last()
            return true
        }
        return false
    }

    fun toggleDemoMode(enabled: Boolean) {
        _isDemoModeActive.value = enabled
        viewModelScope.launch {
            repository.logActivity(
                type = "SETTINGS_UPDATED",
                title = if (enabled) "Demo Mode Enabled" else "Live Mode Enabled",
                details = if (enabled) "AI generations will use sample catalog logic." else "AI generations will query live Gemini API.",
                isDemo = enabled
            )
        }
    }

    fun saveBusinessProfile(updated: BusinessProfile) {
        viewModelScope.launch {
            repository.saveProfile(updated.copy(isSetupCompleted = true))
            repository.logActivity(
                type = "STORE_SETUP",
                title = "Business Profile Updated",
                details = "Updated ${updated.name} (${updated.category})"
            )
            userNotice.value = "Business profile saved successfully!"
        }
    }

    fun saveProduct(product: ProductEntity) {
        viewModelScope.launch {
            if (product.id == 0L) {
                repository.insertProduct(product)
                repository.logActivity(
                    type = "PRODUCT_ADDED",
                    title = "New Product Added",
                    details = "${product.name} - ${product.currency} ${product.price}"
                )
                userNotice.value = "Product added to catalog!"
            } else {
                repository.updateProduct(product)
                repository.logActivity(
                    type = "PRODUCT_UPDATED",
                    title = "Product Updated",
                    details = "Updated ${product.name}"
                )
                userNotice.value = "Product updated successfully!"
            }
        }
    }

    fun deleteProduct(id: Long, name: String) {
        viewModelScope.launch {
            repository.deleteProduct(id)
            repository.logActivity(
                type = "PRODUCT_DELETED",
                title = "Product Deleted",
                details = "Removed $name"
            )
            userNotice.value = "Product removed from catalog."
        }
    }

    fun importProductsCsv(csvString: String): Int {
        val parsed = repository.parseProductsCsv(csvString)
        if (parsed.isNotEmpty()) {
            viewModelScope.launch {
                repository.insertProducts(parsed)
                repository.logActivity(
                    type = "CSV_IMPORT",
                    title = "CSV Products Imported",
                    details = "Added ${parsed.size} items from CSV."
                )
                userNotice.value = "Imported ${parsed.size} products successfully!"
            }
        } else {
            userNotice.value = "Could not parse products from CSV. Check column headers."
        }
        return parsed.size
    }

    fun exportProductsCsv(): String {
        return repository.exportProductsCsv(allProducts.value)
    }

    fun saveLead(lead: LeadEntity) {
        viewModelScope.launch {
            if (lead.id == 0L) {
                repository.insertLead(lead)
                repository.logActivity(
                    type = "LEAD_SAVED",
                    title = "New Customer Lead",
                    details = "${lead.customerName} - ${lead.interestedProduct}"
                )
                userNotice.value = "Customer lead created!"
            } else {
                repository.updateLead(lead)
                userNotice.value = "Lead updated successfully!"
            }
        }
    }

    fun deleteLead(id: Long) {
        viewModelScope.launch {
            repository.deleteLead(id)
            userNotice.value = "Lead deleted."
        }
    }

    fun updateLeadStatus(lead: LeadEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateLead(lead.copy(leadStatus = newStatus))
            repository.logActivity(
                type = "LEAD_STATUS",
                title = "Lead Status: $newStatus",
                details = "${lead.customerName} is now $newStatus"
            )
            userNotice.value = "Lead marked as $newStatus"
        }
    }

    fun exportLeadsCsv(): String {
        return repository.exportLeadsCsv(allLeads.value)
    }

    fun saveTemplate(template: TemplateEntity) {
        viewModelScope.launch {
            if (template.id == 0L) {
                repository.insertTemplate(template)
                userNotice.value = "Template created!"
            } else {
                repository.updateTemplate(template)
                userNotice.value = "Template updated!"
            }
        }
    }

    fun deleteTemplate(id: Long) {
        viewModelScope.launch {
            repository.deleteTemplate(id)
            userNotice.value = "Template deleted."
        }
    }

    fun generateSalesReply() {
        val currentProfile = profile.value ?: return
        val currentProd = allProducts.value.find { it.id == generatorSelectedProductId.value }

        _isGenerating.value = true
        viewModelScope.launch {
            try {
                val result = aiService.generateSalesReplies(
                    customerMessage = generatorCustomerMessage.value,
                    product = currentProd,
                    profile = currentProfile,
                    language = generatorLanguage.value,
                    tone = generatorTone.value,
                    length = generatorLength.value,
                    forceDemo = _isDemoModeActive.value
                )
                _salesReplyResult.value = result
                repository.logActivity(
                    type = "REPLY_GENERATED",
                    title = "AI Sales Reply Generated",
                    details = "Reply created for: \"${generatorCustomerMessage.value.take(35)}...\"",
                    isDemo = result.isDemoMode
                )
            } catch (e: Exception) {
                userNotice.value = "Generation error: ${e.message}"
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun generateMarketingToolContent(toolName: String, inputs: Map<String, String>) {
        val currentProfile = profile.value ?: return
        isMarketingGenerating.value = true
        viewModelScope.launch {
            try {
                val result = aiService.generateMarketingContent(
                    toolName = toolName,
                    inputData = inputs,
                    profile = currentProfile,
                    forceDemo = _isDemoModeActive.value
                )
                marketingToolResult.value = result
                repository.logActivity(
                    type = "TOOL_USED",
                    title = "$toolName Generated",
                    details = "Generated output with inputs: ${inputs.values.firstOrNull()?.take(25) ?: ""}",
                    isDemo = _isDemoModeActive.value
                )
            } catch (e: Exception) {
                userNotice.value = "Error: ${e.message}"
            } finally {
                isMarketingGenerating.value = false
            }
        }
    }

    fun draftReplyForLead(lead: LeadEntity) {
        generatorCustomerMessage.value = lead.inquiryNotes.ifBlank { "Hi, I am interested in ${lead.interestedProduct}. Is it available?" }
        val matchingProduct = allProducts.value.find { it.name.equals(lead.interestedProduct, ignoreCase = true) }
        generatorSelectedProductId.value = matchingProduct?.id
        navigateTo(SelloraScreen.ReplyGenerator)
    }

    fun clearNotice() {
        userNotice.value = null
    }
}
