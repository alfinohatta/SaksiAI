package com.example.saksiai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.saksiai.data.local.entity.EvidenceSourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EvidenceSourceDao {
    @Query("SELECT * FROM evidence_sources WHERE organizationId = :orgId")
    fun getSourcesByOrg(orgId: Long): Flow<List<EvidenceSourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSource(source: EvidenceSourceEntity): Long
}
