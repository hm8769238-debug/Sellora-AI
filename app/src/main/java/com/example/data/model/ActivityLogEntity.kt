package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "REPLY_GENERATED", "PRODUCT_ADDED", "LEAD_SAVED", "TOOL_USED", "CSV_EXPORT"
    val title: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isDemo: Boolean = false
)
