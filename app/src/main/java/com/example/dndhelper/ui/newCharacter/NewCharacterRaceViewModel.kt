package com.example.dndhelper.ui.newCharacter

import android.app.Application
import androidx.lifecycle.*
import com.example.dndhelper.repository.Repository
import com.example.dndhelper.repository.dataClasses.Character
import com.example.dndhelper.repository.dataClasses.Class
import com.example.dndhelper.repository.dataClasses.Race
import com.example.dndhelper.ui.newCharacter.utils.indexOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.IndexOutOfBoundsException
import java.util.*
import javax.inject.Inject

@HiltViewModel
public class NewCharacterRaceViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
):  AndroidViewModel(application) {

    lateinit var races : LiveData<List<Race>>
    var id = -1
    init {

        viewModelScope.launch {
            races = repository.getRaces()
        }
    }

     suspend fun setRace(newRace: Race) {
            if (id == -1)
                id = repository.createDefaultCharacter() ?: -1
            val character = repository.getCharacterById(id)
            character!!.race = newRace
            repository.insertCharacter(character)
    }


}

