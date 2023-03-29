package gmail.loganchazdon.dndhelper.model.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import gmail.loganchazdon.dndhelper.model.Feat
import gmail.loganchazdon.dndhelper.model.database.daos.FeatDao
import gmail.loganchazdon.dndhelper.model.database.daos.FeatureDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class FeatRepository @Inject constructor(
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

    fun getFeats(): LiveData<List<Feat>> {
        return allFeats
    }
}