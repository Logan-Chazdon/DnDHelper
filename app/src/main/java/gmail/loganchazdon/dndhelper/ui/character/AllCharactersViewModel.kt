package gmail.loganchazdon.dndhelper.ui.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.repositories.Repository
import javax.inject.Inject

@HiltViewModel
public class AllCharactersViewModel @Inject constructor(

    val repository: Repository, application: Application
): AndroidViewModel(application) {
    val allCharacters = repository.getAllCharacters()

    fun deleteCharacterById(id: Int) {
        repository.deleteCharacterById(id)
    }
}