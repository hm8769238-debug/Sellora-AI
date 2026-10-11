package com.example.ui.screens.tools

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Festival
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessProfile
import com.example.data.model.ProductEntity
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoyalPurple

data class ToolDefinition(
    val name: String,
    val description: String,
    val icon: ImageVector,
    val defaultProductField: String,
    val defaultFeatureField: String,
    val extraFieldLabel: String? = null,
    val defaultExtraValue: String = ""
)

val MARKETING_TOOLS = listOf(
    ToolDefinition(
        name = "Product Description",
        description = "Craft high-converting, persuasive catalog copy.",
        icon = Icons.Default.Description,
        defaultProductField = "Royal Silk Banarasi Saree",
        defaultFeatureField = "Pure Katan silk, antique gold zari jaal, woven by master artisans, includes blouse piece"
    ),
    ToolDefinition(
        name = "Instagram Caption",
        description = "Engaging captions with hook, story, and call-to-action.",
        icon = Icons.Default.Campaign,
        defaultProductField = "Kundan Choker Set",
        defaultFeatureField = "Artisan handcrafted, ruby teardrop accents, bridal festive collection"
    ),
    ToolDefinition(
        name = "Hashtag Suggestions",
        description = "Optimized tags categorized by reach, community, and buyer intent.",
        icon = Icons.Default.Tag,
        defaultProductField = "Handcrafted Vegan Leather Tote",
        defaultFeatureField = "Cruelty-free, laptop friendly, modern office chic"
    ),
    ToolDefinition(
        name = "Festival Offer Message",
        description = "Festive promo announcements for Diwali, Eid, Durga Puja, etc.",
        icon = Icons.Default.Festival,
        defaultProductField = "Festive Anarkali Suit Set",
        defaultFeatureField = "Lucknowi thread work, georgette fabric",
        extraFieldLabel = "Festival / Occasion",
        defaultExtraValue = "Diwali Festive Glow Sale"
    ),
    ToolDefinition(
        name = "Customer FAQ Generator",
        description = "Instant answers for sizing, materials, and care instructions.",
        icon = Icons.Default.HelpOutline,
        defaultProductField = "Designer Embroidered Kurta",
        defaultFeatureField = "Pure cotton, breathable lining, dry clean recommended"
    ),
    ToolDefinition(
        name = "Shipping & Return Reply",
        description = "Clear, comforting policy messages for courier inquiries.",
        icon = Icons.Default.LocalShipping,
        defaultProductField = "Store Shipping Policy",
        defaultFeatureField = "Standard 3-5 days delivery, free above threshold, 7-day exchange"
    ),
    ToolDefinition(
        name = "Abandoned Inquiry Follow-up",
        description = "Gentle re-engagement messages for prospects who went silent.",
        icon = Icons.Default.Replay,
        defaultProductField = "Pure Silk Banarasi Saree",
        defaultFeatureField = "Limited batch pieces left in stock, fast dispatch"
    ),
    ToolDefinition(
        name = "Customer Objection Response",
        description = "Overcome price doubts, quality skepticism, and COD hesitation.",
        icon = Icons.Default.Security,
        defaultProductField = "Handmade Brass Decor Piece",
        defaultFeatureField = "Solid brass, heirloom durability, 100% money-back guarantee",
        extraFieldLabel = "Objection Type",
        defaultExtraValue = "Customer thinks price is too high"
    ),
    ToolDefinition(
        name = "Upselling & Cross-selling",
        description = "Recommend complimentary bundles and irresistible combos.",
        icon = Icons.Default.TrendingUp,
        defaultProductField = "Partywear Saree",
        defaultFeatureField = "Matches embroidered clutch and Kundan jewellery set"
    ),
    ToolDefinition(
        name = "Product Comparison",
        description = "Help customers choose between two popular store items.",
        icon = Icons.Default.CompareArrows,
        defaultProductField = "Silk Saree vs Georgette Saree",
        defaultFeatureField = "Silk is traditional & regal; Georgette is lightweight & flowy"
    ),
    ToolDefinition(
        name = "Promotional Email Draft",
        description = "Newsletters and drop announcements with subject lines.",
        icon = Icons.Default.Email,
        defaultProductField = "New Festive Arrivals Collection",
        defaultFeatureField = "Handpicked artisanal range, launch discount 10%"
    ),
    ToolDefinition(
        name = "WhatsApp Broadcast Message",
        description = "Short, punchy WhatsApp drafts formatted for mobile readers.",
        icon = Icons.Default.Chat,
        defaultProductField = "Bestselling Jhumkas & Necklace",
        defaultFeatureField = "Only 10 units left, dispatch today with tracking link"
    ),
    ToolDefinition(
        name = "Customer Review Reply",
        description = "Warm replies for 5-star reviews and constructive solutions for negative feedback.",
        icon = Icons.Default.RateReview,
        defaultProductField = "Recent Customer Review",
        defaultFeatureField = "5-star praise for fabric quality and quick delivery",
        extraFieldLabel = "Review Sentiment",
        defaultExtraValue = "5-Star Grateful"
    ),
    ToolDefinition(
        name = "FAQ Manager",
        description = "Pre-written quick answers for everyday buyer questions.",
        icon = Icons.Default.HelpOutline,
        defaultProductField = "General Store FAQs",
        defaultFeatureField = "Payment modes, order tracking, address change, bulk discount"
    )
)

