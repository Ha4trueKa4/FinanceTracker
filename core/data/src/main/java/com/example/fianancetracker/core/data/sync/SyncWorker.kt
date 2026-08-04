package com.example.fianancetracker.core.data.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

internal class SyncWorker(
    context: Context,
    params: WorkerParameters,
    private val syncManager: SyncManager,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return syncManager.syncNow().fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() },
        )
    }
}
