package com.example.saksiai.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.saksiai.data.local.entity.*
import com.example.saksiai.data.repository.EvidenceRepository
import com.example.saksiai.data.worker.AIProcessingWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class EvidenceViewModel(
    application: Application,
    private val repository: EvidenceRepository
) : AndroidViewModel(application) {

    private val searchQuery = MutableStateFlow("")

    val searchResults: LiveData<List<Any>> = searchQuery.flatMapLatest { query ->
        if (query.isEmpty()) {
            repository.allEvidenceItems
        } else {
            combine(
                repository.searchEvidence(query),
                repository.searchEntities(query)
            ) { evidence, entities ->
                evidence + entities
            }
        }
    }.asLiveData()

    val evidenceItems: LiveData<List<EvidenceItemEntity>> = repository.allEvidenceItems.asLiveData()
    val avgConfidence: LiveData<Double?> = repository.getAverageConfidence().asLiveData()
    val totalRisks: LiveData<Int> = repository.getTotalRisksCount().asLiveData()
    val allReviewTasks: LiveData<List<ReviewTaskEntity>> = repository.allReviewTasks.asLiveData()
    val allCases: LiveData<List<CaseEntity>> = repository.allCases.asLiveData()

    // --- Research & Analytics Streams (Blueprint Section 3) ---
    val criticalRisksCount: LiveData<Int> = repository.getCriticalRiskCount().asLiveData()
    val automationRate: LiveData<Double?> = repository.getAutomationRate().asLiveData()
    val avgProcessingTime: LiveData<Long?> = repository.getAverageProcessingTime().asLiveData()
    val taskLatencyBreakdown: LiveData<List<String>> = repository.getTaskLatencyBreakdown().asLiveData()
    val fraudExposure: LiveData<Int> = repository.getFraudExposureRate().asLiveData()
    
    // --- Regulatory & Compliance Health Index (Blueprint Section 8) ---
    val pdpComplianceScore: LiveData<Double?> = repository.getPDPComplianceScore().asLiveData()
    val auditCoverage: LiveData<Double?> = repository.getAuditCoverageScore().asLiveData()
    val criticalExposure: LiveData<Int> = repository.getCriticalExposureCount().asLiveData()
    val orphanedEntities: LiveData<Int> = repository.getOrphanedEntityCount().asLiveData()
    val financialIntegrityRisks: LiveData<Int> = repository.getFinancialIntegrityRisks().asLiveData()

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun getEvidenceItem(id: Long): LiveData<EvidenceItemEntity?> = 
        repository.getEvidenceItemByIdFlow(id).asLiveData()

    fun getRisksForEvidence(id: Long): LiveData<List<RiskAssessmentEntity>> = 
        repository.getRisksForEvidence(id).asLiveData()

    fun getEntitiesForOrg(orgId: Long): LiveData<List<EvidenceEntity>> = 
        repository.getEntitiesForOrg(orgId).asLiveData()

    fun getGlobalRiskProfile(entityId: Long) = repository.getGlobalRiskProfile(entityId).asLiveData()

    fun insert(item: EvidenceItemEntity) = viewModelScope.launch {
        val id = repository.insertEvidence(item)
        startAIProcessing(id)
    }

    private fun startAIProcessing(evidenceId: Long) {
        val workManager = WorkManager.getInstance(getApplication())
        val inputData = Data.Builder()
            .putLong("evidence_id", evidenceId)
            .build()

        val processingRequest = OneTimeWorkRequestBuilder<AIProcessingWorker>()
            .setInputData(inputData)
            .build()

        workManager.enqueue(processingRequest)
    }

    // --- Decision Intelligence & Institutional Memory ---

    fun createCase(title: String, type: String, priority: String) = viewModelScope.launch {
        repository.insertCase(CaseEntity(organizationId = 1, caseTitle = title, caseType = type, priority = priority))
    }

    fun linkEvidenceToCase(caseId: Long, evidenceId: Long) = viewModelScope.launch {
        repository.linkEvidenceToCase(caseId, evidenceId)
    }

    fun finalizeCaseDecision(caseId: Long, outcome: String) = viewModelScope.launch {
        val case = repository.getCaseById(caseId)
        case?.let {
            repository.updateCase(it.copy(status = "DECIDED", updatedAt = System.currentTimeMillis()))
            repository.insertAuditLog(AuditLogEntity(
                organizationId = it.organizationId,
                userId = 1,
                action = "CASE_DECIDED",
                entityType = "CASE",
                entityId = caseId,
                metadataJson = "{\"outcome\": \"$outcome\", \"package_status\": \"READY_FOR_EXPORT\"}"
            ))
        }
    }

    fun exportDecisionPackage(caseId: Long): LiveData<String> {
        val result = MutableLiveData<String>()
        viewModelScope.launch {
            val case = repository.getCaseById(caseId)
            val evidence = repository.getEvidenceForCase(caseId).first()
            val risks = repository.getRisksForCase(caseId).first()
            
            val report = StringBuilder().apply {
                append("SAKSI_AI DECISION INTELLIGENCE PACKAGE\n")
                append("=====================================\n")
                append("CASE: ${case?.caseTitle}\n")
                append("STATUS: ${case?.status}\n\n")
                append("EVIDENCE BUNDLE (${evidence.size} units):\n")
                evidence.forEach { append("- ${it.title} (Trust: ${it.confidenceScore}%)\n") }
                append("\nRISK EXPOSURE (${risks.size} flags):\n")
                risks.forEach { append("! [${it.severity}] ${it.explanation}\n") }
            }.toString()
            
            result.postValue(report)
        }
        return result
    }

    fun getEvidenceForCase(caseId: Long): LiveData<List<EvidenceItemEntity>> = 
        repository.getEvidenceForCase(caseId).asLiveData()

    fun getRisksForCase(caseId: Long): LiveData<List<RiskAssessmentEntity>> =
        repository.getRisksForCase(caseId).asLiveData()
}

class EvidenceViewModelFactory(
    private val application: Application,
    private val repository: EvidenceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EvidenceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EvidenceViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
