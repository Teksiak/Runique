package com.teksiak.core.data.run

import com.teksiak.core.database.dao.RunSyncDao
import com.teksiak.core.database.mappers.toRun
import com.teksiak.core.domain.SessionStorage
import com.teksiak.core.domain.run.LocalRunDataSource
import com.teksiak.core.domain.run.RemoteRunDataSource
import com.teksiak.core.domain.run.Run
import com.teksiak.core.domain.run.RunRepository
import com.teksiak.core.domain.util.DataError
import com.teksiak.core.domain.util.EmptyResult
import com.teksiak.core.domain.util.Result
import com.teksiak.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val runSyncDao: RunSyncDao,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope
) : RunRepository {

    override fun getRuns(): Flow<List<Run>> {
        return localRunDataSource.getRuns()
    }

    override suspend fun fetchRuns(): EmptyResult<DataError> {
        return when (val result = remoteRunDataSource.getRuns()) {
            is Result.Failure -> result.asEmptyDataResult()
            is Result.Success -> {
                applicationScope.async {
                    localRunDataSource.upsertRuns(result.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun upsertRun(run: Run, mapPicture: ByteArray): EmptyResult<DataError> {
        val localResult = localRunDataSource.upsertRun(run)
        if (localResult !is Result.Success) {
            return localResult.asEmptyDataResult()
        }

        val runWithId = run.copy(id = localResult.data)
        val remoteResult = remoteRunDataSource.postRun(
            run = runWithId,
            mapPicture = mapPicture
        )

        return when (remoteResult) {
            is Result.Failure -> {
                Result.Success(Unit)
            }

            is Result.Success -> {
                applicationScope.async {
                    localRunDataSource.upsertRun(remoteResult.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun deleteRun(id: String) {
        localRunDataSource.deleteRun(id)

        val isPendingSync = runSyncDao.getRunPendingSyncEntity(id) != null
        if (isPendingSync) {
            runSyncDao.deleteRunPendingSyncEntity(id)
            return
        }

        val remoteResult = applicationScope.async {
            remoteRunDataSource.deleteRun(id)
        }.await()

    }

    override suspend fun syncRunsWithRemote() {
        withContext(Dispatchers.IO) {
            val userId = sessionStorage.get()?.userId ?: return@withContext

            async {
                syncPendingRuns(userId)
            }.await()

            async {
                syncDeletedRuns(userId)
            }.await()
        }
    }

    private suspend fun syncPendingRuns(userId: String) = coroutineScope {
        val pendingRuns = runSyncDao.getAllRunPendingSyncEntities(userId)

        pendingRuns.map {
            launch {
                val run = it.run.toRun()
                when (remoteRunDataSource.postRun(run, it.mapPictureBytes)) {
                    is Result.Failure -> Unit
                    is Result.Success -> {
                        applicationScope.launch {
                            runSyncDao.deleteRunPendingSyncEntity(it.runId)
                        }.join()
                    }
                }
            }
        }.joinAll()
    }

    private suspend fun syncDeletedRuns(userId: String) = coroutineScope {
        val deletedRuns = runSyncDao.getAllRunDeletedSyncEntities(userId)

        deletedRuns.map {
            launch {
                when (remoteRunDataSource.deleteRun(it.runId)) {
                    is Result.Failure -> Unit
                    is Result.Success -> {
                        applicationScope.launch {
                            runSyncDao.deleteRunDeletedSyncEntity(it.runId)
                        }.join()
                    }
                }
            }
        }.joinAll()
    }

}