package model.repositories


import kotlinx.coroutines.flow.Flow
import model.ItemInterface

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect class ItemRepository {
    fun getAllItems(): Flow<List<ItemInterface>>
}
