package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Feat
import model.Feature
import services.FeatService

actual abstract class FeatDao {
    protected val featService: FeatService
    constructor(featService: FeatService) {
        this.featService = featService
    }

    actual abstract fun getUnfilledFeats(): Flow<List<Feat>>
    actual abstract fun getFeatFeatures(featId: Int): List<Feature>
}

class FeatDaoImpl(featService: FeatService) : FeatDao(featService) {
    override fun getUnfilledFeats(): Flow<List<Feat>> {
        return featService.getUnfilledFeats()
    }

    override fun getFeatFeatures(featId: Int): List<Feature> {
        return featService.getFeatFeatures(featId)
    }

}