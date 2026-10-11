package com.example.ui.screens.wizard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessCategories
import com.example.data.model.BusinessProfile
import com.example.ui.theme.RoyalPurple

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BusinessSetupWizardScreen(
    currentProfile: BusinessProfile?,
    onSaveProfile: (BusinessProfile) -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableIntStateOf(1) }

    var name by remember { mutableStateOf(currentProfile?.name ?: "Vogue Boutique") }
    var category by remember { mutableStateOf(currentProfile?.category ?: BusinessCategories.ALL[0]) }
    var country by remember { mutableStateOf(currentProfile?.country ?: "India") }
    var currency by remember { mutableStateOf(currentProfile?.currency ?: "₹ INR") }
    var language by remember { mutableStateOf(currentProfile?.preferredLanguage ?: "English") }
    var brandTone by remember { mutableStateOf(currentProfile?.brandTone ?: "Friendly") }
    var shippingPolicy by remember {
        mutableStateOf(currentProfile?.shippingPolicy ?: "Standard delivery in 3-5 days across India. Free shipping above ₹999.")
    }
    var returnRefundPolicy by remember {
        mutableStateOf(currentProfile?.returnRefundPolicy ?: "7-day exchange or refund for unused items with original tags. Unboxing video required.")
    }
    var whatsapp by remember { mutableStateOf(currentProfile?.contactWhatsapp ?: "+91 98765 43210") }
    var instagram by remember { mutableStateOf(currentProfile?.contactInstagram ?: "@myboutique_official") }
    var email by remember { mutableStateOf(currentProfile?.contactEmail ?: "support@myboutique.com") }
    var description by remember {
        mutableStateOf(currentProfile?.description ?: "Online store dedicated to bringing customers authentic, trendy fashion.")
    }

    val totalSteps = 4
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("business_setup_wizard_screen")
    ) {
        // Wizard Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (step > 1) {
                IconButton(onClick = { step-- }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            } else {
                IconButton(onClick = onFinish) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Cancel")
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Business Setup Wizard",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Step $step of $totalSteps",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
            OutlinedButton(
                onClick = onFinish,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Skip", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LinearProgressIndicator(
            progress = { step.toFloat() / totalSteps.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = RoyalPurple,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        AnimatedContent(
            targetState = step,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "wizard_step_anim"
        ) { targetStep ->
            when (targetStep) {
                1 -> StepOneBusinessIdentity(
                    name = name,
                    onNameChange = { name = it },
                    selectedCategory = category,
                    onCategorySelect = { category = it },
                    description = description,
                    onDescriptionChange = { description = it }
                )
                2 -> StepTwoRegionalAndTone(
                    country = country,
                    onCountryChange = { country = it },
                    currency = currency,
                    onCurrencyChange = { currency = it },
                    language = language,
                    onLanguageChange = { language = it },
                    brandTone = brandTone,
                    onToneChange = { brandTone = it }
                )
                3 -> StepThreePolicies(
                    shippingPolicy = shippingPolicy,
                    onShippingPolicyChange = { shippingPolicy = it },
                    returnPolicy = returnRefundPolicy,
                    onReturnPolicyChange = { returnRefundPolicy = it }
                )
                4 -> StepFourContacts(
                    whatsapp = whatsapp,
                    onWhatsappChange = { whatsapp = it },
                    instagram = instagram,
                    onInstagramChange = { instagram = it },
                    email = email,
                    onEmailChange = { email = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation Footer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (step > 1) {
                OutlinedButton(
                    onClick = { step-- },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Previous")
                }
            } else {
                Spacer(modifier = Modifier.width(10.dp))
            }

            if (step < totalSteps) {
                Button(
                    onClick = { step++ },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
                    modifier = Modifier.testTag("wizard_next_button")
                ) {
                    Text("Next Step")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            } else {
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
                        onFinish()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
                    modifier = Modifier.testTag("wizard_complete_button")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save & Launch Store")
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepOneBusinessIdentity(
    name: String,
    onNameChange: (String) -> Unit,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Step 1: Your Business Identity",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Tell Sellora AI about your store so replies align with your exact products and style.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Business / Brand Name *") },
            placeholder = { Text("e.g. Royal Silk Boutique") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("wizard_business_name_input"),
            singleLine = true
        )

        Text(
            text = "Select Business Category (15 Options)",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BusinessCategories.ALL.forEach { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { onCategorySelect(cat) },
                    label = { Text(cat, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalPurple.copy(alpha = 0.15f),
                        selectedLabelColor = RoyalPurple
                    )
                )
            }
        }

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Short Store Description") },
            placeholder = { Text("What makes your products special? What do you sell?") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )
    }
}

@Composable
private fun StepTwoRegionalAndTone(
    country: String,
    onCountryChange: (String) -> Unit,
    currency: String,
    onCurrencyChange: (String) -> Unit,
    language: String,
    onLanguageChange: (String) -> Unit,
    brandTone: String,
    onToneChange: (String) -> Unit
) {
    val tones = listOf("Friendly & Warm", "Professional & Formal", "Premium & Luxury", "Casual & Modern")
    val currencies = listOf("₹ INR", "$ USD", "€ EUR", "£ GBP", "৳ BDT", "AED", "CAD")
    val languages = listOf("English", "Hindi (हिंदी)", "Bengali (বাংলা)")

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Step 2: Region, Currency & Tone",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        OutlinedTextField(
            value = country,
            onValueChange = onCountryChange,
            label = { Text("Country of Operation") },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Store Currency",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            currencies.take(4).forEach { cur ->
                FilterChip(
                    selected = currency == cur,
                    onClick = { onCurrencyChange(cur) },
                    label = { Text(cur) }
                )
            }
        }

        Text(
            text = "Primary Language for AI Replies",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            languages.forEach { lang ->
                FilterChip(
                    selected = language == lang,
                    onClick = { onLanguageChange(lang) },
                    label = { Text(lang) }
                )
            }
        }

        Text(
            text = "Brand Tone of Voice",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            tones.forEach { tone ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onToneChange(tone) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (brandTone == tone)
                            RoyalPurple.copy(alpha = 0.12f)
                        else
                            MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tone,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (brandTone == tone) FontWeight.Bold else FontWeight.Normal,
                                color = if (brandTone == tone) RoyalPurple else MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        if (brandTone == tone) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = RoyalPurple)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepThreePolicies(
    shippingPolicy: String,
    onShippingPolicyChange: (String) -> Unit,
    returnPolicy: String,
    onReturnPolicyChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Step 3: Shipping & Return Policies",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Sellora AI will reference these policies directly to answer customer delivery and return inquiries with 100% accuracy.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        OutlinedTextField(
            value = shippingPolicy,
            onValueChange = onShippingPolicyChange,
            label = { Text("Shipping & Delivery Policy *") },
            placeholder = { Text("Timelines, metro vs non-metro, free shipping thresholds, COD availability...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 6
        )

        OutlinedTextField(
            value = returnPolicy,
            onValueChange = onReturnPolicyChange,
            label = { Text("Return & Refund Policy *") },
            placeholder = { Text("Exchange window, condition of returned items, refund turnaround...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 6
        )
    }
}

@Composable
private fun StepFourContacts(
    whatsapp: String,
    onWhatsappChange: (String) -> Unit,
    instagram: String,
    onInstagramChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Step 4: Contact & Social Handles",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = "Included in customer draft sign-offs so buyers can reach your direct messaging channels instantly.",
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )

        OutlinedTextField(
            value = whatsapp,
            onValueChange = onWhatsappChange,
            label = { Text("WhatsApp Business Number *") },
            placeholder = { Text("+91 98765 43210") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = instagram,
            onValueChange = onInstagramChange,
            label = { Text("Instagram Handle") },
            placeholder = { Text("@mystore_official") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Customer Support Email") },
            placeholder = { Text("support@mystore.com") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}
