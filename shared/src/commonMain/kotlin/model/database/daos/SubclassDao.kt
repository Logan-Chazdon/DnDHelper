package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Feature
import model.Subclass
import model.SubclassEntity

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect abstract class SubclassDao {
    abstract fun getSubclassFeatures(subclassId: Int, maxLevel: Int): List<Feature>
    abstract fun getSubclassLiveFeaturesById(id: Int) : Flow<List<Feature>>
    fun getSubclassesByClassId(id: Int) : Flow<List<Subclass>>
    fun insertSubclass(subClass: SubclassEntity): Int
    fun getSubclass(id: Int): Flow<Subclass>
    fun removeSubclassFeatureCrossRef(subclassId: Int, featureId: Int)
    fun insertSubclassFeatureCrossRef(subclassId: Int, featureId: Int)
    fun getHomebrewSubclasses(): Flow<List<SubclassEntity>>
    abstract fun deleteSubclass(subclassId: Int)
}