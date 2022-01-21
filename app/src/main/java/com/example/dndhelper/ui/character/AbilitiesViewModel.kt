package com.example.dndhelper.ui.character

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.dndhelper.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
public class AbilitiesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val repository: Repository, application: Application
): AndroidViewModel(application) {


    init {
        viewModelScope.launch {

        }
    }


}