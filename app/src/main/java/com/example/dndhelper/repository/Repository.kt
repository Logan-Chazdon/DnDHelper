package com.example.dndhelper.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dndhelper.repository.model.DatabaseDao
import com.example.dndhelper.repository.webServices.LocalDataSource
import javax.inject.Inject
import com.example.dndhelper.repository.webServices.LocalDataSourceImpl
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.example.dndhelper.repository.dataClasses.*


class Repository @Inject constructor(
    private var LocalDataSource: LocalDataSource?,
    private val dao: DatabaseDao?,

    ) {
    private val _classes : MutableLiveData<List<Class>> = (LocalDataSource as LocalDataSourceImpl)._classes
    private val _races : MutableLiveData<List<Race>> =  (LocalDataSource as LocalDataSourceImpl)._races
    private val _backgrounds =  (LocalDataSource as LocalDataSourceImpl)._backgrounds
    private val _characters : LiveData<List<Character>>? = dao?.getAllCharacters()
    private val _languages =  (LocalDataSource as LocalDataSourceImpl)._languages
    private val _skills : MutableLiveData<Map<String, List<String>>> =
        (LocalDataSource as LocalDataSourceImpl)._abilitiesToSkills
    private val _items: MutableLiveData<List<ItemInterface>> =  (LocalDataSource as LocalDataSourceImpl)._items

    init {
 /*
        //classes
        GlobalScope.launch {
            _classes.postValue(dao?.getAllClasses())
        }
        _classes.observeForever {
            GlobalScope.launch {
                dao?.insertClasses(it)
            }
        }
        (webservice as WebserviceDnD).generateClasses(_classes)

        //races
        GlobalScope.launch {
            _races.postValue(dao?.getAllRaces())
        }
        _races.observeForever {
            GlobalScope.launch {
                dao?.insertRaces(it)
            }
        }
        (webservice as WebserviceDnD).generateRaces(_races)
*/
    }

    fun getLanguages() : LiveData<List<Language>> {
        return _languages
    }

    fun getSkillsByIndex(index: String):
            MutableLiveData<Map<String, List<String>>>? {
        if(index == "skill_proficiencies"){
            return _skills
        }
        return null
    }

    fun getLanguagesByIndex(index: String): MutableLiveData<List<Language>>? {
        if(index == "all_languages")
        {
            return _languages
        }
        return null
    }

    fun getBackgrounds() : LiveData<List<Background>> {
        return _backgrounds
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

    fun insertCharacter(character: Character) {
        dao?.insertCharacter(character)
    }

    fun deleteCharacterById(id: Int) {
        GlobalScope.launch {
            dao?.deleteCharacter(id)
        }
    }

    suspend fun getCharacterById(id: Int) : Character? {
        return dao?.findCharacterById(id)
    }

    suspend fun getLiveCharacterById(id: Int) : LiveData<Character>? {
        return dao?.findLiveCharacterById(id)
    }

    //Inserts a new character into the database and returns its ID
    fun createDefaultCharacter() : Int? {
        val newCharacter = Character(name = "My Character")
        return dao?.insertCharacter(newCharacter)?.toInt()
    }

    fun getAllItems(): LiveData<List<ItemInterface>> {
        return _items
    }


}