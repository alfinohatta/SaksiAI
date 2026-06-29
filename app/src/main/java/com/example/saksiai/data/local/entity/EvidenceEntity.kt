package com.example.saksiai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entities")
data class EvidenceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val organizationId: Long,
    val entityType: String, // PERSON, COMPANY, ACCOUNT, LOCATION, CONTRACT, TRANSACTION
    val entityName: String,
    val externalIdentifier: String?,
    val createdAt: Long = System.currentTimeMillis()
)
