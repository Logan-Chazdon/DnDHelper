package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.*
import model.pojos.NameAndIdPojo

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect abstract class RaceDao {
    abstract fun getRaceFeatures(raceId: Int): List<Feature>
    abstract fun getSubraceFeatures(subraceId: Int): List<Feature>
    abstract fun getSubraceFeatChoices(id: Int): List<FeatChoiceEntity>
    abstract fun getAllRaces(): Flow<List<Race>>
    fun insertRace(newRace: RaceEntity): Int
    abstract fun deleteRace(id: Int)
    abstract fun getHomebrewRaces(): Flow<List<Race>>
    abstract fun findUnfilledLiveRaceById(id: Int): Flow<Race>
    abstract fun getRaceTraits(id: Int): List<Feature>
    fun insertRaceFeatureCrossRef(featureId: Int, raceId: Int)
    fun removeRaceFeatureCrossRef(featureId: Int, raceId: Int)
    fun insertRaceSubraceCrossRef(subraceId: Int, raceId: Int)
    abstract fun getRaceSubraces(id: Int): Flow<List<NameAndIdPojo>>
    abstract fun getAllRaceIdsAndNames(): Flow<List<NameAndIdPojo>>
}