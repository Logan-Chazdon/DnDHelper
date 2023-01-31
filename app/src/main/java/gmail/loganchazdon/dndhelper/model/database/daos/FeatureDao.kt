package gmail.loganchazdon.dndhelper.model.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.junctionEntities.*

@Dao
abstract class FeatureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeature(feature: FeatureEntity): Long

    @Query("SELECT * FROM features WHERE featureId = :id")
    abstract fun getLiveFeatureById(id: Int): LiveData<Feature>

    @Query("SELECT * FROM features WHERE featureId = :id")
    abstract fun getFeatureById(id: Int): Feature

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeatureOptionsCrossRef(ref: FeatureOptionsCrossRef)

    @Delete
    abstract fun removeFeatureOptionsCrossRef(ref: FeatureOptionsCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOptionsFeatureCrossRef(ref: OptionsFeatureCrossRef)

    @Delete
    abstract fun removeOptionsFeatureCrossRef(ref: OptionsFeatureCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeatureChoice(option: FeatureChoiceEntity): Long

    @Query("DELETE FROM features WHERE featureId = :id")
    abstract fun removeFeatureChoice(id: Int)

    //This returns all featureChoices associate with a feature. It doesn't not contain the options or the chosen fields.
    @Query(
        """SELECT * FROM FeatureChoiceEntity 
JOIN FeatureOptionsCrossRef ON FeatureOptionsCrossRef.id IS FeatureChoiceEntity.id
WHERE FeatureOptionsCrossRef.featureId IS :featureId"""
    )
    abstract fun getFeatureChoices(featureId: Int): List<FeatureChoiceEntity>

    //This returns all features which belong in the options field of a featureChoice.
    @Query("""SELECT * FROM features
LEFT JOIN OptionsFeatureCrossRef ON OptionsFeatureCrossRef.featureId IS features.featureId
LEFT JOIN FeatureChoiceIndexCrossRef ON FeatureChoiceIndexCrossRef.choiceId IS :featureChoiceId
LEFT JOIN IndexRef ON IndexRef.`index` IS FeatureChoiceIndexCrossRef.`index`
WHERE ',' || ids || ',' LIKE '%,' || features.featureId || ',%' OR OptionsFeatureCrossRef.choiceId IS :featureChoiceId""")
    protected abstract fun getFeatureChoiceOptions(featureChoiceId: Int): List<Feature>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeatureSpellCrossRef(ref: FeatureSpellCrossRef)

    @Delete
    abstract fun removeFeatureSpellCrossRef(ref: FeatureSpellCrossRef)

    fun fillOutFeatureListWithoutChosen(features: List<Feature>) {
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

    @Query(
        """SELECT * FROM spells
JOIN FeatureSpellCrossRef ON FeatureSpellCrossRef.spellId IS spells.id
WHERE featureId IS :featureId
"""
    )
    abstract fun getFeatureSpells(featureId: Int): List<Spell>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertIndexRef(ref: IndexRef)

    @Delete
    abstract fun deleteIndexRef(ref: IndexRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeatureChoiceIndexCrossRef(ref: FeatureChoiceIndexCrossRef)

    @Delete
    abstract fun deleteFeatureChoiceIndexCrossRef(ref: FeatureChoiceIndexCrossRef)
}