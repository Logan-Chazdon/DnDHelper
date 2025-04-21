package dataManagers

import model.Feature
import model.FeatureChoiceEntity
import model.FeatureEntity
import model.Infusion

class FeatureManager(
    val insertFeature: suspend (FeatureEntity) -> Unit,
    private val _insertFeatureChoiceIndexCrossRef: (
        Int, String,
        levels: List<Int>?,
        classes: List<String>?,
        schools: List<String>?) -> Unit,
    val insertFeatureOptionsCrossRef: suspend (featureId: Int, choiceId: Int) -> Unit,
    val insertFeatureChoice: suspend (FeatureChoiceEntity) -> Unit,
    val insertIndexRef: suspend (ids: List<Int>, index: String) -> Unit,
    val postManeuvers: (List<Feature>) -> Unit,
    val postInvocations: (List<Feature>) -> Unit,
    val postInfusions: (List<Infusion>) -> Unit,
    val insertFeatureSpellCrossRef: (spellId: Int, featureId: Int) -> Unit,
    val insertOptionsFeatureCrossRef: (
        featureId: Int,
        choiceId: Int
    ) -> Unit,
    val getFeatureIdByName : (String) -> Int,
) {
    fun insertFeatureChoiceIndexCrossRef(
        choiceId: Int,
        index: String,
        levels: List<Int>? = null,
        classes: List<String>? = null,
        schools: List<String>? = null,
    ) {
        _insertFeatureChoiceIndexCrossRef(
            choiceId, index, levels, classes, schools
        )
    }
}