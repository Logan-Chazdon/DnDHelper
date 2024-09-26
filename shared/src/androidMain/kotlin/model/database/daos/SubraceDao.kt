package model.database.daos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.room.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import model.*
import model.junctionEntities.RaceSubraceCrossRef
import model.junctionEntities.SubraceFeatChoiceCrossRef
import model.junctionEntities.SubraceFeatureCrossRef

@Dao
abstract class SubraceDao {
    @Query("SELECT * FROM subraces WHERE id = :id")
    protected abstract fun getUnfilledSubrace(id: Int): LiveData<SubraceEntity>

    fun getSubrace(id: Int): LiveData<Subrace> {
        val result = MediatorLiveData<Subrace>()
        result.addSource(getUnfilledSubrace(id)) { entity ->
            if (entity != null) {
                GlobalScope.launch {
                    result.postValue(Subrace(entity, getSubraceTraits(id), null))
                }
            }
        }
        return result
    }


    @Query(
        """SELECT * FROM featChoices
JOIN SubraceFeatChoiceCrossRef ON SubraceFeatChoiceCrossRef.featChoiceId IS featChoices.id
WHERE SubraceFeatChoiceCrossRef.subraceId IS :id
    """
    )
    abstract fun getSubraceFeatChoices(id: Int): List<FeatChoiceEntity>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertSubclassOrIgnore(subClass: SubclassEntity): Long

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
    protected abstract fun insertSubraceOrIgnore(subrace: SubraceEntity): Long

    fun insertSubrace(subrace: SubraceEntity): Int {
        val id = insertSubraceOrIgnore(subrace).toInt()
        if(id == -1) {
            updateSubrace(subrace)
            return subrace.id
        }
        return id
    }

    @Update
    abstract fun updateSubrace(subrace: SubraceEntity)

    @Query(
        """SELECT * FROM subraces
JOIN RaceSubraceCrossRef ON RaceSubraceCrossRef.subraceId IS subraces.id
WHERE raceId IS :raceId
    """
    )
    @Transaction
    protected abstract fun getSubraceOptionsWithoutFeatures(raceId: Int): LiveData<List<SubraceEntity>>

    @Query(
        """SELECT * FROM features
JOIN SubraceFeatureCrossRef ON SubraceFeatureCrossRef.featureId IS features.featureId
WHERE subraceId IS :subraceId
    """
    )
    protected abstract fun getSubraceTraits(subraceId: Int): List<Feature>

    fun bindSubraceOptions(raceId: Int, subraces: MediatorLiveData<List<Subrace>>) {
        subraces.addSource(getSubraceOptionsWithoutFeatures(raceId)) { entityList ->
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
                    subraces.postValue(temp)
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

    @Delete
    abstract fun removeSubraceFeatureCrossRef(subraceFeatureCrossRef: SubraceFeatureCrossRef)

    @Delete
    abstract fun removeRaceSubraceCrossRef(raceSubraceCrossRef: RaceSubraceCrossRef)

    @Query("SELECT * FROM subraces WHERE isHomebrew IS 1")
    abstract fun getHomebrewSubraces(): LiveData<List<SubraceEntity>>


    @Query(
        """SELECT * FROM features 
JOIN SubraceFeatureCrossRef ON features.featureId IS SubraceFeatureCrossRef.featureId 
WHERE subraceId IS :id"""
    )
    abstract fun getSubraceLiveFeaturesById(id: Int): LiveData<List<Feature>>

    @Query("DELETE FROM subraces WHERE id = :id")
    abstract fun deleteSubrace(id: Int)
}