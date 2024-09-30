package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Feature
import model.FeatureChoiceEntity
import model.FeatureEntity
import model.Spell

actual abstract class FeatureDao actual constructor() {
    actual abstract fun removeFeatureFeatureChoice(choiceId: Int, characterId: Int)
    actual abstract fun getFeatureChoices(featureId: Int): List<FeatureChoiceEntity>
    actual abstract fun getFeatureSpells(featureId: Int): List<Spell>?
    actual fun fillOutFeatureListWithoutChosen(features: List<Feature>) {
    }

    actual fun insertFeature(feature: FeatureEntity): Int {
        TODO("Not yet implemented")
    }

    actual abstract fun getLiveFeatureById(id: Int): Flow<Feature>
    actual fun insertFeatureOptionsCrossRef(featureId: Int, id: Int) {
    }

    actual fun insertFeatureChoice(option: FeatureChoiceEntity): Int {
        TODO("Not yet implemented")
    }

    actual fun removeFeatureOptionsCrossRef(featureId: Int, id: Int) {
    }

    actual fun removeOptionsFeatureCrossRef(featureId: Int, choiceId: Int) {
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
    }

    actual fun insertIndexRef(index: String, ids: List<Int>) {
    }

    actual fun removeIdFromRef(id: Int, ref: String) {
    }

    actual fun insertFeatureSpellCrossRef(spellId: Int, featureId: Int) {
    }

    actual abstract fun getFeatureIdOr0FromSpellId(id: Int): Int
    actual abstract fun getLiveFeatureSpells(id: Int): Flow<List<Spell>?>
    actual fun removeFeatureSpellCrossRef(spellId: Int, featureId: Int) {
    }

    actual abstract fun returnGetAllIndexes(): Flow<List<String>>
    actual fun insertOptionsFeatureCrossRef(featureId: Int, choiceId: Int) {
    }

}


class FeatureDaoImpl : FeatureDao() {
    override fun removeFeatureFeatureChoice(choiceId: Int, characterId: Int) {
        TODO("Not yet implemented")
    }

    override fun getFeatureChoices(featureId: Int): List<FeatureChoiceEntity> {
        TODO("Not yet implemented")
    }

    override fun getFeatureSpells(featureId: Int): List<Spell>? {
        TODO("Not yet implemented")
    }

    override fun getLiveFeatureById(id: Int): Flow<Feature> {
        TODO("Not yet implemented")
    }

    override fun getLiveFeatureChoices(featureId: Int): Flow<List<FeatureChoiceEntity>> {
        TODO("Not yet implemented")
    }

    override fun getFeatureChoiceOptions(featureChoiceId: Int): List<Feature> {
        TODO("Not yet implemented")
    }

    override fun clearFeatureChoiceIndexRefs(id: Int) {
        TODO("Not yet implemented")
    }

    override fun getFeatureIdOr0FromSpellId(id: Int): Int {
        TODO("Not yet implemented")
    }

    override fun getLiveFeatureSpells(id: Int): Flow<List<Spell>?> {
        TODO("Not yet implemented")
    }

    override fun returnGetAllIndexes(): Flow<List<String>> {
        TODO("Not yet implemented")
    }

}