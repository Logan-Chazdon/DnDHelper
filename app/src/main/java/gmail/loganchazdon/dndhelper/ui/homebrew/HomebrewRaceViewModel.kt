package gmail.loganchazdon.dndhelper.ui.homebrew

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.junctionEntities.RaceFeatureCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.RaceSubraceCrossRef
import gmail.loganchazdon.dndhelper.model.repositories.FeatureRepository
import gmail.loganchazdon.dndhelper.model.repositories.RaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class HomebrewRaceViewModel @Inject constructor(
    private val raceRepository: RaceRepository,
    private val featureRepository: FeatureRepository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    fun createDefaultFeature(): Int {
        val featureId = featureRepository.createDefaultFeature()
        raceRepository.insertRaceFeatureCrossRef(
            RaceFeatureCrossRef(
                raceId = id,
                featureId = featureId
            )
        )
        return featureId
    }

    fun createDefaultSubrace() : Int {
        val subraceId = raceRepository.createDefaultSubrace()
        raceRepository.insertRaceSubraceCrossRef(
            RaceSubraceCrossRef(
                raceId = id,
                subraceId = subraceId
            )
        )
        return subraceId
    }

    fun removeFeature(featureId: Int) {
        raceRepository.removeRaceFeatureCrossRef(
            RaceFeatureCrossRef(
                raceId = id,
                featureId = featureId
            )
        )
    }

    fun saveRace() {
        val newRace = RaceEntity(
            raceName = name.value,
            raceId = id,
            groundSpeed = try {
                speed.value.toInt()
            } catch (_: NumberFormatException) {
                30
            },
            size = sizeClass.value,
            abilityBonusChoice = abilityBonusChoice.value,
            abilityBonuses = abilityBonuses,
            isHomebrew = true
        )
        raceRepository.insertRace(newRace)
    }

    fun deleteSubraceAt(it: Int) {
        val subraceId = subraces!!.value!![it].id
        raceRepository.removeRaceSubraceCrossRef(
            raceId = id,
            subraceId = subraceId
        )
    }


    var subraces : LiveData<List<Subrace>>? = null
    val sizeClassOptions = RaceRepository.sizeClasses
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
                        raceRepository.createDefaultRace()
                    }
                }!!
            }.invokeOnCompletion {
                race.addSource(raceRepository.getLiveRaceById(id)) {
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

                subraces = raceRepository.getSubracesByRaceId(id)
            }

        }
    }
}