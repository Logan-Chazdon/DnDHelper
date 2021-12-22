package com.example.dndhelper.ui.newCharacter
import android.app.Application
import androidx.compose.runtime.mutableStateMapOf
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
import kotlinx.coroutines.GlobalScope

@HiltViewModel
public class NewCharacterStatsViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
): AndroidViewModel(application)

{
    var currentStateGenTypeIndex : MutableLiveData<Int> = MutableLiveData(0)
    var currentStats = MutableLiveData(listOf<Int>())
    var selectedStatIndexes = MutableLiveData(listOf<Int>(-1, -1, -1, -1, -1, -1))
    var currentStatsOptions = MutableLiveData(listOf<Int>())
    var pointsRemaining = MutableLiveData(27)
    var id = -1

    private suspend fun updateStats() {
        if(id == -1)
            id = repository.createDefaultCharacter()!!
        val character = repository.getCharacterById(id)
        character!!.baseStats = generateStatMap()
        if(character.baseStats.isNotEmpty())
            repository.insertCharacter(character)
    }

    private fun generateStatMap() : MutableMap<String, Int> {
        val statMap = mutableMapOf<String, Int>()
        val statNames =  listOf("Str", "Dex", "Con", "Int", "Wis", "Cha")
        selectedStatIndexes.value?.forEachIndexed { i, it ->
            if(it != -1) {
                statMap[statNames[i]] = currentStats.value?.get(it)!!
            }
        }
        return statMap
    }


    init {

        currentStateGenTypeIndex.observeForever {
            val newStats = mutableListOf<Int>()

            when(it) {
                //Point Buy
                0 -> {
                    pointsRemaining.postValue(27)
                    newStats.add(8)
                    for(i in 1..7) {
                        if(pointsRemaining.value!! >= i) {
                            newStats.add(8 + i)
                        }
                    }
                }
                //Standard Array
                1 -> {
                    newStats.addAll(listOf(15, 14, 13, 12, 10, 8))
                }
                //Rolled
                2 -> {
                    for(i in 0..5) {
                        newStats.add(i, rollAStat())
                    }
                }
            }
            currentStats.postValue(newStats)
            selectedStatIndexes.postValue(listOf(-1, -1, -1, -1, -1, -1))
            generateCurrentStatOptions(selectedStatIndexes.value!!, newStats)
        }

        selectedStatIndexes.observeForever {
            currentStats.value?.let { it1 ->
                generateCurrentStatOptions(selectedStatIndexes.value!!,
                    it1
                )
            }
            GlobalScope.launch {
                updateStats()
            }
        }

    }

    private fun generateCurrentStatOptions(indexes: List<Int>, newStats: List<Int>) {
        if(currentStateGenTypeIndex.value == 1 ||  currentStateGenTypeIndex.value == 2) {
            val newStatOptions = mutableListOf<Int>()
            newStats.forEachIndexed { i, stat ->
                if(!indexes.contains(i)) {
                    newStatOptions.add(stat)
                }
            }

            currentStatsOptions.postValue(newStatOptions)
        } else {
            //Point Buy
            val newStatOptions = mutableListOf<Int>()
            var points = 27

            selectedStatIndexes.value?.forEach {
                try {
                    points -= pointCost(currentStats.value?.get(it))
                } catch (e: IndexOutOfBoundsException) {}
            }

            newStatOptions.add(8)
            for(i in 1..7) {
                try {
                    if (points > pointCost(currentStats.value?.get(i - 1))) {
                        newStatOptions.add(8 + i)
                    }
                } catch (e: IndexOutOfBoundsException) {}
            }

            pointsRemaining.postValue(points)
            currentStatsOptions.postValue(newStatOptions)
        }
    }

    fun pointCost(score: Int?) : Int {
        when(score){
            8, 9, 10, 11, 12, 13 -> return score - 8
            14 -> return 7
            15 -> return 9
        }
        return 0
    }


    fun selectedStatByIndex(index: Int, element: Int) {
        if(currentStateGenTypeIndex.value == 1 || currentStateGenTypeIndex.value == 2){
            val newIndexes = selectedStatIndexes.value?.toMutableList()

            val x = currentStatsOptions.value!![element]
            val num = currentStats.value!!.indexOf(x, getNumOfStatUses(x)+1)
            newIndexes?.set(index, num!!)

            selectedStatIndexes.postValue(newIndexes)
        } else {
            //Point buy
            val newIndexes = selectedStatIndexes.value?.toMutableList()

            val x = currentStatsOptions.value!![element]
            val num = currentStats.value!!.indexOf(x)
            newIndexes?.set(index, num)

            selectedStatIndexes.postValue(newIndexes)
        }
    }

    private fun getNumOfStatUses(stat: Int): Int {
        var uses = 0
        selectedStatIndexes.value?.forEach { item ->
            try {
                if(currentStats.value?.get(item) ?: -1 == stat) {
                    uses++
                }
            } catch (E : IndexOutOfBoundsException) {}
        }
        return uses
    }

    private fun rollAStat() : Int {
        val random = Random()
        val rolls = mutableListOf<Int>()
        for(i in 0..3)
        {
            rolls.add(i, random.nextInt(6))
        }

        var lowestIndex = 0
        for(i in 1..3) {
            if(rolls[i] < rolls[lowestIndex])
                lowestIndex = i
        }
        rolls.removeAt(lowestIndex)
        return rolls.sum()
    }

    fun setCurrentStatGenTypeIndex(index: Int) {
        currentStateGenTypeIndex.postValue(index)
    }
}

