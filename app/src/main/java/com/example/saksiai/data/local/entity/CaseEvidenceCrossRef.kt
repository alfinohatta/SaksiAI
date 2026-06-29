package com.example.saksiai.data.local.entity

import androidx.room.Entity

@Entity(tableName = "case_evidence_cross_ref", primaryKeys = ["caseId", "evidenceId"])
data class CaseEvidenceCrossRef(
    val caseId: Long,
    val evidenceId: Long
)
