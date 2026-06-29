package com.example.saksiai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evidence_relationships")
data class EvidenceRelationshipEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sourceEntityId: Long,
    val targetEntityId: Long,
    val relationshipType: String, // OWNS, RELATED_TO, MENTIONED_IN, CREATED, PART_OF, CONTRADICTS, VERIFIED_BY
    val confidenceScore: Double?,
    val createdAt: Long = System.currentTimeMillis()
)
