package com.example.saksiai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evidence_items")
data class EvidenceItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val organizationId: Long,
    val sourceId: Long?,
    val title: String?,
    val fileName: String?,
    val fileType: String?,
    val fileUrl: String?,
    val contentText: String?,
    val status: String = "UPLOADED", // UPLOADED, PROCESSING, VERIFIED, FLAGGED, ARCHIVED
    val confidenceScore: Double?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