@Composable
fun AIToolsScreen(
    profile: BusinessProfile?,
    products: List<ProductEntity>,
    activeToolName: String,
    onSelectTool: (String) -> Unit,
    toolResult: String,
    isGenerating: Boolean,
    onGenerateTool: (String, Map<String, String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isCopied by remember { mutableStateOf(false) }

    val activeTool = MARKETING_TOOLS.find { it.name == activeToolName } ?: MARKETING_TOOLS.first()

    var productName by remember(activeTool.name) { mutableStateOf(activeTool.defaultProductField) }
    var features by remember(activeTool.name) { mutableStateOf(activeTool.defaultFeatureField) }
    var extraValue by remember(activeTool.name) { mutableStateOf(activeTool.defaultExtraValue) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("ai_tools_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "14 Specialized AI Commerce Tools",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "High-converting marketing, copywriting, and customer relationship generators.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        // Horizontal Tool Selector Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(MARKETING_TOOLS) { tool ->
                    val isSelected = tool.name == activeTool.name
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectTool(tool.name) },
                        leadingIcon = {
                            Icon(imageVector = tool.icon, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        label = { Text(tool.name, fontSize = 12.sp) },
                        modifier = Modifier.testTag("tool_chip_${tool.name.replace(" ", "_")}")
                    )
                }
            }
        }

        // Active Tool Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(RoyalPurple.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = activeTool.icon, contentDescription = null, tint = RoyalPurple)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = activeTool.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = activeTool.description,
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = productName,
                        onValueChange = { productName = it },
                        label = { Text("Product / Subject *") },
                        placeholder = { Text("e.g. Royal Silk Banarasi Saree") },
                        modifier = Modifier.fillMaxWidth().testTag("tool_product_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = features,
                        onValueChange = { features = it },
                        label = { Text("Key Features / Specifications *") },
                        placeholder = { Text("Fabric, materials, craftsmanship, benefits...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 4
                    )

                    if (activeTool.extraFieldLabel != null) {
                        OutlinedTextField(
                            value = extraValue,
                            onValueChange = { extraValue = it },
                            label = { Text(activeTool.extraFieldLabel) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    Button(
                        onClick = {
                            val inputs = mutableMapOf(
                                "product_name" to productName,
                                "features" to features
                            )
                            if (activeTool.extraFieldLabel != null) {
                                inputs["occasion"] = extraValue
                            }
                            onGenerateTool(activeTool.name, inputs)
                        },
                        enabled = !isGenerating && productName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("generate_tool_button")
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generating with AI...")
                        } else {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate ${activeTool.name}")
                        }
                    }
                }
            }
        }

        // Result Card
        if (toolResult.isNotBlank()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("tool_result_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Generated Content",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText(activeTool.name, toolResult)
                                    clipboard.setPrimaryClip(clip)
                                    isCopied = true
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCopied) EmeraldGreen else RoyalPurple
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("copy_tool_result_button")
                            ) {
                                Icon(
                                    imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isCopied) "Copied!" else "Copy", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = toolResult,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    lineHeight = 22.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
