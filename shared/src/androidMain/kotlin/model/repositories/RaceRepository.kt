package model.repositories

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asFlow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import model.*
import model.database.daos.FeatureDao
import model.database.daos.RaceDao
import model.database.daos.SubraceDao
import model.junctionEntities.RaceFeatureCrossRef
import model.junctionEntities.RaceSubraceCrossRef
import model.junctionEntities.SubraceFeatureCrossRef
import model.pojos.NameAndIdPojo


actual class RaceRepository constructor(
    private val raceDao: RaceDao,
    private val subraceDao: SubraceDao,
    private val featureDao: FeatureDao
) {
    private val _races =
        raceDao.getAllRaces()

    actual fun createDefaultRace(): Int {
        val newRace = RaceEntity()
        return raceDao.insertRace(newRace).toInt()
    }

    actual fun insertRace(race: RaceEntity) {
        raceDao.insertRace(race)
    }

    actual fun deleteRace(id: Int) {
        raceDao.deleteRace(id)
    }

    actual fun getRaces(): Flow<List<Race>> {
        return _races.asFlow()
    }

    actual fun getHomebrewRaces(): Flow<List<Race>> {
        return raceDao.getHomebrewRaces().asFlow()
    }

    actual fun getLiveRaceById(id: Int): Flow<Race> {
        val result = MediatorLiveData<Race>()
        result.addSource(raceDao.findUnfilledLiveRaceById(id)) {
            if(it != null) {
                GlobalScope.launch {
                    it.traits = raceDao.getRaceTraits(id)
                    featureDao.fillOutFeatureListWithoutChosen(it.traits!!)
                    result.postValue(it)
                }
            }
        }
        return result.asFlow()
    }

    actual fun insertRaceFeatureCrossRef(ref: RaceFeatureCrossRef) {
        raceDao.insertRaceFeatureCrossRef(ref)
    }

    actual fun removeRaceFeatureCrossRef(ref: RaceFeatureCrossRef) {
        raceDao.removeRaceFeatureCrossRef(ref)
    }


    actual fun insertSubraceFeatureCrossRef(subraceFeatureCrossRef: SubraceFeatureCrossRef) {
        subraceDao.insertSubraceFeatureCrossRef(subraceFeatureCrossRef)
    }

    actual fun getSubrace(id: Int): Flow<Subrace> {
        return subraceDao.getSubrace(id).asFlow()
    }

    actual fun removeSubraceFeatureCrossRef(subraceFeatureCrossRef: SubraceFeatureCrossRef) {
        subraceDao.removeSubraceFeatureCrossRef(subraceFeatureCrossRef)
    }

    actual fun insertRaceSubraceCrossRef(raceSubraceCrossRef: RaceSubraceCrossRef) {
        raceDao.insertRaceSubraceCrossRef(raceSubraceCrossRef)
    }

    actual fun createDefaultSubrace(): Int {
        return subraceDao.insertSubrace(
            SubraceEntity(
                name = "Homebrew Subrace",
                isHomebrew = true
            )
        ).toInt()
    }

    actual fun insertSubrace(subraceEntity: SubraceEntity) {
        subraceDao.insertSubrace(subraceEntity)
    }

    actual fun getSubracesByRaceId(id: Int): Flow<List<Subrace>> {
        val result = MediatorLiveData<List<Subrace>>()
        subraceDao.bindSubraceOptions(id, result)
        return result.asFlow()
    }

    actual fun removeRaceSubraceCrossRef(raceId: Int, subraceId: Int) {
        subraceDao.removeRaceSubraceCrossRef(
            RaceSubraceCrossRef(
                raceId = raceId,
                subraceId = subraceId
            )
        )
    }

    actual fun getHomebrewSubraces(): Flow<List<SubraceEntity>> {
        return subraceDao.getHomebrewSubraces().asFlow()
    }

    actual fun getRaceSubraces(id: Int): Flow<List<NameAndIdPojo>> {
        return raceDao.getRaceSubraces(id).asFlow()
    }

    actual fun getAllRaceIdsAndNames(): Flow<List<NameAndIdPojo>> {
        return raceDao.getAllRaceIdsAndNames().asFlow()
    }

    actual fun getSubraceLiveFeaturesById(id: Int): Flow<List<Feature>> {
        return subraceDao.getSubraceLiveFeaturesById(id).asFlow()
    }

    actual fun deleteSubrace(id: Int) {
        subraceDao.deleteSubrace(id)
    }

    actual companion object {
        actual val sizeClasses = listOf(
            "Tiny",
            "Small",
            "Medium",
            "Large",
            "Huge",
            "Gargantuan"
        )
    }
}