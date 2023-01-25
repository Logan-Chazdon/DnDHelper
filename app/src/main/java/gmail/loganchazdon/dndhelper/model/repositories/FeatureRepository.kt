package gmail.loganchazdon.dndhelper.model.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import gmail.loganchazdon.dndhelper.model.Feature
import gmail.loganchazdon.dndhelper.model.Infusion
import gmail.loganchazdon.dndhelper.model.database.daos.FeatureDao
import gmail.loganchazdon.dndhelper.model.localDataSources.LocalDataSource
import javax.inject.Inject

class FeatureRepository @Inject constructor(
    private val featureDao: FeatureDao,
    LocalDataSource: LocalDataSource
) {
    private val _infusions = LocalDataSource.getInfusions(MutableLiveData())

    fun getAllInfusions(): LiveData<List<Infusion>> {
        return _infusions
    }

    fun insertFeature(newFeature: Feature) {
        featureDao.insertFeature(newFeature)
    }

    fun getLiveFeature(id: Int): LiveData<Feature> {
        return featureDao.getLiveFeatureById(id)
    }

    fun createDefaultFeature(): Int {
        val newFeature = Feature(name = "", description = "")
        return featureDao.insertFeature(newFeature).toInt()
    }
}