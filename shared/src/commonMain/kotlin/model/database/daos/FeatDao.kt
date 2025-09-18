package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Feat

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect abstract class FeatDao {
    abstract fun getUnfilledFeats(): Flow<List<Feat>>
}