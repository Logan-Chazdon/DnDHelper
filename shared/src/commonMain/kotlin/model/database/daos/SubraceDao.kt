package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Feature
import model.Subrace
import model.SubraceEntity

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect abstract class SubraceDao {
    fun insertSubraceFeatureCrossRef(subraceId: Int, featureId: Int)
    fun getSubrace(id: Int): Flow<Subrace>
    fun removeSubraceFeatureCrossRef(subraceId: Int, featureId: Int)
    fun insertSubrace(subrace: SubraceEntity): Int
    fun bindSubraceOptions(raceId: Int) : Flow<MutableList<Subrace>>
    fun removeRaceSubraceCrossRef(raceId: Int, subraceId: Int)
    abstract fun getHomebrewSubraces(): Flow<List<SubraceEntity>>
    abstract fun getSubraceLiveFeaturesById(id: Int): Flow<List<Feature>>
    abstract fun deleteSubrace(id: Int)


}