package model.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import model.*
import model.pojos.NameAndIdPojo

@Dao
actual abstract class RaceDao {
    @Query("SELECT * FROM races")
    actual abstract fun getAllRaces(): Flow<List<Race>>

    @Query("SELECT * FROM races WHERE isHomebrew IS 1")
    actual abstract fun getHomebrewRaces(): Flow<List<Race>>


    actual fun insertRace(newRace: RaceEntity): Int {
        val id = insertRaceOrIgnore(newRace.asTable()).toInt()
        if (id == -1) {
            updateRace(newRace.asTable())
            return newRace.raceId
        }
        return id
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    protected abstract fun insertRaceOrIgnore(newRace: RaceEntityTable): Long

    @Update
    protected abstract fun updateRace(newRace: RaceEntityTable)

    @Query("DELETE FROM races WHERE raceId = :id")
    actual abstract fun deleteRace(id: Int)

    @Query("SELECT * FROM races WHERE raceId = :id")
    actual abstract fun findUnfilledLiveRaceById(id: Int): Flow<Race>


    @Query(
        """SELECT * FROM featChoices
JOIN SubraceFeatChoiceCrossRef ON SubraceFeatChoiceCrossRef.featChoiceId IS featChoices.id
WHERE SubraceFeatChoiceCrossRef.subraceId IS :id
    """
    )
    actual abstract fun getSubraceFeatChoices(id: Int): List<FeatChoiceEntity>

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """SELECT * FROM features 
JOIN SubraceFeatureCrossRef ON features.featureId IS SubraceFeatureCrossRef.featureId 
WHERE subraceId IS :subraceId"""
    )
    actual abstract fun getSubraceFeatures(subraceId: Int): List<Feature>

    @Query("SELECT * FROM features JOIN RaceFeatureCrossRef ON RaceFeatureCrossRef.featureId IS features.featureId WHERE raceId IS :id")
    actual abstract fun getRaceTraits(id: Int): List<Feature>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRaceFeatureCrossRef(ref: RaceFeatureCrossRef)
    actual fun insertRaceFeatureCrossRef(featureId: Int, raceId: Int) {
        insertRaceFeatureCrossRef(
            RaceFeatureCrossRef(
                featureId = featureId,
                raceId = raceId
            )
        )
    }

    @Delete
    abstract fun removeRaceFeatureCrossRef(ref: RaceFeatureCrossRef)
    actual fun removeRaceFeatureCrossRef(featureId: Int, raceId: Int) {
        removeRaceFeatureCrossRef(
            RaceFeatureCrossRef(
                featureId, raceId
            )
        )
    }

    @RewriteQueriesToDropUnusedColumns
    @Query(
        """SELECT * FROM features 
JOIN RaceFeatureCrossRef ON features.featureId IS RaceFeatureCrossRef.featureId 
WHERE raceId is :raceId"""
    )
    actual abstract fun getRaceFeatures(raceId: Int): List<Feature>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRaceSubraceCrossRef(raceSubraceCrossRef: RaceSubraceCrossRef)
    actual fun insertRaceSubraceCrossRef(subraceId: Int, raceId: Int) {
        insertRaceSubraceCrossRef(
            RaceSubraceCrossRef(
                subraceId = subraceId,
                raceId = raceId
            )
        )
    }

    @Query("SELECT id, name FROM subraces JOIN RaceSubraceCrossRef ON RaceSubraceCrossRef.subraceId IS subraces.id WHERE raceId IS :id")
    actual abstract fun getRaceSubraces(id: Int): Flow<List<NameAndIdPojo>>

    @Query("SELECT raceId AS id, raceName AS name FROM races")
    actual abstract fun getAllRaceIdsAndNames(): Flow<List<NameAndIdPojo>>
}