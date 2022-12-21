package gmail.loganchazdon.dndhelper.ui.newCharacter

import android.app.Application
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.Background
import gmail.loganchazdon.dndhelper.model.Character
import gmail.loganchazdon.dndhelper.model.repositories.Repository
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
public class NewCharacterBackgroundViewModel @Inject constructor(
    private val repository: Repository,
    application: Application, savedStateHandle: SavedStateHandle
): AndroidViewModel(application){
    lateinit var backgrounds : LiveData<List<Background>>
    var id = -1
    val character: MediatorLiveData<Character> = MediatorLiveData()

    init {
        viewModelScope.launch {
           backgrounds = repository.getBackgrounds()
        }
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
}

