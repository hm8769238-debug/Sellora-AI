package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.LeadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LeadDao {
    @Query("SELECT * FROM leads ORDER BY createdAt DESC")
    fun getAllLeads(): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads ORDER BY createdAt DESC")
    suspend fun getAllLeadsSync(): List<LeadEntity>

    @Query("SELECT * FROM leads WHERE customerName LIKE '%' || :query || '%' OR interestedProduct LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchLeads(query: String): Flow<List<LeadEntity>>

    @Query("SELECT * FROM leads WHERE leadStatus = :status ORDER BY createdAt DESC")
    fun getLeadsByStatus(status: String): Flow<List<LeadEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLead(lead: LeadEntity): Long

    @Update
    suspend fun updateLead(lead: LeadEntity)

    @Delete
    suspend fun deleteLead(lead: LeadEntity)

    @Query("DELETE FROM leads WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM leads")
    fun getLeadCount(): Flow<Int>
}
