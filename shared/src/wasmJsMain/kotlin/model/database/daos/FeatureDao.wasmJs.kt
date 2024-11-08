package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Feature
import model.FeatureChoiceEntity
import model.FeatureEntity
import model.Spell
import services.FeatureService

actual abstract class FeatureDao {
    protected val featureService: FeatureService
    constructor(featureService: FeatureService) {
        this.featureService = featureService
    }

    actual abstract fun removeFeatureFeatureChoice(choiceId: Int, characterId: Int)
    actual abstract fun getFeatureChoices(featureId: Int): List<FeatureChoiceEntity>
    actual abstract fun getFeatureSpells(featureId: Int): List<Spell>?
    actual fun fillOutFeatureListWithoutChosen(features: List<Feature>) {
        featureService.fillOutFeatureListWithoutChosen(features)
    }

    actual fun insertFeature(feature: FeatureEntity): Int {
        return featureService.insertFeature(feature)
    }

    actual abstract fun getLiveFeatureById(id: Int): Flow<Feature>
    actual fun insertFeatureOptionsCrossRef(featureId: Int, id: Int) {
        featureService.insertFeatureOptionsCrossRef(featureId, id)
    }

    actual fun insertFeatureChoice(option: FeatureChoiceEntity): Int {
        return featureService.insertFeatureChoice(option)
    }

    actual fun removeFeatureOptionsCrossRef(featureId: Int, id: Int) {
        featureService.removeFeatureOptionsCrossRef(featureId, id)
    }

    actual fun removeOptionsFeatureCrossRef(featureId: Int, choiceId: Int) {
        featureService.removeOptionsFeatureCrossRef(featureId, choiceId)
    }

    actual abstract fun getLiveFeatureChoices(featureId: Int): Flow<List<FeatureChoiceEntity>>
    actual abstract fun getFeatureChoiceOptions(featureChoiceId: Int): List<Feature>
    actual abstract fun clearFeatureChoiceIndexRefs(id: Int)
    actual fun insertFeatureChoiceIndexCrossRef(
        choiceId: Int,
        index: String,
        levels: List<Int>?,
        classes: List<String>?,
        schools: List<String>?
    ) {
        featureService.insertFeatureChoiceIndexCrossRef(choiceId, index, levels, classes, schools)
    }

    actual fun insertIndexRef(index: String, ids: List<Int>) {
        featureService.insertIndexRef(index, ids)
    }

    actual fun removeIdFromRef(id: Int, ref: String) {
        featureService.removeIdFromRef(id, ref)
    }

    actual fun insertFeatureSpellCrossRef(spellId: Int, featureId: Int) {
        featureService.insertFeatureSpellCrossRef(spellId, featureId)
    }

    actual abstract fun getFeatureIdOr0FromSpellId(id: Int): Int
    actual abstract fun getLiveFeatureSpells(id: Int): Flow<List<Spell>?>
    actual fun removeFeatureSpellCrossRef(spellId: Int, featureId: Int) {
        featureService.removeFeatureSpellCrossRef(spellId, featureId)
    }

    actual abstract fun returnGetAllIndexes(): Flow<List<String>>
    actual fun insertOptionsFeatureCrossRef(featureId: Int, choiceId: Int) {
        featureService.insertOptionsFeatureCrossRef(featureId, choiceId)
    }

}


class FeatureDaoImpl(service: FeatureService) : FeatureDao(service) {
    override fun removeFeatureFeatureChoice(choiceId: Int, characterId: Int) {
        featureService.removeFeatureFeatureChoice(choiceId, characterId)
    }

    override fun getFeatureChoices(featureId: Int): List<FeatureChoiceEntity> {
        return featureService.getFeatureChoices(featureId)
    }

    override fun getFeatureSpells(featureId: Int): List<Spell>? {
        return featureService.getFeatureSpells(featureId)
    }

    override fun getLiveFeatureById(id: Int): Flow<Feature> {
        return featureService.getLiveFeatureById(id)
    }

    override fun getLiveFeatureChoices(featureId: Int): Flow<List<FeatureChoiceEntity>> {
        return featureService.getLiveFeatureChoices(featureId)
    }

    override fun getFeatureChoiceOptions(featureChoiceId: Int): List<Feature> {
        return featureService.getFeatureChoiceOptions(featureChoiceId)
    }

    override fun clearFeatureChoiceIndexRefs(id: Int) {
        featureService.clearFeatureChoiceIndexRefs(id)
    }

    override fun getFeatureIdOr0FromSpellId(id: Int): Int {
        return featureService.getFeatureIdOr0FromSpellId(id)
    }

    override fun getLiveFeatureSpells(id: Int): Flow<List<Spell>?> {
        return featureService.getLiveFeatureSpells(id)
    }

    override fun returnGetAllIndexes(): Flow<List<String>> {
        return featureService.returnGetAllIndexes()
    }

}