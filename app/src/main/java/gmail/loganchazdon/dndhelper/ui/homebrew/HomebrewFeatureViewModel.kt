package gmail.loganchazdon.dndhelper.ui.homebrew

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.Choose
import gmail.loganchazdon.dndhelper.model.Feature
import gmail.loganchazdon.dndhelper.model.FeatureChoiceEntity
import gmail.loganchazdon.dndhelper.model.Infusion
import gmail.loganchazdon.dndhelper.model.Language
import gmail.loganchazdon.dndhelper.model.Proficiency
import gmail.loganchazdon.dndhelper.model.Spell
import gmail.loganchazdon.dndhelper.model.junctionEntities.FeatureChoiceIndexCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.FeatureOptionsCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.FeatureSpellCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.OptionsFeatureCrossRef
import gmail.loganchazdon.dndhelper.model.repositories.FeatureRepository
import gmail.loganchazdon.dndhelper.model.repositories.ProficiencyRepository
import gmail.loganchazdon.dndhelper.model.repositories.SpellRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates


@HiltViewModel
class HomebrewFeatureViewModel @Inject constructor(
    spellRepository: SpellRepository,
    proficiencyRepository: ProficiencyRepository,
    private val featureRepository: FeatureRepository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    fun saveFeature() {
        viewModelScope.launch(Dispatchers.IO) {
            val newFeature = Feature(
                name = featureName.value,
                description = featureDesc.value,
                grantedAtLevel = featureLevel.value.toIntOrNull() ?: 1,
                featureId = id,
                expertises = expertises,
                proficiencies = proficiencies,
                infusion = infusion.value
            )
            featureRepository.insertFeature(newFeature)
        }

        featureChoices.value?.forEach { entity ->
            featureRepository.clearFeatureChoiceIndexRefs(entity.id)
            selectedIndexes.filter {
                it.choiceId == entity.id
            }.forEach {
                featureRepository.insertFeatureChoiceIndexCrossRef(it)
            }
        }
    }

    fun createDefaultFeature(choiceId: Int): Int {
        val featureId = featureRepository.createDefaultFeature()
        featureRepository.insertOptionsFeature(
            OptionsFeatureCrossRef(
                choiceId = choiceId,
                featureId = featureId
            )
        )
        return featureId
    }

    fun createDefaultFeatureChoice() {
        val choiceId = featureRepository.createDefaultFeatureChoice()
        featureRepository.insertFeatureOptionsCrossRef(
            FeatureOptionsCrossRef(
                id = choiceId,
                featureId = id
            )
        )
    }

    fun removeFeatureChoice(choiceId: Int) {
        featureRepository.removeFeatureOptionsCrossRef(
            FeatureOptionsCrossRef(
                id = choiceId,
                featureId = id
            )
        )
    }

    fun removeFeatureFromChoice(featureId: Int, choiceId: Int) {
        featureRepository.removeOptionsFeatureCrossRef(
            OptionsFeatureCrossRef(
                featureId = featureId,
                choiceId = choiceId
            )
        )
    }

    fun getOptions(id: Int): List<Feature> {
        return featureRepository.getFeatureChoiceOptions(id)
    }

    fun updateChoice(id: Int, choose: Choose) {
        val choice =  FeatureChoiceEntity(
            choose= choose,
        )
        choice.id= id
        featureRepository.insertFeatureChoice(
            choice
        )
    }

    fun addSpell(it: Spell) {
        featureRepository.insertFeatureSpellCrossRef(
            FeatureSpellCrossRef(
                featureId = id,
                spellId = it.id
            )
        )
    }

    fun removeSpell(it: Spell) {
        featureRepository.removeFeatureSpellCrossRef(
            FeatureSpellCrossRef(
                featureId = id,
                spellId = it.id
            )
        )
    }

    val selectedIndexes: SnapshotStateList<FeatureChoiceIndexCrossRef> = mutableStateListOf()
    val featureIndexes = featureRepository.getAllIndexes()
    val infusion = mutableStateOf<Infusion?>(null)
    val spells = MediatorLiveData<List<Spell>>()
    val languages = mutableStateListOf<Language>()
    val featureName = mutableStateOf("")
    val featureDesc = mutableStateOf("")
    val featureLevel = mutableStateOf("")
    val grantsExpertise = mutableStateOf(false)
    val grantsProficiencies = mutableStateOf(false)
    val grantsLanguages = mutableStateOf(false)
    val containsChoices = mutableStateOf(false)
    val grantsAcBonus = mutableStateOf(false)
    val replacesAc = mutableStateOf(false)
    val grantsInfusions = mutableStateOf(false)
    val grantsSpells = mutableStateOf(false)
    val allSpells = spellRepository.getLiveSpells()
    val allLanguages = proficiencyRepository.getLanguagesByIndex("all_languages")!!
    val allInfusions = featureRepository.getAllInfusions()
    var id by Delegates.notNull<Int>()
    var feature: MediatorLiveData<Feature> = MediatorLiveData()
    val allProficiencies = proficiencyRepository.getAllSkills()
    val proficiencies = mutableStateListOf<Proficiency>()
    val expertises = mutableStateListOf<Proficiency>()
    val baseAc = mutableStateOf("10")
    val dexMax = mutableStateOf("0")
    val wisMax = mutableStateOf("0")
    val conMax = mutableStateOf("0")
    val acBonus = mutableStateOf("0")
    val featureChoices = MediatorLiveData<List<FeatureChoiceEntity>>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            id = savedStateHandle.get<String>("featureId")?.toInt().let {
                if ((it ?: 0) > 0) {
                    it
                } else {
                    featureRepository.createDefaultFeature()
                }
            }!!

            featureChoices.addSource( featureRepository.getFeatureChoiceEntities(id)) {
                if(it.isNotEmpty()) {
                    containsChoices.value = true
                }
                featureChoices.postValue(it)
            }

            feature.addSource(featureRepository.getLiveFeature(id)) {
                feature.value = it
            }

            spells.addSource(featureRepository.getFeatureSpells(id)) {
                spells.value = it
            }
        }
    }
}



