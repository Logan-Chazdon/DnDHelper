package services

import io.ktor.client.*
import kotlinx.coroutines.flow.Flow
import model.Feature
import model.Subrace
import model.SubraceEntity

class SubraceService(client: HttpClient) : Service(client = client) {
    fun insertSubraceFeatureCrossRef(subraceId: Int, featureId: Int) {
        TODO("Not yet implemented")
    }

    fun getSubrace(id: Int): Flow<Subrace> {
        TODO("Not yet implemented")
    }

    fun removeSubraceFeatureCrossRef(subraceId: Int, featureId: Int) {
        TODO("Not yet implemented")
    }

    fun insertSubrace(subrace: SubraceEntity): Int {
        TODO("Not yet implemented")
    }

    fun bindSubraceOptions(raceId: Int): Flow<MutableList<Subrace>> {
        TODO("Not yet implemented")
    }

    fun removeRaceSubraceCrossRef(raceId: Int, subraceId: Int) {
        TODO("Not yet implemented")
    }

    fun getHomebrewSubraces(): Flow<List<SubraceEntity>> {
        TODO("Not yet implemented")
    }

    fun getSubraceLiveFeaturesById(id: Int): Flow<List<Feature>> {
        TODO("Not yet implemented")
    }

    fun deleteSubrace(id: Int) {
        TODO("Not yet implemented")
    }
}