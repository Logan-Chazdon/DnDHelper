package model.database.daos

import kotlinx.coroutines.flow.Flow
import model.Background
import model.BackgroundEntity
import model.Feature
import model.Spell
import model.choiceEntities.BackgroundChoiceEntity
import services.BackgroundService

actual abstract class BackgroundDao {
    protected val backgroundService: BackgroundService

    constructor(backgroundService: BackgroundService) {
        this.backgroundService = backgroundService
    }

    actual abstract suspend fun getBackgroundSpells(backgroundId: Int): List<Spell>?
    actual abstract suspend fun getBackgroundFeatures(id: Int): List<Feature>
    actual fun getAllBackgrounds(): Flow<List<Background>> {
        return backgroundService.getAllBackgrounds()
    }


    actual abstract fun getHomebrewBackgrounds(): Flow<List<BackgroundEntity>>
    actual abstract suspend fun deleteBackground(id: Int)
    actual abstract suspend fun getBackgroundChoiceData(charId: Int): BackgroundChoiceEntity
    actual suspend fun insertBackgroundFeatureCrossRef(backgroundId: Int, featureId: Int) {
        backgroundService.insertBackgroundFeatureCrossRef(backgroundId, featureId)
    }

    actual suspend fun insertBackground(backgroundEntity: BackgroundEntity): Int {
        return backgroundService.insertBackground(backgroundEntity)
    }

    actual suspend fun insertBackgroundSpellCrossRef(backgroundId: Int, spellId: Int) {
        backgroundService.insertBackgroundSpellCrossRef(backgroundId, spellId)
    }

    actual abstract fun getUnfilledBackground(id: Int): Flow<BackgroundEntity>
}


class BackgroundDaoImpl(backgroundService: BackgroundService) : BackgroundDao(backgroundService) {
    override suspend fun getBackgroundSpells(backgroundId: Int): List<Spell>? {
        return backgroundService.getBackgroundSpells(backgroundId)
    }

    override suspend fun getBackgroundFeatures(id: Int): List<Feature> {
        return backgroundService.getBackgroundFeatures(id)
    }


    override fun getHomebrewBackgrounds(): Flow<List<BackgroundEntity>> {
        return backgroundService.getHomebrewBackgrounds()
    }

    override suspend fun deleteBackground(id: Int) {
        backgroundService.deleteBackground(id)
    }

    override suspend fun getBackgroundChoiceData(charId: Int): BackgroundChoiceEntity {
        return backgroundService.getBackgroundChoiceData(charId)
    }

    override fun getUnfilledBackground(id: Int): Flow<BackgroundEntity> {
        return backgroundService.getUnfilledBackground(id)
    }

}