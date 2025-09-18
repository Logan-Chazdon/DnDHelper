package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Feat
import services.FeatService

actual abstract class FeatDao {
    protected val featService: FeatService
    constructor(featService: FeatService) {
        this.featService = featService
    }

    actual abstract fun getUnfilledFeats(): Flow<List<Feat>>
}

class FeatDaoImpl(featService: FeatService) : FeatDao(featService) {
    override fun getUnfilledFeats(): Flow<List<Feat>> {
        return featService.getUnfilledFeats()
    }
}