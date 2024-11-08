package services

import io.ktor.client.*
import kotlinx.coroutines.flow.Flow
import model.Feature
import model.FeatureChoiceEntity
import model.FeatureEntity
import model.Spell

class FeatureService(client: HttpClient) : Service(client = client) {
    fun fillOutFeatureListWithoutChosen(features: List<Feature>) {
        TODO("Not yet implemented")
    }

    fun insertFeature(feature: FeatureEntity): Int {
        TODO("Not yet implemented")
    }

    fun insertFeatureOptionsCrossRef(featureId: Int, id: Int) {
        TODO("Not yet implemented")
    }

    fun insertFeatureChoice(option: FeatureChoiceEntity): Int {
        TODO("Not yet implemented")
    }

    fun removeFeatureOptionsCrossRef(featureId: Int, id: Int) {
        TODO("Not yet implemented")
    }

    fun removeOptionsFeatureCrossRef(featureId: Int, choiceId: Int) {
        TODO("Not yet implemented")
    }

    fun insertFeatureChoiceIndexCrossRef(
        choiceId: Int,
        index: String,
        levels: List<Int>?,
        classes: List<String>?,
        schools: List<String>?
    ) {
        TODO("Not yet implemented")
    }

    fun insertIndexRef(index: String, ids: List<Int>) {
        TODO("Not yet implemented")
    }

    fun removeIdFromRef(id: Int, ref: String) {
        TODO("Not yet implemented")
    }

    fun insertFeatureSpellCrossRef(spellId: Int, featureId: Int) {
        TODO("Not yet implemented")
    }

    fun removeFeatureSpellCrossRef(spellId: Int, featureId: Int) {
        TODO("Not yet implemented")
    }

    fun insertOptionsFeatureCrossRef(featureId: Int, choiceId: Int) {
        TODO("Not yet implemented")
    }

    fun removeFeatureFeatureChoice(choiceId: Int, characterId: Int) {
        TODO("Not yet implemented")
    }

    fun getFeatureChoices(featureId: Int): List<FeatureChoiceEntity> {
        TODO("Not yet implemented")
    }

    fun getFeatureSpells(featureId: Int): List<Spell>? {
        TODO("Not yet implemented")
    }

    fun getLiveFeatureById(id: Int): Flow<Feature> {
        TODO("Not yet implemented")
    }

    fun getLiveFeatureChoices(featureId: Int): Flow<List<FeatureChoiceEntity>> {
        TODO("Not yet implemented")
    }

    fun getFeatureChoiceOptions(featureChoiceId: Int) : List<Feature>  {
        TODO("Not yet implemented")
    }

    fun clearFeatureChoiceIndexRefs(id: Int) {
        TODO("Not yet implemented")
    }

    fun getFeatureIdOr0FromSpellId(id: Int): Int {
        TODO("Not yet implemented")
    }

    fun getLiveFeatureSpells(id: Int): Flow<List<Spell>?> {
        TODO("Not yet implemented")
    }

    fun returnGetAllIndexes(): Flow<List<String>> {
        TODO("Not yet implemented")
    }
}