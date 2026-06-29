package com.example.saksiai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.saksiai.data.local.entity.EvidenceRelationshipEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RelationshipDao {
    /**
     * Layer 4: Evidence Graph traversal.
     * Finds all relationships connected to a specific entity.
     */
    @Query("""
        SELECT * FROM evidence_relationships 
        WHERE sourceEntityId = :entityId OR targetEntityId = :entityId
    """)
    fun getRelationshipsForEntity(entityId: Long): Flow<List<EvidenceRelationshipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelationship(relationship: EvidenceRelationshipEntity): Long

    /**
     * Layer 3: Contradiction Detection.
     * Finds conflicting relationships discovered by the Trust Engine.
     */
    @Query("SELECT * FROM evidence_relationships WHERE relationshipType = 'CONTRADICTS'")
    fun getGlobalContradictions(): Flow<List<EvidenceRelationshipEntity>>
}
