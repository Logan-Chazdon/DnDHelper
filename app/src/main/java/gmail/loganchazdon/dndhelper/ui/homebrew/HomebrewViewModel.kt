package gmail.loganchazdon.dndhelper.ui.homebrew

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.repositories.ClassRepository
import gmail.loganchazdon.dndhelper.model.repositories.RaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomebrewViewModel @Inject constructor(
    private val raceRepository: RaceRepository,
    private val classRepository: ClassRepository,
    application: Application
) : AndroidViewModel(application) {
    fun deleteRace(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            raceRepository.deleteRace(id)
        }
    }

    fun deleteClass(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            classRepository.deleteClass(id)
        }
    }

    val races = raceRepository.getHomebrewRaces()
    val classes = classRepository.getHomebrewClasses()
}