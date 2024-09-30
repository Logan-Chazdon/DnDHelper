package ui.character

import androidx.lifecycle.ViewModel
import model.repositories.CharacterRepository
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
public class AllCharactersViewModel constructor(
    val repository: CharacterRepository
): ViewModel() {
    val allCharacters = repository.getAllCharacters()

    fun deleteCharacterById(id: Int) {
        repository.deleteCharacterById(id)
    }
}