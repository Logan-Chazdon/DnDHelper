package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.FeatChoiceEntity
import model.Feature
import model.Race
import model.RaceEntity
import model.pojos.NameAndIdPojo

actual abstract class RaceDao {
    actual abstract fun getRaceFeatures(raceId: Int): List<Feature>
    actual abstract fun getSubraceFeatures(subraceId: Int): List<Feature>
    actual abstract fun getSubraceFeatChoices(id: Int): List<FeatChoiceEntity>
    actual abstract fun getAllRaces(): Flow<List<Race>>
    actual fun insertRace(newRace: RaceEntity): Int {
        TODO("Not yet implemented")
    }

    actual abstract fun deleteRace(id: Int)
    actual abstract fun getHomebrewRaces(): Flow<List<Race>>
    actual abstract fun findUnfilledLiveRaceById(id: Int): Flow<Race>
    actual abstract fun getRaceTraits(id: Int): List<Feature>
    actual fun insertRaceFeatureCrossRef(featureId: Int, raceId: Int) {
    }

    actual fun removeRaceFeatureCrossRef(featureId: Int, raceId: Int) {
    }

    actual fun insertRaceSubraceCrossRef(subraceId: Int, raceId: Int) {
    }

    actual abstract fun getRaceSubraces(id: Int): Flow<List<NameAndIdPojo>>
    actual abstract fun getAllRaceIdsAndNames(): Flow<List<NameAndIdPojo>>
}

class RaceDaoImpl : RaceDao() {
    override fun getRaceFeatures(raceId: Int): List<Feature> {
        TODO("Not yet implemented")
    }

    override fun getSubraceFeatures(subraceId: Int): List<Feature> {
        TODO("Not yet implemented")
    }

    override fun getSubraceFeatChoices(id: Int): List<FeatChoiceEntity> {
        TODO("Not yet implemented")
    }

    override fun getAllRaces(): Flow<List<Race>> {
        TODO("Not yet implemented")
    }

    override fun deleteRace(id: Int) {
        TODO("Not yet implemented")
    }

    override fun getHomebrewRaces(): Flow<List<Race>> {
        TODO("Not yet implemented")
    }

    override fun findUnfilledLiveRaceById(id: Int): Flow<Race> {
        TODO("Not yet implemented")
    }

    override fun getRaceTraits(id: Int): List<Feature> {
        TODO("Not yet implemented")
    }

    override fun getRaceSubraces(id: Int): Flow<List<NameAndIdPojo>> {
        TODO("Not yet implemented")
    }

    override fun getAllRaceIdsAndNames(): Flow<List<NameAndIdPojo>> {
        TODO("Not yet implemented")
    }

}