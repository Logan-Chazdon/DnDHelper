package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Feature
import model.Subrace
import model.SubraceEntity

@Suppress("NO_ACTUAL_FOR_EXPECT")
actual abstract class SubraceDao {
    actual fun insertSubraceFeatureCrossRef(subraceId: Int, featureId: Int) {
    }

    actual fun getSubrace(id: Int): Flow<Subrace> {
        TODO("Not yet implemented")
    }

    actual fun removeSubraceFeatureCrossRef(subraceId: Int, featureId: Int) {
    }

    actual fun insertSubrace(subrace: SubraceEntity): Int {
        TODO("Not yet implemented")
    }

    actual fun bindSubraceOptions(raceId: Int): Flow<MutableList<Subrace>> {
        TODO("Not yet implemented")
    }

    actual fun removeRaceSubraceCrossRef(raceId: Int, subraceId: Int) {
    }

    actual abstract fun getHomebrewSubraces(): Flow<List<SubraceEntity>>
    actual abstract fun getSubraceLiveFeaturesById(id: Int): Flow<List<Feature>>
    actual abstract fun deleteSubrace(id: Int)
}


class SubraceDaoImpl : SubraceDao() {
    override fun getHomebrewSubraces(): Flow<List<SubraceEntity>> {
        TODO("Not yet implemented")
    }

    override fun getSubraceLiveFeaturesById(id: Int): Flow<List<Feature>> {
        TODO("Not yet implemented")
    }

    override fun deleteSubrace(id: Int) {
        TODO("Not yet implemented")
    }
}