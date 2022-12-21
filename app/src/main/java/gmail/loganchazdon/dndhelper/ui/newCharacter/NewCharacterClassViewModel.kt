package gmail.loganchazdon.dndhelper.ui.newCharacter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.Character
import gmail.loganchazdon.dndhelper.model.Class
import gmail.loganchazdon.dndhelper.model.repositories.Repository
import javax.inject.Inject

@HiltViewModel
class NewCharacterClassViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    var classes: LiveData<List<Class>> = repository.getClasses()
    var id = -1
    val character: MediatorLiveData<Character> = MediatorLiveData()



    init {
        id = try {
            savedStateHandle.get<String>("characterId")!!.toInt()
        } catch (e: Exception) {
            -1
        }

        if(id !=-1) {
            repository.getLiveCharacterById(
                savedStateHandle.get<String>("characterId")!!.toInt(),
                character
            )
        }
    }

    fun removeClass(classId: Int) {
        repository.removeClassFromCharacter(classId = classId, characterId = id)
    }
}



