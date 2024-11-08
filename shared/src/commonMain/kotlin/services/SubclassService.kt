package services

import io.ktor.client.*
import kotlinx.coroutines.flow.Flow
import model.Feature
import model.Subclass
import model.SubclassEntity

class SubclassService(client: HttpClient) : Service(client = client) {
    fun getSubclassesByClassId(id: Int): Flow<List<Subclass>> {
        TODO("Not yet implemented")
    }

    fun insertSubclass(subClass: SubclassEntity): Int {
        TODO("Not yet implemented")
    }

    fun getSubclass(id: Int): Flow<Subclass> {
        TODO("Not yet implemented")
    }

    fun removeSubclassFeatureCrossRef(subclassId: Int, featureId: Int) {
        TODO("Not yet implemented")
    }

    fun insertSubclassFeatureCrossRef(subclassId: Int, featureId: Int) {
        TODO("Not yet implemented")
    }

    fun getHomebrewSubclasses(): Flow<List<SubclassEntity>> {
        TODO("Not yet implemented")
    }

    fun getSubclassFeatures(subclassId: Int, maxLevel: Int): List<Feature> {
        TODO("Not yet implemented")
    }

    fun getSubclassLiveFeaturesById(id: Int): Flow<List<Feature>> {
        TODO("Not yet implemented")
    }

    fun deleteSubclass(subclassId: Int) {
        TODO("Not yet implemented")
    }
}