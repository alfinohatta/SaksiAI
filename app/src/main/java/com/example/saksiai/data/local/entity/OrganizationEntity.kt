package com.example.saksiai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "organizations")
data class OrganizationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val industry: String, // Enum in MySQL: BANKING, INSURANCE, etc.
    val country: String = "Indonesia",
    val subscriptionPlan: String = "STARTER",
    val status: String = "TRIAL",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
