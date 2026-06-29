package com.example.saksiai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.saksiai.data.local.entity.EvidenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EntityDao {
    @Query("SELECT * FROM entities WHERE organizationId = :orgId")
    fun getEntitiesByOrg(orgId: Long): Flow<List<EvidenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntity(entity: EvidenceEntity): Long

    @Query("SELECT * FROM entities WHERE id = :id")
    suspend fun getEntityById(id: Long): EvidenceEntity?

    @Query("SELECT * FROM entities WHERE externalIdentifier = :identifier LIMIT 1")
    suspend fun findEntityByIdentifier(identifier: String): EvidenceEntity?

    @Query("SELECT * FROM entities WHERE entityName LIKE :query OR externalIdentifier LIKE :query")
    fun searchEntities(query: String): Flow<List<EvidenceEntity>>
}
