package gmail.loganchazdon.dndhelper.ui.homebrew

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.Feature
import gmail.loganchazdon.dndhelper.model.Item
import gmail.loganchazdon.dndhelper.model.Spell
import gmail.loganchazdon.dndhelper.model.junctionEntities.ClassSpellCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.FeatureSpellCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.IndexRef
import gmail.loganchazdon.dndhelper.model.pojos.NameAndIdPojo
import gmail.loganchazdon.dndhelper.model.repositories.ClassRepository
import gmail.loganchazdon.dndhelper.model.repositories.FeatureRepository
import gmail.loganchazdon.dndhelper.model.repositories.SpellRepository
import gmail.loganchazdon.dndhelper.ui.utils.allNames
import javax.inject.Inject


@HiltViewModel
class HomebrewSpellViewModel @Inject constructor(
    private val spellRepository: SpellRepository,
    private val featureRepository: FeatureRepository,
    classRepository: ClassRepository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
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
        val featureId =  featureRepository.insertFeature(spellFeature)
        featureRepository.insertFeatureSpellCrossRef(
            FeatureSpellCrossRef(
                featureId = featureId,
                spellId = spell.id
            )
        )

        featureRepository.updateIndexRef(
            IndexRef(
                index = "Spells",
                ids = listOf(featureId)
            )
        )
    }

    fun removeClass(index: Int) {
        spellRepository.removeClassSpellCrossRef(
            ClassSpellCrossRef(
                classId = classes.value!![index].id,
                spellId = id
            )
        )
    }

    fun toggleClass(it: NameAndIdPojo) {
        val ref = ClassSpellCrossRef(
            classId = it.id,
            spellId = id
        )
        if (classes.value?.firstOrNull { item -> item.id == it.id } != null) {
            spellRepository.removeClassSpellCrossRef(
                ref
            )
        } else {
            spellRepository.addClassSpellCrossRef(
                ref
            )
        }
    }

    init {
        val observer = object : Observer<Spell> {
            override fun onChanged(newValue: Spell?) {
                newValue?.let {
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
                    spell.removeObserver(this)
                }
            }
        }
        spell.observeForever(observer)
    }
}
