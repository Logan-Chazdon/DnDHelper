package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Feature
import model.Subclass
import model.SubclassEntity
import services.SubclassService

actual abstract class SubclassDao {

    protected val subclassService: SubclassService
    constructor(subclassService: SubclassService) {
        this.subclassService = subclassService
    }

    actual abstract fun getSubclassFeatures(
        subclassId: Int,
        maxLevel: Int
    ): List<Feature>

    actual abstract fun getSubclassLiveFeaturesById(id: Int): Flow<List<Feature>>
    actual fun getSubclassesByClassId(id: Int): Flow<List<Subclass>> {
        return subclassService.getSubclassesByClassId(id)
    }

    actual fun insertSubclass(subClass: SubclassEntity): Int {
        return subclassService.insertSubclass(subClass)
    }

    actual fun getSubclass(id: Int): Flow<Subclass> {
        return subclassService.getSubclass(id)
    }

    actual fun removeSubclassFeatureCrossRef(subclassId: Int, featureId: Int) {
        subclassService.removeSubclassFeatureCrossRef(subclassId, featureId)
    }

    actual fun insertSubclassFeatureCrossRef(subclassId: Int, featureId: Int) {
        subclassService.insertSubclassFeatureCrossRef(subclassId, featureId)
    }

    actual fun getHomebrewSubclasses(): Flow<List<SubclassEntity>> {
        return subclassService.getHomebrewSubclasses()
    }

    actual abstract fun deleteSubclass(subclassId: Int)
}


class SubclassDaoImpl(service: SubclassService) : SubclassDao(service) {
    override fun getSubclassFeatures(subclassId: Int, maxLevel: Int): List<Feature> {
        return subclassService.getSubclassFeatures(subclassId, maxLevel)
    }

    override fun getSubclassLiveFeaturesById(id: Int): Flow<List<Feature>> {
        return subclassService.getSubclassLiveFeaturesById(id)
    }

    override fun deleteSubclass(subclassId: Int) {
        subclassService.deleteSubclass(subclassId)
    }

}