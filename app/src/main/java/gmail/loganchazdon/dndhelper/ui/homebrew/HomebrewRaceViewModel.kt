package gmail.loganchazdon.dndhelper.ui.homebrew

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.repositories.Repository
import javax.inject.Inject

@HiltViewModel
class HomebrewRaceViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    val newRaceName = mutableStateOf("")
    val id = savedStateHandle.let {
        try {
            it.get<Int>("id")
        } catch(_ : Exception) {
            repository.createDefaultRace()
        }!!
    }
    val race = repository.getLiveRaceById(id)
}