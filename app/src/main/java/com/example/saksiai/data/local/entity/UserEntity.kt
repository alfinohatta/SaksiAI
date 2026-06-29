package com.example.saksiai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val organizationId: Long,
    val fullName: String,
    val email: String,
    val passwordHash: String?,
    val role: String = "VIEWER", // ADMIN, COMPLIANCE, ANALYST, REVIEWER, VIEWER
    val status: String = "ACTIVE", // ACTIVE, DISABLED
    val createdAt: Long = System.currentTimeMillis()
)
