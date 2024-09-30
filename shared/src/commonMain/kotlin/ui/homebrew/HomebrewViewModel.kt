package ui.homebrew

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import model.repositories.*
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class HomebrewViewModel constructor(
    private val raceRepository: RaceRepository,
    private val spellRepository: SpellRepository,
    private val classRepository: ClassRepository,
    private val backgroundRepository: BackgroundRepository,
    private val featureRepository: FeatureRepository,
) : ViewModel() {
    fun deleteRace(id: Int) {
        viewModelScope.launch {
            raceRepository.deleteRace(id)
        }
    }

    fun deleteClass(id: Int) {
        viewModelScope.launch {
            classRepository.deleteClass(id)
        }
    }

    fun deleteSpell(id: Int) {
        viewModelScope.launch {
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

    fun createDefaultSubrace(): Int {
        return raceRepository.createDefaultSubrace()
    }

    fun deleteSubrace(id: Int) {
        viewModelScope.launch {
            raceRepository.deleteSubrace(id)
        }
    }

    fun deleteSubclass(subclassId: Int) {
        viewModelScope.launch {
            classRepository.deleteSubclass(subclassId)
        }
    }

    fun deleteBackground(id: Int) {
        viewModelScope.launch {
            backgroundRepository.deleteBackground(id)
        }
    }

    val races = raceRepository.getHomebrewRaces()
    val spells = spellRepository.getHomebrewSpells()
    val classes = classRepository.getHomebrewClasses()
    val subraces = raceRepository.getHomebrewSubraces()
    val subclasses = classRepository.getHomebrewSubclasses()
    val backgrounds = backgroundRepository.getHomebrewBackgrounds()
}