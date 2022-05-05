package com.example.dndhelper.ui.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.dndhelper.model.Character
import com.example.dndhelper.model.repositories.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
public class AllCharactersViewModel @Inject constructor(
  //  savedStateHandle: SavedStateHandle,
    val repository: Repository, application: Application
): AndroidViewModel(application) {
    //val userId: String = savedStateHandle["uid"] ?:
    //throw IllegalArgumentException("missing user id")



    private var _allCharacters : LiveData<List<Character>>? = null

    init {
        viewModelScope.launch {
            _allCharacters = repository.getAllCharacters()
        }
    }

    fun getAllCharacters() : LiveData<List<Character>>? {
        return _allCharacters
    }

    fun deleteCharacterById(id: Int) {
        repository.deleteCharacterById(id)
    }
}