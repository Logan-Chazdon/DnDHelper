package ui.character

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import model.repositories.CharacterRepository
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
/**
 * @param currentId this is used to pass the character to edit when navigating to the new character views */
public class AllCharactersViewModel constructor(
    val repository: CharacterRepository,
    val currentId: MutableStateFlow<Int>
): ViewModel() {
    val allCharacters = repository.getAllCharacters()

    fun deleteCharacterById(id: Int) {
        repository.deleteCharacterById(id)
    }
}