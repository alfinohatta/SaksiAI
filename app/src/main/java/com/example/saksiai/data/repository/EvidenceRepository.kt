package com.example.saksiai.data.repository

import com.example.saksiai.data.local.dao.*
import com.example.saksiai.data.local.entity.*
import kotlinx.coroutines.flow.Flow

class EvidenceRepository(
    private val evidenceDao: EvidenceDao,
    private val entityDao: EntityDao,
    private val riskDao: RiskDao,
    private val auditDao: AuditDao,
    private val reviewTaskDao: ReviewTaskDao,
    private val relationshipDao: RelationshipDao,
    private val caseDao: CaseDao,
    private val analyticsDao: AnalyticsDao,
    private val decisionDao: DecisionIntelligenceDao,
    private val complianceDao: ComplianceDao
) {

    val allEvidenceItems: Flow<List<EvidenceItemEntity>> = evidenceDao.getAllEvidenceItems()

    /**
     * Institutional Memory: Searches for specific evidence records.
     */
    fun searchEvidence(query: String): Flow<List<EvidenceItemEntity>> = 
        evidenceDao.searchEvidenceItems("%$query%")

    /**
     * Institutional Memory: Searches for specific entities (People/Companies) across the graph.
     */
    fun searchEntities(query: String): Flow<List<EvidenceEntity>> = 
        entityDao.searchEntities("%$query%")

    fun getEvidenceItemByIdFlow(id: Long): Flow<EvidenceItemEntity?> = 
        evidenceDao.getEvidenceItemByIdFlow(id)

    fun getAverageConfidence(): Flow<Double?> = evidenceDao.getAverageConfidence()

    fun getTotalRisksCount(): Flow<Int> = riskDao.getTotalRisksCount()

    suspend fun insertEvidence(item: EvidenceItemEntity): Long {
        return evidenceDao.insertEvidenceItem(item)
    }

    suspend fun getEvidenceById(id: Long): EvidenceItemEntity? {
        return evidenceDao.getEvidenceItemById(id)
    }

    suspend fun updateEvidence(item: EvidenceItemEntity) {
        evidenceDao.updateEvidenceItem(item)
    }

    suspend fun deleteEvidenceFully(id: Long) {
        evidenceDao.deleteEvidenceFully(id)
    }

    // Entity methods
    fun getEntitiesForOrg(orgId: Long): Flow<List<EvidenceEntity>> = entityDao.getEntitiesByOrg(orgId)
    suspend fun insertEntity(entity: EvidenceEntity) = entityDao.insertEntity(entity)
    suspend fun getEntityById(id: Long): EvidenceEntity? = entityDao.getEntityById(id)

    // Risk methods
    fun getRisksForEvidence(evidenceId: Long): Flow<List<RiskAssessmentEntity>> = riskDao.getRisksByEvidence(evidenceId)
    fun getRisksByEntity(entityId: Long): Flow<List<RiskAssessmentEntity>> = riskDao.getRisksByEntity(entityId)
    suspend fun insertRisk(risk: RiskAssessmentEntity) = riskDao.insertRisk(risk)

    // Audit methods
    fun getLogsForEvidence(evidenceId: Long): Flow<List<AuditLogEntity>> = auditDao.getLogsForEvidence(evidenceId)
    suspend fun insertAuditLog(log: AuditLogEntity) = auditDao.insertLog(log)

    // Review Task methods
    val allReviewTasks: Flow<List<ReviewTaskEntity>> = reviewTaskDao.getAllReviewTasks()
    suspend fun insertReviewTask(task: ReviewTaskEntity) = reviewTaskDao.insertTask(task)
    suspend fun updateReviewTask(task: ReviewTaskEntity) = reviewTaskDao.updateTask(task)
    suspend fun getReviewTaskByEvidenceId(evidenceId: Long) = reviewTaskDao.getTaskByEvidenceId(evidenceId)

    // Evidence for Entity
    fun getEvidenceForEntity(entityId: Long): Flow<List<EvidenceItemEntity>> = evidenceDao.getEvidenceForEntity(entityId)

    // Relationship methods
    fun getRelationshipsForEntity(entityId: Long): Flow<List<EvidenceRelationshipEntity>> = 
        relationshipDao.getRelationshipsForEntity(entityId)
    suspend fun insertRelationship(relationship: EvidenceRelationshipEntity) = 
        relationshipDao.insertRelationship(relationship)

    // Case methods
    val allCases: Flow<List<CaseEntity>> = caseDao.getAllCases()
    suspend fun getCaseById(caseId: Long): CaseEntity? = caseDao.getCaseById(caseId)
    suspend fun insertCase(case: CaseEntity): Long = caseDao.insertCase(case)
    suspend fun updateCase(case: CaseEntity) = caseDao.updateCase(case)
    suspend fun linkEvidenceToCase(caseId: Long, evidenceId: Long) = 
        caseDao.insertCaseEvidenceCrossRef(CaseEvidenceCrossRef(caseId, evidenceId))
    fun getEvidenceForCase(caseId: Long): Flow<List<EvidenceItemEntity>> = caseDao.getEvidenceForCase(caseId)
    fun getRisksForCase(caseId: Long): Flow<List<RiskAssessmentEntity>> = caseDao.getRisksForCase(caseId)
    suspend fun deleteCase(caseId: Long) = caseDao.deleteCase(caseId)

    // Analytics methods (Requirement C & D)
    fun getAutomationRate(): Flow<Double?> = analyticsDao.getAutomationRate()
    fun getAverageProcessingTime(): Flow<Long?> = analyticsDao.getAverageProcessingTime()
    fun getTaskLatencyBreakdown(): Flow<List<String>> = analyticsDao.getTaskLatencyBreakdown()
    fun getCriticalRiskCount(): Flow<Int> = analyticsDao.getCriticalRiskCount()
    fun getFraudExposureRate(): Flow<Int> = analyticsDao.getFraudExposureRate()

    // --- Regulatory Strategy Methods (Blueprint Section 8) ---
    fun getPDPComplianceScore(): Flow<Double?> = analyticsDao.getPDPComplianceScore()
    fun getFinancialIntegrityRisks(): Flow<Int> = analyticsDao.getFinancialIntegrityRisks()
    
    // --- Compliance Health Index Methods ---
    fun getAuditCoverageScore(): Flow<Double?> = complianceDao.getAuditCoverageScore()
    fun getOrphanedEntityCount(): Flow<Int> = complianceDao.getOrphanedEntityCount()
    fun getCriticalExposureCount(): Flow<Int> = complianceDao.getCriticalExposureCount()

    // Decision Intelligence (Layer 4 traversal)
    fun getGlobalRiskProfile(entityId: Long): Flow<List<RiskAssessmentEntity>> = 
        decisionDao.getGlobalRiskProfile(entityId)
}
