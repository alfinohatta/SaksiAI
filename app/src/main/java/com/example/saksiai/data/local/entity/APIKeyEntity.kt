package com.example.saksiai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "api_keys")
data class APIKeyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val organizationId: Long,
    val keyHash: String,
    val name: String,
    val lastUsed: Long?,
    val expiresAt: Long?,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
