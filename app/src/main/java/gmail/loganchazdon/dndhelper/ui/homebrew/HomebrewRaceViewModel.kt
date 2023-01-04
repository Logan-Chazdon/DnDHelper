package gmail.loganchazdon.dndhelper.ui.homebrew

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.AbilityBonus
import gmail.loganchazdon.dndhelper.model.AbilityBonusChoice
import gmail.loganchazdon.dndhelper.model.Race
import gmail.loganchazdon.dndhelper.model.Subrace
import gmail.loganchazdon.dndhelper.model.junctionEntities.RaceFeatureCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.RaceSubraceCrossRef
import gmail.loganchazdon.dndhelper.model.repositories.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class HomebrewRaceViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    fun createDefaultFeature(): Int {
        val featureId = repository.createDefaultFeature()!!
        repository.insertRaceFeatureCrossRef(
            RaceFeatureCrossRef(
                raceId = id,
                featureId = featureId
            )
        )
        return featureId
    }

    fun createDefaultSubrace() : Int {
        val subraceId = repository.createDefaultSubrace()
        repository.insertRaceSubraceCrossRef(
            RaceSubraceCrossRef(
                raceId = id,
                subraceId = subraceId
            )
        )
        return subraceId
    }

    fun removeFeature(featureId: Int) {
        repository.removeRaceFeatureCrossRef(
            RaceFeatureCrossRef(
                raceId = id,
                featureId = featureId
            )
        )
    }

    fun saveRace() {
        val newRace = Race(
            name = name.value,
            id = id,
            groundSpeed = try {
                speed.value.toInt()
            } catch (_: NumberFormatException) {
                30
            },
            size = sizeClass.value,
            abilityBonusChoice = abilityBonusChoice.value,
            abilityBonuses = abilityBonuses
        )
        repository.insertRace(newRace)
    }

    fun deleteSubraceAt(it: Int) {

    }


    var subraces : LiveData<List<Subrace>>? = null
    val sizeClassOptions = Repository.sizeClasses
    val race: MediatorLiveData<Race> = MediatorLiveData()
    val name = mutableStateOf("")
    val speed = mutableStateOf("")
    val sizeClass = mutableStateOf("")
    var id by Delegates.notNull<Int>()
    val abilityBonuses = mutableStateListOf<AbilityBonus>()
    val abilityBonusChoice = mutableStateOf<AbilityBonusChoice?>(null)

    init {
        runBlocking {
            viewModelScope.async(Dispatchers.IO) {
                id = savedStateHandle.get<String>("raceId")?.toInt().let {
                    if ((it ?: 0) > 0) {
                        it
                    } else {
                        repository.createDefaultRace()
                    }
                }!!
            }.invokeOnCompletion {
                race.addSource(repository.getLiveRaceById(id)!!) {
                    race.value = it

                    //Set all data in the viewModel to match the race.
                    name.value = it.raceName
                    speed.value = it.groundSpeed.toString()
                    sizeClass.value = it.size

                    it.abilityBonuses?.let { bonuses ->
                        if (abilityBonuses.isEmpty()) {
                            abilityBonuses.addAll(bonuses)
                        }
                    }
                    abilityBonusChoice.value = it.abilityBonusChoice
                }

                subraces = repository.getSubracesByRaceId(id)
            }

        }
    }
}