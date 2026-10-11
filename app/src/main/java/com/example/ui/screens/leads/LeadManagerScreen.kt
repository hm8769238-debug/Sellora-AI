package com.example.ui.screens.leads

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeadEntity
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoyalPurple

@Composable
fun LeadManagerScreen(
    leads: List<LeadEntity>,
    onSaveLead: (LeadEntity) -> Unit,
    onDeleteLead: (Long) -> Unit,
    onUpdateStatus: (LeadEntity, String) -> Unit,
    onDraftReplyForLead: (LeadEntity) -> Unit,
    onExportLeadsCsv: () -> String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("All") }

    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingLead by remember { mutableStateOf<LeadEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf<LeadEntity?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportedCsvText by remember { mutableStateOf("") }

    val statusFilters = listOf("All", "New", "Interested", "Follow-up Needed", "Converted", "Lost")

    val filteredLeads = leads.filter { lead ->
        val matchesSearch = searchQuery.isBlank() ||
                lead.customerName.contains(searchQuery, ignoreCase = true) ||
                lead.interestedProduct.contains(searchQuery, ignoreCase = true) ||
                lead.contactInfo.contains(searchQuery, ignoreCase = true) ||
                lead.inquiryNotes.contains(searchQuery, ignoreCase = true)
        val matchesStatus = selectedStatusFilter == "All" || lead.leadStatus.equals(selectedStatusFilter, ignoreCase = true)
        matchesSearch && matchesStatus
    }

    Box(modifier = modifier.fillMaxSize().testTag("lead_manager_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Customer Leads Manager",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${leads.size} recorded customer inquiries",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            exportedCsvText = onExportLeadsCsv()
                            showExportDialog = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("export_leads_csv_button")
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export Leads", fontSize = 12.sp)
                    }
                }
            }

            // Search
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by buyer name, product, or phone...") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("lead_search_input"),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }

            // Status Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(statusFilters) { status ->
                        FilterChip(
                            selected = selectedStatusFilter == status,
                            onClick = { selectedStatusFilter = status },
                            label = { Text(status) },
                            modifier = Modifier.testTag("lead_filter_chip_$status")
                        )
                    }
                }
            }

            // Leads List
            if (filteredLeads.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No leads found",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Record incoming buyer inquiries from WhatsApp, Instagram, or Facebook.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    editingLead = null
                                    showAddEditDialog = true
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple)
                            ) {
                                Text("Add Customer Lead")
                            }
                        }
                    }
                }
            } else {
                items(filteredLeads, key = { it.id }) { lead ->
                    LeadCardItem(
                        lead = lead,
                        onEdit = {
                            editingLead = lead
                            showAddEditDialog = true
                        },
                        onDelete = { showDeleteDialog = lead },
                        onDraftReply = { onDraftReplyForLead(lead) },
                        onStatusChange = { newStatus -> onUpdateStatus(lead, newStatus) }
                    )
                }
                item { Spacer(modifier = Modifier.height(72.dp)) }
            }
        }

        // Add FAB
        FloatingActionButton(
            onClick = {
                editingLead = null
                showAddEditDialog = true
            },
            containerColor = RoyalPurple,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_lead_fab")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Lead")
        }
    }

    // Add / Edit Dialog
    if (showAddEditDialog) {
        AddEditLeadDialog(
            initialLead = editingLead,
            onDismiss = { showAddEditDialog = false },
            onSave = {
                onSaveLead(it)
                showAddEditDialog = false
            }
        )
    }

    // Delete Dialog
    showDeleteDialog?.let { leadToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Customer Lead?") },
            text = { Text("Are you sure you want to delete lead \"${leadToDelete.customerName}\"?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteLead(leadToDelete.id)
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Export Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Exported Customer Leads CSV") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Copy or back up customer leads in CSV format:", style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = exportedCsvText,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().height(160.dp),
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Leads CSV", exportedCsvText)
                        clipboard.setPrimaryClip(clip)
                        showExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy CSV")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showExportDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun LeadCardItem(
    lead: LeadEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDraftReply: () -> Unit,
    onStatusChange: (String) -> Unit
) {
    val statusColor = when (lead.leadStatus) {
        "Converted" -> EmeraldGreen
        "Interested" -> Color(0xFF0984E3)
        "Follow-up Needed" -> Color(0xFFF59E0B)
        "New" -> RoyalPurple
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("lead_card_${lead.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = lead.customerName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    if (lead.contactInfo.isNotBlank()) {
                        Text(
                            text = lead.contactInfo,
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = lead.leadStatus,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = statusColor,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (lead.interestedProduct.isNotBlank()) {
                Text(
                    text = "🛍️ Interested in: ${lead.interestedProduct}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                )
            }

            if (lead.inquiryNotes.isNotBlank()) {
                Text(
                    text = "💬 \"${lead.inquiryNotes}\"",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    maxLines = 2
                )
            }

            if (lead.nextFollowUpDate.isNotBlank()) {
                Text(
                    text = "⏰ Reminder: ${lead.nextFollowUpDate}",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFF59E0B)),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDraftReply,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("draft_reply_for_lead_${lead.id}")
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Draft AI Reply", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
