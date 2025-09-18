package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.FeatChoiceEntity
import model.Feature
import model.Race
import model.RaceEntity
import model.pojos.NameAndIdPojo

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect abstract class RaceDao {
    abstract suspend fun getRaceFeatures(raceId: Int): List<Feature>
    abstract suspend fun getSubraceFeatures(subraceId: Int): List<Feature>
    abstract suspend fun getSubraceFeatChoices(id: Int): List<FeatChoiceEntity>
    abstract fun getAllRaces(): Flow<List<Race>>
    suspend fun insertRace(newRace: RaceEntity): Int
    abstract suspend fun deleteRace(id: Int)
    abstract fun getHomebrewRaces(): Flow<List<Race>>
    abstract fun findUnfilledLiveRaceById(id: Int): Flow<Race>

    suspend fun insertRaceFeatureCrossRef(featureId: Int, raceId: Int)
    suspend fun removeRaceFeatureCrossRef(featureId: Int, raceId: Int)
    suspend fun insertRaceSubraceCrossRef(subraceId: Int, raceId: Int)
    abstract fun getRaceSubraces(id: Int): Flow<List<NameAndIdPojo>>
    abstract fun getAllRaceIdsAndNames(): Flow<List<NameAndIdPojo>>
}