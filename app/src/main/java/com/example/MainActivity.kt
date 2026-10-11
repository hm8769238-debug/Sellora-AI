package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.SelloraAppHeader
import com.example.ui.components.SelloraBottomBar
import com.example.ui.screens.catalog.ProductCatalogScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.generator.SalesReplyGeneratorScreen
import com.example.ui.screens.leads.LeadManagerScreen
import com.example.ui.screens.packageinfo.ProductPackageInfoScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.templates.QuickReplyTemplatesScreen
import com.example.ui.screens.tools.AIToolsScreen
import com.example.ui.screens.wizard.BusinessSetupWizardScreen
import com.example.ui.theme.SelloraTheme
import com.example.ui.viewmodel.SelloraScreen
import com.example.ui.viewmodel.SelloraViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SelloraTheme {
                SelloraApp()
            }
        }
    }
}

@Composable
fun SelloraApp(viewModel: SelloraViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val products by viewModel.allProducts.collectAsStateWithLifecycle()
    val leads by viewModel.allLeads.collectAsStateWithLifecycle()
    val templates by viewModel.allTemplates.collectAsStateWithLifecycle()
    val activities by viewModel.recentActivities.collectAsStateWithLifecycle()
    val isDemoMode by viewModel.isDemoModeActive.collectAsStateWithLifecycle()

    val isGeneratingReply by viewModel.isGenerating.collectAsStateWithLifecycle()
    val salesReplyResult by viewModel.salesReplyResult.collectAsStateWithLifecycle()
    val customerMessage by viewModel.generatorCustomerMessage.collectAsStateWithLifecycle()
    val selectedProductId by viewModel.generatorSelectedProductId.collectAsStateWithLifecycle()
    val generatorLanguage by viewModel.generatorLanguage.collectAsStateWithLifecycle()
    val generatorTone by viewModel.generatorTone.collectAsStateWithLifecycle()
    val generatorLength by viewModel.generatorLength.collectAsStateWithLifecycle()

    val activeMarketingTool by viewModel.activeMarketingTool.collectAsStateWithLifecycle()
    val marketingToolResult by viewModel.marketingToolResult.collectAsStateWithLifecycle()
    val isMarketingGenerating by viewModel.isMarketingGenerating.collectAsStateWithLifecycle()

    val userNotice by viewModel.userNotice.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle notice messages as snackbars
    LaunchedEffect(userNotice) {
        userNotice?.let { notice ->
            snackbarHostState.showSnackbar(notice)
            viewModel.clearNotice()
        }
    }

    // Predictive Back Handling
    BackHandler(enabled = currentScreen != SelloraScreen.Dashboard) {
        viewModel.navigateBack()
    }

    Scaffold(
        topBar = {
            if (currentScreen != SelloraScreen.Wizard) {
                SelloraAppHeader(
                    storeName = profile?.name ?: "Vogue Boutique",
                    category = profile?.category ?: "Online Store",
                    isDemoMode = isDemoMode,
                    onToggleDemoMode = { viewModel.toggleDemoMode(it) },
                    onOpenSettings = { viewModel.navigateTo(SelloraScreen.Settings) },
                    onOpenPackageInfo = { viewModel.navigateTo(SelloraScreen.PackageInfo) }
                )
            }
        },
        bottomBar = {
            if (currentScreen != SelloraScreen.Wizard &&
                currentScreen != SelloraScreen.Settings &&
                currentScreen != SelloraScreen.PackageInfo
            ) {
                SelloraBottomBar(
                    currentScreen = currentScreen,
                    onNavigate = { viewModel.navigateTo(it) }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                when (screen) {
                    SelloraScreen.Dashboard -> {
                        DashboardScreen(
                            profile = profile,
                            products = products,
                            leads = leads,
                            activities = activities,
                            isDemoMode = isDemoMode,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }

                    SelloraScreen.Wizard -> {
                        BusinessSetupWizardScreen(
                            currentProfile = profile,
                            onSaveProfile = { viewModel.saveBusinessProfile(it) },
                            onFinish = { viewModel.navigateTo(SelloraScreen.Dashboard) }
                        )
                    }

                    SelloraScreen.Catalog -> {
                        ProductCatalogScreen(
                            products = products,
                            defaultCurrency = profile?.currency ?: "₹ INR",
                            onSaveProduct = { viewModel.saveProduct(it) },
                            onDeleteProduct = { id, name -> viewModel.deleteProduct(id, name) },
                            onImportCsv = { viewModel.importProductsCsv(it) },
                            onExportCsv = { viewModel.exportProductsCsv() },
                            onDraftReplyForProduct = { prod ->
                                viewModel.generatorSelectedProductId.value = prod.id
                                viewModel.generatorCustomerMessage.value =
                                    "Is ${prod.name} available in ${prod.colors.ifBlank { "colors" }}, and what is the price?"
                                viewModel.navigateTo(SelloraScreen.ReplyGenerator)
                            }
                        )
                    }

                    SelloraScreen.ReplyGenerator -> {
                        SalesReplyGeneratorScreen(
                            profile = profile,
                            products = products,
                            customerMessage = customerMessage,
                            onMessageChange = { viewModel.generatorCustomerMessage.value = it },
                            selectedProductId = selectedProductId,
                            onProductSelect = { viewModel.generatorSelectedProductId.value = it },
                            language = generatorLanguage,
                            onLanguageChange = { viewModel.generatorLanguage.value = it },
                            tone = generatorTone,
                            onToneChange = { viewModel.generatorTone.value = it },
                            length = generatorLength,
                            onLengthChange = { viewModel.generatorLength.value = it },
                            isGenerating = isGeneratingReply,
                            salesReplyResult = salesReplyResult,
                            onGenerate = { viewModel.generateSalesReply() },
                            onSaveAsLead = { viewModel.saveLead(it) }
                        )
                    }

                    SelloraScreen.AITools -> {
                        AIToolsScreen(
                            profile = profile,
                            products = products,
                            activeToolName = activeMarketingTool,
                            onSelectTool = { viewModel.activeMarketingTool.value = it },
                            toolResult = marketingToolResult,
                            isGenerating = isMarketingGenerating,
                            onGenerateTool = { name, inputs ->
                                viewModel.generateMarketingToolContent(name, inputs)
                            }
                        )
                    }

                    SelloraScreen.Leads -> {
                        LeadManagerScreen(
                            leads = leads,
                            onSaveLead = { viewModel.saveLead(it) },
                            onDeleteLead = { viewModel.deleteLead(it) },
                            onUpdateStatus = { lead, status -> viewModel.updateLeadStatus(lead, status) },
                            onDraftReplyForLead = { lead -> viewModel.draftReplyForLead(lead) },
                            onExportLeadsCsv = { viewModel.exportLeadsCsv() }
                        )
                    }

                    SelloraScreen.Templates -> {
                        QuickReplyTemplatesScreen(
                            templates = templates,
                            profile = profile,
                            onSaveTemplate = { viewModel.saveTemplate(it) },
                            onDeleteTemplate = { viewModel.deleteTemplate(it) }
                        )
                    }

                    SelloraScreen.PackageInfo -> {
                        ProductPackageInfoScreen(
                            profile = profile,
                            onBack = { viewModel.navigateBack() }
                        )
                    }

                    SelloraScreen.Settings -> {
                        SettingsScreen(
                            profile = profile,
                            isDemoMode = isDemoMode,
                            onToggleDemoMode = { viewModel.toggleDemoMode(it) },
                            onSaveProfile = { viewModel.saveBusinessProfile(it) },
                            onRerunWizard = { viewModel.navigateTo(SelloraScreen.Wizard) },
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                }
            }
        }
    }
}
