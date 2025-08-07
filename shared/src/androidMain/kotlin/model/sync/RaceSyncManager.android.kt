package model.sync

import android.content.Context
import model.RaceEntity
import model.SubraceEntity
import model.sync.workers.*

actual class RaceSyncManager(context: Context) : SyncManager(context){
    actual fun postRace(race: RaceEntity) {
        pushSync<PostRaceWorker>(gson.toJson(
            race
        ))
    }

    actual fun deleteRace(id: Int) {
        pushSync<DeleteRaceWorker>(gson.toJson(
            id
        ))
    }

    actual fun postRaceFeatureCrossRef(featureId: Int, raceId: Int) {
        pushSync<PostRaceFeatureCrossRefWorker>(gson.toJson(
            Pair(featureId, raceId)
        ))
    }

    actual fun deleteRaceFeatureCrossRef(featureId: Int, raceId: Int) {
        pushSync<DeleteRaceFeatureCrossRefWorker>(gson.toJson(
            Pair(featureId, raceId)
        ))
    }

    actual fun postSubraceFeatureCrossRef(subraceId: Int, featureId: Int) {
        pushSync<PostSubraceFeatureCrossRefWorker>(gson.toJson(
            Pair(featureId, subraceId)
        ))
    }

    actual fun deleteSubraceFeatureCrossRef(subraceId: Int, featureId: Int) {
        pushSync<DeleteSubraceFeatureCrossRefWorker>(gson.toJson(
            Pair(featureId, subraceId)
        ))
    }

    actual fun postRaceSubraceCrossRef(subraceId: Int, raceId: Int) {
        pushSync<PostRaceSubraceCrossRefWorker>(gson.toJson(
            Pair(subraceId, raceId)
        ))
    }

    actual fun postSubrace(subrace: SubraceEntity) {
        pushSync<PostSubraceWorker>(gson.toJson(subrace))
    }

    actual fun deleteRaceSubraceCrossRef(raceId: Int, subraceId: Int) {
        pushSync<DeleteRaceSubraceCrossRefWorker>(gson.toJson(
            Pair(raceId, subraceId)
        ))
    }

    actual fun deleteSubrace(id: Int) {
        pushSync<DeleteSubraceWorker>(gson.toJson(
            id
        ))
    }
}