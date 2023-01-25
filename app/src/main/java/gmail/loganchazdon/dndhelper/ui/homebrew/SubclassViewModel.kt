package gmail.loganchazdon.dndhelper.ui.homebrew

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.Subclass
import gmail.loganchazdon.dndhelper.model.SubclassEntity
import gmail.loganchazdon.dndhelper.model.junctionEntities.SubclassFeatureCrossRef
import gmail.loganchazdon.dndhelper.model.repositories.ClassRepository
import gmail.loganchazdon.dndhelper.model.repositories.FeatureRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SubclassViewModel @Inject constructor(
    private val classRepository: ClassRepository,
    private val featureRepository: FeatureRepository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    fun createDefaultFeature(): Int {
        val featureId = featureRepository.createDefaultFeature()
        classRepository.insertSubclassFeatureCrossRef(
            SubclassFeatureCrossRef(
                subclassId = id,
                featureId = featureId
            )
        )
        return featureId
    }

    fun saveSubclass() {
        val subclass = SubclassEntity(
            name = name.value,
            spellCasting = null, //TODO
            spellAreFree = false //TODO,
        )
        subclass.subclassId = id
        classRepository.insertSubclass(
            subclass
        )
    }

    fun removeFeature(featureId: Int) {
        classRepository.removeSubclassFeatureCrossRef(
            SubclassFeatureCrossRef(
                featureId = featureId,
                subclassId = id
            )
        )
    }

    val name = mutableStateOf("")
    var subclass: LiveData<Subclass>? = null
    var id: Int = -1

    init {
        viewModelScope.launch(Dispatchers.IO) {
            savedStateHandle.get<String>("id")!!.toInt().let {
                if (it == -1) {
                    id = classRepository.createDefaultSubclass()
                }
                subclass = classRepository.getSubclass(id)
                id = it
            }
        }
    }
}