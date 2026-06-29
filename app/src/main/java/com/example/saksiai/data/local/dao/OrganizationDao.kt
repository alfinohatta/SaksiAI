package com.example.saksiai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.saksiai.data.local.entity.OrganizationEntity

@Dao
interface OrganizationDao {
    @Query("SELECT * FROM organizations WHERE id = :id")
    suspend fun getOrganizationById(id: Long): OrganizationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrganization(org: OrganizationEntity): Long
}
