package com.example.dndhelper.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.dndhelper.repository.dataClasses.*
import com.example.dndhelper.repository.localDataSources.LocalDataSource
import com.example.dndhelper.repository.localDataSources.LocalDataSourceImpl
import com.example.dndhelper.repository.model.DatabaseDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


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
    private val _feats: MutableLiveData<List<Feat>> =  (LocalDataSource as LocalDataSourceImpl)._feats
    private val _spells: MutableLiveData<List<Spell>> =  (LocalDataSource as LocalDataSourceImpl)._spells

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


    fun getClassIndex(name: String): Int {
        _classes.value?.forEachIndexed { index, it ->
            if (it.name == name) {
                return index
            }
        }
        return -1
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

    fun getFeats(): LiveData<List<Feat>> {
        return _feats
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

    fun getAllSpellsByClassIndex(classIndex: Int): MediatorLiveData<MutableList<Spell>> {
        val result = MediatorLiveData<MutableList<Spell>>()
        result.addSource(_spells) {
            it.forEach { spell ->
                if(_classes.value?.get(classIndex)?.let {  it1 -> spell.classes.contains(it1.name.lowercase()) } == true){
                    val newList = mutableListOf<Spell>()
                    result.value?.let { oldValues -> newList.addAll(oldValues) }
                    newList.add(spell)
                    result.value = newList
                }
            }
        }
        return result
    }

    companion object {
        val allSpellLevels = listOf(
            Pair(1, "First Level"),
            Pair(2, "Second Level"),
            Pair(3, "Third Level"),
            Pair(4, "Forth Level"),
            Pair(5, "Fifth Level"),
            Pair(6, "Sixth Level"),
            Pair(7, "Seventh Level"),
            Pair(8, "Eighth Level"),
            Pair(9, "Ninth Level"),
        )
    }


}