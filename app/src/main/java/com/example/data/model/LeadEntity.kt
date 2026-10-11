package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leads")
data class LeadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerName: String,
    val contactInfo: String = "",
    val interestedProduct: String = "",
    val inquiryNotes: String = "",
    val leadStatus: String = "New", // "New", "Interested", "Follow-up Needed", "Converted", "Lost"
    val nextFollowUpDate: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
