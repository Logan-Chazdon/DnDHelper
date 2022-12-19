package gmail.loganchazdon.dndhelper.ui.newCharacter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.Race
import gmail.loganchazdon.dndhelper.model.repositories.Repository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
public class NewCharacterRaceViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    val characterId: Int = savedStateHandle.get<String>("characterId")!!.toInt()
    lateinit var races: LiveData<List<Race>>

    init {
        viewModelScope.launch {
            races = repository.getRaces()
        }
    }
}

