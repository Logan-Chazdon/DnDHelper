package model.repositories


import kotlinx.coroutines.flow.Flow
import model.*
import model.database.daos.FeatureDao
import model.localDataSources.DataSource


class FeatureRepository {
    private val featureDao: FeatureDao

    constructor(featureDao: FeatureDao, dataSource: DataSource) {
        this.featureDao = featureDao
        this._infusions = dataSource.getInfusions()
    }

    private val _infusions: Flow<List<Infusion>>

    fun getAllInfusions(): Flow<List<Infusion>> {
        return _infusions
    }

    fun insertFeature(newFeature: Feature): Int {
        return featureDao.insertFeature(newFeature)
    }

    fun getLiveFeature(id: Int): Flow<Feature> {
        return featureDao.getLiveFeatureById(id)
    }

    fun createDefaultFeature(): Int {
        val newFeature = Feature(name = "", description = "")
        return featureDao.insertFeature(newFeature).toInt()
    }

    fun getAllIndexes(): Flow<List<String>> {
        return featureDao.returnGetAllIndexes()
    }

    fun insertFeatureOptionsCrossRef(
        featureId: Int,
        id: Int
    ) {
        featureDao.insertFeatureOptionsCrossRef(
            featureId = featureId,
            id = id
        )
    }

    fun insertOptionsFeature(
        featureId: Int,
        choiceId: Int,
    ) {
        featureDao.insertOptionsFeatureCrossRef(
            featureId = featureId,
            choiceId = choiceId
        )
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

    fun removeFeatureOptionsCrossRef(
        featureId: Int,
        id: Int
    ) {
        featureDao.removeFeatureOptionsCrossRef(
            featureId = featureId,
            id = id
        )
    }

    fun removeOptionsFeatureCrossRef(
        featureId: Int,
        choiceId: Int,
    ) {
        featureDao.removeOptionsFeatureCrossRef(
            featureId = featureId,
            choiceId = choiceId
        )
    }

    fun getFeatureChoiceEntities(id: Int): Flow<List<FeatureChoiceEntity>> {
        return featureDao.getLiveFeatureChoices(id)
    }

    fun getFeatureChoiceOptions(id: Int): List<Feature> {
        return featureDao.getFeatureChoiceOptions(id)
    }

    fun clearFeatureChoiceIndexRefs(id: Int) {
        featureDao.clearFeatureChoiceIndexRefs(id)
    }

    fun insertFeatureChoiceIndexCrossRef(
        choiceId: Int,
        index: String,
        levels: List<Int>?,
        classes: List<String>?,
        schools: List<String>?,
    ) {
        featureDao.insertFeatureChoiceIndexCrossRef(
            choiceId = choiceId,
            index = index,
            levels = levels,
            classes = classes,
            schools = schools
        )
    }

    fun insertFeatureChoice(choice: FeatureChoiceEntity) {
        featureDao.insertFeatureChoice(choice)
    }

    fun updateIndexRef(
        index: String,
        ids: List<Int>
    ) {
        featureDao.insertIndexRef(
            index = index,
            ids = ids
        )
    }

    fun removeIdFromRef(id: Int, ref: String) {
        featureDao.removeIdFromRef(id, ref)
    }

    fun insertFeatureSpellCrossRef(
        spellId: Int,
        featureId: Int
    ) {
        featureDao.insertFeatureSpellCrossRef(
            spellId = spellId,
            featureId = featureId
        )
    }

    fun getFeatureIdOr0FromSpellId(id: Int): Int {
        return featureDao.getFeatureIdOr0FromSpellId(id)
    }

    fun getFeatureSpells(id: Int): Flow<List<Spell>?> {
        return featureDao.getLiveFeatureSpells(id)
    }

    fun removeFeatureSpellCrossRef(
        spellId: Int,
        featureId: Int
    ) {
        featureDao.removeFeatureSpellCrossRef(
            spellId = spellId,
            featureId = featureId
        )
    }
}