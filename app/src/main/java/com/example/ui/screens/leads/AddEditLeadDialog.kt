package com.example.ui.screens.leads

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeadEntity
import com.example.ui.theme.RoyalPurple

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditLeadDialog(
    initialLead: LeadEntity?,
    onDismiss: () -> Unit,
    onSave: (LeadEntity) -> Unit
) {
    var name by remember { mutableStateOf(initialLead?.customerName ?: "") }
    var contact by remember { mutableStateOf(initialLead?.contactInfo ?: "") }
    var product by remember { mutableStateOf(initialLead?.interestedProduct ?: "") }
    var inquiryNotes by remember { mutableStateOf(initialLead?.inquiryNotes ?: "") }
    var status by remember { mutableStateOf(initialLead?.leadStatus ?: "New") }
    var nextFollowUp by remember { mutableStateOf(initialLead?.nextFollowUpDate ?: "Tomorrow") }
    var notes by remember { mutableStateOf(initialLead?.notes ?: "") }
    var error by remember { mutableStateOf<String?>(null) }

    val statusOptions = listOf("New", "Interested", "Follow-up Needed", "Converted", "Lost")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialLead == null) "Add Customer Lead" else "Edit Lead",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (error != null) {
                    Text(text = error ?: "", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Customer Name *") },
                    placeholder = { Text("e.g. Pooja Sharma") },
                    modifier = Modifier.fillMaxWidth().testTag("lead_name_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    label = { Text("Contact Info (Phone / WhatsApp / IG)") },
                    placeholder = { Text("e.g. +91 98201 11223 or @pooja_ig") },
                    modifier = Modifier.fillMaxWidth().testTag("lead_contact_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = product,
                    onValueChange = { product = it },
                    label = { Text("Interested Product") },
                    placeholder = { Text("e.g. Royal Silk Banarasi Saree") },
                    modifier = Modifier.fillMaxWidth().testTag("lead_product_input"),
                    singleLine = true
                )

                Text("Lead Status", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    statusOptions.forEach { opt ->
                        FilterChip(
                            selected = status == opt,
                            onClick = { status = opt },
                            label = { Text(opt, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = nextFollowUp,
                    onValueChange = { nextFollowUp = it },
                    label = { Text("Next Follow-up Date / Reminder") },
                    placeholder = { Text("e.g. Tomorrow 11 AM") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = inquiryNotes,
                    onValueChange = { inquiryNotes = it },
                    label = { Text("Inquiry Question / Details") },
                    placeholder = { Text("Customer asked for maroon color and COD options...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Internal Notes") },
                    placeholder = { Text("Prefers delivery before 15th for wedding.") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        error = "Please enter customer name."
                        return@Button
                    }
                    val lead = LeadEntity(
                        id = initialLead?.id ?: 0L,
                        customerName = name.trim(),
                        contactInfo = contact.trim(),
                        interestedProduct = product.trim(),
                        inquiryNotes = inquiryNotes.trim(),
                        leadStatus = status,
                        nextFollowUpDate = nextFollowUp.trim(),
                        notes = notes.trim(),
                        createdAt = initialLead?.createdAt ?: System.currentTimeMillis()
                    )
                    onSave(lead)
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_lead_button")
            ) {
                Text("Save Lead")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) {
                Text("Cancel")
            }
        }
    )
}
