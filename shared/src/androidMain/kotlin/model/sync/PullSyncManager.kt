package model.sync

import android.content.Context
import androidx.work.*
import model.sync.workers.PullSyncWorker

class PullSyncManager(context: Context) {
    private val workManager =  WorkManager.getInstance(context)

    private val syncConstraints
        get() = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

    fun sync(pushLocal: Boolean = false) {
        workManager.enqueueUniqueWork(
            "PullSync",
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<PullSyncWorker>()
                .setInputData(Data.Builder().putBoolean("pushLocal", pushLocal).build())
                .setConstraints(syncConstraints)
                .build()
        )
    }
}