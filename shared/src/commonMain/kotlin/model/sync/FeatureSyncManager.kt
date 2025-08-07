package model.sync

import model.Feature
import model.FeatureChoiceEntity

expect class FeatureSyncManager {
    fun postFeature(newFeature: Feature)
    fun postOptionsFeatureCrossRef(featureId: Int, choiceId: Int)
    fun postFeatureChoice(apply: FeatureChoiceEntity)
    fun deleteFeatureOptionsCrossRef(featureId: Int, id: Int)
    fun deleteOptionsFeatureCrossRef(featureId: Int, choiceId: Int)
    fun clearFeatureChoiceIndexRefs(id: Int)
    fun postFeatureChoiceIndexCrossRef(
        choiceId: Int,
        index: String,
        levels: List<Int>?,
        classes: List<String>?,
        schools: List<String>?
    )

    fun postIndexRef(index: String, ids: List<Int>)
    fun deleteIdFromRef(id: Int, ref: String)
    fun postFeatureSpellCrossRef(spellId: Int, featureId: Int)
    fun deleteFeatureSpellCrossRef(spellId: Int, featureId: Int)
}