package com.example.saksiai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cases")
data class CaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val organizationId: Long,
    val caseTitle: String,
    val caseType: String, // e.g., MORTGAGE_APPLICATION, KYC_REFRESH, FRAUD_INVESTIGATION
    val status: String = "OPEN", // OPEN, UNDER_REVIEW, DECIDED, ARCHIVED
    val priority: String = "MEDIUM", // LOW, MEDIUM, HIGH, CRITICAL
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
