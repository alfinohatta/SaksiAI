package com.example.saksiai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.saksiai.data.local.entity.ReviewTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewTaskDao {
    @Query("SELECT * FROM review_tasks ORDER BY id DESC")
    fun getAllReviewTasks(): Flow<List<ReviewTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: ReviewTaskEntity): Long

    @Update
    suspend fun updateTask(task: ReviewTaskEntity)

    @Query("SELECT * FROM review_tasks WHERE evidenceId = :evidenceId")
    suspend fun getTaskByEvidenceId(evidenceId: Long): ReviewTaskEntity?
}
