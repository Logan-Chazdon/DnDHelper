package gmail.loganchazdon.dndhelper.ui.homebrew

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.AbilityBonus
import gmail.loganchazdon.dndhelper.model.AbilityBonusChoice
import gmail.loganchazdon.dndhelper.model.Subrace
import gmail.loganchazdon.dndhelper.model.SubraceEntity
import gmail.loganchazdon.dndhelper.model.junctionEntities.SubraceFeatureCrossRef
import gmail.loganchazdon.dndhelper.model.repositories.Repository
import javax.inject.Inject


@HiltViewModel
class SubraceViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    fun createDefaultFeature(): Int {
        val featureId = repository.createDefaultFeature()!!
        repository.insertSubraceFeatureCrossRef(
            SubraceFeatureCrossRef(
                subraceId = id,
                featureId = featureId
            )
        )
        return featureId
    }

    fun removeFeature(featureId: Int) {
        repository.removeSubraceFeatureCrossRef(
            SubraceFeatureCrossRef(
                subraceId = id,
                featureId = featureId
            )
        )
    }

    fun saveSubrace() {
        repository.insertSubrace(
            SubraceEntity(
                name = name.value,
                abilityBonuses = abilityBonuses,
                abilityBonusChoice = abilityBonusChoice.value,
            )
        )
    }

    val abilityBonuses = mutableStateListOf<AbilityBonus>()
    val abilityBonusChoice = mutableStateOf<AbilityBonusChoice?>(null)
    val speed = mutableStateOf("")
    val sizeClass = mutableStateOf("")
    val name = mutableStateOf("")
    val subrace = MediatorLiveData<Subrace>()
    val id = savedStateHandle.get<String>("id")?.toInt()!!
    val sizeClassOptions = Repository.sizeClasses

    init {
        subrace.addSource(repository.getSubrace(id)) {
            //Set all data in the viewModel to match the race.
            name.value = it.name
            sizeClass.value = it.size ?: "Medium"
            speed.value = (it.groundSpeed ?: 30).toString()
        }
    }
}
