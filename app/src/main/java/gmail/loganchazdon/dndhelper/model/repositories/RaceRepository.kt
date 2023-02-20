package gmail.loganchazdon.dndhelper.model.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import gmail.loganchazdon.dndhelper.model.Race
import gmail.loganchazdon.dndhelper.model.RaceEntity
import gmail.loganchazdon.dndhelper.model.Subrace
import gmail.loganchazdon.dndhelper.model.SubraceEntity
import gmail.loganchazdon.dndhelper.model.database.daos.RaceDao
import gmail.loganchazdon.dndhelper.model.database.daos.SubraceDao
import gmail.loganchazdon.dndhelper.model.junctionEntities.RaceFeatureCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.RaceSubraceCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.SubraceFeatureCrossRef
import javax.inject.Inject

class RaceRepository @Inject constructor(
    private val raceDao: RaceDao,
    private val subraceDao: SubraceDao
) {
    private val _races =
        raceDao.getAllRaces()

    fun createDefaultRace(): Int {
        val newRace = RaceEntity()
        return raceDao.insertRace(newRace).toInt()
    }

    fun insertRace(race: RaceEntity) {
        raceDao.insertRace(race)
    }

    fun deleteRace(id: Int) {
        raceDao.deleteRace(id)
    }

    fun getRaces(): LiveData<List<Race>> {
        return _races
    }

    fun getHomebrewRaces(): LiveData<List<Race>> {
        return raceDao.getHomebrewRaces()
    }

    fun getLiveRaceById(id: Int): LiveData<Race> {
        val result = MediatorLiveData<Race>()
        raceDao.bindLiveRaceById(id, result)
        return result
    }

    fun insertRaceFeatureCrossRef(ref: RaceFeatureCrossRef) {
        raceDao.insertRaceFeatureCrossRef(ref)
    }

    fun removeRaceFeatureCrossRef(ref: RaceFeatureCrossRef) {
        raceDao.removeRaceFeatureCrossRef(ref)
    }


    fun insertSubraceFeatureCrossRef(subraceFeatureCrossRef: SubraceFeatureCrossRef) {
        subraceDao.insertSubraceFeatureCrossRef(subraceFeatureCrossRef)
    }

    fun getSubrace(id: Int): LiveData<Subrace> {
        return subraceDao.getSubrace(id)
    }

    fun removeSubraceFeatureCrossRef(subraceFeatureCrossRef: SubraceFeatureCrossRef) {
        subraceDao.removeSubraceFeatureCrossRef(subraceFeatureCrossRef)
    }

    fun insertRaceSubraceCrossRef(raceSubraceCrossRef: RaceSubraceCrossRef) {
        raceDao.insertRaceSubraceCrossRef(raceSubraceCrossRef)
    }

    fun createDefaultSubrace(): Int {
        return subraceDao.insertSubrace(
            SubraceEntity(
                name = "Homebrew Subrace"
            )
        ).toInt()
    }

    fun insertSubrace(subraceEntity: SubraceEntity) {
        subraceDao.insertSubrace(subraceEntity)
    }

    fun getSubracesByRaceId(id: Int): LiveData<List<Subrace>> {
        val result = MediatorLiveData<List<Subrace>>()
        subraceDao.bindSubraceOptions(id, result)
        return result
    }

    fun removeRaceSubraceCrossRef(raceId: Int, subraceId: Int) {
        subraceDao.removeRaceSubraceCrossRef(
            RaceSubraceCrossRef(
                raceId = raceId,
                subraceId = subraceId
            )
        )
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