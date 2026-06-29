package com.example.saksiai.data.local.dao

import androidx.room.*
import com.example.saksiai.data.local.entity.CaseEntity
import com.example.saksiai.data.local.entity.CaseEvidenceCrossRef
import com.example.saksiai.data.local.entity.EvidenceItemEntity
import com.example.saksiai.data.local.entity.RiskAssessmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDao {
    @Query("SELECT * FROM cases ORDER BY updatedAt DESC")
    fun getAllCases(): Flow<List<CaseEntity>>

    @Query("SELECT * FROM cases WHERE id = :caseId")
    suspend fun getCaseById(caseId: Long): CaseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(case: CaseEntity): Long

    @Update
    suspend fun updateCase(case: CaseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCaseEvidenceCrossRef(crossRef: CaseEvidenceCrossRef)

    @Transaction
    @Query("""
        SELECT ei.* FROM evidence_items ei
        INNER JOIN case_evidence_cross_ref ce ON ei.id = ce.evidenceId
        WHERE ce.caseId = :caseId
    """)
    fun getEvidenceForCase(caseId: Long): Flow<List<EvidenceItemEntity>>

    @Transaction
    @Query("""
        SELECT ra.* FROM risk_assessments ra
        INNER JOIN case_evidence_cross_ref ce ON ra.evidenceId = ce.evidenceId
        WHERE ce.caseId = :caseId
    """)
    fun getRisksForCase(caseId: Long): Flow<List<RiskAssessmentEntity>>

    @Query("DELETE FROM cases WHERE id = :caseId")
    suspend fun deleteCase(caseId: Long)
}
