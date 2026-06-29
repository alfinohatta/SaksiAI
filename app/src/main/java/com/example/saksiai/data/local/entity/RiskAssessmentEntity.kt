package com.example.saksiai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "risk_assessments")
data class RiskAssessmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entityId: Long?,
    val evidenceId: Long?,
    val riskType: String, // FRAUD, COMPLIANCE, OPERATIONAL, SECURITY
    val riskScore: Double?,
    val severity: String, // LOW, MEDIUM, HIGH, CRITICAL
    val explanation: String?,
    val createdAt: Long = System.currentTimeMillis()
)
