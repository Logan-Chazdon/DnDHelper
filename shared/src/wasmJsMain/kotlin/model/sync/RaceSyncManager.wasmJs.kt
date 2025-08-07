package model.sync

import model.RaceEntity
import model.SubraceEntity

actual class RaceSyncManager {
    actual fun postRace(race: RaceEntity) {
    }

    actual fun deleteRace(id: Int) {
    }

    actual fun postRaceFeatureCrossRef(featureId: Int, raceId: Int) {
    }

    actual fun deleteRaceFeatureCrossRef(featureId: Int, raceId: Int) {
    }

    actual fun postSubraceFeatureCrossRef(subraceId: Int, featureId: Int) {
    }

    actual fun deleteSubraceFeatureCrossRef(subraceId: Int, featureId: Int) {
    }

    actual fun postRaceSubraceCrossRef(subraceId: Int, raceId: Int) {
    }

    actual fun postSubrace(subrace: SubraceEntity) {
    }

    actual fun deleteRaceSubraceCrossRef(raceId: Int, subraceId: Int) {
    }

    actual fun deleteSubrace(id: Int) {
    }
}