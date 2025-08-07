package model.repositories


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import model.Background
import model.BackgroundEntity
import model.database.daos.BackgroundDao
import model.database.daos.FeatureDao
import model.sync.BackgroundSyncManager


class BackgroundRepository {
    private val backgroundDao: BackgroundDao
    private val featureDao: FeatureDao
    private val backgroundSyncManager: BackgroundSyncManager


    constructor(backgroundDao: BackgroundDao, featureDao: FeatureDao, backgroundSyncManager: BackgroundSyncManager) {
        this.backgroundDao = backgroundDao
        this.featureDao = featureDao
        this.scope = CoroutineScope(Job())
        this._backgrounds = backgroundDao.getAllBackgrounds()
        this.backgroundSyncManager = backgroundSyncManager
    }

    private val scope: CoroutineScope
    private val _backgrounds: Flow<List<Background>>

    fun getBackgrounds(): Flow<List<Background>> {
        return _backgrounds
    }

    fun getBackground(id: Int): Flow<Background> {
        return (backgroundDao.getUnfilledBackground(id)).transform {
            if (it != null) {
                val background = Background(
                    it,
                    backgroundDao.getUnfilledBackgroundFeatures(id)
                )
                background.spells = backgroundDao.getBackgroundSpells(id)
                featureDao.fillOutFeatureListWithoutChosen(background.features!!)
                emit(background)
            }
        }
    }

    suspend fun insertBackground(backgroundEntity: BackgroundEntity) {
        backgroundSyncManager.postBackground(backgroundEntity)
        backgroundDao.insertBackground(backgroundEntity)
    }

    suspend fun insertBackgroundFeatureCrossRef(backgroundId: Int, featureId: Int) {
        backgroundSyncManager.postBackgroundFeatureCrossRef(backgroundId, featureId)
        backgroundDao.insertBackgroundFeatureCrossRef(backgroundId, featureId)
    }

    suspend fun createDefaultBackground(): Int {
        val default = BackgroundEntity(
            name = "",
            desc = "",
            equipmentChoices = emptyList(),
            equipment = emptyList(),
            languages = emptyList(),
            proficiencies = emptyList(),
            spells = null
        )
        val id = backgroundDao.insertBackground(
            default
        )
        backgroundSyncManager.postBackground(default.apply { this.id = id })
        return id
    }

    fun getHomebrewBackgrounds(): Flow<List<BackgroundEntity>> {
        return backgroundDao.getHomebrewBackgrounds()
    }

    suspend fun deleteBackground(id: Int) {
        backgroundSyncManager.deleteBackground(id)
        backgroundDao.deleteBackground(id)
    }
}