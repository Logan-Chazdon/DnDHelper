package ui.newCharacter

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var dropDownStates = mutableStateMapOf<String, MultipleChoiceDropdownStateImpl>()
    var featureDropDownStates = mutableStateMapOf<String, MultipleChoiceDropdownStateFeatureImpl>()
    var id = -1
    val character = MutableStateFlow(Character())
    val background =
        backgroundRepository.getBackground(savedStateHandle.get<String>("backgroundId")!!.toInt())


    init {
        id = try {
            savedStateHandle.get<String>("characterId")!!.toInt()
        } catch (e: Exception) {
            -1
        }

        if (id != -1) {
            characterRepository.getLiveCharacterById(
                savedStateHandle.get<String>("characterId")!!.toInt(),
                character
            )
        }
    }


    suspend fun setBackGround() {
        if (id == -1)
            id = characterRepository.createDefaultCharacter()

        background.first().let { value ->
            characterRepository.insertCharacterBackgroundCrossRef(
                backgroundId = value.id,
                characterId = id
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
                id
            )

            characterRepository.insertBackgroundChoiceEntity(
                backgroundId = value.id,
                characterId = id,
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

    private suspend fun saveFeatures(features: List<Feature>) {
        features.forEach { feature ->
            feature.choices?.forEach { choice ->
                choice.chosen?.forEach { chosen ->
                    characterRepository.insertFeatureChoiceChoiceEntity(
                        featureId = chosen.featureId,
                        choiceId = choice.id,
                        characterId = id
                    )
                }
            }
        }
    }
}