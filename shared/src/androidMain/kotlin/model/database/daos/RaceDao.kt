package model.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import model.FeatChoiceEntity
import model.Feature
import model.Race
import model.RaceEntity
import model.junctionEntities.RaceFeatureCrossRef
import model.junctionEntities.RaceSubraceCrossRef
import model.pojos.NameAndIdPojo

@Dao
abstract class RaceDao {
    @Query("SELECT * FROM races")
    abstract fun getAllRaces(): LiveData<List<Race>>

    @Query("SELECT * FROM races WHERE isHomebrew IS 1")
    abstract fun getHomebrewRaces(): LiveData<List<Race>>


    fun insertRace(newRace: RaceEntity): Int {
        val id = insertRaceOrIgnore(newRace).toInt()
        if(id == -1) {
            updateRace(newRace)
            return newRace.raceId
        }
        return id
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertRaceOrIgnore(newRace: RaceEntity): Long

    @Update
    protected abstract fun updateRace(newRace: RaceEntity)

    @Query("DELETE FROM races WHERE raceId = :id")
    abstract fun deleteRace(id: Int)

    @Query("SELECT * FROM races WHERE raceId = :id")
    abstract fun findUnfilledLiveRaceById(id: Int): LiveData<Race>


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
    abstract fun getRaceTraits(id: Int) : List<Feature>

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

    @Query("SELECT id, name FROM subraces JOIN RaceSubraceCrossRef ON RaceSubraceCrossRef.subraceId IS subraces.id WHERE raceId IS :id")
    abstract fun getRaceSubraces(id: Int): LiveData<List<NameAndIdPojo>>

    @Query("SELECT raceId AS id, raceName AS name FROM races")
    abstract fun getAllRaceIdsAndNames(): LiveData<List<NameAndIdPojo>>
}