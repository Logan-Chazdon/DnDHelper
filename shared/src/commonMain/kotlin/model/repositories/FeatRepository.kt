package model.repositories

import kotlinx.coroutines.flow.Flow

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class FeatRepository {
    fun getFeats(): Flow<List<model.Feat>>
}