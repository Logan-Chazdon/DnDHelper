package model.sync

import model.Feature
import model.FeatureChoiceEntity

actual class FeatureSyncManager {
    actual fun postFeature(newFeature: Feature) {
    }

    actual fun postOptionsFeatureCrossRef(featureId: Int, choiceId: Int) {
    }

    actual fun postFeatureChoice(apply: FeatureChoiceEntity) {
    }

    actual fun deleteFeatureOptionsCrossRef(featureId: Int, id: Int) {
    }

    actual fun deleteOptionsFeatureCrossRef(featureId: Int, choiceId: Int) {
    }

    actual fun clearFeatureChoiceIndexRefs(id: Int) {
    }

    actual fun postFeatureChoiceIndexCrossRef(
        choiceId: Int,
        index: String,
        levels: List<Int>?,
        classes: List<String>?,
        schools: List<String>?
    ) {
    }

    actual fun postIndexRef(index: String, ids: List<Int>) {
    }

    actual fun deleteIdFromRef(id: Int, ref: String) {
    }

    actual fun postFeatureSpellCrossRef(spellId: Int, featureId: Int) {
    }

    actual fun deleteFeatureSpellCrossRef(spellId: Int, featureId: Int) {
    }
}