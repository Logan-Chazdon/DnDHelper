package ui.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle

import model.Character
import model.repositories.CharacterRepository
import model.repositories.ProficiencyRepository
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
public class StatsViewModel constructor(
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