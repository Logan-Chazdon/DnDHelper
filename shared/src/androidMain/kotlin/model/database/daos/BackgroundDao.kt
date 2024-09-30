package model.database.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import model.*
import model.choiceEntities.BackgroundChoiceEntity


@Dao
actual abstract class BackgroundDao {
    @Query(
        """SELECT * FROM spells 
JOIN BackgroundSpellCrossRef ON BackgroundSpellCrossRef.spellId IS spells.id
WHERE backgroundId IS :backgroundId
    """
    )
    actual abstract fun getBackgroundSpells(backgroundId: Int): List<Spell>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertBackgroundOrIgnore(backgroundEntity: BackgroundEntityTable): Long

    @Update
    protected abstract fun updateBackground(backgroundEntity: BackgroundEntityTable)

    actual fun insertBackground(backgroundEntity: BackgroundEntity): Int {
        val id = insertBackgroundOrIgnore(backgroundEntity.asTable()).toInt()
        if (id == -1) {
            updateBackground(backgroundEntity.asTable())
            return backgroundEntity.id
        }
        return id
    }

    @Query("DELETE FROM backgrounds WHERE id = :id")
    actual abstract fun removeBackgroundById(id: Int)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertBackgroundFeatureCrossRef(ref: BackgroundFeatureCrossRef)

    actual fun insertBackgroundFeatureCrossRef(
        backgroundId: Int,
        featureId: Int
    ) {
        insertBackgroundFeatureCrossRef(BackgroundFeatureCrossRef(backgroundId, featureId))
    }

    @Query("SELECT * FROM BackgroundChoiceEntity WHERE characterId IS :charId")
    actual abstract fun getBackgroundChoiceData(charId: Int): BackgroundChoiceEntity

    @Query(
        """SELECT * FROM features 
JOIN BackgroundFeatureCrossRef ON features.featureId IS BackgroundFeatureCrossRef.featureId
WHERE backgroundId IS :id
    """
    )
    actual abstract fun getBackgroundFeatures(id: Int): List<Feature>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertBackgroundSpellCrossRef(ref: BackgroundSpellCrossRef)

    @Delete
    abstract fun removeBackgroundSpellCrossRef(ref: BackgroundSpellCrossRef)

    actual fun insertBackgroundSpellCrossRef(backgroundId: Int, spellId: Int) {
        insertBackgroundSpellCrossRef(
            BackgroundSpellCrossRef(
                backgroundId = backgroundId,
                spellId = spellId
            )
        )
    }


    @Query("SELECT * FROM backgrounds")
    protected abstract fun getUnfilledBackgrounds(): Flow<List<BackgroundEntity>>

    actual fun getAllBackgrounds(): Flow<List<Background>> {
        return getUnfilledBackgrounds().transform { backgroundEntities ->
            val backgrounds = mutableListOf<Background>()
            backgroundEntities.forEach {
                backgrounds.add(
                    Background(
                        it,
                        getBackgroundFeatures(it.id)
                    )
                )
            }
            emit(backgrounds)
        }
    }

    @Query("SELECT * FROM backgrounds WHERE id IS :id")
    actual abstract fun getUnfilledBackground(id: Int): Flow<BackgroundEntity>


    @Query(
        """SELECT * FROM features
JOIN BackgroundFeatureCrossRef ON BackgroundFeatureCrossRef.featureId IS features.featureId 
WHERE backgroundId IS :id"""
    )
    actual abstract fun getUnfilledBackgroundFeatures(id: Int): List<Feature>

    @Query("SELECT * FROM backgrounds WHERE isHomebrew = 1")
    actual abstract fun getHomebrewBackgrounds(): Flow<List<BackgroundEntity>>

    @Query("DELETE FROM backgrounds WHERE id = :id")
    actual abstract fun deleteBackground(id: Int)
}