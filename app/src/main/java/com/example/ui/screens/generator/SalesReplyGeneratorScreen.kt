package com.example.ui.screens.generator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.SalesReplyResult
import com.example.data.model.BusinessProfile
import com.example.data.model.LeadEntity
import com.example.data.model.ProductEntity
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoyalPurple

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SalesReplyGeneratorScreen(
    profile: BusinessProfile?,
    products: List<ProductEntity>,
    customerMessage: String,
    onMessageChange: (String) -> Unit,
    selectedProductId: Long?,
    onProductSelect: (Long?) -> Unit,
    language: String,
    onLanguageChange: (String) -> Unit,
    tone: String,
    onToneChange: (String) -> Unit,
    length: String,
    onLengthChange: (String) -> Unit,
    isGenerating: Boolean,
    salesReplyResult: SalesReplyResult?,
    onGenerate: () -> Unit,
    onSaveAsLead: (LeadEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var copiedVariantKey by remember { mutableStateOf<String?>(null) }
    var productDropdownExpanded by remember { mutableStateOf(false) }
    var selectedTabIdx by remember { mutableIntStateOf(0) }

    val selectedProduct = products.find { it.id == selectedProductId }

    val promptExamples = listOf(
        "Is this saree available in maroon, and what is the price?",
        "Do you provide Cash on Delivery (COD) to Kolkata?",
        "Can I get any discount if I buy 2 pieces together?",
        "What is your return policy if the size does not fit?"
    )

    val languages = listOf("English", "Hindi (हिंदी)", "Bengali (বাংলা)")
    val tones = listOf("Friendly", "Professional", "Premium", "Casual")
    val lengths = listOf("Short", "Standard", "Detailed")

    fun copyToClipboard(text: String, key: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Sellora Reply", text)
        clipboard.setPrimaryClip(clip)
        copiedVariantKey = key
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("sales_reply_generator_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "AI Sales Reply Generator",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = RoyalPurple.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Core Engine",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = RoyalPurple,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "Grounded in your real catalog specs, prices, and shipping policies. Never invents fake information.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        // Customer Inquiry Input Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Customer's Inquiry / Message *",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    OutlinedTextField(
                        value = customerMessage,
                        onValueChange = onMessageChange,
                        placeholder = { Text("e.g. Is this available in maroon, and what is the delivery time?") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("customer_message_input"),
                        minLines = 3,
                        maxLines = 6
                    )

                    // Quick Prompt Chips
                    Text(
                        text = "Or pick sample inquiry:",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline)
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        promptExamples.forEach { prompt ->
                            FilterChip(
                                selected = customerMessage == prompt,
                                onClick = { onMessageChange(prompt) },
                                label = { Text(prompt.take(34) + "...", fontSize = 11.sp) }
                            )
                        }
                    }

                    // Product Selector
                    Text(
                        text = "Grounded Product Catalog Match:",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { productDropdownExpanded = true }
                                .testTag("select_product_dropdown_trigger"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = selectedProduct?.name ?: "General Store Inquiry (No specific item)",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        maxLines = 1
                                    )
                                    if (selectedProduct != null) {
                                        Text(
                                            text = "${selectedProduct.currency} ${selectedProduct.price.toInt()} • ${selectedProduct.stockStatus}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        )
                                    }
                                }
                                Icon(imageVector = Icons.Default.ExpandMore, contentDescription = null)
                            }
                        }

                        DropdownMenu(
                            expanded = productDropdownExpanded,
                            onDismissRequest = { productDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            DropdownMenuItem(
                                text = { Text("None / General Store Policies") },
                                onClick = {
                                    onProductSelect(null)
                                    productDropdownExpanded = false
                                }
                            )
                            products.forEach { prod ->
                                DropdownMenuItem(
                                    text = { Text("${prod.name} (${prod.currency} ${prod.price.toInt()})") },
                                    onClick = {
                                        onProductSelect(prod.id)
                                        productDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Language Chips
                    Text(
                        text = "Response Language (English, Hindi, Bengali):",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        languages.forEach { lang ->
                            FilterChip(
                                selected = language == lang,
                                onClick = { onLanguageChange(lang) },
                                label = { Text(lang, fontSize = 12.sp) },
                                modifier = Modifier.testTag("lang_chip_${lang.take(2)}")
                            )
                        }
                    }

                    // Tone & Length
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Desired Tone:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                tones.forEach { t ->
                                    FilterChip(
                                        selected = tone == t,
                                        onClick = { onToneChange(t) },
                                        label = { Text(t, fontSize = 10.sp) }
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Reply Length:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                lengths.forEach { l ->
                                    FilterChip(
                                        selected = length == l,
                                        onClick = { onLengthChange(l) },
                                        label = { Text(l, fontSize = 10.sp) }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Generate Button
                    Button(
                        onClick = onGenerate,
                        enabled = !isGenerating && customerMessage.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("generate_sales_reply_button")
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Synthesizing Grounded Replies...")
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Sales Replies", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Generated Results Container
        salesReplyResult?.let { result ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("generated_replies_container"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Notice banner
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = result.notice,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }

                        // Variants Tabs
                        val tabs = listOf(
                            "Ready to Send" to result.readyToSendReply,
                            "Short" to result.shortReply,
                            "Friendly" to result.friendlyReply,
                            "Professional" to result.professionalReply,
                            "Premium" to result.premiumReply
                        )

                        TabRow(
                            selectedTabIndex = selectedTabIdx,
                            containerColor = Color.Transparent,
                            contentColor = RoyalPurple
                        ) {
                            tabs.forEachIndexed { idx, (title, _) ->
                                Tab(
                                    selected = selectedTabIdx == idx,
                                    onClick = { selectedTabIdx = idx },
                                    text = { Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                                )
                            }
                        }

                        val activeText = tabs[selectedTabIdx].second
                        val activeLabel = tabs[selectedTabIdx].first

                        // Active Variant Card
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = activeText,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        lineHeight = 22.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val isCopied = copiedVariantKey == activeLabel
                                    Button(
                                        onClick = { copyToClipboard(activeText, activeLabel) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isCopied) EmeraldGreen else RoyalPurple
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("copy_reply_button_$selectedTabIdx")
                                    ) {
                                        Icon(
                                            imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (isCopied) "Copied!" else "Copy to Clipboard", fontSize = 12.sp)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            val newLead = LeadEntity(
                                                customerName = "Inquiry: ${customerMessage.take(20)}...",
                                                contactInfo = profile?.contactWhatsapp ?: "",
                                                interestedProduct = selectedProduct?.name ?: "Store Catalog",
                                                inquiryNotes = customerMessage,
                                                leadStatus = "New",
                                                nextFollowUpDate = "Today",
                                                notes = "AI Reply drafted: $activeLabel"
                                            )
                                            onSaveAsLead(newLead)
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("save_as_lead_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Save as Lead", fontSize = 11.sp)
                                    }
                                }
                            }
                        }

                        // Suggested Follow-Up Closing Question
                        if (result.suggestedFollowUp.isNotBlank()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = RoyalPurple,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Suggested Sales Closing Question:",
                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = result.suggestedFollowUp,
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurface)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedButton(
                                        onClick = { copyToClipboard(result.suggestedFollowUp, "follow_up") },
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(if (copiedVariantKey == "follow_up") "Copied!" else "Copy Follow-up Question", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
