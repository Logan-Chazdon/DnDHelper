package model.repositories


import kotlinx.coroutines.flow.Flow
import model.*
import model.database.daos.FeatureDao
import model.localDataSources.DataSource
import model.sync.FeatureSyncManager


class FeatureRepository {
    private val featureDao: FeatureDao
    private val featureSyncManager: FeatureSyncManager

    constructor(featureDao: FeatureDao, dataSource: DataSource, featureSyncManager: FeatureSyncManager) {
        this.featureDao = featureDao
        this.featureSyncManager = featureSyncManager
        this._infusions = dataSource.getInfusions()
    }

    private val _infusions: Flow<List<Infusion>>

    fun getAllInfusions(): Flow<List<Infusion>> {
        return _infusions
    }

    suspend fun insertFeature(newFeature: Feature): Int {
        val id = featureDao.insertFeature(newFeature)
        featureSyncManager.postFeature(newFeature.apply { this.featureId = id })
        return id
    }

    fun getLiveFeature(id: Int): Flow<Feature> {
        return featureDao.getLiveFeatureById(id)
    }

    suspend fun createDefaultFeature(): Int {
        val newFeature = Feature(name = "", description = "")
        newFeature.isHomebrew = true
        return insertFeature(newFeature)
    }

    fun getAllIndexes(): Flow<List<String>> {
        return featureDao.returnGetAllIndexes()
    }

    suspend fun insertFeatureOptionsCrossRef(
        featureId: Int,
        id: Int
    ) {
        featureDao.insertFeatureOptionsCrossRef(
            featureId = featureId,
            id = id
        )
    }

    suspend fun insertOptionsFeature(
        featureId: Int,
        choiceId: Int,
    ) {
        featureSyncManager.postOptionsFeatureCrossRef(
            featureId = featureId,
            choiceId = choiceId
        )
        featureDao.insertOptionsFeatureCrossRef(
            featureId = featureId,
            choiceId = choiceId
        )
    }

    suspend fun createDefaultFeatureChoice(): Int {
        val default = FeatureChoiceEntity(
            choose = Choose(static = 1)
        )
        val id = featureDao.insertFeatureChoice(
            default
        )
        featureSyncManager.postFeatureChoice(
            default.apply { this.id = id}
        )
        return id
    }

    suspend fun removeFeatureOptionsCrossRef(
        featureId: Int,
        id: Int
    ) {
        featureSyncManager.deleteFeatureOptionsCrossRef(
            featureId = featureId,
            id = id
        )

        featureDao.removeFeatureOptionsCrossRef(
            featureId = featureId,
            id = id
        )
    }

    suspend fun removeOptionsFeatureCrossRef(
        featureId: Int,
        choiceId: Int,
    ) {
        featureSyncManager.deleteOptionsFeatureCrossRef(
            featureId = featureId,
            choiceId = choiceId
        )
        featureDao.removeOptionsFeatureCrossRef(
            featureId = featureId,
            choiceId = choiceId
        )
    }

    fun getFeatureChoiceEntities(id: Int): Flow<List<FeatureChoiceEntity>> {
        return featureDao.getLiveFeatureChoices(id)
    }

    suspend fun getFeatureChoiceOptions(id: Int): List<Feature> {
        return featureDao.getFeatureChoiceOptions(id)
    }

    suspend fun clearFeatureChoiceIndexRefs(id: Int) {
        featureSyncManager.clearFeatureChoiceIndexRefs(id)
        featureDao.clearFeatureChoiceIndexRefs(id)
    }

    suspend fun insertFeatureChoiceIndexCrossRef(
        choiceId: Int,
        index: String,
        levels: List<Int>?,
        classes: List<String>?,
        schools: List<String>?,
    ) {
        featureSyncManager.postFeatureChoiceIndexCrossRef(
            choiceId = choiceId,
            index = index,
            levels = levels,
            classes = classes,
            schools = schools
        )
        featureDao.insertFeatureChoiceIndexCrossRef(
            choiceId = choiceId,
            index = index,
            levels = levels,
            classes = classes,
            schools = schools
        )
    }

    suspend fun insertFeatureChoice(choice: FeatureChoiceEntity) {
        featureSyncManager.postFeatureChoice(choice)
        featureDao.insertFeatureChoice(choice)
    }

    suspend fun updateIndexRef(
        index: String,
        ids: List<Int>
    ) {
        featureSyncManager.postIndexRef(
            index = index,
            ids = ids
        )
        featureDao.insertIndexRef(
            index = index,
            ids = ids
        )
    }

    suspend fun removeIdFromRef(id: Int, ref: String) {
        featureSyncManager.deleteIdFromRef(id, ref)
        featureDao.removeIdFromRef(id, ref)
    }

    suspend fun insertFeatureSpellCrossRef(
        spellId: Int,
        featureId: Int
    ) {
        featureSyncManager.postFeatureSpellCrossRef(
            spellId = spellId,
            featureId = featureId
        )
        featureDao.insertFeatureSpellCrossRef(
            spellId = spellId,
            featureId = featureId
        )
    }

    suspend fun getFeatureIdOr0FromSpellId(id: Int): Int {
        return featureDao.getFeatureIdOr0FromSpellId(id)
    }

    fun getFeatureSpells(id: Int): Flow<List<Spell>?> {
        return featureDao.getLiveFeatureSpells(id)
    }

    suspend fun removeFeatureSpellCrossRef(
        spellId: Int,
        featureId: Int
    ) {
        featureSyncManager.deleteFeatureSpellCrossRef(
            spellId = spellId,
            featureId = featureId
        )
        featureDao.removeFeatureSpellCrossRef(
            spellId = spellId,
            featureId = featureId
        )
    }
}