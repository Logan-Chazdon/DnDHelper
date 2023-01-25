package gmail.loganchazdon.dndhelper.model.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import gmail.loganchazdon.dndhelper.model.ItemInterface
import gmail.loganchazdon.dndhelper.model.localDataSources.LocalDataSource
import javax.inject.Inject

class ItemRepository @Inject constructor(
    LocalDataSource: LocalDataSource
){
    private val _items = LocalDataSource.getItems(
        MutableLiveData()
    )

    fun getAllItems(): LiveData<List<ItemInterface>> {
        return _items
    }

}