package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ActivityLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC LIMIT 30")
    fun getRecentActivities(): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityLogEntity): Long

    @Query("SELECT COUNT(*) FROM activity_logs WHERE type = 'REPLY_GENERATED'")
    fun getRepliesGeneratedCount(): Flow<Int>

    @Query("DELETE FROM activity_logs")
    suspend fun clearActivities()
}
