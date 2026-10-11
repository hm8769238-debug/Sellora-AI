package com.example.data.ai

data class SalesReplyResult(
    val readyToSendReply: String,
    val shortReply: String,
    val friendlyReply: String,
    val professionalReply: String,
    val premiumReply: String,
    val suggestedFollowUp: String,
    val isDemoMode: Boolean = false,
    val modelUsed: String = "Gemini 3.5 Flash",
    val notice: String = "AI-generated draft — review before sending to customer."
)
