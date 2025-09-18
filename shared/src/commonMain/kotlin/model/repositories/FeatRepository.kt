package model.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.transform
import model.Feat
import model.database.daos.FeatDao
import model.database.daos.FeatureDao
import ui.platformSpecific.IO


class FeatRepository {
    private val featDao: FeatDao
    private val featureDao: FeatureDao

    constructor(featDao: FeatDao, featureDao: FeatureDao) {
        this.featDao = featDao
        this.featureDao = featureDao
        this.allFeats = featDao.getUnfilledFeats().shareIn(
            CoroutineScope(Job() + Dispatchers.IO),
            started = SharingStarted.Lazily,
            replay = 1
        ).transform {
            emit(it)
            it.forEach { feat ->
                feat.features = featureDao.getFeatFeatures(feat.id)
            }
            emit(it)
        }
    }

    private val allFeats: Flow<List<Feat>>

    fun getFeats(): Flow<List<Feat>> {
        return allFeats
    }
}