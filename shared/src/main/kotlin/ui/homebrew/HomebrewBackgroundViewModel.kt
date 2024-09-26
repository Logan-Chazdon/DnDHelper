package ui.homebrew

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import model.BackgroundEntity
import model.ItemInterface
import model.junctionEntities.BackgroundFeatureCrossRef
import model.repositories.BackgroundRepository
import model.repositories.FeatureRepository
import model.repositories.ItemRepository
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class HomebrewBackgroundViewModel constructor(
    itemsRepository: ItemRepository,
    private val featureRepository: FeatureRepository,
    private val backgroundRepository: BackgroundRepository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
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
            BackgroundFeatureCrossRef(
                backgroundId = id,
                featureId = featureId
            )
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