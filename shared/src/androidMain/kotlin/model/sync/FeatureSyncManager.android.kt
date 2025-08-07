package model.sync

import android.content.Context
import model.Feature
import model.FeatureChoiceEntity
import model.FeatureChoiceIndexCrossRef
import model.IndexRef
import model.sync.workers.*

actual class FeatureSyncManager(context: Context) : SyncManager(context) {
    actual fun postFeature(newFeature: Feature) {
        pushSync<PostFeatureWorker>(gson.toJson(
            newFeature
        ))
    }

    actual fun postOptionsFeatureCrossRef(featureId: Int, choiceId: Int) {
        pushSync<PostOptionsFeatureCrossRefWorker>(gson.toJson(
            Pair(featureId, choiceId)
        ))
    }

    actual fun postFeatureChoice(apply: FeatureChoiceEntity) {
        pushSync<PostFeatureChoiceWorker>(gson.toJson(apply))
    }

    actual fun deleteFeatureOptionsCrossRef(featureId: Int, id: Int) {
        pushSync<DeleteFeatureOptionsCrossRefWorker>(gson.toJson(
            Pair(id, featureId)
        ))
    }

    actual fun deleteOptionsFeatureCrossRef(featureId: Int, choiceId: Int) {
        pushSync<DeleteOptionsFeatureCrossRefWorker>(gson.toJson(
            Pair(choiceId, featureId)
        ))
    }

    actual fun clearFeatureChoiceIndexRefs(id: Int) {
        pushSync<ClearFeatureChoiceIndexRefsWorker>(gson.toJson(
            id
        ))
    }

    actual fun postFeatureChoiceIndexCrossRef(
        choiceId: Int,
        index: String,
        levels: List<Int>?,
        classes: List<String>?,
        schools: List<String>?
    ) {
        pushSync<PostFeatureChoiceIndexCrossRefWorker>(gson.toJson(
            FeatureChoiceIndexCrossRef(
                choiceId = choiceId,
                index = index,
                levels = levels,
                classes = classes,
                schools = schools
            )
        ))
    }

    actual fun postIndexRef(index: String, ids: List<Int>) {
        pushSync<PostIndexRefWorker>(gson.toJson(
            IndexRef(
                index = index,
                ids = ids
            )
        ))
    }

    actual fun deleteIdFromRef(id: Int, ref: String) {
        pushSync<DeleteIdFromRefWorker>(gson.toJson(
            Pair(ref, id)
        ))
    }

    actual fun postFeatureSpellCrossRef(spellId: Int, featureId: Int) {
        pushSync<PostFeatureSpellCrossRefWorker>(gson.toJson(
            Pair(spellId, featureId)
        ))
    }

    actual fun deleteFeatureSpellCrossRef(spellId: Int, featureId: Int) {
        pushSync<DeleteFeatureSpellCrossRefWorker>(gson.toJson(
            Pair(spellId, featureId)
        ))
    }
}