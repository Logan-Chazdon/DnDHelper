package ui.homebrew

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import model.*
import model.repositories.FeatureRepository
import model.repositories.ProficiencyRepository
import model.repositories.SpellRepository
import org.koin.android.annotation.KoinViewModel
import kotlin.properties.Delegates


@KoinViewModel
class HomebrewFeatureViewModel(
    spellRepository: SpellRepository,
    proficiencyRepository: ProficiencyRepository,
    private val featureRepository: FeatureRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    fun saveFeature() {
        viewModelScope.launch {
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


            featureChoices.lastOrNull()?.forEach { entity ->
                featureRepository.clearFeatureChoiceIndexRefs(entity.id)
                selectedIndexes.filter {
                    it.choiceId == entity.id
                }.forEach {
                    featureRepository.insertFeatureChoiceIndexCrossRef(
                        choiceId = it.choiceId,
                        index = it.index,
                        levels = it.levels,
                        classes = it.classes,
                        schools = it.schools,
                    )
                }
            }
        }
    }

    fun createDefaultFeature(choiceId: Int): Int {
        val featureId = featureRepository.createDefaultFeature()
        featureRepository.insertOptionsFeature(
            choiceId = choiceId,
            featureId = featureId
        )
        return featureId
    }

    fun createDefaultFeatureChoice() {
        val choiceId = featureRepository.createDefaultFeatureChoice()
        featureRepository.insertFeatureOptionsCrossRef(
            id = choiceId,
            featureId = id
        )
    }

    fun removeFeatureChoice(choiceId: Int) {
        featureRepository.removeFeatureOptionsCrossRef(
            id = choiceId,
            featureId = id
        )
    }

    fun removeFeatureFromChoice(featureId: Int, choiceId: Int) {
        featureRepository.removeOptionsFeatureCrossRef(
            featureId = featureId,
            choiceId = choiceId
        )
    }

    fun getOptions(id: Int): List<Feature> {
        return featureRepository.getFeatureChoiceOptions(id)
    }

    fun updateChoice(id: Int, choose: Choose) {
        val choice = FeatureChoiceEntity(
            choose = choose,
        )
        choice.id = id
        featureRepository.insertFeatureChoice(
            choice
        )
    }

    fun addSpell(it: Spell) {
        featureRepository.insertFeatureSpellCrossRef(
            featureId = id,
            spellId = it.id
        )
    }

    fun removeSpell(it: Spell) {
        featureRepository.removeFeatureSpellCrossRef(
            featureId = id,
            spellId = it.id
        )
    }

    val selectedIndexes: SnapshotStateList<FeatureChoiceIndexCrossRef> = mutableStateListOf()
    val featureIndexes = featureRepository.getAllIndexes()
    val infusion = mutableStateOf<Infusion?>(null)
    val spells = mutableStateOf<List<Spell>>(emptyList())
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
    var feature: MutableStateFlow<Feature> = MutableStateFlow(Feature(name ="", description = ""))
    val allProficiencies = proficiencyRepository.getAllSkills()
    val proficiencies = mutableStateListOf<Proficiency>()
    val expertises = mutableStateListOf<Proficiency>()
    val baseAc = mutableStateOf("10")
    val dexMax = mutableStateOf("0")
    val wisMax = mutableStateOf("0")
    val conMax = mutableStateOf("0")
    val acBonus = mutableStateOf("0")
    val featureChoices = featureRepository.getFeatureChoiceEntities(id)

    init {
        viewModelScope.launch {
            id = savedStateHandle.get<String>("featureId")?.toInt().let {
                if ((it ?: 0) > 0) {
                    it
                } else {
                    featureRepository.createDefaultFeature()
                }
            }!!



            featureRepository.getLiveFeature(id).collect {
                feature.value = it
            }

            featureRepository.getFeatureSpells(id).collect {
                if (it != null) {
                    spells.value = it
                }
            }
        }
    }
}



