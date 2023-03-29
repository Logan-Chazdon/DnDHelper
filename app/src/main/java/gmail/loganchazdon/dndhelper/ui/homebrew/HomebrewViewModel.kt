package gmail.loganchazdon.dndhelper.ui.homebrew

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.repositories.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomebrewViewModel @Inject constructor(
    private val raceRepository: RaceRepository,
    private val spellRepository: SpellRepository,
    private val classRepository: ClassRepository,
    private val backgroundRepository: BackgroundRepository,
    private val featureRepository: FeatureRepository,
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

    fun deleteSpell(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            spellRepository.deleteSpell(id)
            featureRepository.removeIdFromRef(id, "Spells")
        }
    }

    fun createDefaultSpell(): Int {
        return spellRepository.createDefaultSpell()
    }

    fun createDefaultBackground(): Int {
        return backgroundRepository.createDefaultBackground()
    }

    fun createDefaultSubclass(): Int {
        return classRepository.createDefaultSubclass()
    }

    fun createDefaultSubrace(): Any {
        return raceRepository.createDefaultSubrace()
    }

    val races = raceRepository.getHomebrewRaces()
    val spells = spellRepository.getHomebrewSpells()
    val classes = classRepository.getHomebrewClasses()
    val subraces = raceRepository.getHomebrewSubraces()
    val subclasses = classRepository.getHomebrewSubclasses()
}