package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.BusinessProfile
import com.example.data.model.ProductEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiSalesService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun isApiKeyConfigured(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return !key.isNullOrBlank() && key != "MY_GEMINI_API_KEY"
    }

    suspend fun generateSalesReplies(
        customerMessage: String,
        product: ProductEntity?,
        profile: BusinessProfile,
        language: String,
        tone: String,
        length: String,
        forceDemo: Boolean = false
    ): SalesReplyResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (forceDemo || apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext DemoSalesEngine.generateReply(
                customerMessage = customerMessage,
                product = product,
                profile = profile,
                language = language,
                tone = tone,
                length = length
            )
        }

        try {
            val systemPrompt = buildSystemPrompt(product, profile, language, tone, length)
            val fullPrompt = "$systemPrompt\n\nCustomer Inquiry: \"$customerMessage\""

            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", fullPrompt) })
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.4)
                })
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    // Fall back to demo with notice
                    val fallback = DemoSalesEngine.generateReply(customerMessage, product, profile, language, tone, length)
                    return@withContext fallback.copy(
                        modelUsed = "Demo Mode (API returned code ${response.code})",
                        notice = "API key issue or rate limit. Showing catalog-grounded demo response."
                    )
                }

                val responseString = response.body?.string() ?: ""
                val rootJson = JSONObject(responseString)
                val candidates = rootJson.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val text = parts?.optJSONObject(0)?.optString("text") ?: ""

                if (text.isBlank()) {
                    return@withContext DemoSalesEngine.generateReply(customerMessage, product, profile, language, tone, length)
                }

                parseStructuredReply(text, product, profile, language, tone, length)
            }
        } catch (e: Exception) {
            val fallback = DemoSalesEngine.generateReply(customerMessage, product, profile, language, tone, length)
            return@withContext fallback.copy(
                modelUsed = "Demo Mode (Network fallback)",
                notice = "Network error: ${e.message?.take(50)}. Showing catalog-grounded demo response."
            )
        }
    }

    suspend fun generateMarketingContent(
        toolName: String,
        inputData: Map<String, String>,
        profile: BusinessProfile,
        forceDemo: Boolean = false
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (forceDemo || apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext DemoMarketingEngine.generateContent(toolName, inputData, profile)
        }

        try {
            val prompt = buildMarketingPrompt(toolName, inputData, profile)
            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.6)
                })
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext DemoMarketingEngine.generateContent(toolName, inputData, profile)
                }
                val responseString = response.body?.string() ?: ""
                val rootJson = JSONObject(responseString)
                val candidates = rootJson.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val text = parts?.optJSONObject(0)?.optString("text") ?: ""
                if (text.isNotBlank()) text.trim() else DemoMarketingEngine.generateContent(toolName, inputData, profile)
            }
        } catch (e: Exception) {
            DemoMarketingEngine.generateContent(toolName, inputData, profile)
        }
    }

    private fun buildSystemPrompt(
        product: ProductEntity?,
        profile: BusinessProfile,
        language: String,
        tone: String,
        length: String
    ): String {
        return buildString {
            append("You are Sellora AI, a professional sales assistant for an online commerce business named \"${profile.name}\" (${profile.category}).\n")
            append("STRICT ACCURACY RULES:\n")
            append("1. Never invent prices, discounts, delivery dates, or stock availability.\n")
            append("2. If information is missing from the provided product specs or store policies, politely inform the customer or state you will confirm.\n")
            append("3. Never claim an order was placed or a message was automatically sent.\n")
            append("4. Language: Generate the output in $language.\n")
            append("5. Tone: $tone. Preferred length: $length.\n\n")

            append("BUSINESS PROFILE:\n")
            append("- Store Name: ${profile.name}\n")
            append("- Category: ${profile.category}\n")
            append("- Shipping Policy: ${profile.shippingPolicy}\n")
            append("- Return/Refund Policy: ${profile.returnRefundPolicy}\n")
            append("- WhatsApp: ${profile.contactWhatsapp}\n\n")

            if (product != null) {
                append("SELECTED PRODUCT SPECIFICATIONS:\n")
                append("- Name: ${product.name}\n")
                append("- Price: ${product.currency} ${product.price}\n")
                append("- Stock Status: ${product.stockStatus}\n")
                append("- Available Sizes: ${product.availableSizes}\n")
                append("- Available Colors: ${product.colors}\n")
                append("- Delivery Details: ${product.deliveryDetails}\n")
                append("- Product FAQs: ${product.faqs}\n")
                append("- Description: ${product.description}\n\n")
            } else {
                append("No specific product selected. Answer using general store policies.\n\n")
            }

            append("Format your reply into these exact labeled sections:\n")
            append("[READY_TO_SEND]\n(The complete best customer response)\n")
            append("[SHORT_REPLY]\n(Brief 1-2 sentence response)\n")
            append("[FRIENDLY_REPLY]\n(Warm, charming, friendly response)\n")
            append("[PROFESSIONAL_REPLY]\n(Polite, formal, crisp corporate response)\n")
            append("[PREMIUM_REPLY]\n(Luxury boutique concierge response)\n")
            append("[SUGGESTED_FOLLOW_UP]\n(One smart closing question to nudge the sale)")
        }
    }

    private fun parseStructuredReply(
        rawText: String,
        product: ProductEntity?,
        profile: BusinessProfile,
        language: String,
        tone: String,
        length: String
    ): SalesReplyResult {
        val ready = extractSection(rawText, "[READY_TO_SEND]", "[SHORT_REPLY]")
        val short = extractSection(rawText, "[SHORT_REPLY]", "[FRIENDLY_REPLY]")
        val friendly = extractSection(rawText, "[FRIENDLY_REPLY]", "[PROFESSIONAL_REPLY]")
        val professional = extractSection(rawText, "[PROFESSIONAL_REPLY]", "[PREMIUM_REPLY]")
        val premium = extractSection(rawText, "[PREMIUM_REPLY]", "[SUGGESTED_FOLLOW_UP]")
        val followUp = extractSection(rawText, "[SUGGESTED_FOLLOW_UP]", null)

        if (ready.isBlank() && rawText.isNotBlank()) {
            // Raw text fallback
            return SalesReplyResult(
                readyToSendReply = rawText.trim(),
                shortReply = rawText.take(150),
                friendlyReply = rawText.trim(),
                professionalReply = rawText.trim(),
                premiumReply = rawText.trim(),
                suggestedFollowUp = "Would you like to confirm your shipping pincode?",
                isDemoMode = false,
                modelUsed = "Gemini 3.5 Flash",
                notice = "AI-generated draft — review before sending to customer."
            )
        }

        return SalesReplyResult(
            readyToSendReply = ready.ifBlank { rawText.trim() },
            shortReply = short.ifBlank { "Available at ${product?.currency ?: "₹"} ${product?.price ?: ""}." },
            friendlyReply = friendly.ifBlank { ready },
            professionalReply = professional.ifBlank { ready },
            premiumReply = premium.ifBlank { ready },
            suggestedFollowUp = followUp.ifBlank { "Would you like me to book this for you today?" },
            isDemoMode = false,
            modelUsed = "Gemini 3.5 Flash (Live)",
            notice = "AI-generated draft — review before sending to customer."
        )
    }

    private fun extractSection(text: String, startTag: String, endTag: String?): String {
        val startIdx = text.indexOf(startTag)
        if (startIdx == -1) return ""
        val contentStart = startIdx + startTag.length
        val endIdx = if (endTag != null) text.indexOf(endTag, contentStart) else text.length
        return if (endIdx != -1) {
            text.substring(contentStart, endIdx).trim()
        } else {
            text.substring(contentStart).trim()
        }
    }

    private fun buildMarketingPrompt(
        toolName: String,
        data: Map<String, String>,
        profile: BusinessProfile
    ): String {
        val store = profile.name
        val cat = profile.category
        return "You are Sellora AI marketing specialist for \"$store\" ($cat). Create content for tool: $toolName.\nInput specifications: $data\nMake it creative, highly engaging, sales-driven, and ready to copy-paste. Never hallucinate fake guarantees."
    }
}
