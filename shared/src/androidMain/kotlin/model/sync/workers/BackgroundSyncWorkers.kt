package model.sync.workers

import com.google.gson.reflect.TypeToken
import model.BackgroundEntity

class PostBackgroundWorker : SyncWorker<BackgroundEntity>(TypeToken.get(BackgroundEntity::class.java)) {
    override suspend fun sync(it: BackgroundEntity) {
        backgroundService.insertBackground(it)
    }
}

class PostBackgroundFeatureCrossRefWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        backgroundService.insertBackgroundFeatureCrossRef(
            backgroundId = it.first,
            featureId = it.second
        )
    }
}

class DeleteBackgroundWorker : SyncWorker<Int>(TypeToken.get(Int::class.java)) {
    override suspend fun sync(it: Int) {
        backgroundService.deleteBackground(
            it
        )
    }
}