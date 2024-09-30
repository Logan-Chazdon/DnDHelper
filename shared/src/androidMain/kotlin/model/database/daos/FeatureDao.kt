package model.database.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import model.*

@Dao
actual abstract class FeatureDao {
    actual fun insertFeature(feature: FeatureEntity): Int {
        val id = insertFeatureOrIgnore(feature.asTable()).toInt()
        if (id == -1) {
            updateFeature(feature.asTable())
            return feature.featureId
        }
        return id
    }


    @Update
    protected abstract fun updateFeature(feature: FeatureEntityTable)

    actual fun insertFeatureOptionsCrossRef(featureId: Int, id: Int) {
        insertFeatureOptionsCrossRef(
            FeatureOptionsCrossRef(
                featureId = featureId,
                id = id
            )
        )
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertFeatureOrIgnore(feature: FeatureEntityTable): Long

    @Query("SELECT * FROM features WHERE featureId = :id")
    actual abstract fun getLiveFeatureById(id: Int): Flow<Feature>

    @Query("SELECT * FROM features WHERE featureId = :id")
    abstract fun getFeatureById(id: Int): Feature

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeatureOptionsCrossRef(ref: FeatureOptionsCrossRef)

    @Delete
    abstract fun removeFeatureOptionsCrossRef(ref: FeatureOptionsCrossRef)
    actual fun removeFeatureOptionsCrossRef(featureId: Int, id: Int) {
        removeFeatureOptionsCrossRef(
            FeatureOptionsCrossRef(
                featureId = featureId,
                id = id
            )
        )
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOptionsFeatureCrossRef(ref: OptionsFeatureCrossRef)
    actual fun insertOptionsFeatureCrossRef(
        featureId: Int,
        choiceId: Int
    ) {
        insertOptionsFeatureCrossRef(
            OptionsFeatureCrossRef(
                featureId = featureId,
                choiceId = choiceId
            )
        )
    }

    @Delete
    abstract fun removeOptionsFeatureCrossRef(ref: OptionsFeatureCrossRef)
    actual fun removeOptionsFeatureCrossRef(featureId: Int, choiceId: Int) {
        removeOptionsFeatureCrossRef(
            OptionsFeatureCrossRef(
                featureId = featureId,
                choiceId = choiceId
            )
        )
    }


    actual fun insertFeatureChoice(option: FeatureChoiceEntity): Int {
        val id = insertFeatureChoiceOrIgnore(option.asTable()).toInt()
        if (id == -1) {
            updateFeatureChoice(option.asTable())
            return option.id
        }
        return id
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertFeatureChoiceOrIgnore(option: FeatureChoiceEntityTable): Long

    @Update
    protected abstract fun updateFeatureChoice(option: FeatureChoiceEntityTable)

    @Query("DELETE FROM features WHERE featureId = :id")
    abstract fun removeFeatureChoice(id: Int)

    //This returns all featureChoices associate with a feature. It doesn't contain the options or the chosen fields.
    @Query(
        """SELECT * FROM FeatureChoiceEntity
JOIN FeatureOptionsCrossRef ON FeatureOptionsCrossRef.id IS FeatureChoiceEntity.id
WHERE FeatureOptionsCrossRef.featureId IS :featureId"""
    )
    actual abstract fun getFeatureChoices(featureId: Int): List<FeatureChoiceEntity>

    @Query(
        """SELECT * FROM FeatureChoiceEntity
JOIN FeatureOptionsCrossRef ON FeatureOptionsCrossRef.id IS FeatureChoiceEntity.id
WHERE FeatureOptionsCrossRef.featureId IS :featureId"""
    )
    actual abstract fun getLiveFeatureChoices(featureId: Int): Flow<List<FeatureChoiceEntity>>

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
WHERE (REPLACE(REPLACE(ids, '[', ','), ']', ',') LIKE '%,' || features.featureId || ',%' OR OptionsFeatureCrossRef.choiceId IS :featureChoiceId)
AND (FeatureChoiceIndexCrossRef.levels = 'null'
OR  FeatureChoiceIndexCrossRef.levels IS NULL 
OR FeatureChoiceIndexCrossRef.levels LIKE '%' || spellDetails.level || '%') 
AND(FeatureChoiceIndexCrossRef.schools = 'null' 
OR  FeatureChoiceIndexCrossRef.schools IS NULL 
OR FeatureChoiceIndexCrossRef.schools  LIKE '%' || spellDetails.school || '%' )
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
    actual abstract fun getFeatureChoiceOptions(featureChoiceId: Int): List<Feature>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeatureSpellCrossRef(ref: FeatureSpellCrossRef)
    actual fun insertFeatureSpellCrossRef(spellId: Int, featureId: Int) {
        insertFeatureSpellCrossRef(
            FeatureSpellCrossRef(
                spellId, featureId
            )
        )
    }

    @Delete
    abstract fun removeFeatureSpellCrossRef(ref: FeatureSpellCrossRef)
    actual fun removeFeatureSpellCrossRef(spellId: Int, featureId: Int) {
        removeFeatureSpellCrossRef(
            FeatureSpellCrossRef(spellId, featureId)
        )
    }

    actual fun fillOutFeatureListWithoutChosen(features: List<Feature>) {
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
    actual abstract fun getFeatureSpells(featureId: Int): List<Spell>?

    /**This checks if a IndexRef exists in the database.
     * If it does not it inserts the one passed. If it
     * does then it combines the two index refs and updates the database
     * with the result.
     */
    actual fun insertIndexRef(index: String, ids: List<Int>) {
        val ref = IndexRef(index, ids)
        if (insertIndexRefOrIgnore(ref).toInt() == -1) {
            val oldRef = getIndexRef(ref.index)
            val newIds = oldRef.ids.union(ref.ids).toList()
            val newRef = IndexRef(
                index = ref.index,
                ids = newIds
            )
            updateIndexRef(newRef)
        }
    }


    /**Removes the passed id from the ids list of the IndexRef
     */
    actual fun removeIdFromRef(id: Int, ref: String) {
        val oldRef = getIndexRef(ref)
        val newIds = oldRef.ids.toMutableList()
        newIds.remove(id)
        val newIndexRef = IndexRef(
            index = ref,
            ids = newIds
        )
        updateIndexRef(newIndexRef)
    }

    @Query("SELECT * FROM IndexRef WHERE IndexRef.`index` = :index")
    protected abstract fun getIndexRef(index: String): IndexRef

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertIndexRefOrIgnore(ref: IndexRef): Long

    @Update
    protected abstract fun updateIndexRef(ref: IndexRef)

    @Delete
    abstract fun deleteIndexRef(ref: IndexRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeatureChoiceIndexCrossRef(ref: FeatureChoiceIndexCrossRef)
    actual fun insertFeatureChoiceIndexCrossRef(
        choiceId: Int,
        index: String,
        levels: List<Int>?,
        classes: List<String>?,
        schools: List<String>?
    ) {
        insertFeatureChoiceIndexCrossRef(
            FeatureChoiceIndexCrossRef(
                choiceId = choiceId,
                index = index,
                levels = levels,
                classes = classes,
                schools = schools
            )
        )
    }

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
    actual abstract fun returnGetAllIndexes(): Flow<List<String>>

    @Query("DELETE FROM FeatureChoiceIndexCrossRef WHERE choiceId = :id")
    actual abstract fun clearFeatureChoiceIndexRefs(id: Int)

    @Query("""SELECT featureId FROM FeatureSpellCrossRef WHERE spellId IS :id""")
    actual abstract fun getFeatureIdOr0FromSpellId(id: Int): Int

    @Query("DELETE FROM FeatureChoiceChoiceEntity WHERE choiceId IS :choiceId AND characterId IS :characterId")
    actual abstract fun removeFeatureFeatureChoice(choiceId: Int, characterId: Int)

    @Transaction
    @Query(
        """SELECT * FROM spells
JOIN FeatureSpellCrossRef ON FeatureSpellCrossRef.spellId IS spells.id
WHERE featureId IS :id
"""
    )
    actual abstract fun getLiveFeatureSpells(id: Int): Flow<List<Spell>?>
}