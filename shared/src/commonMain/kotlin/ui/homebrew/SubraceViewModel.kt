package ui.homebrew

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.last
import model.AbilityBonus
import model.AbilityBonusChoice
import model.SubraceEntity
import model.pojos.NameAndIdPojo
import model.repositories.FeatureRepository
import model.repositories.RaceRepository
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class SubraceViewModel constructor(
    private val raceRepository: RaceRepository,
    private val featureRepository: FeatureRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    fun createDefaultFeature(): Int {
        val featureId = featureRepository.createDefaultFeature()
        raceRepository.insertSubraceFeatureCrossRef(
            subraceId = id,
            featureId = featureId
        )
        return featureId
    }

    fun removeFeature(featureId: Int) {
        raceRepository.removeSubraceFeatureCrossRef(
            subraceId = id,
            featureId = featureId
        )
    }

    fun saveSubrace() {
        val entity = SubraceEntity(
            name = name.value,
            size = sizeClass.value,
            abilityBonuses = abilityBonuses,
            abilityBonusChoice = abilityBonusChoice.value,
            isHomebrew = true
        )
        entity.id = id
        raceRepository.insertSubrace(
            entity
        )
    }

    suspend fun removeRace(i: Int) {
        raceRepository.removeRaceSubraceCrossRef(
            subraceId = id,
            raceId = races.last()[i].id
        )
    }

    suspend fun toggleRace(it: NameAndIdPojo) {
        if (races.last().firstOrNull { item -> item.id == it.id } != null) {
            raceRepository.removeRaceSubraceCrossRef(raceId = it.id, id)
        } else {
            raceRepository.insertRaceSubraceCrossRef(
                raceId = it.id,
                subraceId = id
            )
        }
    }

    val allRaces= raceRepository.getAllRaceIdsAndNames()
    val abilityBonuses = mutableStateListOf<AbilityBonus>()
    val abilityBonusChoice = mutableStateOf<AbilityBonusChoice?>(null)
    val speed = mutableStateOf("")
    val sizeClass = mutableStateOf("")
    val name = mutableStateOf("")
    val id = savedStateHandle.get<String>("id")?.toInt()!!
    val subrace = raceRepository.getSubrace(id)
    val sizeClassOptions = RaceRepository.sizeClasses
    val races = raceRepository.getRaceSubraces(id)
    val features = raceRepository.getSubraceLiveFeaturesById(id)
}
