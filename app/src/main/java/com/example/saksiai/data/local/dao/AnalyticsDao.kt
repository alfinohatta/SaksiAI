package com.example.saksiai.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalyticsDao {

    @Query("SELECT AVG(confidenceScore) FROM evidence_items WHERE status = 'VERIFIED'")
    fun getTrustIndex(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM risk_assessments WHERE severity IN ('HIGH', 'CRITICAL')")
    fun getCriticalRiskCount(): Flow<Int>

    /**
     * Process Intelligence: Average latency by task type.
     * Identifies which AI models or tasks are bottlenecks.
     */
    @Query("""
        SELECT taskType || ': ' || AVG(finishedAt - startedAt) / 1000 || 's' 
        FROM ai_jobs 
        WHERE status = 'SUCCESS' 
        GROUP BY taskType
    """)
    fun getTaskLatencyBreakdown(): Flow<List<String>>

    @Query("SELECT (CAST(COUNT(CASE WHEN status = 'VERIFIED' THEN 1 END) AS FLOAT) / COUNT(*)) * 100 FROM evidence_items")
    fun getAutomationRate(): Flow<Double?>

    // --- Regulatory Strategy Metrics ---
    
    @Query("""
        SELECT (CAST(COUNT(ei.id) AS FLOAT) / (SELECT COUNT(*) FROM evidence_items)) * 100 
        FROM evidence_items ei
        INNER JOIN evidence_sources es ON ei.source_id = es.id
        WHERE ei.status != 'FLAGGED'
    """)
    fun getPDPComplianceScore(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM risk_assessments WHERE riskType = 'FRAUD' AND severity = 'CRITICAL'")
    fun getFinancialIntegrityRisks(): Flow<Int>
}
