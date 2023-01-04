package gmail.loganchazdon.dndhelper.ui.homebrew

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.Class
import gmail.loganchazdon.dndhelper.model.ClassEntity
import gmail.loganchazdon.dndhelper.model.Subclass
import gmail.loganchazdon.dndhelper.model.junctionEntities.ClassFeatureCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.ClassSubclassCrossRef
import gmail.loganchazdon.dndhelper.model.repositories.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomebrewClassViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    fun createDefaultFeature(): Int {
        val featureId = repository.createDefaultFeature()!!
        repository.insertClassFeatureCrossRef(
            ClassFeatureCrossRef(
                id = id,
                featureId = featureId
            )
        )
        return featureId
    }

    fun saveClass() {
        repository.insertClass(
            ClassEntity(
                name = name.value,
                isHomebrew = true,
                hitDie = 0, //TODO
                subclassLevel = 0, //TODO
                proficiencyChoices = emptyList(), //TODO
                proficiencies = emptyList(), //TODO
                equipmentChoices = emptyList(), //TODO
                equipment = emptyList(), //TODO
                startingGoldD4s = 0, //TODO
                startingGoldMultiplier = 0, //TODO
                spellCasting = null, //TODO
                pactMagic = null //TODO
            )
        )
    }

    fun deleteSubclass(it: Int) {
        repository.removeClassSubclassCrossRef(
            ClassSubclassCrossRef(
                classId = id,
                subclassId = subclasses!!.value!![it].subclassId
            )
        )
    }

    var subclasses : LiveData<List<Subclass>>? = null
    val name = mutableStateOf("")
    var clazz: LiveData<Class>? = null
    var id: Int = -1
    val subclassLevel = mutableStateOf("1")

    init {
        viewModelScope.launch(Dispatchers.IO) {
            savedStateHandle.get<String>("id")!!.toInt().let {
                if (it == -1) {
                    id = repository.createDefaultClass()
                }
                clazz = repository.getClass(it)
                id = it
                subclasses  = repository.getSubclassesByClassId(id)
            }
        }
    }
}