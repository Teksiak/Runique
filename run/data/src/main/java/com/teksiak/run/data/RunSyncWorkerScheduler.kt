package com.teksiak.run.data

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.teksiak.core.database.dao.RunSyncDao
import com.teksiak.core.database.entity.RunDeletedSyncEntity
import com.teksiak.core.database.entity.RunPendingSyncEntity
import com.teksiak.core.database.mapper.toRunEntity
import com.teksiak.core.domain.SessionStorage
import com.teksiak.core.domain.run.Run
import com.teksiak.core.domain.run.RunId
import com.teksiak.core.domain.run.RunSyncScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class RunSyncWorkerScheduler(
    context: Context,
    private val syncDao: RunSyncDao,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope
): RunSyncScheduler {

    private val workManager = WorkManager.getInstance(context)

    override suspend fun scheduleSync(type: RunSyncScheduler.SyncType) {
        when(type) {
            is RunSyncScheduler.SyncType.FetchRuns -> scheduleFetchRunsWorker(type.interval)
            is RunSyncScheduler.SyncType.CreateRun -> scheduleCreateRunWorker(type.run, type.mapPictureBytes)
            is RunSyncScheduler.SyncType.DeleteRun -> scheduleDeleteRunWorker(type.runId)
        }
    }

    private suspend fun scheduleFetchRunsWorker(interval: Duration) {
        val isSyncScheduled = withContext(Dispatchers.IO) {
            workManager
                .getWorkInfosByTag(FETCH_RUNS_TAG)
                .get()
                .isNotEmpty()
        }
        if(isSyncScheduled) {
            return
        }

        val workRequest = PeriodicWorkRequestBuilder<FetchRunsWorker>(
            repeatInterval = interval.toJavaDuration()
        )
            .addTag(FETCH_RUNS_TAG)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInitialDelay(
                duration = 30,
                timeUnit = TimeUnit.MINUTES
            )
            .build()

        workManager.enqueue(workRequest).await()
    }

    private suspend fun scheduleCreateRunWorker(run: Run, mapPictureBytes: ByteArray) {
        val userId = sessionStorage.get()?.userId ?: return
        val pendingRun = RunPendingSyncEntity(
            run = run.toRunEntity(),
            mapPictureBytes = mapPictureBytes,
            userId = userId
        )
        syncDao.upsertRunPendingSyncEntity(pendingRun)

        val workerRequest = OneTimeWorkRequestBuilder<CreateRunWorker>()
            .addTag(CREATE_RUN_TAG)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                    .putString(CreateRunWorker.RUN_ID, pendingRun.runId)
                    .build()
            )
            .build()

        applicationScope.launch {
            workManager.enqueue(workerRequest).await()
        }.join()
    }

    private suspend fun scheduleDeleteRunWorker(runId: RunId) {
        val userId = sessionStorage.get()?.userId ?: return
        val deletedRun = RunDeletedSyncEntity(
            runId = runId,
            userId = userId
        )
        syncDao.upsertRunDeletedSyncEntity(deletedRun)

        val workerRequest = OneTimeWorkRequestBuilder<DeleteRunWorker>()
            .addTag(DELETE_RUN_TAG)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                    .putString(DeleteRunWorker.RUN_ID, runId)
                    .build()
            )
            .build()

        applicationScope.launch {
            workManager.enqueue(workerRequest).await()
        }.join()
    }

    override suspend fun cancelAllSyncs() {
        workManager.cancelAllWork().await()
    }

    companion object {
        const val FETCH_RUNS_TAG = "fetch_runs"
        const val CREATE_RUN_TAG = "create_run"
        const val DELETE_RUN_TAG = "delete_run"
    }
}