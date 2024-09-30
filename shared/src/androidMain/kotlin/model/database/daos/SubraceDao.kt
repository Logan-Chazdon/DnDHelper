package model.database.daos

import androidx.room.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import model.*

@Dao
actual abstract class SubraceDao {
    @Query("SELECT * FROM subraces WHERE id = :id")
    protected abstract fun getUnfilledSubrace(id: Int): Flow<SubraceEntity>

    actual fun getSubrace(id: Int): Flow<Subrace> {
        return getUnfilledSubrace(id).transform { entity ->
            if (entity != null) {
                GlobalScope.launch {
                    emit(Subrace(entity, getSubraceTraits(id), null))
                }
            }
        }
    }


    @Query(
        """SELECT * FROM featChoices
JOIN SubraceFeatChoiceCrossRef ON SubraceFeatChoiceCrossRef.featChoiceId IS featChoices.id
WHERE SubraceFeatChoiceCrossRef.subraceId IS :id
    """
    )
    abstract fun getSubraceFeatChoices(id: Int): List<FeatChoiceEntity>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertSubclassOrIgnore(subClass: SubclassEntityTable): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSubraceFeatChoiceCrossRef(subraceFeatChoiceCrossRef: SubraceFeatChoiceCrossRef)


    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """SELECT * FROM features 
JOIN SubraceFeatureCrossRef ON features.featureId IS SubraceFeatureCrossRef.featureId 
WHERE subraceId IS :subraceId"""
    )
    protected abstract fun getSubraceFeatures(subraceId: Int): List<Feature>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertSubraceOrIgnore(subrace: SubraceEntityTable): Long

    actual fun insertSubrace(subrace: SubraceEntity): Int {
        val id = insertSubraceOrIgnore(subrace.asTable()).toInt()
        if(id == -1) {
            updateSubrace(subrace.asTable())
            return subrace.id
        }
        return id
    }

    @Update
    abstract fun updateSubrace(subrace: SubraceEntityTable)

    @Query(
        """SELECT * FROM subraces
JOIN RaceSubraceCrossRef ON RaceSubraceCrossRef.subraceId IS subraces.id
WHERE raceId IS :raceId
    """
    )
    @Transaction
    protected abstract fun getSubraceOptionsWithoutFeatures(raceId: Int): Flow<List<SubraceEntity>>

    @Query(
        """SELECT * FROM features
JOIN SubraceFeatureCrossRef ON SubraceFeatureCrossRef.featureId IS features.featureId
WHERE subraceId IS :subraceId
    """
    )
    protected abstract fun getSubraceTraits(subraceId: Int): List<Feature>

    actual fun bindSubraceOptions(raceId: Int): Flow<MutableList<Subrace>> {
        return getSubraceOptionsWithoutFeatures(raceId).transform { entityList ->
            if (entityList != null) {
                GlobalScope.launch {
                    val temp: MutableList<Subrace> = mutableListOf()
                    entityList.forEach { subraceEntity ->
                        val featChoices = mutableListOf<FeatChoice>()
                        getSubraceFeatChoices(subraceEntity.id).forEach {
                            featChoices.add(it.toFeatChoice(chosen = emptyList(), from = getFeatChoiceOptions(it.id)))
                        }
                        temp.add(
                            Subrace(
                                subraceEntity,
                                getSubraceTraits(subraceEntity.id),
                                featChoices
                            )
                        )
                    }
                    emit(temp)
                }
            }
        }
    }

    /**
     Returns all feats which belong in the options field of a feature choice.
     Note this uses FeatChoiceCrossRef to fetch the feats. If there are no
     FeatChoiceCrossRefs this function assumes it is designed to return all feats.
     */
    @Query("""
WITH featIds AS (SELECT COUNT(FeatChoiceFeatCrossRef.featId) as amt FROM FeatChoiceFeatCrossRef WHERE FeatChoiceFeatCrossRef.featChoiceId IS :id)
SELECT feats.* FROM feats, featIds
LEFT JOIN FeatChoiceFeatCrossRef ON FeatChoiceFeatCrossRef.featId IS feats.id
WHERE featChoiceId IS :id OR featIds.amt IS 0""")
    protected abstract fun getFeatChoiceOptions(id: Int) : List<Feat>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSubraceFeatureCrossRef(subraceFeatureCrossRef: SubraceFeatureCrossRef)
    actual fun insertSubraceFeatureCrossRef(subraceId: Int, featureId: Int) {
        insertSubraceFeatureCrossRef(
            SubraceFeatureCrossRef(subraceId, featureId)
        )
    }

    @Query(
        """SELECT * FROM features 
JOIN SubraceFeatureCrossRef ON features.featureId IS SubraceFeatureCrossRef.featureId 
WHERE subraceId IS :id"""
    )
    actual abstract fun getSubraceLiveFeaturesById(id: Int): Flow<List<Feature>>

    @Delete
    abstract fun removeSubraceFeatureCrossRef(subraceFeatureCrossRef: SubraceFeatureCrossRef)

    @Delete
    abstract fun removeRaceSubraceCrossRef(raceSubraceCrossRef: RaceSubraceCrossRef)


    @Query("DELETE FROM subraces WHERE id = :id")
    actual abstract fun deleteSubrace(id: Int)
    actual fun removeSubraceFeatureCrossRef(subraceId: Int, featureId: Int) {
        removeSubraceFeatureCrossRef(
            SubraceFeatureCrossRef(subraceId, featureId)
        )
    }

    actual fun removeRaceSubraceCrossRef(raceId: Int, subraceId: Int) {
        removeRaceSubraceCrossRef(
            RaceSubraceCrossRef(
                subraceId = subraceId,
                raceId = raceId
            )
        )
    }

    @Query("SELECT * FROM subraces WHERE isHomebrew IS 1")
    actual abstract fun getHomebrewSubraces(): Flow<List<SubraceEntity>>
}