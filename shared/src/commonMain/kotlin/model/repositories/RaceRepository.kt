package model.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import model.*
import model.database.daos.FeatureDao
import model.database.daos.RaceDao
import model.database.daos.SubraceDao
import model.pojos.NameAndIdPojo
import model.sync.RaceSyncManager


class RaceRepository {
    private val raceDao: RaceDao
    private val subraceDao: SubraceDao
    val featureDao: FeatureDao
    private val raceSyncManager: RaceSyncManager

    constructor(raceDao: RaceDao, subraceDao: SubraceDao, featureDao: FeatureDao, raceSyncManager: RaceSyncManager) {
        this.raceDao = raceDao
        this.subraceDao = subraceDao
        this.featureDao = featureDao
        this.raceSyncManager = raceSyncManager
        this._races = raceDao.getAllRaces()
    }

    private val _races: Flow<List<Race>>

    suspend fun createDefaultRace(): Int {
        val newRace = RaceEntity()
        val id =  raceDao.insertRace(newRace)
        raceSyncManager.postRace(newRace.apply { this.raceId = id})
        return id
    }

    suspend fun insertRace(race: RaceEntity) {
        raceSyncManager.postRace(race)
        raceDao.insertRace(race)
    }

    suspend fun deleteRace(id: Int) {
        raceSyncManager.deleteRace(id)
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

    suspend fun insertRaceFeatureCrossRef(
        featureId: Int,
        raceId: Int
    ) {
        raceSyncManager.postRaceFeatureCrossRef(
            featureId = featureId,
            raceId = raceId
        )
        raceDao.insertRaceFeatureCrossRef(
            featureId = featureId,
            raceId = raceId
        )
    }

    suspend fun removeRaceFeatureCrossRef(
        featureId: Int,
        raceId: Int
    ) {
        raceSyncManager.deleteRaceFeatureCrossRef(
            featureId = featureId,
            raceId = raceId
        )
        raceDao.removeRaceFeatureCrossRef(
            featureId = featureId,
            raceId = raceId
        )
    }


    suspend fun insertSubraceFeatureCrossRef(
        subraceId: Int,
        featureId: Int
    ) {
        raceSyncManager.postSubraceFeatureCrossRef(
            subraceId = subraceId,
            featureId = featureId
        )
        subraceDao.insertSubraceFeatureCrossRef(
            subraceId = subraceId,
            featureId = featureId
        )
    }

    fun getSubrace(id: Int): Flow<Subrace> {
        return subraceDao.getSubrace(id)
    }

    suspend fun removeSubraceFeatureCrossRef(
        subraceId: Int,
        featureId: Int
    ) {
        raceSyncManager.deleteSubraceFeatureCrossRef(
            subraceId = subraceId,
            featureId = featureId
        )
        subraceDao.removeSubraceFeatureCrossRef(
            subraceId = subraceId,
            featureId = featureId
        )
    }

    suspend fun insertRaceSubraceCrossRef(
        subraceId: Int,
        raceId: Int
    ) {
        raceSyncManager.postRaceSubraceCrossRef(
            subraceId = subraceId,
            raceId = raceId
        )
        raceDao.insertRaceSubraceCrossRef(
            subraceId = subraceId,
            raceId = raceId
        )
    }

    suspend fun createDefaultSubrace(): Int {
        val default = SubraceEntity(
            name = "Homebrew Subrace",
            isHomebrew = true
        )
        val id = subraceDao.insertSubrace(default)
        raceSyncManager.postSubrace(default.apply { this.id = id})
        return id
    }

    suspend fun insertSubrace(subraceEntity: SubraceEntity) {
        raceSyncManager.postSubrace(subraceEntity)
        subraceDao.insertSubrace(subraceEntity)
    }

    fun getSubracesByRaceId(id: Int): Flow<List<Subrace>> {
        return subraceDao.bindSubraceOptions(id)
    }

    suspend fun removeRaceSubraceCrossRef(raceId: Int, subraceId: Int) {
        raceSyncManager.deleteRaceSubraceCrossRef(
            raceId = raceId,
            subraceId = subraceId
        )
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

    suspend fun deleteSubrace(id: Int) {
        raceSyncManager.deleteSubrace(id)
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