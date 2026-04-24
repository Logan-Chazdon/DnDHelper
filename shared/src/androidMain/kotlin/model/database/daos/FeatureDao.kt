package model.database.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import model.*

@Dao
actual abstract class FeatureDao  : FilledFeatureDao() {
    actual suspend fun insertFeature(feature: FeatureEntity): Int {
        val id = insertFeatureOrIgnore(feature.asTable()).toInt()
        if (id == -1) {
            updateFeature(feature.asTable())
            return feature.featureId
        }
        return id
    }


    @Update
    protected abstract fun updateFeature(feature: FeatureEntityTable)

    actual suspend fun insertFeatureOptionsCrossRef(featureId: Int, id: Int) {
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
    actual suspend fun removeFeatureOptionsCrossRef(featureId: Int, id: Int) {
        removeFeatureOptionsCrossRef(
            FeatureOptionsCrossRef(
                featureId = featureId,
                id = id
            )
        )
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOptionsFeatureCrossRef(ref: OptionsFeatureCrossRef)
    actual suspend fun insertOptionsFeatureCrossRef(
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
    actual suspend fun removeOptionsFeatureCrossRef(featureId: Int, choiceId: Int) {
        removeOptionsFeatureCrossRef(
            OptionsFeatureCrossRef(
                featureId = featureId,
                choiceId = choiceId
            )
        )
    }


    actual suspend fun insertFeatureChoice(option: FeatureChoiceEntity): Int {
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



    @Query(
        """SELECT * FROM FeatureChoiceEntity
JOIN FeatureOptionsCrossRef ON FeatureOptionsCrossRef.id IS FeatureChoiceEntity.id
WHERE FeatureOptionsCrossRef.featureId IS :featureId"""
    )
    actual abstract fun getLiveFeatureChoices(featureId: Int): Flow<List<FeatureChoiceEntity>>




    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeatureSpellCrossRef(ref: FeatureSpellCrossRef)
    actual suspend fun insertFeatureSpellCrossRef(spellId: Int, featureId: Int) {
        insertFeatureSpellCrossRef(
            FeatureSpellCrossRef(
                spellId, featureId
            )
        )
    }

    @Delete
    abstract fun removeFeatureSpellCrossRef(ref: FeatureSpellCrossRef)
    actual suspend fun removeFeatureSpellCrossRef(spellId: Int, featureId: Int) {
        removeFeatureSpellCrossRef(
            FeatureSpellCrossRef(spellId, featureId)
        )
    }




    /**This checks if a IndexRef exists in the database.
     * If it does not it inserts the one passed. If it
     * does then it combines the two index refs and updates the database
     * with the result.
     */
    actual suspend fun insertIndexRef(index: String, ids: List<Int>) {
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
    actual suspend fun removeIdFromRef(id: Int, ref: String) {
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
    actual suspend fun insertFeatureChoiceIndexCrossRef(
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
    actual abstract  suspend fun clearFeatureChoiceIndexRefs(id: Int)

    @Query("""SELECT featureId FROM FeatureSpellCrossRef WHERE spellId IS :id""")
    actual abstract suspend fun getFeatureIdOr0FromSpellId(id: Int): Int

    @Query("DELETE FROM FeatureChoiceChoiceEntity WHERE choiceId IS :choiceId AND characterId IS :characterId")
    actual abstract suspend fun removeFeatureFeatureChoice(choiceId: Int, characterId: Int)

    @Transaction
    @Query(
        """SELECT * FROM spells
JOIN FeatureSpellCrossRef ON FeatureSpellCrossRef.spellId IS spells.id
WHERE featureId IS :id
"""
    )
    actual abstract fun getLiveFeatureSpells(id: Int): Flow<List<Spell>?>

    @Query("SELECT * FROM FeatureOptionsCrossRef JOIN features ON features.featureId IS FeatureOptionsCrossRef.featureId WHERE isHomebrew")
    abstract fun featureOptionsTable(): List<FeatureOptionsCrossRef>

    @Query("SELECT * FROM FeatureSpellCrossRef JOIN features ON features.featureId IS FeatureSpelLCrossRef.featureId WHERE isHomebrew")
    abstract fun featureSpellTable(): List<FeatureSpellCrossRef>

    @Query("SELECT * FROM OptionsFeatureCrossRef JOIN features ON OptionsFeatureCrossRef.featureId IS features.featureId WHERE features.isHomebrew")
    abstract fun optionsFeatureTable(): List<OptionsFeatureCrossRef>

    @Query("SELECT * FROM features WHERE isHomebrew")
    abstract fun featureTable(): List<FeatureEntity>

    @Query("DELETE FROM features WHERE featureId =:featureId")
    abstract fun deleteFeature(featureId: Int)


    @Query("""SELECT features.* FROM features
JOIN FeatFeatureCrossRef ON FeatFeatureCrossRef.featureId IS features.featureId
WHERE featId IS :featId
    """)
    abstract suspend fun getUnfilledFeatFeatures(featId: Int) : List<Feature>

    @Query(
        """SELECT * FROM features
JOIN BackgroundFeatureCrossRef ON BackgroundFeatureCrossRef.featureId IS features.featureId 
WHERE backgroundId IS :id"""
    )
    abstract suspend fun getUnfilledBackgroundFeatures(id: Int): List<Feature>

    @Query(
        """SELECT * FROM features
JOIN ClassFeatureCrossRef ON ClassFeatureCrossRef.featureId IS features.featureId
WHERE ClassFeatureCrossRef.id IS :id"""
    )
    abstract suspend fun getUnfilledLevelPath(id: Int): MutableList<Feature>

    @Query("SELECT * FROM features JOIN RaceFeatureCrossRef ON RaceFeatureCrossRef.featureId IS features.featureId WHERE raceId IS :id")
    abstract suspend fun getUnfilledRaceTraits(id: Int): List<Feature>



    /**Fetch a classes features*/
    actual suspend fun getFilledLevelPath(id: Int): MutableList<Feature> {2
        val features = getUnfilledLevelPath(id)
        fillOutFeatureListWithoutChosen(features)
        return features
    }

    actual suspend fun getFilledBackgroundFeatures(id: Int): List<Feature> {
        val features = getUnfilledBackgroundFeatures(id)
        fillOutFeatureListWithoutChosen(features)
        return features
    }

    actual suspend fun getFeatFeatures(featId: Int): List<Feature> {
        val features = getUnfilledFeatFeatures(featId)
        fillOutFeatureListWithoutChosen(features)
        return features
    }

    actual suspend fun getRaceTraits(id: Int): List<Feature> {
        val features = getUnfilledRaceTraits(id)
        fillOutFeatureListWithoutChosen(features)
        return features
    }
}