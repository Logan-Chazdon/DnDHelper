package model.sync

import android.content.Context
import model.BackgroundEntity
import model.sync.workers.DeleteBackgroundWorker
import model.sync.workers.PostBackgroundFeatureCrossRefWorker
import model.sync.workers.PostBackgroundWorker

actual class BackgroundSyncManager(context: Context) : SyncManager(context) {
    actual fun postBackground(backgroundEntity: BackgroundEntity) {
        pushSync<PostBackgroundWorker>(gson.toJson(
            backgroundEntity
        ))
    }

    actual fun postBackgroundFeatureCrossRef(backgroundId: Int, featureId: Int) {
        pushSync<PostBackgroundFeatureCrossRefWorker>(gson.toJson(
            Pair(backgroundId, featureId)
        ))
    }

    actual fun deleteBackground(id: Int) {
        pushSync<DeleteBackgroundWorker>(gson.toJson(id))
    }
}