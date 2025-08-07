package model.sync.workers

import com.google.gson.reflect.TypeToken
import model.RaceEntity
import model.SubraceEntity

class PostRaceWorker : SyncWorker<RaceEntity>(TypeToken.get(RaceEntity::class.java)) {
    override suspend fun sync(it: RaceEntity) {
        raceService.insertRace(it)
    }
}

class DeleteRaceWorker : SyncWorker<Int>(TypeToken.get(Int::class.java)) {
    override suspend fun sync(it: Int) {
        raceService.deleteRace(it)
    }
}

class PostRaceFeatureCrossRefWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        raceService.insertRaceFeatureCrossRef(
            featureId = it.first,
            raceId = it.second
        )
    }
}


class DeleteRaceFeatureCrossRefWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        raceService.removeRaceFeatureCrossRef(
            featureId = it.first,
            raceId = it.second
        )
    }
}

class PostSubraceFeatureCrossRefWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        subraceService.insertSubraceFeatureCrossRef(
            featureId = it.first,
            subraceId = it.second
        )
    }
}

class DeleteSubraceFeatureCrossRefWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        subraceService.insertSubraceFeatureCrossRef(
            featureId = it.first,
            subraceId = it.second
        )
    }
}

class PostRaceSubraceCrossRefWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        raceService.insertRaceSubraceCrossRef(
            raceId = it.second,
            subraceId = it.first
        )
    }
}

class PostSubraceWorker : SyncWorker<SubraceEntity>(TypeToken.get(SubraceEntity::class.java)) {
    override suspend fun sync(it: SubraceEntity) {
        subraceService.insertSubrace(it)
    }
}

class DeleteSubraceWorker : SyncWorker<Int>(TypeToken.get(Int::class.java)) {
    override suspend fun sync(it: Int) {
        subraceService.deleteSubrace(it)
    }
}

class DeleteRaceSubraceCrossRefWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        subraceService.removeRaceSubraceCrossRef(
            raceId = it.first,
            subraceId = it.second
        )
    }
}