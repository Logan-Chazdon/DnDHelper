package ui.newCharacter

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import model.Character
import model.repositories.CharacterRepository
import org.koin.android.annotation.KoinViewModel
import ui.newCharacter.utils.indexOf
import kotlin.random.Random

@KoinViewModel
class NewCharacterStatsViewModel constructor(
    private val characterRepository: CharacterRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var character: Character? = null
    var currentStateGenTypeIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    var currentStats = MutableStateFlow(listOf<Int>())
    var selectedStatIndexes = MutableStateFlow(listOf(-1, -1, -1, -1, -1, -1))
    var currentStatsOptions = MutableStateFlow(listOf<Int>())
    var pointsRemaining = MutableStateFlow(27)
    var id = -1

    private suspend fun updateStats() {
        if (id == -1)
            id = characterRepository.createDefaultCharacter()
        character = characterRepository.getCharacterById(id)
        character!!.baseStats = generateStatMap()
        character!!.statGenerationMethodIndex = currentStateGenTypeIndex.value!!
        if (character!!.baseStats.isNotEmpty())
            characterRepository.insertCharacter(character!!)
    }

    private fun generateStatMap(): MutableMap<String, Int> {
        val statMap = mutableMapOf<String, Int>()
        val statNames = listOf("Str", "Dex", "Con", "Int", "Wis", "Cha")
        selectedStatIndexes.value?.forEachIndexed { i, it ->
            if (it != -1) {
                statMap[statNames[i]] = currentStats.value?.get(it)!!
            }
        }
        return statMap
    }


    init {
        id = try {
            savedStateHandle.get<String>("characterId")!!.toInt()
        } catch (E: Exception) {
            -1
        }
        viewModelScope.launch() {
            if (id != -1) {
                character = characterRepository.getCharacterById(id)
                character?.let {
                    if (!it.baseStats.values.contains(0) && it.baseStats.isNotEmpty()) {
                        currentStateGenTypeIndex.value = it.statGenerationMethodIndex
                        selectedStatIndexes.emit(listOf(0, 1, 2, 3, 4, 5))
                        currentStats.emit(it.baseStats.values.toList())
                    }
                }
            }


            currentStateGenTypeIndex.collect {
                val newStats = mutableListOf<Int>()

                when (it) {
                    //Point Buy
                    0 -> {
                        pointsRemaining.value = (27)
                        newStats.add(8)
                        for (i in 1..7) {
                            if (pointsRemaining.value!! >= i) {
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
                        for (i in 0..5) {
                            newStats.add(i, rollAStat())
                        }
                    }
                    //Manual
                    3 -> {
                        selectedStatIndexes.emit(mutableListOf(0, 1, 2, 3, 4, 5))
                        newStats.addAll(mutableListOf(3, 3, 3, 3, 3, 3))
                    }
                }
                currentStats.emit(newStats)
                if (currentStateGenTypeIndex.value != 3)
                    selectedStatIndexes.emit(listOf(-1, -1, -1, -1, -1, -1))
                generateCurrentStatOptions(selectedStatIndexes.value!!, newStats)
            }

            selectedStatIndexes.collect {
                currentStats.value?.let { it1 ->
                    generateCurrentStatOptions(
                        selectedStatIndexes.value!!,
                        it1
                    )
                }
            }
        }
    }

    private fun generateCurrentStatOptions(indexes: List<Int>, newStats: List<Int>) {
        if (currentStateGenTypeIndex.value == 1 || currentStateGenTypeIndex.value == 2) {
            val newStatOptions = mutableListOf<Int>()
            newStats.forEachIndexed { i, stat ->
                if (!indexes.contains(i)) {
                    newStatOptions.add(stat)
                }
            }

            currentStatsOptions.value = (newStatOptions)
        } else {
            //Point Buy
            val newStatOptions = mutableListOf<Int>()
            var points = 27

            selectedStatIndexes.value?.forEach {
                try {
                    points -= pointCost(currentStats.value?.get(it))
                } catch (e: IndexOutOfBoundsException) {
                }
            }

            newStatOptions.add(8)
            for (i in 1..7) {
                try {
                    if (points > pointCost(currentStats.value?.get(i - 1))) {
                        newStatOptions.add(8 + i)
                    }
                } catch (e: IndexOutOfBoundsException) {
                }
            }

            pointsRemaining.value = (points)
            currentStatsOptions.value = (newStatOptions)
        }
    }

    fun pointCost(score: Int?): Int {
        when (score) {
            8, 9, 10, 11, 12, 13 -> return score - 8
            14 -> return 7
            15 -> return 9
        }
        return 0
    }


    fun selectedStatByIndex(index: Int, element: Int) {
        if (currentStateGenTypeIndex.value == 1 || currentStateGenTypeIndex.value == 2) {
            val newIndexes = selectedStatIndexes.value?.toMutableList()

            val x = currentStatsOptions.value!![element]
            val num = currentStats.value!!.indexOf(x, getNumOfStatUses(x) + 1)
            newIndexes?.set(index, num!!)

            selectedStatIndexes.value = (newIndexes!!)
        } else {
            //Point buy
            val newIndexes = selectedStatIndexes.value?.toMutableList()

            val x = currentStatsOptions.value!![element]
            val num = currentStats.value!!.indexOf(x)
            newIndexes?.set(index, num)

            selectedStatIndexes.value = newIndexes!!
        }
        GlobalScope.launch {
            updateStats()
        }
    }

    private fun getNumOfStatUses(stat: Int): Int {
        var uses = 0
        selectedStatIndexes.value?.forEach { item ->
            try {
                if (currentStats.value?.get(item) ?: -1 == stat) {
                    uses++
                }
            } catch (E: IndexOutOfBoundsException) {
            }
        }
        return uses
    }

    private fun rollAStat(): Int {
        val rolls = mutableListOf<Int>()
        for (i in 0..3) {
            rolls.add(i, Random.nextInt(6) + 1)
        }

        var lowestIndex = 0
        for (i in 1..3) {
            if (rolls[i] < rolls[lowestIndex])
                lowestIndex = i
        }
        rolls.removeAt(lowestIndex)
        return rolls.sum()
    }

    fun setCurrentStatGenTypeIndex(index: Int) {
        currentStateGenTypeIndex.value = (index)
    }

    suspend fun longRest() {
        characterRepository.setHp(id, (character?.maxHp ?: 0).toString())
    }
}

