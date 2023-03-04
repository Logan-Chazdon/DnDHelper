package gmail.loganchazdon.dndhelper.model.database.daos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.room.*
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.junctionEntities.RaceSubraceCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.SubraceFeatChoiceCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.SubraceFeatureCrossRef
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSubrace(subrace: SubraceEntity): Long

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
                            featChoices.add(it.toFeatChoice(emptyList()))
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSubraceFeatureCrossRef(subraceFeatureCrossRef: SubraceFeatureCrossRef)

    @Delete
    abstract fun removeSubraceFeatureCrossRef(subraceFeatureCrossRef: SubraceFeatureCrossRef)

    @Delete
    abstract fun removeRaceSubraceCrossRef(raceSubraceCrossRef: RaceSubraceCrossRef)
}