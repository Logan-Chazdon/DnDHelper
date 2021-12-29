package com.example.dndhelper.ui.newCharacter

import android.app.Application
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.remember
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.*
import com.example.dndhelper.repository.Repository
import com.example.dndhelper.repository.dataClasses.Class
import com.example.dndhelper.repository.dataClasses.Race
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.IndexOutOfBoundsException
import java.util.*
import javax.inject.Inject
import com.example.dndhelper.ui.newCharacter.utils.indexOf
import com.example.dndhelper.repository.dataClasses.Character
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.internal.lifecycle.HiltViewModelMap

@HiltViewModel
public class NewCharacterClassViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
): AndroidViewModel(application){
    lateinit var classes : LiveData<List<Class>>
    var id = -1
    var isBaseClass =  mutableStateOf(true)


    init {
        viewModelScope.launch {
            classes = repository.getClasses()
        }

    }

}

