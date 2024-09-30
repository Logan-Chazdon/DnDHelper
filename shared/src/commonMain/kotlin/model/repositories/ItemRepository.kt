package model.repositories


import kotlinx.coroutines.flow.Flow
import model.ItemInterface
import model.localDataSources.DataSource


 class ItemRepository {
     constructor(dataSource: DataSource) {
        this._items = dataSource.getItems()
    }

    private val _items: Flow<List<ItemInterface>>

     fun getAllItems(): Flow<List<ItemInterface>> {
        return _items
    }

}