package model.repositories


import kotlinx.coroutines.flow.Flow
import model.Background
import model.BackgroundEntity
import model.junctionEntities.BackgroundFeatureCrossRef


@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class BackgroundRepository(

) {
    fun getBackgrounds() : Flow<List<Background>>
    fun getBackground(id: Int) : Flow<Background>
    fun insertBackground(backgroundEntity: BackgroundEntity)
    fun insertBackgroundFeatureCrossRef(backgroundFeatureCrossRef: BackgroundFeatureCrossRef)
    fun createDefaultBackground() : Int
    fun getHomebrewBackgrounds(): Flow<List<model.BackgroundEntity>>
    fun deleteBackground(id: Int)
}