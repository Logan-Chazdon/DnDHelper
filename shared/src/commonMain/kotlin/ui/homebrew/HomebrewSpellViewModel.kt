package ui.homebrew


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import model.Feature
import model.Item
import model.Spell
import model.pojos.NameAndIdPojo
import model.repositories.ClassRepository
import model.repositories.FeatureRepository
import model.repositories.SpellRepository
import org.koin.android.annotation.KoinViewModel
import ui.utils.allNames


@KoinViewModel
class HomebrewSpellViewModel(
    private val spellRepository: SpellRepository,
    private val featureRepository: FeatureRepository,
    classRepository: ClassRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val allClasses = classRepository.getAllClassNameAndIds()
    val id = savedStateHandle.get<String>("id")!!.toInt()
    val spell = spellRepository.getLiveSpell(id)
    val name = mutableStateOf("")
    val desc = mutableStateOf("")
    val range = mutableStateOf("")
    val damage = mutableStateOf("")
    val area = mutableStateOf("")
    val duration = mutableStateOf("")
    val school = mutableStateOf("")
    val level = mutableStateOf("")
    val hasSomatic = mutableStateOf(false)
    val hasVerbal = mutableStateOf(false)
    val isRitual = mutableStateOf(false)
    val hasMaterial = mutableStateOf(false)
    val materials = mutableStateOf("")
    val castingTime = mutableStateOf("")
    val classes = spellRepository.getSpellClasses(id)
    fun saveSpell() {
        val components = mutableListOf<String>()
        val itemComponents = mutableListOf<Item>()

        val spell = Spell(
            name = name.value,
            desc = desc.value,
            damage = damage.value,
            range = range.value,
            level = level.value.toIntOrNull() ?: 1,
            area = area.value,
            castingTime = castingTime.value,
            classes = emptyList(),
            components = components,
            itemComponents = itemComponents,
            isRitual = isRitual.value,
            duration = duration.value,
            school = school.value,
            isHomebrew = true,
        )
        spell.id = id

        spellRepository.insertSpell(
            spell
        )

        val spellFeature = Feature(
            name = spell.name,
            description = spell.desc,
            featureId = featureRepository.getFeatureIdOr0FromSpellId(spell.id)
        )
        val featureId = featureRepository.insertFeature(spellFeature)
        featureRepository.insertFeatureSpellCrossRef(
            featureId = featureId,
            spellId = spell.id
        )

        featureRepository.updateIndexRef(
            index = "Spells",
            ids = listOf(featureId)
        )
    }

    suspend fun removeClass(index: Int) {
        spellRepository.removeClassSpellCrossRef(
            classId = classes.lastOrNull()!![index].id,
            spellId = id
        )
    }

    suspend fun toggleClass(it: NameAndIdPojo) {
        if (classes.lastOrNull()?.firstOrNull { item -> item.id == it.id } != null) {
            spellRepository.removeClassSpellCrossRef(
                classId = it.id,
                spellId = id
            )
        } else {
            spellRepository.addClassSpellCrossRef(
                classId = it.id,
                spellId = id
            )
        }
    }

    init {
        viewModelScope.launch {
            spell.firstOrNull { value ->
                value.let {
                    name.value = it.name
                    materials.value = it.itemComponents.allNames
                    level.value = it.level.toString()
                    desc.value = it.desc
                    damage.value = it.damage
                    hasSomatic.value = it.components.contains("Somatic")
                    hasVerbal.value = it.components.contains("Verbal")
                    hasMaterial.value = it.components.contains("Material")
                    range.value = it.range
                    area.value = it.area
                    castingTime.value = it.castingTime
                    isRitual.value = it.isRitual
                    duration.value = it.duration
                    school.value = it.school
                }
                true //TODO check this
            }
        }
    }
}
