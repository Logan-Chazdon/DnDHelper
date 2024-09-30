package model.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import model.*
import model.database.daos.FeatureDao
import model.database.daos.RaceDao
import model.database.daos.SubraceDao
import model.pojos.NameAndIdPojo


class RaceRepository {
    val raceDao: RaceDao
    val subraceDao: SubraceDao
    val featureDao: FeatureDao

    constructor(raceDao: RaceDao, subraceDao: SubraceDao, featureDao: FeatureDao) {
        this.raceDao = raceDao
        this.subraceDao = subraceDao
        this.featureDao = featureDao
        this._races = raceDao.getAllRaces()
    }

    private val _races: Flow<List<Race>>

     fun createDefaultRace(): Int {
        val newRace = RaceEntity()
        return raceDao.insertRace(newRace)
    }

     fun insertRace(race: RaceEntity) {
        raceDao.insertRace(race)
    }

     fun deleteRace(id: Int) {
        raceDao.deleteRace(id)
    }

     fun getRaces(): Flow<List<Race>> {
        return _races
    }

     fun getHomebrewRaces(): Flow<List<Race>> {
        return raceDao.getHomebrewRaces()
    }

     fun getLiveRaceById(id: Int): Flow<Race> {
        return raceDao.findUnfilledLiveRaceById(id).transform {
            it.traits = raceDao.getRaceTraits(id)
            featureDao.fillOutFeatureListWithoutChosen(it.traits!!)
            emit(it)
        }
    }

     fun insertRaceFeatureCrossRef(
        featureId: Int,
        raceId: Int
    ) {
        raceDao.insertRaceFeatureCrossRef(
            featureId = featureId,
            raceId = raceId
        )
    }

     fun removeRaceFeatureCrossRef(
        featureId: Int,
        raceId: Int
    ) {
        raceDao.removeRaceFeatureCrossRef(
            featureId = featureId,
            raceId = raceId
        )
    }


     fun insertSubraceFeatureCrossRef(
        subraceId: Int,
        featureId: Int
    ) {
        subraceDao.insertSubraceFeatureCrossRef(
            subraceId = subraceId,
            featureId = featureId
        )
    }

     fun getSubrace(id: Int): Flow<Subrace> {
        return subraceDao.getSubrace(id)
    }

     fun removeSubraceFeatureCrossRef(
        subraceId: Int,
        featureId: Int
    ) {
        subraceDao.removeSubraceFeatureCrossRef(
            subraceId = subraceId,
            featureId = featureId
        )
    }

     fun insertRaceSubraceCrossRef(
        subraceId: Int,
        raceId: Int
    ) {
        raceDao.insertRaceSubraceCrossRef(
               subraceId = subraceId,
               raceId = raceId
        )
    }

     fun createDefaultSubrace(): Int {
        return subraceDao.insertSubrace(
            SubraceEntity(
                name = "Homebrew Subrace",
                isHomebrew = true
            )
        )
    }

     fun insertSubrace(subraceEntity: SubraceEntity) {
        subraceDao.insertSubrace(subraceEntity)
    }

     fun getSubracesByRaceId(id: Int): Flow<List<Subrace>> {
        return subraceDao.bindSubraceOptions(id)
    }

     fun removeRaceSubraceCrossRef(raceId: Int, subraceId: Int) {
        subraceDao.removeRaceSubraceCrossRef(
            raceId = raceId,
            subraceId = subraceId
        )
    }

     fun getHomebrewSubraces(): Flow<List<SubraceEntity>> {
        return subraceDao.getHomebrewSubraces()
    }

     fun getRaceSubraces(id: Int): Flow<List<NameAndIdPojo>> {
        return raceDao.getRaceSubraces(id)
    }

     fun getAllRaceIdsAndNames(): Flow<List<NameAndIdPojo>> {
        return raceDao.getAllRaceIdsAndNames()
    }

     fun getSubraceLiveFeaturesById(id: Int): Flow<List<Feature>> {
        return subraceDao.getSubraceLiveFeaturesById(id)
    }

     fun deleteSubrace(id: Int) {
        subraceDao.deleteSubrace(id)
    }

     companion object {
         val sizeClasses = listOf(
            "Tiny",
            "Small",
            "Medium",
            "Large",
            "Huge",
            "Gargantuan"
        )
    }
}