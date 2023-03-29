package gmail.loganchazdon.dndhelper.ui.homebrew

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.BackgroundEntity
import gmail.loganchazdon.dndhelper.model.ItemInterface
import gmail.loganchazdon.dndhelper.model.junctionEntities.BackgroundFeatureCrossRef
import gmail.loganchazdon.dndhelper.model.repositories.BackgroundRepository
import gmail.loganchazdon.dndhelper.model.repositories.FeatureRepository
import gmail.loganchazdon.dndhelper.model.repositories.ItemRepository
import javax.inject.Inject

@HiltViewModel
class HomebrewBackgroundViewModel @Inject constructor(
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