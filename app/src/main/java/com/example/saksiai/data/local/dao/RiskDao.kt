package com.example.saksiai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.saksiai.data.local.entity.RiskAssessmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RiskDao {
    @Query("SELECT * FROM risk_assessments WHERE evidenceId = :evidenceId")
    fun getRisksByEvidence(evidenceId: Long): Flow<List<RiskAssessmentEntity>>

    @Query("SELECT * FROM risk_assessments WHERE entityId = :entityId")
    fun getRisksByEntity(entityId: Long): Flow<List<RiskAssessmentEntity>>

    @Query("SELECT COUNT(*) FROM risk_assessments")
    fun getTotalRisksCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRisk(risk: RiskAssessmentEntity): Long
}
