package com.example.saksiai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import com.example.saksiai.data.local.entity.EvidenceItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EvidenceDao {
    @Query("SELECT * FROM evidence_items ORDER BY createdAt DESC")
    fun getAllEvidenceItems(): Flow<List<EvidenceItemEntity>>

    /**
     * Institutional Memory Search: 
     * Performs a deep search across evidence titles and extracted AI content.
     */
    @Query("SELECT * FROM evidence_items WHERE title LIKE :query OR contentText LIKE :query ORDER BY createdAt DESC")
    fun searchEvidenceItems(query: String): Flow<List<EvidenceItemEntity>>

    @Query("SELECT * FROM evidence_items WHERE id = :id")
    suspend fun getEvidenceItemById(id: Long): EvidenceItemEntity?

    @Query("SELECT * FROM evidence_items WHERE id = :id")
    fun getEvidenceItemByIdFlow(id: Long): Flow<EvidenceItemEntity?>

    @Query("SELECT AVG(confidenceScore) FROM evidence_items WHERE status = 'VERIFIED'")
    fun getAverageConfidence(): Flow<Double?>

    /**
     * Decision Intelligence: Cross-references evidence bundle for an entity.
     * Fulfills "Show every risk connected to this customer."
     */
    @Query("""
        SELECT ei.* FROM evidence_items ei
        INNER JOIN entities e ON ei.organizationId = e.organizationId
        WHERE e.id = :entityId AND (ei.contentText LIKE '%' || e.entityName || '%' OR ei.contentText LIKE '%' || e.externalIdentifier || '%')
    """)
    fun getEvidenceForEntity(entityId: Long): Flow<List<EvidenceItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvidenceItem(item: EvidenceItemEntity): Long

    @Update
    suspend fun updateEvidenceItem(item: EvidenceItemEntity)

    @Transaction
    suspend fun deleteEvidenceFully(id: Long) {
        deleteRisksByEvidenceId(id)
        deleteAIJobsByEvidenceId(id)
        deleteReviewTasksByEvidenceId(id)
        deleteEvidenceItemById(id)
    }

    @Query("DELETE FROM evidence_items WHERE id = :id")
    suspend fun deleteEvidenceItemById(id: Long)
    
    @Query("DELETE FROM risk_assessments WHERE evidenceId = :id")
    suspend fun deleteRisksByEvidenceId(id: Long)
    
    @Query("DELETE FROM ai_jobs WHERE evidenceId = :id")
    suspend fun deleteAIJobsByEvidenceId(id: Long)
    
    @Query("DELETE FROM review_tasks WHERE evidenceId = :id")
    suspend fun deleteReviewTasksByEvidenceId(id: Long)
}
