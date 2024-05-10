package com.teksiak.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.teksiak.core.database.entity.RunDeletedSyncEntity
import com.teksiak.core.database.entity.RunPendingSyncEntity

@Dao
interface RunSyncDao: RunPendingSyncDao, RunDeletedSyncDao

@Dao
interface RunPendingSyncDao {

    @Query("SELECT * FROM RunPendingSync WHERE userId = :userId")
    suspend fun getAllRunPendingSyncEntities(userId: String): List<RunPendingSyncEntity>

    @Query("SELECT * FROM RunPendingSync WHERE runId = :runId")
    suspend fun getRunPendingSyncEntity(runId: String): RunPendingSyncEntity?

    @Upsert
    suspend fun upsertRunPendingSyncEntity(entity: RunPendingSyncEntity)

    @Query("DELETE FROM RunPendingSync WHERE runId = :runId")
    suspend fun deleteRunPendingSyncEntity(runId: String)

}

@Dao
interface RunDeletedSyncDao {

    @Query("SELECT * FROM RunDeletedSync WHERE userId = :userId")
    suspend fun getAllRunDeletedSyncEntities(userId: String): List<RunDeletedSyncEntity>

    @Upsert
    suspend fun upsertRunDeletedSyncEntity(entity: RunDeletedSyncEntity)

    @Query("DELETE FROM RunDeletedSync WHERE runId = :runId")
    suspend fun deleteRunDeletedSyncEntity(runId: String)
}