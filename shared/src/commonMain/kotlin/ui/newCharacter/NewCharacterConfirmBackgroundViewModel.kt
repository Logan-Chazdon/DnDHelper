package ui.newCharacter

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.lastOrNull
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

        characterRepository.insertCharacterBackgroundCrossRef(
            backgroundId = background.lastOrNull()!!.id,
            characterId = id
        )

        background.lastOrNull()!!.languageChoices!!.forEach {
            it.chosen = dropDownStates[it.name]?.getSelected(it.from) as List<Language>
        }
        background.lastOrNull()!!.equipmentChoices.forEach {
            it.chosen = dropDownStates[it.name]?.getSelected(it.from) as List<List<Item>>
        }

        val backgroundCurrencyMap = Currency.getEmptyCurrencyMap()
        background.lastOrNull()!!.equipment.forEach {
            if (it is Currency) {
                backgroundCurrencyMap[it.abbreviatedName]!!.amount += it.amount
            }
        }
        characterRepository.setBackgroundCurrency(
            backgroundCurrencyMap,
            id
        )

        characterRepository.insertBackgroundChoiceEntity(
            backgroundId = background.lastOrNull()!!.id,
            characterId = id,
            languageChoices = background.lastOrNull()!!.languageChoices?.toStringList() ?: emptyList()
        )

        background.lastOrNull()!!.features?.forEach { feature ->
            feature.choices?.forEach {
                it.chosen =
                    featureDropDownStates[feature.name + feature.grantedAtLevel]?.getSelected()
            }
        }
        saveFeatures(background.lastOrNull()!!.features!!)
    }

    private fun saveFeatures(features: List<Feature>) {
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