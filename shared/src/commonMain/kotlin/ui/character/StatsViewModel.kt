package ui.character

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import model.Character
import model.repositories.CharacterRepository
import model.repositories.ProficiencyRepository
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
public class StatsViewModel constructor(
    savedStateHandle: SavedStateHandle,
    proficiencyRepository: ProficiencyRepository,
    private val characterRepository: CharacterRepository,
): ViewModel() {
    var skills = proficiencyRepository.getSkillsByIndex("skill_proficiencies")

    var character : MutableStateFlow<Character> = MutableStateFlow(Character())

    init {
        characterRepository.getLiveCharacterById(
            savedStateHandle.get<String>("characterId")!!.toInt(),
            character
        )
    }

    suspend fun setName(it: String) {
        character.value!!.name = it
        characterRepository.insertCharacter(character.value!!)
    }

    suspend fun toggleInspiration() {
        character.value!!.inspiration = !character.value!!.inspiration
        characterRepository.insertCharacter(character.value!!)
    }

    fun checkForProficienciesOrExpertise(stats: List<String>): Map<String, Int>? {
        return character.value?.checkForProficienciesOrExpertise(stats)
    }
}