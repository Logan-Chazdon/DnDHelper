package model.database.daos

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import model.Feat
import model.Feature
import model.Prerequisite

actual abstract class FeatDao {
    actual abstract fun getUnfilledFeats(): Flow<List<Feat>>
    actual abstract fun getFeatFeatures(featId: Int): List<Feature>
}

class FeatDaoImpl() : FeatDao() {
    override fun getUnfilledFeats(): Flow<List<Feat>> {
        //TODO update me
        val testFeat = Feat("Test Feat", "Test", Prerequisite())
        return channelFlow {
            send(listOf(testFeat))
        }
    }

    override fun getFeatFeatures(featId: Int): List<Feature> {
        TODO("Not yet implemented")
    }

}