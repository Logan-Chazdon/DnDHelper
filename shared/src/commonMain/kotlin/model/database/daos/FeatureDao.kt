package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Feature
import model.FeatureChoiceEntity
import model.FeatureEntity
import model.Spell

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect abstract class FeatureDao {
    abstract suspend fun removeFeatureFeatureChoice(choiceId: Int, characterId: Int)
    abstract suspend fun getFeatureChoices(featureId: Int): List<FeatureChoiceEntity>
    abstract suspend fun getFeatureSpells(featureId: Int): List<Spell>?
    suspend fun fillOutFeatureListWithoutChosen(features: List<Feature>)
    suspend fun insertFeature(feature: FeatureEntity): Int
    abstract fun getLiveFeatureById(id: Int): Flow<Feature>
    suspend fun insertFeatureOptionsCrossRef(featureId: Int, id: Int)
    suspend fun insertFeatureChoice(option: FeatureChoiceEntity): Int
    suspend fun removeFeatureOptionsCrossRef(featureId: Int, id: Int)
    suspend fun removeOptionsFeatureCrossRef(featureId: Int, choiceId: Int)
    abstract fun getLiveFeatureChoices(featureId: Int): Flow<List<FeatureChoiceEntity>>
    abstract suspend fun getFeatureChoiceOptions(featureChoiceId: Int): List<Feature>
    abstract suspend fun clearFeatureChoiceIndexRefs(id: Int)
    suspend fun insertFeatureChoiceIndexCrossRef(
        choiceId: Int,
        index: String,
        levels: List<Int>?,
        classes: List<String>?,
        schools: List<String>?
    )

    suspend fun insertIndexRef(index: String, ids: List<Int>)
    suspend fun removeIdFromRef(id: Int, ref: String)
    suspend fun insertFeatureSpellCrossRef(spellId: Int, featureId: Int)
    abstract suspend fun getFeatureIdOr0FromSpellId(id: Int): Int
    abstract fun getLiveFeatureSpells(id: Int): Flow<List<Spell>?>
    suspend fun removeFeatureSpellCrossRef(spellId: Int, featureId: Int)
    abstract fun returnGetAllIndexes(): Flow<List<String>>
    suspend fun insertOptionsFeatureCrossRef(
        featureId: Int,
        choiceId: Int
    )
}