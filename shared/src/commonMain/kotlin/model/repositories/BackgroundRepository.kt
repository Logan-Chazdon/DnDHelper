package model.repositories


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import model.Background
import model.BackgroundEntity
import model.database.daos.BackgroundDao
import model.database.daos.FeatureDao


class BackgroundRepository {
    private val backgroundDao: BackgroundDao
    private val featureDao: FeatureDao

    constructor(backgroundDao: BackgroundDao, featureDao: FeatureDao) {
        this.backgroundDao = backgroundDao
        this.featureDao = featureDao
        this.scope = CoroutineScope(Job())
        this._backgrounds = backgroundDao.getAllBackgrounds()
    }

    private val scope: CoroutineScope
    private val _backgrounds: Flow<List<Background>>

    fun getBackgrounds(): Flow<List<Background>> {
        return _backgrounds
    }

    fun getBackground(id: Int): Flow<Background> {
        return (backgroundDao.getUnfilledBackground(id)).transform {
            if (it != null) {
                scope.launch {
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
    }

    fun insertBackground(backgroundEntity: BackgroundEntity) {
        backgroundDao.insertBackground(backgroundEntity)
    }

    fun insertBackgroundFeatureCrossRef(backgroundId: Int, featureId: Int) {
        backgroundDao.insertBackgroundFeatureCrossRef(backgroundId, featureId)
    }

    fun createDefaultBackground(): Int {
        return backgroundDao.insertBackground(
            BackgroundEntity(
                name = "",
                desc = "",
                equipmentChoices = emptyList(),
                equipment = emptyList(),
                languages = emptyList(),
                proficiencies = emptyList(),
                spells = null
            )
        ).toInt()
    }

    fun getHomebrewBackgrounds(): Flow<List<BackgroundEntity>> {
        return backgroundDao.getHomebrewBackgrounds()
    }

    fun deleteBackground(id: Int) {
        backgroundDao.deleteBackground(id)
    }
}