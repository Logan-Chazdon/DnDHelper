package model.repositories

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.Flow
import model.*
import model.database.daos.FeatureDao
import model.junctionEntities.*
import model.localDataSources.LocalDataSource


actual class FeatureRepository constructor(
    private val featureDao: FeatureDao,
    localDataSource: LocalDataSource
)  {
    private val _infusions = localDataSource.getInfusions(MutableLiveData())

    actual fun getAllInfusions(): Flow<List<Infusion>> {
        return _infusions.asFlow()
    }

    actual fun insertFeature(newFeature: Feature): Int {
       return featureDao.insertFeature(newFeature)
    }

    actual fun getLiveFeature(id: Int): Flow<Feature> {
        return featureDao.getLiveFeatureById(id).asFlow()
    }

    actual fun createDefaultFeature(): Int {
        val newFeature = Feature(name = "", description = "")
        return featureDao.insertFeature(newFeature).toInt()
    }

    actual fun getAllIndexes(): Flow<List<String>> {
        val result = MediatorLiveData<List<String>>()
        result.addSource(featureDao.returnGetAllIndexes()) {
            if(it != null) {
                result.value = it
            }
        }
        return featureDao.returnGetAllIndexes().asFlow()
    }

    actual fun insertFeatureOptionsCrossRef(featureOptionsCrossRef: FeatureOptionsCrossRef) {
        featureDao.insertFeatureOptionsCrossRef(featureOptionsCrossRef)
    }

    actual fun insertOptionsFeature(optionsFeatureCrossRef: OptionsFeatureCrossRef) {
        featureDao.insertOptionsFeatureCrossRef(optionsFeatureCrossRef)
    }

    actual fun createDefaultFeatureChoice(): Int {
        return featureDao.insertFeatureChoice(
            FeatureChoiceEntity(
                choose = Choose(static = 1)
            )
        ).toInt()
    }

    actual fun getFeatureChoices(id: Int): List<FeatureChoiceEntity> {
        return featureDao.getFeatureChoices(id)
    }

    actual fun removeFeatureOptionsCrossRef(featureOptionsCrossRef: FeatureOptionsCrossRef) {
        featureDao.removeFeatureOptionsCrossRef(featureOptionsCrossRef)
    }

    actual fun removeOptionsFeatureCrossRef(optionsFeatureCrossRef: OptionsFeatureCrossRef) {
        featureDao.removeOptionsFeatureCrossRef(optionsFeatureCrossRef)
    }

    actual fun getFeatureChoiceEntities(id: Int): Flow<List<FeatureChoiceEntity>> {
        return featureDao.getLiveFeatureChoices(id).asFlow()
    }

    actual fun getFeatureChoiceOptions(id: Int): List<Feature> {
        return featureDao.getFeatureChoiceOptions(id)
    }

    actual fun clearFeatureChoiceIndexRefs(id: Int) {
        featureDao.clearFeatureChoiceIndexRefs(id)
    }

    actual fun insertFeatureChoiceIndexCrossRef(ref: FeatureChoiceIndexCrossRef) {
        featureDao.insertFeatureChoiceIndexCrossRef(ref)
    }

    actual fun insertFeatureChoice(choice: FeatureChoiceEntity) {
        featureDao.insertFeatureChoice(choice)
    }

    actual fun updateIndexRef(ref: IndexRef) {
        featureDao.insertIndexRef(ref)
    }

    actual fun removeIdFromRef(id: Int, ref: String) {
        featureDao.removeIdFromRef(id, ref)
    }

    actual fun insertFeatureSpellCrossRef(featureSpellCrossRef: FeatureSpellCrossRef) {
        featureDao.insertFeatureSpellCrossRef(featureSpellCrossRef)
    }

    actual fun getFeatureIdOr0FromSpellId(id: Int): Int {
        return featureDao.getFeatureIdOr0FromSpellId(id)
    }

    actual fun getFeatureSpells(id: Int): Flow<List<Spell>?> {
        return featureDao.getLiveFeatureSpells(id).asFlow()
    }

    actual fun removeFeatureSpellCrossRef(featureSpellCrossRef: FeatureSpellCrossRef) {
        featureDao.removeFeatureSpellCrossRef(featureSpellCrossRef)
    }
}