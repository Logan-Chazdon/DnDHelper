package com.example.dndhelper.ui.newCharacter

import android.app.Application
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.example.dndhelper.repository.Repository
import com.example.dndhelper.repository.dataClasses.Character
import com.example.dndhelper.repository.dataClasses.Class
import com.example.dndhelper.repository.dataClasses.Race
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
public class NewCharacterViewModel @Inject constructor(
    //  savedStateHandle: SavedStateHandle,
    repository: Repository, application: Application
): AndroidViewModel(application) {
    lateinit var classes : LiveData<List<Class>>
    lateinit var races : LiveData<List<Race>>
    init {
        viewModelScope.launch {
            classes = repository.getClasses()
            races = repository.getRaces()
        }
    }




}