package gmail.loganchazdon.dndhelper.model.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import gmail.loganchazdon.dndhelper.model.Choose
import gmail.loganchazdon.dndhelper.model.Feature
import gmail.loganchazdon.dndhelper.model.FeatureChoiceEntity
import gmail.loganchazdon.dndhelper.model.Infusion
import gmail.loganchazdon.dndhelper.model.database.daos.FeatureDao
import gmail.loganchazdon.dndhelper.model.junctionEntities.FeatureChoiceIndexCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.FeatureOptionsCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.OptionsFeatureCrossRef
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

    fun getAllIndexes(): LiveData<List<String>> {
        val result = MediatorLiveData<List<String>>()
        result.addSource(featureDao.returnGetAllIndexes()) {
            if(it != null) {
                result.value = it
            }
        }
        return featureDao.returnGetAllIndexes()
    }

    fun insertFeatureOptionsCrossRef(featureOptionsCrossRef: FeatureOptionsCrossRef) {
        featureDao.insertFeatureOptionsCrossRef(featureOptionsCrossRef)
    }

    fun insertOptionsFeature(optionsFeatureCrossRef: OptionsFeatureCrossRef) {
        featureDao.insertOptionsFeatureCrossRef(optionsFeatureCrossRef)
    }

    fun createDefaultFeatureChoice(): Int {
        return featureDao.insertFeatureChoice(
            FeatureChoiceEntity(
                choose = Choose(static = 1)
            )
        ).toInt()
    }

    fun getFeatureChoices(id: Int): List<FeatureChoiceEntity> {
        return featureDao.getFeatureChoices(id)
    }

    fun removeFeatureOptionsCrossRef(featureOptionsCrossRef: FeatureOptionsCrossRef) {
        featureDao.removeFeatureOptionsCrossRef(featureOptionsCrossRef)
    }

    fun removeOptionsFeatureCrossRef(optionsFeatureCrossRef: OptionsFeatureCrossRef) {
        featureDao.removeOptionsFeatureCrossRef(optionsFeatureCrossRef)
    }

    fun getFeatureChoiceEntities(id: Int): LiveData<List<FeatureChoiceEntity>> {
        return featureDao.getLiveFeatureChoices(id)
    }

    fun getFeatureChoiceOptions(id: Int): List<Feature> {
        return featureDao.getFeatureChoiceOptions(id)
    }

    fun clearFeatureChoiceIndexRefs(id: Int) {
        featureDao.clearFeatureChoiceIndexRefs(id)
    }

    fun insertFeatureChoiceIndexCrossRef(ref: FeatureChoiceIndexCrossRef) {
        featureDao.insertFeatureChoiceIndexCrossRef(ref)
    }

    fun insertFeatureChoice(choice: FeatureChoiceEntity) {
        featureDao.insertFeatureChoice(choice)
    }
}