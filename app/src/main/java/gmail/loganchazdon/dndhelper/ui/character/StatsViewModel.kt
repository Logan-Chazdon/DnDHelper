package gmail.loganchazdon.dndhelper.ui.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.Character
import gmail.loganchazdon.dndhelper.model.repositories.CharacterRepository
import gmail.loganchazdon.dndhelper.model.repositories.ProficiencyRepository
import javax.inject.Inject

@HiltViewModel
public class StatsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    proficiencyRepository: ProficiencyRepository,
    private val characterRepository: CharacterRepository,
    application: Application
): AndroidViewModel(application) {
    var skills: LiveData<Map<String, List<String>>>? = proficiencyRepository.getSkillsByIndex("skill_proficiencies")

    var character : MediatorLiveData<Character> = MediatorLiveData()

    init {
        characterRepository.getLiveCharacterById(
            savedStateHandle.get<String>("characterId")!!.toInt(),
            character
        )
    }

    fun setName(it: String) {
        character.value!!.name = it
        characterRepository.insertCharacter(character.value!!)
    }

    fun toggleInspiration() {
        character.value!!.inspiration = !character.value!!.inspiration
        characterRepository.insertCharacter(character.value!!)
    }

    fun checkForProficienciesOrExpertise(stats: List<String>): Map<String, Int>? {
        return character.value?.checkForProficienciesOrExpertise(stats)
    }
}