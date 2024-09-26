package ui.newCharacter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import model.Character
import model.Class
import model.repositories.CharacterRepository
import model.repositories.ClassRepository
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
class NewCharacterClassViewModel constructor(
    private val characterRepository: CharacterRepository,
    classRepository: ClassRepository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    var classes: LiveData<List<Class>> = classRepository.getClasses()
    var id = -1
    val character: MediatorLiveData<Character> = MediatorLiveData()


    init {
        id = try {
            savedStateHandle.get<String>("characterId")!!.toInt()
        } catch (e: Exception) {
            -1
        }

        if (id != -1) {
            characterRepository.getLiveCharacterById(
                id,
                character
            )
        }
    }

    fun removeClass(classId: Int) {
        characterRepository.removeClassFromCharacter(classId = classId, characterId = id)
    }
}



