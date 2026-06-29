package com.example.saksiai.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.saksiai.data.local.entity.RiskAssessmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DecisionIntelligenceDao {

    /**
     * Institutional Memory: Aggregates all risks for a specific entity
     * and any entities connected via the Evidence Graph.
     * 
     * This fulfills the blueprint requirement: 
     * "Show every risk connected to this customer."
     */
    @Transaction
    @Query("""
        SELECT * FROM risk_assessments 
        WHERE entityId = :entityId 
        OR evidenceId IN (
            SELECT id FROM evidence_items 
            WHERE id IN (
                SELECT evidenceId FROM ai_jobs 
                WHERE evidenceId IN (
                    SELECT id FROM evidence_items WHERE sourceId IN (
                        SELECT id FROM evidence_sources WHERE organizationId = (
                            SELECT organizationId FROM entities WHERE id = :entityId
                        )
                    )
                )
            )
        )
        OR entityId IN (
            SELECT targetEntityId FROM evidence_relationships WHERE sourceEntityId = :entityId
            UNION
            SELECT sourceEntityId FROM evidence_relationships WHERE targetEntityId = :entityId
        )
    """)
    fun getGlobalRiskProfile(entityId: Long): Flow<List<RiskAssessmentEntity>>

    /**
     * Trust Intelligence: Calculates the aggregate Trust Score for an organization.
     */
    @Query("SELECT AVG(confidenceScore) FROM evidence_items WHERE organizationId = :orgId AND status = 'VERIFIED'")
    fun getOrganizationTrustIndex(orgId: Long): Flow<Double?>
}
