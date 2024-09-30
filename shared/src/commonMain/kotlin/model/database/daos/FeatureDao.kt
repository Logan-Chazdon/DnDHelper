package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Feature
import model.FeatureChoiceEntity
import model.FeatureEntity
import model.Spell

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect abstract class FeatureDao() {
    abstract fun removeFeatureFeatureChoice(choiceId: Int, characterId: Int)
    abstract fun getFeatureChoices(featureId: Int): List<FeatureChoiceEntity>
    abstract fun getFeatureSpells(featureId: Int): List<Spell>?
    fun fillOutFeatureListWithoutChosen(features: List<Feature>)
    fun insertFeature(feature: FeatureEntity): Int
    abstract fun getLiveFeatureById(id: Int): Flow<Feature>
    fun insertFeatureOptionsCrossRef(featureId: Int, id: Int)
    fun insertFeatureChoice(option: FeatureChoiceEntity): Int
    fun removeFeatureOptionsCrossRef(featureId: Int, id: Int)
    fun removeOptionsFeatureCrossRef(featureId: Int, choiceId: Int)
    abstract fun getLiveFeatureChoices(featureId: Int): Flow<List<FeatureChoiceEntity>>
    abstract fun getFeatureChoiceOptions(featureChoiceId: Int): List<Feature>
    abstract fun clearFeatureChoiceIndexRefs(id: Int)
    fun insertFeatureChoiceIndexCrossRef(
        choiceId: Int,
        index: String,
        levels: List<Int>?,
        classes: List<String>?,
        schools: List<String>?
    )

    fun insertIndexRef(index: String, ids: List<Int>)
    fun removeIdFromRef(id: Int, ref: String)
    fun insertFeatureSpellCrossRef(spellId: Int, featureId: Int)
    abstract fun getFeatureIdOr0FromSpellId(id: Int): Int
    abstract fun getLiveFeatureSpells(id: Int): Flow<List<Spell>?>
    fun removeFeatureSpellCrossRef(spellId: Int, featureId: Int)
    abstract fun returnGetAllIndexes(): Flow<List<String>>
    fun insertOptionsFeatureCrossRef(
        featureId: Int,
        choiceId: Int
    )
}