package com.example.saksiai.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ComplianceDao {

    /**
     * UU PDP Compliance: Verification that every 'Verified' item 
     * has a corresponding entry in the Audit Log.
     */
    @Query("""
        SELECT (CAST(COUNT(DISTINCT entityId) AS FLOAT) / (SELECT COUNT(*) FROM evidence_items WHERE status = 'VERIFIED')) * 100 
        FROM audit_logs 
        WHERE entityType = 'EVIDENCE_ITEM' AND action = 'AI_DECISION_PATH'
    """)
    fun getAuditCoverageScore(): Flow<Double?>

    /**
     * Institutional Memory: Identifies "orphaned" entities in the graph
     * that are not connected to any verified evidence (potential data rot).
     */
    @Query("""
        SELECT COUNT(*) FROM entities 
        WHERE id NOT IN (SELECT sourceEntityId FROM evidence_relationships)
        AND id NOT IN (SELECT targetEntityId FROM evidence_relationships)
    """)
    fun getOrphanedEntityCount(): Flow<Int>

    /**
     * OJK Requirement: Real-time count of unresolved "Critical" risks.
     */
    @Query("SELECT COUNT(*) FROM risk_assessments WHERE severity = 'CRITICAL'")
    fun getCriticalExposureCount(): Flow<Int>
}
