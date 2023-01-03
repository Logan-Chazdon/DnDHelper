package gmail.loganchazdon.dndhelper.ui.homebrew

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.Feature
import gmail.loganchazdon.dndhelper.model.Infusion
import gmail.loganchazdon.dndhelper.model.Language
import gmail.loganchazdon.dndhelper.model.Spell
import gmail.loganchazdon.dndhelper.model.repositories.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates


@HiltViewModel
class HomebrewFeatureViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    fun saveFeature() {
        viewModelScope.launch(Dispatchers.IO) {
            val newFeature = Feature(
                spells = if(grantsSpells.value) spells else listOf(),
                name = featureName.value,
                description = featureDesc.value,
                grantedAtLevel = featureLevel.value.toIntOrNull() ?: 1,
                featureId = id
            )
            repository.insertFeature(newFeature)
        }
    }

    val infusions = mutableStateListOf<Infusion>()
    val spells = mutableStateListOf<Spell>()
    val languages = mutableStateListOf<Language>()
    val featureName = mutableStateOf("")
    val featureDesc = mutableStateOf("")
    val featureLevel = mutableStateOf("")
    val grantsExpertise = mutableStateOf(true)
    val grantsProficiencies = mutableStateOf(true)
    val grantsLanguages = mutableStateOf(true)
    val containsChoices = mutableStateOf(true)
    val grantsAcBonus = mutableStateOf(true)
    val replacesAc = mutableStateOf(true)
    val grantsInfusions = mutableStateOf(true)
    val grantsSpells = mutableStateOf(true)
    val allSpells = repository.getLiveSpells()
    val allLanguages = repository.getLanguagesByIndex("all_languages")!!
    val allInfusions = repository.getAllInfusions()
    var id by Delegates.notNull<Int>()
    var feature: MediatorLiveData<Feature> = MediatorLiveData()

    val baseAc = mutableStateOf("10")
    val dexMax = mutableStateOf("0")
    val wisMax = mutableStateOf("0")
    val conMax = mutableStateOf("0")
    val acBonus = mutableStateOf("0")

    init {
        viewModelScope.launch(Dispatchers.IO) {
            id = savedStateHandle.get<String>("featureId")?.toInt().let {
                if ((it ?: 0) > 0) {
                    it
                } else {
                    repository.createDefaultFeature()
                }
            }!!

            feature.addSource(repository.getLiveFeature(id)!!) {
                //Set all viewModel data to match feature in database.

                //General
                featureName.value = it.name
                featureDesc.value = it.description
                featureLevel.value = it.grantedAtLevel.toString()

                //Toggles and data
                grantsSpells.value = it.getSpellsGiven().isNotEmpty()
                if(grantsSpells.value) {
                    //Fill out spells
                    spells.addAll(it.spells!!)
                }

                grantsInfusions.value = it.grantsInfusions
                if(grantsInfusions.value) {
                    //Fill out infusions

                }

                grantsExpertise.value = !it.expertises.isNullOrEmpty()
                if(grantsExpertise.value) {
                    //Fill out expertises

                }

                grantsProficiencies.value = !it.languages.isNullOrEmpty()
                if(grantsProficiencies.value) {
                    //Fill out proficiencies

                }

                grantsLanguages.value = !it.languages.isNullOrEmpty()
                if(grantsLanguages.value) {
                    //Fill out languages

                }

                grantsAcBonus.value = it.acBonus != 0
                if(grantsAcBonus.value) {
                    //Fill out ac bonus

                }
            }
        }
    }
}



