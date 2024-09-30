package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Feature
import model.Subclass
import model.SubclassEntity

actual abstract class SubclassDao {
    actual abstract fun getSubclassFeatures(
        subclassId: Int,
        maxLevel: Int
    ): List<Feature>

    actual abstract fun getSubclassLiveFeaturesById(id: Int): Flow<List<Feature>>
    actual fun getSubclassesByClassId(id: Int): Flow<List<Subclass>> {
        TODO("Not yet implemented")
    }

    actual fun insertSubclass(subClass: SubclassEntity): Int {
        TODO("Not yet implemented")
    }

    actual fun getSubclass(id: Int): Flow<Subclass> {
        TODO("Not yet implemented")
    }

    actual fun removeSubclassFeatureCrossRef(subclassId: Int, featureId: Int) {
    }

    actual fun insertSubclassFeatureCrossRef(subclassId: Int, featureId: Int) {
    }

    actual fun getHomebrewSubclasses(): Flow<List<SubclassEntity>> {
        TODO("Not yet implemented")
    }

    actual abstract fun deleteSubclass(subclassId: Int)
}


class SubclassDaoImpl : SubclassDao() {
    override fun getSubclassFeatures(subclassId: Int, maxLevel: Int): List<Feature> {
        TODO("Not yet implemented")
    }

    override fun getSubclassLiveFeaturesById(id: Int): Flow<List<Feature>> {
        TODO("Not yet implemented")
    }

    override fun deleteSubclass(subclassId: Int) {
        TODO("Not yet implemented")
    }

}