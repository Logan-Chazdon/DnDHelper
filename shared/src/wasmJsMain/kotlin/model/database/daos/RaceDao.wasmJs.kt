package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.FeatChoiceEntity
import model.Feature
import model.Race
import model.RaceEntity
import model.pojos.NameAndIdPojo
import services.RaceService

actual abstract class RaceDao {
    protected val raceService: RaceService
    constructor(raceService: RaceService) {
        this.raceService = raceService
    }

    actual abstract suspend fun getRaceFeatures(raceId: Int): List<Feature>
    actual abstract suspend fun getSubraceFeatures(subraceId: Int): List<Feature>
    actual abstract suspend fun getSubraceFeatChoices(id: Int): List<FeatChoiceEntity>
    actual abstract fun getAllRaces(): Flow<List<Race>>
    actual suspend fun insertRace(newRace: RaceEntity): Int {
        return raceService.insertRace(newRace)
    }

    actual abstract suspend fun deleteRace(id: Int)
    actual abstract fun getHomebrewRaces(): Flow<List<Race>>
    actual abstract fun findUnfilledLiveRaceById(id: Int): Flow<Race>
    actual abstract suspend fun getRaceTraits(id: Int): List<Feature>
    actual suspend fun insertRaceFeatureCrossRef(featureId: Int, raceId: Int) {
        raceService.insertRaceFeatureCrossRef(featureId, raceId)
    }

    actual suspend fun removeRaceFeatureCrossRef(featureId: Int, raceId: Int) {
        raceService.removeRaceFeatureCrossRef(featureId, raceId)
    }

    actual suspend fun insertRaceSubraceCrossRef(subraceId: Int, raceId: Int) {
        raceService.insertRaceSubraceCrossRef(subraceId, raceId)
    }

    actual abstract fun getRaceSubraces(id: Int): Flow<List<NameAndIdPojo>>
    actual abstract fun getAllRaceIdsAndNames(): Flow<List<NameAndIdPojo>>
}

class RaceDaoImpl(raceService: RaceService) : RaceDao(raceService) {
    override suspend fun getRaceFeatures(raceId: Int): List<Feature> {
        return raceService.getRaceFeatures(raceId)
    }

    override suspend fun getSubraceFeatures(subraceId: Int): List<Feature> {
        return raceService.getSubraceFeatures(subraceId)
    }

    override suspend fun getSubraceFeatChoices(id: Int): List<FeatChoiceEntity> {
        return raceService.getSubraceFeatChoices(id)
    }

    override fun getAllRaces(): Flow<List<Race>> {
        return raceService.getAllRaces()
    }

    override suspend fun deleteRace(id: Int) {
       raceService.deleteRace(id)
    }

    override fun getHomebrewRaces(): Flow<List<Race>> {
        return raceService.getHomebrewRaces()
    }

    override fun findUnfilledLiveRaceById(id: Int): Flow<Race> {
        return raceService.findUnfilledLiveRaceById(id)
    }

    override suspend fun getRaceTraits(id: Int): List<Feature> {
        return raceService.getRaceFeatures(id)
    }

    override fun getRaceSubraces(id: Int): Flow<List<NameAndIdPojo>> {
        return raceService.getRaceSubraces(id)
    }

    override fun getAllRaceIdsAndNames(): Flow<List<NameAndIdPojo>> {
        return raceService.getAllRaceIdsAndNames()
    }

}