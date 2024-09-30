package ui.homebrew

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.lastOrNull
import model.*
import model.repositories.FeatureRepository
import model.repositories.RaceRepository
import org.koin.android.annotation.KoinViewModel
import kotlin.properties.Delegates

@KoinViewModel
class HomebrewRaceViewModel constructor(
    private val raceRepository: RaceRepository,
    private val featureRepository: FeatureRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    fun createDefaultFeature(): Int {
        val featureId = featureRepository.createDefaultFeature()
        raceRepository.insertRaceFeatureCrossRef(
            raceId = id,
            featureId = featureId
        )
        return featureId
    }

    fun createDefaultSubrace(): Int {
        val subraceId = raceRepository.createDefaultSubrace()
        raceRepository.insertRaceSubraceCrossRef(
            raceId = id,
            subraceId = subraceId
        )
        return subraceId
    }

    fun removeFeature(featureId: Int) {
        raceRepository.removeRaceFeatureCrossRef(
            raceId = id,
            featureId = featureId
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

    suspend fun deleteSubraceAt(it: Int) {
         subraces?.lastOrNull()?.get(it)?.id?.let {
            raceRepository.removeRaceSubraceCrossRef(
                raceId = id,
                subraceId = it
            )
        }
    }


    var subraces: Flow<List<Subrace>>? = null
    val sizeClassOptions = RaceRepository.sizeClasses
    val race: MutableStateFlow<Race> = MutableStateFlow(Race())
    val name = mutableStateOf("")
    val speed = mutableStateOf("")
    val sizeClass = mutableStateOf("")
    var id by Delegates.notNull<Int>()
    val abilityBonuses = mutableStateListOf<AbilityBonus>()
    val abilityBonusChoice = mutableStateOf<AbilityBonusChoice?>(null)

    init {

        viewModelScope.async(/*Dispatchers.IO*/) {
            id = savedStateHandle.get<String>("raceId")?.toInt().let {
                if ((it ?: 0) > 0) {
                    it
                } else {
                    raceRepository.createDefaultRace()
                }
            }!!
            raceRepository.getLiveRaceById(id).first {
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
                true
            }

            subraces = raceRepository.getSubracesByRaceId(id)

        }
    }
}