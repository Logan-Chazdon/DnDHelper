package ui.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import model.repositories.CharacterRepository
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
public class AllCharactersViewModel constructor(
    val repository: CharacterRepository,
    application: Application
): AndroidViewModel(application) {
    val allCharacters = repository.getAllCharacters()

    fun deleteCharacterById(id: Int) {
        repository.deleteCharacterById(id)
    }
}