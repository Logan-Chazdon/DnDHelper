package com.example.dndhelper.ui.newCharacter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.dndhelper.repository.Repository
import com.example.dndhelper.repository.dataClasses.Background
import com.example.dndhelper.repository.dataClasses.Class
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
public class NewCharacterBackgroundViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
): AndroidViewModel(application){
    lateinit var backgrounds : LiveData<List<Background>>
    var id = -1

    init {
        viewModelScope.launch {
           backgrounds = repository.getBackgrounds()
        }

    }

    suspend fun setBackGround(newBackground : Background) {
        if (id == -1)
            id = repository.createDefaultCharacter() ?: -1
        val character = repository.getCharacterById(id)
        character!!.background = newBackground
        repository.insertCharacter(character)
    }

}

