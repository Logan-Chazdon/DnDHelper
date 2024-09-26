package model.repositories

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.Flow
import model.ItemInterface
import model.localDataSources.LocalDataSource


actual class ItemRepository constructor(
    LocalDataSource: LocalDataSource
){
    private val _items = LocalDataSource.getItems(
        MutableLiveData()
    )

    actual fun getAllItems(): Flow<List<ItemInterface>> {
        return _items.asFlow()
    }

}