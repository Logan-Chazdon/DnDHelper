package ui.homebrew

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import model.AbilityBonus
import model.AbilityBonusChoice
import model.Subrace
import model.SubraceEntity
import model.junctionEntities.RaceSubraceCrossRef
import model.junctionEntities.SubraceFeatureCrossRef
import model.pojos.NameAndIdPojo
import model.repositories.FeatureRepository
import model.repositories.RaceRepository
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class SubraceViewModel constructor(
    private val raceRepository: RaceRepository,
    private val featureRepository: FeatureRepository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    fun createDefaultFeature(): Int {
        val featureId = featureRepository.createDefaultFeature()
        raceRepository.insertSubraceFeatureCrossRef(
            SubraceFeatureCrossRef(
                subraceId = id,
                featureId = featureId
            )
        )
        return featureId
    }

    fun removeFeature(featureId: Int) {
        raceRepository.removeSubraceFeatureCrossRef(
            SubraceFeatureCrossRef(
                subraceId = id,
                featureId = featureId
            )
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

    fun removeRace(i: Int) {
        raceRepository.removeRaceSubraceCrossRef(
            subraceId = id,
            raceId = races.value!![i].id
        )
    }

    fun toggleRace(it: NameAndIdPojo) {
        if (races.value?.firstOrNull { item -> item.id == it.id } != null) {
            raceRepository.removeRaceSubraceCrossRef(raceId = it.id, id)
        } else {
            raceRepository.insertRaceSubraceCrossRef(
                RaceSubraceCrossRef(
                    raceId = it.id,
                    subraceId = id
                )
            )
        }
    }

    val allRaces= raceRepository.getAllRaceIdsAndNames()
    val abilityBonuses = mutableStateListOf<AbilityBonus>()
    val abilityBonusChoice = mutableStateOf<AbilityBonusChoice?>(null)
    val speed = mutableStateOf("")
    val sizeClass = mutableStateOf("")
    val name = mutableStateOf("")
    val subrace = MediatorLiveData<Subrace>()
    val id = savedStateHandle.get<String>("id")?.toInt()!!
    val sizeClassOptions = RaceRepository.sizeClasses
    val races = raceRepository.getRaceSubraces(id)
    val features = raceRepository.getSubraceLiveFeaturesById(id)

    init {
        subrace.addSource(raceRepository.getSubrace(id)) {
            //Set all data in the viewModel to match the race.
            name.value = it.name
            sizeClass.value = it.size ?: "Medium"
            speed.value = (it.groundSpeed ?: 30).toString()
        }
    }
}
