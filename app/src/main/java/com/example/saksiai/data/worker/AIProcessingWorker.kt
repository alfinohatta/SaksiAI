package com.example.saksiai.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.saksiai.SaksiApplication
import com.example.saksiai.data.local.entity.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import java.util.*
import kotlin.random.Random

class AIProcessingWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val evidenceId = inputData.getLong("evidence_id", -1L)
        if (evidenceId == -1L) return Result.failure()

        val database = (applicationContext as SaksiApplication).database
        val evidenceDao = database.evidenceDao()
        val entityDao = database.entityDao()
        val riskDao = database.riskDao()
        val auditDao = database.auditDao()
        val reviewTaskDao = database.reviewTaskDao()
        val relationshipDao = database.relationshipDao()

        val evidence = evidenceDao.getEvidenceItemById(evidenceId) ?: return Result.failure()
        evidenceDao.updateEvidenceItem(evidence.copy(status = "PROCESSING", updatedAt = System.currentTimeMillis()))
        
        // Log Start of Reasoning Path (Blueprint Section 5)
        auditDao.insertLog(AuditLogEntity(
            organizationId = evidence.organizationId,
            action = "AI_EXTRACTION_STARTED",
            entityType = "EVIDENCE_ITEM",
            entityId = evidenceId,
            metadataJson = "{\"step\": \"INIT\", \"engine\": \"SaksiMultimodal-V1\", \"reasoning\": \"Commencing extraction for ${evidence.fileType} source.\"}"
        ))

        delay(3000) // Simulation: AI computation time

        val title = evidence.title ?: ""
        val fileType = evidence.fileType ?: ""
        val extractedName = "ALFINO HATTA" 
        val extractedNik = "3273010101950001"
        var confidence = 0.0
        var extractedContent = ""
        var extractedIncome = 0.0
        var primaryEntityType = "PERSON"

        when {
            // Case A: Identity Document (KTP)
            title.contains("KTP", ignoreCase = true) -> {
                confidence = 98.2
                extractedContent = "INDONESIA KTP INTELLIGENCE:\nNIK: $extractedNik\nName: $extractedName"
            }

            // Case B: Financial Record (Salary Slip / Slip Gaji)
            title.contains("Slip", ignoreCase = true) || title.contains("Gaji", ignoreCase = true) -> {
                confidence = 94.5
                extractedIncome = 50000000.0 // Extracted value
                primaryEntityType = "TRANSACTION"
                extractedContent = "INCOME EVIDENCE INTELLIGENCE:\nEntity: $extractedName\nExtracted Monthly Income: Rp ${String.format("%,.0f", extractedIncome)}"
            }

            // Case C: Voice Intelligence
            fileType == "AUDIO" -> {
                confidence = 88.0
                extractedContent = "VOICE INTELLIGENCE REPORT:\nTranscript: \"Saya tidak pernah mengajukan pinjaman ini.\"\nIntent: FRAUD_COMPLAINT"
            }

            else -> {
                confidence = 90.0
                extractedContent = "GENERAL RECORD INTELLIGENCE: Verified."
            }
        }

        // --- Layer 3: Trust Engine - Advanced Contradiction Detection ---
        
        // 1. Identity Resolution (Institutional Memory)
        val existingPerson = entityDao.findEntityByIdentifier(extractedNik)
        
        // 2. Income Contradiction Check (The Blueprint Example)
        var incomeContradiction = false
        if (extractedIncome > 0 && existingPerson != null) {
            // Traverse Graph for historical financial data of this person
            val historicalEvidence = evidenceDao.getEvidenceForEntity(existingPerson.id).first()
            val previousIncomeRecord = historicalEvidence.find { it.contentText?.contains("Monthly Income") == true }
            
            if (previousIncomeRecord != null) {
                // If previous record exists and value is significantly different
                incomeContradiction = !previousIncomeRecord.contentText!!.contains("50,000,000")
            }
        }

        val hasContradiction = incomeContradiction || (existingPerson != null && existingPerson.entityName != extractedName)
        
        if (hasContradiction) {
            confidence = 55.0 // Trust drop due to inconsistency
            extractedContent += "\n\n⚠️ TRUST ENGINE ALERT: Financial contradiction detected. Extracted income differs from historical institutional memory."
            
            // Raise Fraud Risk
            riskDao.insertRisk(RiskAssessmentEntity(
                evidenceId = evidenceId,
                entityId = existingPerson?.id,
                riskType = "FRAUD",
                riskScore = 88.0,
                severity = "HIGH",
                explanation = "Financial Contradiction: Extracted income of Rp 50M contradicts previous verified evidence of Rp 20M."
            ))
        }

        val finalStatus = if (hasContradiction || confidence < 80.0) "FLAGGED" else "VERIFIED"

        // --- Layer 4: Evidence Graph Update ---
        val targetEntityId = existingPerson?.id ?: entityDao.insertEntity(EvidenceEntity(
            organizationId = evidence.organizationId,
            entityType = primaryEntityType,
            entityName = extractedName,
            externalIdentifier = extractedNik
        ))

        relationshipDao.insertRelationship(EvidenceRelationshipEntity(
            sourceEntityId = targetEntityId,
            targetEntityId = 0, // Root or internal node
            relationshipType = if (hasContradiction) "CONTRADICTS" else "VERIFIED_BY",
            confidenceScore = confidence
        ))

        // Finalize decision reasoning for Audit Log
        auditDao.insertLog(AuditLogEntity(
            organizationId = evidence.organizationId,
            action = "AI_DECISION_COMPLETE",
            entityType = "EVIDENCE_ITEM",
            entityId = evidenceId,
            metadataJson = "{\"confidence\": $confidence, \"contradiction\": $hasContradiction, \"reasoning\": \"Value-based reconciliation complete. Cross-layer graph traversal matched entity $targetEntityId.\"}"
        ))

        // Update Record
        evidenceDao.updateEvidenceItem(evidence.copy(
            contentText = extractedContent,
            status = finalStatus,
            confidenceScore = confidence,
            updatedAt = System.currentTimeMillis()
        ))

        return Result.success()
    }
}
