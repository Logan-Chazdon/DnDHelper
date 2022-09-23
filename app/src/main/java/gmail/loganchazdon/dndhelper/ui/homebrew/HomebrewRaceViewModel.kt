package gmail.loganchazdon.dndhelper.ui.homebrew

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.AbilityBonus
import gmail.loganchazdon.dndhelper.model.AbilityBonusChoice
import gmail.loganchazdon.dndhelper.model.Race
import gmail.loganchazdon.dndhelper.model.junctionEntities.RaceFeatureCrossRef
import gmail.loganchazdon.dndhelper.model.repositories.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class HomebrewRaceViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    fun createDefaultFeature() : Int{
        val featureId = repository.createDefaultFeature()!!
        repository.insertRaceFeatureCrossRef(
            RaceFeatureCrossRef(
                id = id,
                featureId = featureId
            )
        )
        return featureId
    }


    fun removeFeature(featureId: Int) {
        repository.removeRaceFeatureCrossRef(
            RaceFeatureCrossRef(
                id = id,
                featureId = featureId
            )
        )
    }

    fun saveRace() {
        val newRace = Race(
            name = name.value,
            id = id,
            groundSpeed = try { speed.value.toInt() } catch(_: NumberFormatException) { 30 },
            size = sizeClass.value
        )
        repository.insertRace(newRace)
    }


    val sizeClassOptions = Repository.sizeClasses
    val race : MediatorLiveData<Race> = MediatorLiveData()
    val name = mutableStateOf("")
    val speed = mutableStateOf("")
    val sizeClass = mutableStateOf("")
    var id by Delegates.notNull<Int>()
    val abilityBonuses = mutableStateListOf<AbilityBonus>()
    val abilityBonusChoice = mutableStateOf<AbilityBonusChoice?>(null)


    init {
        viewModelScope.launch(Dispatchers.IO) {
            id  = savedStateHandle.get<String>("raceId")?.toInt().let {
                if ((it ?: 0) > 0) {
                    it
                } else {
                    repository.createDefaultRace()
                }
            }!!
            race.addSource(repository.getLiveRaceById(id)!!) {
                race.value = it

                //Set all data in the viewModel to match the race.
                name.value = it.name
                speed.value = it.groundSpeed.toString()
                sizeClass.value = it.size

                it.abilityBonuses?.let { bonuses ->
                    if(abilityBonuses.isEmpty()) {
                        abilityBonuses.addAll(bonuses)
                    }
                }
                abilityBonusChoice.value = it.abilityBonusChoice
            }
        }
    }
}