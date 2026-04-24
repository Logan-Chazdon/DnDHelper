package model.database.daos

import androidx.room.Query
import model.Feature
import model.FeatureChoice
import model.FeatureChoiceEntity
import model.Spell

abstract class FilledFeatureDao {
    protected suspend fun fillOutFeatureListWithoutChosen(features: List<Feature>) {
        features.forEach { feature ->
            feature.spells = getFeatureSpells(feature.featureId)
            feature.choices = getFeatureChoices(feature.featureId).let { choiceEntities ->
                val temp = mutableListOf<FeatureChoice>()
                choiceEntities.forEach { choice ->
                    val filledChoice = FeatureChoice(
                        entity = choice,
                        options = getFeatureChoiceOptions(choice.id),
                        chosen = null
                    )
                    filledChoice.options?.let { fillOutFeatureListWithoutChosen(it) }
                    temp.add(
                        filledChoice
                    )
                }
                temp
            }
        }
    }

    //This returns all featureChoices associate with a feature. It doesn't contain the options or the chosen fields.
    @Query(
        """SELECT * FROM FeatureChoiceEntity
JOIN FeatureOptionsCrossRef ON FeatureOptionsCrossRef.id IS FeatureChoiceEntity.id
WHERE FeatureOptionsCrossRef.featureId IS :featureId"""
    )
    abstract suspend fun getFeatureChoices(featureId: Int): List<FeatureChoiceEntity>

    /**This returns all features which belong in the options field of a featureChoice.*/
    @Query(
        """
WITH spellDetails AS (SELECT classes, school, level, id FROM spells)        
SELECT DISTINCT features.* FROM features
LEFT JOIN OptionsFeatureCrossRef ON OptionsFeatureCrossRef.featureId IS features.featureId
LEFT JOIN FeatureChoiceIndexCrossRef ON FeatureChoiceIndexCrossRef.choiceId IS :featureChoiceId
LEFT JOIN IndexRef ON LOWER(IndexRef.`index`) IS LOWER(FeatureChoiceIndexCrossRef.`index`)
LEFT JOIN FeatureSpellCrossRef ON FeatureSpellCrossRef.featureId IS features.featureId
LEFT JOIN spellDetails ON spellDetails.id IS FeatureSpellCrossRef.spellId
WHERE (',' || SUBSTR(ids, 2, LENGTH(ids) - 2) || ','
      LIKE '%,' || CAST(features.featureId AS TEXT) || ',%'
         OR ids = '[' || CAST(features.featureId AS TEXT) || ']' OR OptionsFeatureCrossRef.choiceId IS :featureChoiceId)
AND (FeatureChoiceIndexCrossRef.levels = 'null'
OR  FeatureChoiceIndexCrossRef.levels IS NULL
OR FeatureChoiceIndexCrossRef.levels LIKE '%' || spellDetails.level || '%')
AND(FeatureChoiceIndexCrossRef.schools = 'null'
OR  FeatureChoiceIndexCrossRef.schools IS NULL
OR FeatureChoiceIndexCrossRef.schools  LIKE '%' || spellDetails.school || '%' )
AND (FeatureChoiceIndexCrossRef.classes LIKE 'null' OR FeatureChoiceIndexCrossRef.classes IS NULL
OR spellDetails.classes LIKE '%' || SUBSTR(FeatureChoiceIndexCrossRef.classes, 3, LENGTH(FeatureChoiceIndexCrossRef.classes) - 4) || '%')
"""
    )
    abstract suspend fun getFeatureChoiceOptions(featureChoiceId: Int): List<Feature>

    @Query(
        """SELECT * FROM spells
JOIN FeatureSpellCrossRef ON FeatureSpellCrossRef.spellId IS spells.id
WHERE featureId IS :featureId
"""
    )
    abstract suspend fun getFeatureSpells(featureId: Int): List<Spell>?
}