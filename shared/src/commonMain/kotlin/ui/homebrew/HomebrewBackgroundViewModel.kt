package ui.homebrew

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import model.BackgroundEntity
import model.ItemInterface
import model.repositories.BackgroundRepository
import model.repositories.FeatureRepository
import model.repositories.ItemRepository
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class HomebrewBackgroundViewModel constructor(
    itemsRepository: ItemRepository,
    private val featureRepository: FeatureRepository,
    private val backgroundRepository: BackgroundRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    fun saveBackground() {
        val background = BackgroundEntity(
            name = name.value,
            desc = desc.value,
            equipment = equipment,
            equipmentChoices = emptyList(),
            languages = emptyList(),
            proficiencies = emptyList(),
            spells = emptyList(),
            isHomebrew = true
        )
        background.id = id
        backgroundRepository.insertBackground(
            background
        )
    }

    fun createDefaultFeature(): Int {
        val featureId = featureRepository.createDefaultFeature()
        backgroundRepository.insertBackgroundFeatureCrossRef(
            backgroundId = id,
            featureId = featureId
        )
        return featureId
    }

    val allItems = itemsRepository.getAllItems()
    val id = savedStateHandle.get<String>("id")!!.toInt()
    val background = backgroundRepository.getBackground(id)
    val name = mutableStateOf("")
    val desc = mutableStateOf("")
    val equipment = mutableStateListOf<ItemInterface>()
}