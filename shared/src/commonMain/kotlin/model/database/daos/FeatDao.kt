package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Feat
import model.Feature

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect abstract class FeatDao {
    abstract fun getUnfilledFeats(): Flow<List<Feat>>
    abstract fun getFeatFeatures(featId: Int): List<Feature>
}