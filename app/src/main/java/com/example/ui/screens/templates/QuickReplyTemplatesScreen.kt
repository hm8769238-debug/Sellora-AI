package com.example.ui.screens.templates

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessProfile
import com.example.data.model.TemplateEntity
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoyalPurple

@Composable
fun QuickReplyTemplatesScreen(
    templates: List<TemplateEntity>,
    profile: BusinessProfile?,
    onSaveTemplate: (TemplateEntity) -> Unit,
    onDeleteTemplate: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("All") }
    var copiedTemplateId by remember { mutableStateOf<Long?>(null) }
    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingTemplate by remember { mutableStateOf<TemplateEntity?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf<TemplateEntity?>(null) }

    val categories = listOf("All", "Availability", "Pricing", "Size/Color", "Shipping", "Delivery", "Payment", "Return", "Refund", "OrderStatus", "Discount", "ThankYou", "FollowUp", "Custom")

    val filteredTemplates = templates.filter {
        selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)
    }

    fun replaceVariables(text: String): String {
        return text
            .replace("{business_name}", profile?.name ?: "Our Store")
            .replace("{product_name}", "this item")
            .replace("{price}", "${profile?.currency ?: "₹"} 1,499")
            .replace("{colors}", "all displayed shades")
            .replace("{sizes}", "standard sizes")
            .replace("{contact}", profile?.contactWhatsapp ?: "")
    }

    fun copyTemplate(template: TemplateEntity) {
        val filled = replaceVariables(template.content)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(template.title, filled)
        clipboard.setPrimaryClip(clip)
        copiedTemplateId = template.id
    }

    Box(modifier = modifier.fillMaxSize().testTag("quick_reply_templates_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "Quick Reply Templates",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Pre-saved customer responses. Supports variables like {business_name}, {product_name}, and {price}.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }

            // Categories Filter
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp) }
                        )
                    }
                }
            }

            // Templates Cards
            items(filteredTemplates, key = { it.id }) { template ->
                val isCopied = copiedTemplateId == template.id
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("template_card_${template.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = template.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = RoyalPurple.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = template.category,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = RoyalPurple,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = template.content,
                            style = MaterialTheme.typography.bodySmall.copy(
                                lineHeight = 20.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { copyTemplate(template) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCopied) EmeraldGreen else RoyalPurple
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("copy_template_button_${template.id}")
                            ) {
                                Icon(
                                    imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isCopied) "Copied!" else "Copy to Clipboard", fontSize = 11.sp)
                            }

                            Row {
                                IconButton(onClick = {
                                    editingTemplate = template
                                    showAddEditDialog = true
                                }) {
                                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                                }
                                IconButton(onClick = { showDeleteConfirmDialog = template }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(72.dp)) }
        }

        FloatingActionButton(
            onClick = {
                editingTemplate = null
                showAddEditDialog = true
            },
            containerColor = RoyalPurple,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_template_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Template")
        }
    }

    // Add / Edit Dialog
    if (showAddEditDialog) {
        var title by remember { mutableStateOf(editingTemplate?.title ?: "") }
        var category by remember { mutableStateOf(editingTemplate?.category ?: "Custom") }
        var content by remember { mutableStateOf(editingTemplate?.content ?: "") }

        AlertDialog(
            onDismissRequest = { showAddEditDialog = false },
            title = { Text(if (editingTemplate == null) "New Reply Template" else "Edit Template") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Template Title *") },
                        placeholder = { Text("e.g. Shipping Delay Apology") },
                        modifier = Modifier.fillMaxWidth().testTag("template_title_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Template Message *") },
                        placeholder = { Text("Use {product_name}, {price}, {business_name} placeholders...") },
                        modifier = Modifier.fillMaxWidth().height(140.dp).testTag("template_content_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && content.isNotBlank()) {
                            val entity = TemplateEntity(
                                id = editingTemplate?.id ?: 0L,
                                title = title.trim(),
                                category = category.trim(),
                                content = content.trim(),
                                isDefault = false,
                                updatedAt = System.currentTimeMillis()
                            )
                            onSaveTemplate(entity)
                            showAddEditDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple)
                ) {
                    Text("Save Template")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAddEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Delete Dialog
    showDeleteConfirmDialog?.let { templateToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = null },
            title = { Text("Delete Template?") },
            text = { Text("Are you sure you want to remove \"${templateToDelete.title}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteTemplate(templateToDelete.id)
                        showDeleteConfirmDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
