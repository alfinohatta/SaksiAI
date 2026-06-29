package com.example.saksiai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evidence_sources")
data class EvidenceSourceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val organizationId: Long,
    val sourceType: String, // DOCUMENT, IMAGE, AUDIO, VIDEO, EMAIL, DATABASE, API
    val name: String?,
    val description: String?,
    val createdAt: Long = System.currentTimeMillis()
)
