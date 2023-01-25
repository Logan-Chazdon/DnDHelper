package gmail.loganchazdon.dndhelper.model.repositories

import androidx.lifecycle.LiveData
import gmail.loganchazdon.dndhelper.model.Feat
import gmail.loganchazdon.dndhelper.model.database.daos.FeatDao
import javax.inject.Inject

class FeatRepository @Inject constructor(
    private val featDao: FeatDao
){
    private val _feats = featDao.getAllFeats()

    fun getFeats(): LiveData<List<Feat>> {
        return _feats
    }
}