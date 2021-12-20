package com.example.dndhelper.repository

import android.app.Application
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dndhelper.repository.model.DatabaseDao
import com.example.dndhelper.repository.model.RoomDataBase
import com.example.dndhelper.repository.webServices.Webservice
import javax.inject.Inject
import com.example.dndhelper.repository.dataClasses.Character
import com.example.dndhelper.repository.dataClasses.Class
import com.example.dndhelper.repository.dataClasses.Race
import com.example.dndhelper.repository.webServices.WebserviceDnD
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.os.AsyncTask




class Repository @Inject constructor(
    private var webservice: Webservice?,
    private val dao: DatabaseDao?,

) {
    private val _classes : MutableLiveData<List<Class>> = MutableLiveData<List<Class>>()
    private val _races : MutableLiveData<List<Race>> = MutableLiveData<List<Race>>()
    init {
        //classes
        GlobalScope.launch {
            _classes.postValue(dao?.getAllClasses())
        }
        _classes.observeForever {
            GlobalScope.launch {
                dao?.insertClasses(it)
            }
        }
        (webservice as WebserviceDnD).getLocalClasses(_classes)

        //races
        GlobalScope.launch {
            _races.postValue(dao?.getAllRaces())
        }
        _races.observeForever {
            GlobalScope.launch {
                dao?.insertRaces(it)
            }
        }
        (webservice as WebserviceDnD).getLocalRaces(_races)

    }

    fun getRaces(): LiveData<List<Race>> {
        return _races
    }

    fun getClasses(): LiveData<List<Class>> {
        return _classes
    }

    fun getAllCharacters() : LiveData<List<Character>>? {
        return dao?.getAllCharacters()
    }

    suspend fun insertCharacter(character: Character) {
        GlobalScope.launch {
            dao?.insertCharacter(character)
        }
    }

    fun deleteCharacterById(id: Int) {
        GlobalScope.launch {
            dao?.deleteCharacter(id)
        }
    }

    fun getCharacterById(id: Int) : Character? {
        return dao?.findCharacterById(id)
    }


}