package gmail.loganchazdon.dndhelper.ui.newCharacter

import android.app.Application
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.Character
import gmail.loganchazdon.dndhelper.model.Feature
import gmail.loganchazdon.dndhelper.model.Item
import gmail.loganchazdon.dndhelper.model.Language
import gmail.loganchazdon.dndhelper.model.choiceEntities.BackgroundChoiceEntity
import gmail.loganchazdon.dndhelper.model.choiceEntities.FeatureChoiceChoiceEntity
import gmail.loganchazdon.dndhelper.model.repositories.Repository
import gmail.loganchazdon.dndhelper.ui.newCharacter.stateHolders.MultipleChoiceDropdownStateFeatureImpl
import gmail.loganchazdon.dndhelper.ui.newCharacter.stateHolders.MultipleChoiceDropdownStateImpl
import gmail.loganchazdon.dndhelper.ui.utils.toStringList
import javax.inject.Inject


@HiltViewModel
class NewCharacterConfirmBackgroundViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    var dropDownStates = mutableStateMapOf<String, MultipleChoiceDropdownStateImpl>()
    var featureDropDownStates = mutableStateMapOf<String, MultipleChoiceDropdownStateFeatureImpl>()
    var id = -1
    val character: MediatorLiveData<Character> = MediatorLiveData()
    val background =
        repository.getBackground(savedStateHandle.get<String>("backgroundId")!!.toInt())


    init {
        id = try {
            savedStateHandle.get<String>("characterId")!!.toInt()
        } catch (e: Exception) {
            -1
        }

        if (id != -1) {
            repository.getLiveCharacterById(
                savedStateHandle.get<String>("characterId")!!.toInt(),
                character
            )
        }
    }


    suspend fun setBackGround() {
        if (id == -1)
            id = repository.createDefaultCharacter() ?: -1

        repository.insertCharacterBackgroundCrossRef(
            backgroundId = background.value!!.id,
            characterId = id
        )

        background.value!!.languageChoices!!.forEach {
            it.chosen = dropDownStates[it.name]?.getSelected(it.from) as List<Language>
        }
        background.value!!.equipmentChoices.forEach {
            it.chosen = dropDownStates[it.name]?.getSelected(it.from) as List<List<Item>>
        }

        repository.insertBackgroundChoiceEntity(
            BackgroundChoiceEntity(
                backgroundId = background.value!!.id,
                characterId = id,
                languageChoices = background.value!!.languageChoices?.toStringList() ?: emptyList()
            )
        )

        background.value!!.features?.forEach { feature ->
            feature.choices?.forEach {
                it.chosen =
                    featureDropDownStates[feature.name + feature.grantedAtLevel]?.getSelected()
            }
        }
        saveFeatures(background.value!!.features!!)
    }

    private fun saveFeatures(features: List<Feature>) {
        features.forEach { feature ->
            feature.choices?.forEach { choice ->
                choice.chosen?.forEach { chosen ->
                    repository.insertFeatureChoiceChoiceEntity(
                        FeatureChoiceChoiceEntity(
                            featureId = chosen.featureId,
                            choiceId = choice.id,
                            characterId = id
                        )
                    )
                }
            }
        }
    }
}