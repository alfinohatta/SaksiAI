package com.example.saksiai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "review_tasks")
data class ReviewTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val evidenceId: Long,
    val assignedUserId: Long?,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED, ESCALATED
    val reviewerNotes: String?,
    val reviewedAt: Long?
)
