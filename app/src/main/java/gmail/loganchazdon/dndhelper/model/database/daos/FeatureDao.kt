package gmail.loganchazdon.dndhelper.model.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.junctionEntities.*

@Dao
abstract class FeatureDao {
    fun insertFeature(feature: FeatureEntity): Int {
        val id = insertFeatureOrIgnore(feature).toInt()
        if(id == -1) {
            updateFeature(feature)
            return feature.featureId
        }
        return id
    }


    @Update
    protected abstract fun updateFeature(feature: FeatureEntity)


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertFeatureOrIgnore(feature: FeatureEntity) : Long

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


    fun insertFeatureChoice(option: FeatureChoiceEntity): Int {
        val id = insertFeatureChoiceOrIgnore(option).toInt()
        if(id == -1) {
            updateFeatureChoice(option)
            return option.id
        }
        return id
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertFeatureChoiceOrIgnore(option : FeatureChoiceEntity) : Long

    @Update
    protected abstract fun updateFeatureChoice(option: FeatureChoiceEntity)

    @Query("DELETE FROM features WHERE featureId = :id")
    abstract fun removeFeatureChoice(id: Int)

    //This returns all featureChoices associate with a feature. It doesn't not contain the options or the chosen fields.
    @Query(
        """SELECT * FROM FeatureChoiceEntity 
JOIN FeatureOptionsCrossRef ON FeatureOptionsCrossRef.id IS FeatureChoiceEntity.id
WHERE FeatureOptionsCrossRef.featureId IS :featureId"""
    )
    abstract fun getFeatureChoices(featureId: Int): List<FeatureChoiceEntity>

    @Query(
        """SELECT * FROM FeatureChoiceEntity 
JOIN FeatureOptionsCrossRef ON FeatureOptionsCrossRef.id IS FeatureChoiceEntity.id
WHERE FeatureOptionsCrossRef.featureId IS :featureId"""
    )
    abstract fun getLiveFeatureChoices(featureId: Int): LiveData<List<FeatureChoiceEntity>>

    /**This returns all features which belong in the options field of a featureChoice.*/
    @Query(
        """
WITH spellDetails AS (SELECT classes, school, level, id FROM spells)        
SELECT DISTINCT features.* FROM features
LEFT JOIN OptionsFeatureCrossRef ON OptionsFeatureCrossRef.featureId IS features.featureId
LEFT JOIN FeatureChoiceIndexCrossRef ON FeatureChoiceIndexCrossRef.choiceId IS :featureChoiceId
LEFT JOIN IndexRef ON IndexRef.`index` IS FeatureChoiceIndexCrossRef.`index`
LEFT JOIN FeatureSpellCrossRef ON FeatureSpellCrossRef.featureId IS features.featureId
LEFT JOIN spellDetails ON spellDetails.id IS FeatureSpellCrossRef.spellId
WHERE (',' || ids || ',' LIKE '%,' || features.featureId || ',%' OR OptionsFeatureCrossRef.choiceId IS :featureChoiceId)
AND (FeatureChoiceIndexCrossRef.levels = 'null'
OR  FeatureChoiceIndexCrossRef.levels IS NULL 
OR FeatureChoiceIndexCrossRef.levels LIKE '%' || spellDetails.level || '%') 
AND(FeatureChoiceIndexCrossRef.schools = 'null' 
OR  FeatureChoiceIndexCrossRef.schools IS NULL 
OR ',' || FeatureChoiceIndexCrossRef.schools || ',' LIKE '%,' || spellDetails.school || ',%' )
AND (
   FeatureChoiceIndexCrossRef.classes LIKE 'null' OR FeatureChoiceIndexCrossRef.classes IS NULL
   OR 0 NOT LIKE (
SELECT COUNT(*)
FROM
  (SELECT replace(FeatureChoiceIndexCrossRef.classes, ',', '') AS fcic_class
   FROM FeatureChoiceIndexCrossRef) AS vt
WHERE spellDetails.classes LIKE '%' ||  vt.fcic_class || '%'  
	))
"""
    )
    abstract fun getFeatureChoiceOptions(featureChoiceId: Int): List<Feature>


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


    fun insertIndexRef(ref: IndexRef) {
        if(insertIndexRefOrIgnore(ref).toInt() == -1) {
            updateIndexRef(ref)
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertIndexRefOrIgnore(ref: IndexRef) : Long

    @Update
    protected abstract fun updateIndexRef(ref: IndexRef)

    @Delete
    abstract fun deleteIndexRef(ref: IndexRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeatureChoiceIndexCrossRef(ref: FeatureChoiceIndexCrossRef)

    @Delete
    abstract fun deleteFeatureChoiceIndexCrossRef(ref: FeatureChoiceIndexCrossRef)

    @Query(
        """
SELECT features.featureId FROM features
JOIN IndexRef ON IndexRef.`index` IS 'Fighting Styles' AND (',' || ids || ',' LIKE '%,' || features.featureId || ',%') 
WHERE name LIKE :index
        """
    )
    abstract fun getFightingStyleIdByName(index: String): Int

    @Query("SELECT IndexRef.'index' FROM IndexRef")
    abstract fun returnGetAllIndexes(): LiveData<List<String>>

    @Query("DELETE FROM FeatureChoiceIndexCrossRef WHERE choiceId = :id")
    abstract fun clearFeatureChoiceIndexRefs(id: Int)
}