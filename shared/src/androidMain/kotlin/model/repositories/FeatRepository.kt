package model.repositories

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import model.Feat
import model.database.daos.FeatDao
import model.database.daos.FeatureDao


actual class FeatRepository constructor(
    private val featDao: FeatDao,
    private val featureDao: FeatureDao
) {
    private val allFeats = MediatorLiveData<List<Feat>>()

    init {
        allFeats.addSource(featDao.getUnfilledFeats()) {
            if (it != null) {
                CoroutineScope(Job()).launch {
                    it.forEach { feat ->
                        feat.features = featDao.getFeatFeatures(feat.id)
                        featureDao.fillOutFeatureListWithoutChosen(feat.features!!)
                    }
                    allFeats.postValue(it)
                }
            }
        }
    }

    actual fun getFeats(): Flow<List<Feat>> {
        return allFeats.asFlow()
    }
}