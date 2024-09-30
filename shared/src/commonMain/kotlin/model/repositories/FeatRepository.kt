package model.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import model.Feat
import model.database.daos.FeatDao
import model.database.daos.FeatureDao


 class FeatRepository {
    private val featDao: FeatDao
    private val featureDao: FeatureDao

     constructor(featDao: FeatDao, featureDao: FeatureDao) {
        this.featDao = featDao
        this.featureDao = featureDao
        this.allFeats = featDao.getUnfilledFeats().transform {
            CoroutineScope(Job()).launch {
                it.forEach { feat ->
                    feat.features = featDao.getFeatFeatures(feat.id)
                    featureDao.fillOutFeatureListWithoutChosen(feat.features!!)
                }
                emit(it)
            }
        }
    }

    private val allFeats: Flow<List<Feat>>

     fun getFeats(): Flow<List<Feat>> {
        return allFeats
    }
}