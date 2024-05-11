package com.teksiak.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.teksiak.core.domain.run.RunRepository
import com.teksiak.core.domain.util.DataError
import com.teksiak.core.domain.util.Result

class FetchRunsWorker(
    context: Context,
    private val params: WorkerParameters,
    private val runRepository: RunRepository
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        if(runAttemptCount >= 5) {
            return Result.failure()
        }
        return when(val result = runRepository.fetchRuns()) {
            is com.teksiak.core.domain.util.Result.Failure -> {
                result.error.toWorkerResult()
            }
            is com.teksiak.core.domain.util.Result.Success -> Result.success()
        }
    }

}