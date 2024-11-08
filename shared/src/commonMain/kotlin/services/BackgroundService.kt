package services

import io.ktor.client.*
import kotlinx.coroutines.flow.Flow
import model.Background
import model.BackgroundEntity
import model.Feature
import model.Spell
import model.choiceEntities.BackgroundChoiceEntity

class BackgroundService(client: HttpClient) : Service(client = client){
    fun getAllBackgrounds(): Flow<List<Background>> {
        TODO("Not yet implemented")
    }

    fun insertBackground(backgroundEntity: BackgroundEntity): Int {
        TODO("Not yet implemented")
    }

    fun insertBackgroundSpellCrossRef(backgroundId: Int, spellId: Int) {
        TODO("Not yet implemented")
    }

    fun getBackgroundSpells(backgroundId: Int): List<Spell>? {
        TODO("Not yet implemented")
    }

    fun removeBackgroundById(id: Int) {
        TODO("Not yet implemented")
    }

    fun getBackgroundFeatures(id: Int): List<Feature> {
        TODO("Not yet implemented")
    }

    fun getUnfilledBackgroundFeatures(id: Int): List<Feature> {
        TODO("Not yet implemented")
    }

    fun getHomebrewBackgrounds(): Flow<List<BackgroundEntity>> {
        TODO("Not yet implemented")
    }

    fun deleteBackground(id: Int) {
        TODO("Not yet implemented")
    }

    fun getBackgroundChoiceData(charId: Int): BackgroundChoiceEntity {
        TODO("Not yet implemented")
    }

    fun getUnfilledBackground(id: Int): Flow<BackgroundEntity> {
        TODO("Not yet implemented")
    }

}