package com.applimit.data

import java.util.UUID

// Enum to define the type of block
enum class BlockType {
    APP_BLOCK,  // The original behavior: shows a block screen
    GRAYSCALE   // The new behavior: turns the screen to grayscale
}

data class BlockRule(
    val id: String = UUID.randomUUID().toString(),
    val packageName: String,
    val days: Set<Int>, // 1=Mon ... 7=Sun
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val type: BlockType = BlockType.APP_BLOCK // Add the new field with a default value
)

