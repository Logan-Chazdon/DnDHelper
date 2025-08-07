package model.sync.workers

import com.google.gson.reflect.TypeToken
import model.Feature
import model.FeatureChoiceEntity
import model.FeatureChoiceIndexCrossRef
import model.IndexRef

class PostFeatureWorker : SyncWorker<Feature>(TypeToken.get(Feature::class.java)) {
    override suspend fun sync(it: Feature) {
        featureService.insertFeature(it)
    }
}

class PostOptionsFeatureCrossRefWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        featureService.insertOptionsFeatureCrossRef(
            featureId = it.first,
            choiceId = it.second
        )
    }
}

class PostFeatureChoiceWorker : SyncWorker<FeatureChoiceEntity>(TypeToken.get(FeatureChoiceEntity::class.java)) {
    override suspend fun sync(it: FeatureChoiceEntity) {
        featureService.insertFeatureChoice(it)
    }
}

class DeleteFeatureOptionsCrossRefWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        featureService.removeFeatureOptionsCrossRef(
            featureId = it.second,
            id = it.first
        )
    }
}

class DeleteOptionsFeatureCrossRefWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        featureService.removeOptionsFeatureCrossRef(
            featureId = it.second,
            choiceId = it.first,
        )
    }
}

class ClearFeatureChoiceIndexRefsWorker : SyncWorker<Int>(TypeToken.get(Int::class.java)) {
    override suspend fun sync(it: Int) {
        featureService.clearFeatureChoiceIndexRefs(it)
    }
}


class PostFeatureChoiceIndexCrossRefWorker : SyncWorker<FeatureChoiceIndexCrossRef>(TypeToken.get(FeatureChoiceIndexCrossRef::class.java)) {
    override suspend fun sync(it: FeatureChoiceIndexCrossRef) {
        featureService.insertFeatureChoiceIndexCrossRef(
            choiceId = it.choiceId,
            index = it.index,
            levels = it.levels,
            classes = it.classes,
            schools = it.schools
        )
    }
}

class PostIndexRefWorker : SyncWorker<IndexRef>(TypeToken.get(IndexRef::class.java)) {
    override suspend fun sync(it: IndexRef) {
        featureService.insertIndexRef(
            index = it.index,
            ids = it.ids
        )
    }
}

class DeleteIdFromRefWorker : SyncWorker<Pair<String, Int>>(stringIdPairToken) {
    override suspend fun sync(it: Pair<String, Int>) {
        featureService.removeIdFromRef(
            id = it.second,
            ref = it.first
        )
    }
}

class PostFeatureSpellCrossRefWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        featureService.insertFeatureSpellCrossRef(
            spellId = it.first,
            featureId = it.second
        )
    }
}

class DeleteFeatureSpellCrossRefWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        featureService.removeFeatureSpellCrossRef(
            spellId = it.first,
            featureId = it.second
        )
    }
}