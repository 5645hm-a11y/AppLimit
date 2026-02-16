package com.applimit.tracking

import androidx.room.Entity

@Entity(
    tableName = "usage_entries",
    primaryKeys = ["packageName", "date"]
)
data class UsageEntry(
    val packageName: String,
    val date: String,
    val totalForegroundTimeMs: Long
)
