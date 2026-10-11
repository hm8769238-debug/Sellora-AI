package com.example.ui.screens.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessCategories
import com.example.data.model.ProductEntity
import com.example.ui.theme.RoyalPurple

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditProductDialog(
    initialProduct: ProductEntity?,
    defaultCurrency: String,
    onDismiss: () -> Unit,
    onSave: (ProductEntity) -> Unit
) {
    var name by remember { mutableStateOf(initialProduct?.name ?: "") }
    var category by remember { mutableStateOf(initialProduct?.category ?: BusinessCategories.ALL.first()) }
    var priceText by remember { mutableStateOf(initialProduct?.price?.toString() ?: "") }
    var currency by remember { mutableStateOf(initialProduct?.currency ?: defaultCurrency) }
    var sizes by remember { mutableStateOf(initialProduct?.availableSizes ?: "Free Size") }
    var colors by remember { mutableStateOf(initialProduct?.colors ?: "") }
    var stockStatus by remember { mutableStateOf(initialProduct?.stockStatus ?: "In Stock") }
    var deliveryDetails by remember { mutableStateOf(initialProduct?.deliveryDetails ?: "Delivered in 3-5 days. COD available.") }
    var faqs by remember { mutableStateOf(initialProduct?.faqs ?: "") }
    var description by remember { mutableStateOf(initialProduct?.description ?: "") }
    var sku by remember { mutableStateOf(initialProduct?.sku ?: "") }

    var validationError by remember { mutableStateOf<String?>(null) }
    val stockOptions = listOf("In Stock", "Low Stock", "Pre-order", "Out of Stock")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialProduct == null) "Add New Product" else "Edit Product",
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
                if (validationError != null) {
                    Text(
                        text = validationError ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name *") },
                    placeholder = { Text("e.g. Pure Silk Banarasi Saree") },
                    modifier = Modifier.fillMaxWidth().testTag("product_name_input"),
                    singleLine = true
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Price *") },
                        placeholder = { Text("1499") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("product_price_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = currency,
                        onValueChange = { currency = it },
                        label = { Text("Currency") },
                        modifier = Modifier.weight(0.7f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = sku,
                    onValueChange = { sku = it },
                    label = { Text("SKU / Product Code") },
                    placeholder = { Text("e.g. SRE-BNR-01") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("Stock Availability", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    stockOptions.forEach { opt ->
                        FilterChip(
                            selected = stockStatus == opt,
                            onClick = { stockStatus = opt },
                            label = { Text(opt, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = colors,
                    onValueChange = { colors = it },
                    label = { Text("Available Colors / Shades") },
                    placeholder = { Text("e.g. Maroon, Emerald Green, Royal Blue") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = sizes,
                    onValueChange = { sizes = it },
                    label = { Text("Available Sizes") },
                    placeholder = { Text("e.g. S, M, L, XL or Free Size") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = deliveryDetails,
                    onValueChange = { deliveryDetails = it },
                    label = { Text("Delivery Details & Timelines") },
                    placeholder = { Text("e.g. Dispatched in 24 hrs. Metro 2-3 days.") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                OutlinedTextField(
                    value = faqs,
                    onValueChange = { faqs = it },
                    label = { Text("Product FAQs / Care Instructions") },
                    placeholder = { Text("e.g. Dry clean only. Pure zari weave. Blouse piece unstitched included.") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    placeholder = { Text("Detailed product story, craftsmanship, styling notes...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        validationError = "Please enter product name."
                        return@Button
                    }
                    val priceVal = priceText.toDoubleOrNull()
                    if (priceVal == null || priceVal < 0) {
                        validationError = "Please enter a valid price."
                        return@Button
                    }
                    val product = ProductEntity(
                        id = initialProduct?.id ?: 0L,
                        name = name.trim(),
                        category = category.trim(),
                        price = priceVal,
                        currency = currency.trim(),
                        availableSizes = sizes.trim(),
                        colors = colors.trim(),
                        stockStatus = stockStatus,
                        deliveryDetails = deliveryDetails.trim(),
                        faqs = faqs.trim(),
                        description = description.trim(),
                        sku = sku.trim(),
                        createdAt = initialProduct?.createdAt ?: System.currentTimeMillis()
                    )
                    onSave(product)
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_product_button")
            ) {
                Text("Save Product")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(10.dp)) {
                Text("Cancel")
            }
        }
    )
}
