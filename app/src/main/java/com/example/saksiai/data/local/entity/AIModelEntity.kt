package com.example.saksiai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_models")
data class AIModelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val provider: String?,
    val version: String?,
    val purpose: String?,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
