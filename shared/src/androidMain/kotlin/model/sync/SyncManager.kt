package model.sync

import android.content.Context
import androidx.work.*
import com.google.gson.Gson


abstract class SyncManager(val context: Context) {
    protected val workManager =  WorkManager.getInstance(context)
    protected val gson = Gson()

    protected val syncConstraints
        get() = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()


    protected inline fun <reified W: ListenableWorker> pushSync(json: String) {
        workManager.enqueue(
            OneTimeWorkRequestBuilder<W>()
                .setInputData(
                    Data.Builder().putString("json", json).build()
                )
                .setConstraints(syncConstraints)
                .build()
        )
    }
}