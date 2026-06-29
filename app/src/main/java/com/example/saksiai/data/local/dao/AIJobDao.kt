package com.example.saksiai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.saksiai.data.local.entity.AIJobEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AIJobDao {
    @Query("SELECT * FROM ai_jobs WHERE evidenceId = :evidenceId")
    fun getJobsForEvidence(evidenceId: Long): Flow<List<AIJobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: AIJobEntity): Long

    @Update
    suspend fun updateJob(job: AIJobEntity)

    @Query("SELECT * FROM ai_jobs WHERE status = 'QUEUED' ORDER BY startedAt ASC")
    suspend fun getQueuedJobs(): List<AIJobEntity>
}
