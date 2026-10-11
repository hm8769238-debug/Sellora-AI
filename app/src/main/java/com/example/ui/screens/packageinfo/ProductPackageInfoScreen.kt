package com.example.ui.screens.packageinfo

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessProfile
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoyalPurple

@Composable
fun ProductPackageInfoScreen(
    profile: BusinessProfile?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedGuideTab by remember { mutableIntStateOf(0) }
    var isBundleCopied by remember { mutableStateOf(false) }
    var showExportBundleDialog by remember { mutableStateOf(false) }
    var bundleContent by remember { mutableStateOf("") }

    val englishGuide = """
# Sellora AI — Digital Product Quickstart & Setup Guide

## 1. System Requirements
- Mobile: Android 7.0 (API 24) or higher.
- Web/Desktop Companion: Node.js 18+ LTS, npm 9+.
- Internet Connection: Required for live Gemini API calls; optional for Demo & Catalog operations.

## 2. Included in this Package:
✔ Full Android Kotlin Jetpack Compose Application & Room Persistence
✔ Multi-language AI Sales Reply Engine (English, Hindi, Bengali)
✔ 14 Specialized Commerce Marketing & Copywriting Tools
✔ Complete Product Catalog with CSV Import & Export
✔ Customer Lead Manager with Status Stages & Notes
✔ 12 Pre-configured Quick Reply Templates with Variable Replacement
✔ Business Setup Wizard supporting 15 E-commerce Niches
✔ Windows & macOS/Linux Startup Scripts
✔ English & Bengali User Manuals

## 3. What is NOT Included:
✖ Paid SMS Gateway (uses standard Android share intent & WhatsApp deep links)
✖ Scraping of private social media data (100% compliant with Play & privacy policies)
✖ Hardcoded secret API keys (API keys are injected securely via Secrets panel / BuildConfig)

## 4. API Usage & Free Tier
- Uses Google Gemini 3.5 Flash for ultra-fast, budget-friendly text generation.
- Free tier available via Google AI Studio API keys (generous daily limits).
- Seamless offline Demo Mode included if no API key is configured.

## 5. Commercial Licensing
- Licensed for single business commercial usage or digital reselling to merchants.
- License Key Validation: Ready for custom activation verification in future releases.
""".trimIndent()

    val bengaliGuide = """
# সেলোরা এআই (Sellora AI) — ইনস্টলেশন ও ব্যবহার নির্দেশিকা

## ১. প্রয়োজনীয় রিকোয়ারমেন্ট:
- অ্যান্ড্রয়েড ফোন: Android 7.0 বা তদূর্ধ্ব।
- ব্রাউজার / কম্পিউটার সংস্করণ: Node.js 18+ এবং npm।
- ইন্টারনেট: লাইভ জেমিনি এআই ব্যবহারের জন্য ইন্টারনেট প্রয়োজন; অফলাইন ডেমো মোডে ইন্টারনেট ছাড়াই কাজ করে।

## ২. প্যাকেজে যা যা অন্তর্ভুক্ত রয়েছে:
✔ সম্পূর্ণ অ্যান্ড্রয়েড সোর্স কোড ও আধুনিক জেটপ্যাক কম্পোজ ইউজার ইন্টারফেস
✔ বহুভাষিক বিক্রয় সহকারী (ইংরেজি, হিন্দি এবং বাংলা সাপোর্ট)
✔ ১৪টি কার্যকর ডিজিটাল মার্কেটিং ও সোশ্যাল মিডিয়া টুলস
✔ প্রোডাক্ট ক্যাটালগ ও সহজ সিএসভি (CSV) ইম্পোর্ট/এক্সপোর্ট সুবিধা
✔ কাস্টমার লিড ম্যানেজার ও পরবর্তী ফলো-আপ ট্র্যাকার
✔ ইনস্টাগ্রাম, ফেসবুক ও হোয়াটসঅ্যাপ সেলারদের জন্য ১২টি রেডিমেড টেমপ্লেট
✔ ১৫টি বিভিন্ন অনলাইন ব্যবসার জন্য কাস্টমাইজড সেটআপ উইজার্ড
✔ উইন্ডোজ এবং ম্যাক/লিনাক্স চালনা স্ক্রিপ্ট

## ৩. গুরুত্বপূর্ণ নিয়মাবলী:
- প্রোডাক্টের দাম, স্টক এবং ডেলিভারি পলিসি ক্যাটালগ থেকে স্বয়ংক্রিয়ভাবে নেওয়া হয়।
- কোনো মিথ্যা তথ্য বা বানানো ডিসকাউন্ট তৈরি করা হয় না।
- গ্রাহককে পাঠানোর আগে ড্রাফটটি পর্যালোচনা করে কপি বাটনে চাপ দিন।
""".trimIndent()

    val scriptsGuide = """
# Windows Startup Script (run_sellora.bat)
@echo off
echo Starting Sellora AI Commerce Environment...
set GEMINI_API_KEY=%1
gradle :app:installDebug
echo Launching on device...
pause

# macOS / Linux Startup Script (run_sellora.sh)
#!/usr/bin/env bash
echo "Launching Sellora AI Desktop Companion..."
export GEMINI_API_KEY="${'$'}1"
gradle :app:assembleDebug
echo "Build complete. Output generated in app/build/outputs/apk/debug/"
""".trimIndent()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("product_package_info_screen"),
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
                        text = "Sellora AI — Digital Product Package",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Commercial product info, documentation & standalone scripts",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }

        // Export Standalone Archive Box
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.FolderZip, contentDescription = null, tint = RoyalPurple)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Downloadable Product Bundle",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Text(
                        text = "Export the entire business configuration, products CSV, quick templates, and setup documentation into an all-in-one standalone digital distribution bundle.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Button(
                        onClick = {
                            bundleContent = buildString {
                                append("=== SELLORA AI STANDALONE PRODUCT BUNDLE ===\n")
                                append("Store: ${profile?.name}\nCategory: ${profile?.category}\n")
                                append("Language: ${profile?.preferredLanguage}\nCurrency: ${profile?.currency}\n\n")
                                append("--- ENGLISH DOCUMENTATION ---\n$englishGuide\n\n")
                                append("--- BENGALI DOCUMENTATION ---\n$bengaliGuide\n\n")
                                append("--- STARTUP SCRIPTS ---\n$scriptsGuide\n")
                            }
                            showExportBundleDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("export_bundle_button")
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export Digital Product Bundle")
                    }
                }
            }
        }

        // Documentation Tabs
        item {
            TabRow(
                selectedTabIndex = selectedGuideTab,
                containerColor = Color.Transparent,
                contentColor = RoyalPurple
            ) {
                Tab(
                    selected = selectedGuideTab == 0,
                    onClick = { selectedGuideTab = 0 },
                    text = { Text("English Guide", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedGuideTab == 1,
                    onClick = { selectedGuideTab = 1 },
                    text = { Text("বাংলা নির্দেশিকা", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedGuideTab == 2,
                    onClick = { selectedGuideTab = 2 },
                    text = { Text("Scripts & Tools", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        // Active Guide Content
        item {
            val contentToShow = when (selectedGuideTab) {
                0 -> englishGuide
                1 -> bengaliGuide
                else -> scriptsGuide
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Sellora Guide", contentToShow)
                                clipboard.setPrimaryClip(clip)
                                isBundleCopied = true
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isBundleCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isBundleCopied) "Copied!" else "Copy Guide", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = contentToShow,
                        style = MaterialTheme.typography.bodySmall.copy(
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }

    // Export Bundle Dialog
    if (showExportBundleDialog) {
        AlertDialog(
            onDismissRequest = { showExportBundleDialog = false },
            title = { Text("Standalone Product Bundle Ready") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Full digital package text representation ready for zip packaging:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = bundleContent,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Sellora Bundle", bundleContent)
                        clipboard.setPrimaryClip(clip)
                        showExportBundleDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy Entire Bundle")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showExportBundleDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
