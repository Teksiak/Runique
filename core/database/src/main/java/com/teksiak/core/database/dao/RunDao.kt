package com.teksiak.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.teksiak.core.database.entity.RunEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {

    @Upsert
    suspend fun upsertRun(run: RunEntity)

    @Upsert
    suspend fun upsertRuns(runs: List<RunEntity>)

    @Query("SELECT * FROM Run ORDER BY dateTimeUtc DESC")
    fun getRuns(): Flow<List<RunEntity>>

    @Query("SELECT * FROM Run ORDER BY dateTimeUtc ASC")
    suspend fun getUnsortedRuns(): List<RunEntity>

    @Query("DELETE FROM Run WHERE id = :id")
    suspend fun deleteRun(id: String)

    @Query("DELETE FROM Run")
    suspend fun deleteAllRuns()
}