package gmail.loganchazdon.dndhelper.model.database.daos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.room.*
import gmail.loganchazdon.dndhelper.model.FeatChoiceEntity
import gmail.loganchazdon.dndhelper.model.Feature
import gmail.loganchazdon.dndhelper.model.Race
import gmail.loganchazdon.dndhelper.model.RaceEntity
import gmail.loganchazdon.dndhelper.model.junctionEntities.RaceFeatureCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.RaceSubraceCrossRef
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Dao
abstract class RaceDao {
    @Query("SELECT * FROM races")
    abstract fun getAllRaces(): LiveData<List<Race>>

    @Query("SELECT * FROM races WHERE isHomebrew IS 1")
    abstract fun getHomebrewRaces(): LiveData<List<Race>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRace(newRace: RaceEntity): Long

    @Query("DELETE FROM races WHERE raceId = :id")
    abstract fun deleteRace(id: Int)

    @Query("SELECT * FROM races WHERE raceId = :id")
    protected abstract fun findUnfilledLiveRaceById(id: Int): LiveData<Race>

    fun bindLiveRaceById(id: Int, result: MediatorLiveData<Race>) {
        result.addSource(findUnfilledLiveRaceById(id)) {
            if(it != null) {
                GlobalScope.launch {
                    it.traits = getRaceTraits(id)
                    result.postValue(it)
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

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """SELECT * FROM features 
JOIN SubraceFeatureCrossRef ON features.featureId IS SubraceFeatureCrossRef.featureId 
WHERE subraceId IS :subraceId"""
    )
    abstract fun getSubraceFeatures(subraceId: Int): List<Feature>

    @Query("SELECT * FROM features JOIN RaceFeatureCrossRef ON RaceFeatureCrossRef.featureId IS features.featureId WHERE raceId IS :id")
    protected abstract fun getRaceTraits(id: Int) : List<Feature>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRaceFeatureCrossRef(ref: RaceFeatureCrossRef)

    @Delete
    abstract fun removeRaceFeatureCrossRef(ref: RaceFeatureCrossRef)

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """SELECT * FROM features 
JOIN RaceFeatureCrossRef ON features.featureId IS RaceFeatureCrossRef.featureId 
WHERE raceId is :raceId"""
    )
    abstract fun getRaceFeatures(raceId: Int): List<Feature>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRaceSubraceCrossRef(raceSubraceCrossRef: RaceSubraceCrossRef)

}