package ui.homebrew

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import model.Feature
import model.Subclass
import model.SubclassEntity
import model.pojos.NameAndIdPojo
import model.repositories.ClassRepository
import model.repositories.FeatureRepository
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class SubclassViewModel constructor(
    private val classRepository: ClassRepository,
    private val featureRepository: FeatureRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    fun createDefaultFeature(): Int {
        val featureId = featureRepository.createDefaultFeature()
        classRepository.insertSubclassFeatureCrossRef(
            subclassId = id,
            featureId = featureId
        )
        return featureId
    }

    fun saveSubclass() {
        val subclass = SubclassEntity(
            name = name.value,
            spellCasting = null, //TODO
            spellAreFree = false, //TODO,
            isHomebrew = true
        )
        subclass.subclassId = id
        classRepository.insertSubclass(
            subclass
        )
    }

    fun removeFeature(featureId: Int) {
        classRepository.removeSubclassFeatureCrossRef(
            featureId = featureId,
            subclassId = id
        )
    }

    fun removeClass(i: Int) {
        classRepository.removeClassSubclassCrossRef(
            subclassId = id,
            classId = classes.value!![i].id
        )
    }

    fun toggleClass(it: NameAndIdPojo) {
        if (classes.value?.firstOrNull { item -> item.id == it.id } != null) {
            classRepository.removeClassSubclassCrossRef(
                classId = it.id,
                subclassId = id
            )
        } else {
            classRepository.insertClassSubclassCrossRef(
                classId = it.id,
                subclassId = id
            )
        }
    }

    val allClasses = classRepository.getAllClassNameAndIds()
    val classes = MutableStateFlow<List<NameAndIdPojo>>(emptyList())
    val name = mutableStateOf("")
    val features: MutableStateFlow<List<Feature>> = MutableStateFlow(emptyList())
    val subclass: MutableStateFlow<Subclass?> = MutableStateFlow(null)
    var id: Int = -1

    init {
        viewModelScope.async(/*Dispatchers.IO*/) {
            savedStateHandle.get<String>("id")!!.toInt().let {
                id = if (it == -1) {
                    classRepository.createDefaultSubclass()
                } else {
                    it
                }
            }

            classRepository.getSubclass(id).firstOrNull {
                name.value = it.name
                subclass.value = it
                true
            }

            classRepository.getSubclassLiveFeaturesById(id).firstOrNull {
                features.value = it
                true
            }

            classRepository.getSubclassClasses(id).firstOrNull {
                classes.value = it
                true
            }
        }
    }
}