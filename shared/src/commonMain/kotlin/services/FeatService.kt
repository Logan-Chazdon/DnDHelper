package services

import io.ktor.client.*
import kotlinx.coroutines.flow.Flow
import model.Feat
import model.Feature

class FeatService(client: HttpClient) : Service(client = client) {
    fun getUnfilledFeats(): Flow<List<Feat>> {
        TODO("Not yet implemented")
    }

    fun getFeatFeatures(featId: Int): List<Feature> {
        TODO("Not yet implemented")
    }
}