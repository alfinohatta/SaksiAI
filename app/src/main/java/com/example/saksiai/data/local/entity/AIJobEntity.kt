package com.example.saksiai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_jobs")
data class AIJobEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val evidenceId: Long,
    val modelName: String?,
    val taskType: String, // OCR, CLASSIFICATION, SUMMARIZATION, RISK_ANALYSIS, ENTITY_EXTRACTION, FRAUD_DETECTION, SENTIMENT
    val status: String = "QUEUED", // QUEUED, RUNNING, SUCCESS, FAILED
    val startedAt: Long?,
    val finishedAt: Long?,
    val outputJson: String? // Room doesn't handle JSON natively as MySQL does, so we store as String
)
