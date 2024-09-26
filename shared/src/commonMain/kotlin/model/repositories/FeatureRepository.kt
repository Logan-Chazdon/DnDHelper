package model.repositories


import kotlinx.coroutines.flow.Flow
import model.Feature
import model.FeatureChoiceEntity
import model.Infusion
import model.Spell
import model.junctionEntities.*

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class FeatureRepository {
    fun getAllInfusions(): Flow<List<Infusion>>
    fun insertFeature(newFeature: Feature): Int
    fun getLiveFeature(id: Int): Flow<Feature>
    fun createDefaultFeature(): Int
    fun getAllIndexes(): Flow<List<String>>
    fun insertFeatureOptionsCrossRef(featureOptionsCrossRef: FeatureOptionsCrossRef)
    fun insertOptionsFeature(optionsFeatureCrossRef: OptionsFeatureCrossRef)
    fun createDefaultFeatureChoice(): Int
    fun getFeatureChoices(id: Int): List<FeatureChoiceEntity>
    fun removeFeatureOptionsCrossRef(featureOptionsCrossRef: FeatureOptionsCrossRef)
    fun removeOptionsFeatureCrossRef(optionsFeatureCrossRef: OptionsFeatureCrossRef)
    fun getFeatureChoiceEntities(id: Int): Flow<List<FeatureChoiceEntity>>
    fun getFeatureChoiceOptions(id: Int): List<Feature>
    fun clearFeatureChoiceIndexRefs(id: Int)
    fun insertFeatureChoiceIndexCrossRef(ref: FeatureChoiceIndexCrossRef)
    fun insertFeatureChoice(choice: FeatureChoiceEntity)
    fun updateIndexRef(ref: IndexRef)
    fun removeIdFromRef(id: Int, ref: String)
    fun insertFeatureSpellCrossRef(featureSpellCrossRef: FeatureSpellCrossRef)
    fun getFeatureIdOr0FromSpellId(id: Int): Int
    fun getFeatureSpells(id: Int): Flow<List<Spell>?>
    fun removeFeatureSpellCrossRef(featureSpellCrossRef: FeatureSpellCrossRef)
}
