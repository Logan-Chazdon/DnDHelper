package model.sync

import model.BackgroundEntity

expect class BackgroundSyncManager {
    fun postBackground(backgroundEntity: BackgroundEntity)
    fun postBackgroundFeatureCrossRef(backgroundId: Int, featureId: Int)
    fun deleteBackground(id: Int)
}