package model.sync

import model.RaceEntity
import model.SubraceEntity

expect class RaceSyncManager {
    fun postRace(race: RaceEntity)
    fun deleteRace(id: Int)
    fun postRaceFeatureCrossRef(featureId: Int, raceId: Int)
    fun deleteRaceFeatureCrossRef(featureId: Int, raceId: Int)
    fun postSubraceFeatureCrossRef(subraceId: Int, featureId: Int)
    fun deleteSubraceFeatureCrossRef(subraceId: Int, featureId: Int)
    fun postRaceSubraceCrossRef(subraceId: Int, raceId: Int)
    fun postSubrace(subrace: SubraceEntity)
    fun deleteRaceSubraceCrossRef(raceId: Int, subraceId: Int)
    fun deleteSubrace(id: Int)
}