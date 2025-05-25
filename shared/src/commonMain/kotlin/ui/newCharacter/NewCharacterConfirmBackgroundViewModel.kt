package ui.newCharacter

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import model.*
import model.repositories.BackgroundRepository
import model.repositories.CharacterRepository
import org.koin.android.annotation.KoinViewModel
import ui.newCharacter.stateHolders.MultipleChoiceDropdownStateFeatureImpl
import ui.newCharacter.stateHolders.MultipleChoiceDropdownStateImpl
import ui.utils.toStringList


@KoinViewModel
class NewCharacterConfirmBackgroundViewModel constructor(
    backgroundRepository: BackgroundRepository,
    private val characterRepository: CharacterRepository,
    savedStateHandle: SavedStateHandle,
    val id : MutableStateFlow<Int>
) : ViewModel() {
    var dropDownStates = mutableStateMapOf<String, MultipleChoiceDropdownStateImpl>()
    var featureDropDownStates = mutableStateMapOf<String, MultipleChoiceDropdownStateFeatureImpl>()
    val character = MutableStateFlow(Character())
    val background =
        backgroundRepository.getBackground(savedStateHandle.get<String>("backgroundId")!!.toInt())


    init {
        viewModelScope.launch {
            id.collect {
                characterRepository.getLiveCharacterById(
                    it,
                    character
                )
            }
        }
    }


    suspend fun setBackGround() {
        if (id.value == -1)
            id.value = characterRepository.createDefaultCharacter()

        viewModelScope.launch {
            background.first().let { value ->
                characterRepository.insertCharacterBackgroundCrossRef(
                    backgroundId = value.id,
                    characterId = id.value
                )

                value.languageChoices!!.forEach {
                    it.chosen = dropDownStates[it.name]?.getSelected(it.from) as List<Language>
                }
                value.equipmentChoices.forEach {
                    it.chosen = dropDownStates[it.name]?.getSelected(it.from) as List<List<Item>>
                }

                val backgroundCurrencyMap = Currency.getEmptyCurrencyMap()
                value.equipment.forEach {
                    if (it is Currency) {
                        backgroundCurrencyMap[it.abbreviatedName]!!.amount += it.amount
                    }
                }
                characterRepository.setBackgroundCurrency(
                    backgroundCurrencyMap,
                    id.value
                )

                characterRepository.insertBackgroundChoiceEntity(
                    backgroundId = value.id,
                    characterId = id.value,
                    languageChoices = value.languageChoices?.toStringList() ?: emptyList()
                )

                value.features?.forEach { feature ->
                    feature.choices?.forEach {
                        it.chosen =
                            featureDropDownStates[feature.name + feature.grantedAtLevel]?.getSelected()
                    }
                }
                saveFeatures(value.features!!)
            }
        }
    }

    private suspend fun saveFeatures(features: List<Feature>) {
        features.forEach { feature ->
            feature.choices?.forEach { choice ->
                choice.chosen?.forEach { chosen ->
                    characterRepository.insertFeatureChoiceChoiceEntity(
                        featureId = chosen.featureId,
                        choiceId = choice.id,
                        characterId = id.value
                    )
                }
            }
        }
    }
}