package model.repositories


import kotlinx.coroutines.flow.Flow
import model.*
import model.junctionEntities.RaceFeatureCrossRef
import model.junctionEntities.RaceSubraceCrossRef
import model.junctionEntities.SubraceFeatureCrossRef
import model.pojos.NameAndIdPojo

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class RaceRepository {
    fun createDefaultRace(): Int
    fun insertRace(race: RaceEntity)
    fun deleteRace(id: Int)
    fun getRaces() : Flow<List<Race>>
    fun getHomebrewRaces() : Flow<List<Race>>
    fun getLiveRaceById(id: Int) : Flow<Race>
    fun insertRaceFeatureCrossRef(ref: RaceFeatureCrossRef)
    fun removeRaceFeatureCrossRef(ref: RaceFeatureCrossRef)
    fun insertSubraceFeatureCrossRef(subraceFeatureCrossRef: SubraceFeatureCrossRef)
    fun getSubrace(id: Int): Flow<Subrace>
    fun removeSubraceFeatureCrossRef(subraceFeatureCrossRef: SubraceFeatureCrossRef)
    fun insertRaceSubraceCrossRef(raceSubraceCrossRef: RaceSubraceCrossRef)
    fun createDefaultSubrace(): Int
    fun insertSubrace(subraceEntity: SubraceEntity)
    fun getSubracesByRaceId(id: Int): Flow<List<Subrace>>
    fun removeRaceSubraceCrossRef(raceId: Int, subraceId: Int)
    fun getHomebrewSubraces() : Flow<List<SubraceEntity>>
    fun getRaceSubraces(id: Int) : Flow<List<NameAndIdPojo>>
    fun getAllRaceIdsAndNames() : Flow<List<NameAndIdPojo>>
    fun getSubraceLiveFeaturesById(id: Int) : Flow<List<Feature>>
    fun deleteSubrace(id: Int)
    companion object {
        val sizeClasses: List<String>
    }
}