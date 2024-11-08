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

    actual abstract fun getBackgroundSpells(backgroundId: Int): List<Spell>?
    actual abstract fun removeBackgroundById(id: Int)
    actual abstract fun getBackgroundFeatures(id: Int): List<Feature>
    actual fun getAllBackgrounds(): Flow<List<Background>> {
        return backgroundService.getAllBackgrounds()
    }

    actual abstract fun getUnfilledBackgroundFeatures(id: Int): List<Feature>
    actual abstract fun getHomebrewBackgrounds(): Flow<List<BackgroundEntity>>
    actual abstract fun deleteBackground(id: Int)
    actual abstract fun getBackgroundChoiceData(charId: Int): BackgroundChoiceEntity
    actual fun insertBackgroundFeatureCrossRef(backgroundId: Int, featureId: Int) {
    }

    actual fun insertBackground(backgroundEntity: BackgroundEntity): Int {
        return backgroundService.insertBackground(backgroundEntity)
    }

    actual fun insertBackgroundSpellCrossRef(backgroundId: Int, spellId: Int) {
        backgroundService.insertBackgroundSpellCrossRef(backgroundId, spellId)
    }

    actual abstract fun getUnfilledBackground(id: Int): Flow<BackgroundEntity>
}


class BackgroundDaoImpl(backgroundService: BackgroundService) : BackgroundDao(backgroundService) {
    override fun getBackgroundSpells(backgroundId: Int): List<Spell>? {
        return backgroundService.getBackgroundSpells(backgroundId)
    }

    override fun removeBackgroundById(id: Int) {
        backgroundService.removeBackgroundById(id)
    }

    override fun getBackgroundFeatures(id: Int): List<Feature> {
        return backgroundService.getBackgroundFeatures(id)
    }

    override fun getUnfilledBackgroundFeatures(id: Int): List<Feature> {
        return backgroundService.getUnfilledBackgroundFeatures(id)
    }

    override fun getHomebrewBackgrounds(): Flow<List<BackgroundEntity>> {
        return backgroundService.getHomebrewBackgrounds()
    }

    override fun deleteBackground(id: Int) {
        backgroundService.deleteBackground(id)
    }

    override fun getBackgroundChoiceData(charId: Int): BackgroundChoiceEntity {
        return backgroundService.getBackgroundChoiceData(charId)
    }

    override fun getUnfilledBackground(id: Int): Flow<BackgroundEntity> {
        return backgroundService.getUnfilledBackground(id)
    }

}