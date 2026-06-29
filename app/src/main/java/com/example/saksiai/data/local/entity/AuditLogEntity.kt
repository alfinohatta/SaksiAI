package com.example.saksiai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val organizationId: Long?,
    val userId: Long?,
    val action: String, // e.g. AI_EXTRACTION, HUMAN_REVIEW, DATA_WIPE
    val entityType: String,
    val entityId: Long?,
    val ipAddress: String? = "127.0.0.1",
    val metadataJson: String?, // Stores the "Reasoning Path" and UUID
    val transactionHash: String = UUID.randomUUID().toString(),
    val createdAt: Long = System.currentTimeMillis()
)
