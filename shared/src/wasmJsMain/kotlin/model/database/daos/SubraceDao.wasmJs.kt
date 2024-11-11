package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Feature
import model.Subrace
import model.SubraceEntity
import services.SubraceService

@Suppress("NO_ACTUAL_FOR_EXPECT")
actual abstract class SubraceDao {

    protected val subraceSubrace: SubraceService
    constructor(subraceSubrace: SubraceService) {
        this.subraceSubrace = subraceSubrace
    }

    actual suspend fun insertSubraceFeatureCrossRef(subraceId: Int, featureId: Int) {
        subraceSubrace.insertSubraceFeatureCrossRef(subraceId, featureId)
    }

    actual fun getSubrace(id: Int): Flow<Subrace> {
        return subraceSubrace.getSubrace(id)
    }

    actual suspend fun removeSubraceFeatureCrossRef(subraceId: Int, featureId: Int) {
        subraceSubrace.removeSubraceFeatureCrossRef(subraceId, featureId)
    }

    actual suspend fun insertSubrace(subrace: SubraceEntity): Int {
        return subraceSubrace.insertSubrace(subrace)
    }

    actual fun bindSubraceOptions(raceId: Int): Flow<MutableList<Subrace>> {
        return subraceSubrace.bindSubraceOptions(raceId)
    }

    actual suspend fun removeRaceSubraceCrossRef(raceId: Int, subraceId: Int) {
        subraceSubrace.removeRaceSubraceCrossRef(raceId, subraceId)
    }

    actual abstract fun getHomebrewSubraces(): Flow<List<SubraceEntity>>
    actual abstract fun getSubraceLiveFeaturesById(id: Int): Flow<List<Feature>>
    actual abstract suspend fun deleteSubrace(id: Int)
}


class SubraceDaoImpl(service: SubraceService) : SubraceDao(service) {
    override fun getHomebrewSubraces(): Flow<List<SubraceEntity>> {
        return subraceSubrace.getHomebrewSubraces()
    }

    override fun getSubraceLiveFeaturesById(id: Int): Flow<List<Feature>> {
        return subraceSubrace.getSubraceLiveFeaturesById(id)
    }

    override suspend fun deleteSubrace(id: Int) {
        subraceSubrace.deleteSubrace(id)
    }
}