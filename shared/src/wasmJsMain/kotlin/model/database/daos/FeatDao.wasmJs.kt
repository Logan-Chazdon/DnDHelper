package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Feat
import model.Feature

actual abstract class FeatDao {
    actual abstract fun getUnfilledFeats(): Flow<List<Feat>>
    actual abstract fun getFeatFeatures(featId: Int): List<Feature>
}

class FeatDaoImpl() : FeatDao() {
    override fun getUnfilledFeats(): Flow<List<Feat>> {
        TODO("Not yet implemented")
    }

    override fun getFeatFeatures(featId: Int): List<Feature> {
        TODO("Not yet implemented")
    }

}