package com.example.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessProfile
import com.example.ui.theme.RoyalPurple

@Composable
fun SettingsScreen(
    profile: BusinessProfile?,
    isDemoMode: Boolean,
    onToggleDemoMode: (Boolean) -> Unit,
    onSaveProfile: (BusinessProfile) -> Unit,
    onRerunWizard: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember(profile) { mutableStateOf(profile?.name ?: "") }
    var category by remember(profile) { mutableStateOf(profile?.category ?: "") }
    var country by remember(profile) { mutableStateOf(profile?.country ?: "India") }
    var currency by remember(profile) { mutableStateOf(profile?.currency ?: "₹ INR") }
    var language by remember(profile) { mutableStateOf(profile?.preferredLanguage ?: "English") }
    var brandTone by remember(profile) { mutableStateOf(profile?.brandTone ?: "Friendly") }
    var shippingPolicy by remember(profile) { mutableStateOf(profile?.shippingPolicy ?: "") }
    var returnRefundPolicy by remember(profile) { mutableStateOf(profile?.returnRefundPolicy ?: "") }
    var whatsapp by remember(profile) { mutableStateOf(profile?.contactWhatsapp ?: "") }
    var instagram by remember(profile) { mutableStateOf(profile?.contactInstagram ?: "") }
    var email by remember(profile) { mutableStateOf(profile?.contactEmail ?: "") }
    var description by remember(profile) { mutableStateOf(profile?.description ?: "") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Store & AI Settings",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Customize your store profile, AI behavior, and contact policies",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }

        // AI Engine Mode Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Demo Mode (Offline / Sandbox)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (isDemoMode)
                                    "Active: AI generation uses smart local catalog rules without API quota."
                                else
                                    "Inactive: AI queries live Gemini API server-side using your configured API key.",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        Switch(
                            checked = isDemoMode,
                            onCheckedChange = onToggleDemoMode,
                            modifier = Modifier.testTag("demo_mode_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "To configure a live Gemini API key, add GEMINI_API_KEY into your project Secrets in AI Studio or the .env file.",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.outline, fontSize = 11.sp)
                    )
                }
            }
        }

        // Store Profile Form Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Store Details",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Store Name *") },
                        modifier = Modifier.fillMaxWidth().testTag("settings_store_name_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Business Category") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = country,
                            onValueChange = { country = it },
                            label = { Text("Country") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = currency,
                            onValueChange = { currency = it },
                            label = { Text("Currency") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = language,
                            onValueChange = { language = it },
                            label = { Text("Language") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = brandTone,
                            onValueChange = { brandTone = it },
                            label = { Text("Brand Tone") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = shippingPolicy,
                        onValueChange = { shippingPolicy = it },
                        label = { Text("Shipping Policy") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    OutlinedTextField(
                        value = returnRefundPolicy,
                        onValueChange = { returnRefundPolicy = it },
                        label = { Text("Return & Refund Policy") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        label = { Text("WhatsApp Business Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = instagram,
                        onValueChange = { instagram = it },
                        label = { Text("Instagram Handle") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Support Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Store Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onRerunWizard,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Re-run Wizard", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                val updated = BusinessProfile(
                                    id = 1,
                                    name = name.ifBlank { "My Online Store" },
                                    category = category,
                                    country = country,
                                    currency = currency,
                                    preferredLanguage = language,
                                    brandTone = brandTone,
                                    shippingPolicy = shippingPolicy,
                                    returnRefundPolicy = returnRefundPolicy,
                                    contactWhatsapp = whatsapp,
                                    contactInstagram = instagram,
                                    contactEmail = email,
                                    description = description,
                                    isSetupCompleted = true
                                )
                                onSaveProfile(updated)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("save_settings_button")
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save Changes")
                        }
                    }
                }
            }
        }
    }
}
