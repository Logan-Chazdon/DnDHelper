package model.repositories


import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import model.Background
import model.BackgroundEntity
import model.database.daos.BackgroundDao
import model.database.daos.FeatureDao
import model.junctionEntities.BackgroundFeatureCrossRef


actual class BackgroundRepository constructor(
    private val backgroundDao : BackgroundDao,
    private val featureDao: FeatureDao
) {
    private val scope = CoroutineScope(Job())
    private val _backgrounds =
        backgroundDao.getAllBackgrounds()

    actual fun getBackgrounds(): Flow<List<Background>> {
        return _backgrounds.asFlow()
    }

    actual fun getBackground(id: Int): Flow<Background> {
        val result = MediatorLiveData<Background>()
        result.addSource(backgroundDao.getUnfilledBackground(id)) {
            if (it != null) {
                scope.launch {
                    val background = Background(
                        it,
                        backgroundDao.getUnfilledBackgroundFeatures(id)
                    )
                    background.spells = backgroundDao.getBackgroundSpells(id)
                    featureDao.fillOutFeatureListWithoutChosen(background.features!!)
                    result.postValue(background)
                }
            }
        }
        return result.asFlow()
    }

    actual fun insertBackground(backgroundEntity: BackgroundEntity) {
        backgroundDao.insertBackground(backgroundEntity)
    }

    actual fun insertBackgroundFeatureCrossRef(backgroundFeatureCrossRef: BackgroundFeatureCrossRef) {
        backgroundDao.insertBackgroundFeatureCrossRef(backgroundFeatureCrossRef)
    }

    actual fun createDefaultBackground(): Int {
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

    actual  fun getHomebrewBackgrounds(): Flow<List<BackgroundEntity>> {
        return backgroundDao.getHomebrewBackgrounds().asFlow()
    }

    actual fun deleteBackground(id: Int) {
        backgroundDao.deleteBackground(id)
    }
}

