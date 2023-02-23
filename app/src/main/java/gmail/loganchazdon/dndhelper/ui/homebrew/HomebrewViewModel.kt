package gmail.loganchazdon.dndhelper.ui.homebrew

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.repositories.ClassRepository
import gmail.loganchazdon.dndhelper.model.repositories.RaceRepository
import gmail.loganchazdon.dndhelper.model.repositories.SpellRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomebrewViewModel @Inject constructor(
    private val raceRepository: RaceRepository,
    private val spellRepository: SpellRepository,
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

    fun createDefaultSpell(): Int {
        return spellRepository.createDefaultSpell()
    }

    val races = raceRepository.getHomebrewRaces()
    val classes = classRepository.getHomebrewClasses()
}