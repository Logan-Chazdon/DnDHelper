package gmail.loganchazdon.dndhelper.model.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import gmail.loganchazdon.dndhelper.model.Background
import gmail.loganchazdon.dndhelper.model.BackgroundEntity
import gmail.loganchazdon.dndhelper.model.database.daos.BackgroundDao
import gmail.loganchazdon.dndhelper.model.database.daos.FeatureDao
import gmail.loganchazdon.dndhelper.model.junctionEntities.BackgroundFeatureCrossRef
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class BackgroundRepository @Inject constructor(
    private val backgroundDao : BackgroundDao,
    private val featureDao: FeatureDao
) {
    private val scope = CoroutineScope(Job())
    private val _backgrounds =
        backgroundDao.getAllBackgrounds()

    fun getBackgrounds(): LiveData<List<Background>> {
        return _backgrounds
    }

    fun getBackground(id: Int): LiveData<Background> {
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
        return result
    }

    fun insertBackground(backgroundEntity: BackgroundEntity) {
        backgroundDao.insertBackground(backgroundEntity)
    }

    fun insertBackgroundFeatureCrossRef(backgroundFeatureCrossRef: BackgroundFeatureCrossRef) {
        backgroundDao.insertBackgroundFeatureCrossRef(backgroundFeatureCrossRef)
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
}