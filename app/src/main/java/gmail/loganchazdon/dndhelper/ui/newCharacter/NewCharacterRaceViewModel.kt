package gmail.loganchazdon.dndhelper.ui.newCharacter

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.repositories.Repository
import gmail.loganchazdon.dndhelper.ui.newCharacter.stateHolders.MultipleChoiceDropdownStateFeatureImpl
import gmail.loganchazdon.dndhelper.ui.newCharacter.stateHolders.MultipleChoiceDropdownStateImpl
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.emptyList
import kotlin.collections.first
import kotlin.collections.forEach
import kotlin.collections.getOrNull
import kotlin.collections.listOf
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.plusAssign
import kotlin.collections.set
import kotlin.properties.Delegates

@HiltViewModel
public class NewCharacterRaceViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    val customizeStats = mutableStateOf(false)
    val customRaceStatsMap = mutableStateMapOf<String, String>()
    val customSubraceStatsMap = mutableStateMapOf<String, String>()
    val character: MediatorLiveData<Character> = MediatorLiveData()
    var raceFeaturesDropdownStates = mutableStateMapOf<String, MultipleChoiceDropdownStateFeatureImpl>()
    val raceProficiencyChoiceDropdownStates = mutableStateMapOf<String, MultipleChoiceDropdownStateImpl>()
    var subraceFeaturesDropdownStates = mutableStateMapOf<String, MultipleChoiceDropdownStateFeatureImpl>()
    var subraceASIDropdownState = mutableStateOf<MultipleChoiceDropdownStateImpl?>(null)
    val subraceFeatDropdownStates = mutableStateListOf<MultipleChoiceDropdownStateImpl>()
    val subraceFeatChoiceDropDownStates = mutableStateMapOf<String, MultipleChoiceDropdownStateImpl>()
    val languageDropdownStates = mutableStateMapOf<String, MultipleChoiceDropdownStateImpl>()
    lateinit var races: LiveData<List<Race>>
    var id by Delegates.notNull<Int>()
    var raceIndex = 0
    var subraceIndex = mutableStateOf(0)

    val statNames = listOf(
        "Strength",
        "Dexterity",
        "Constitution",
        "Intelligence",
        "Wisdom",
        "Charisma"
    )


    init {
        try {
            id = savedStateHandle.get<String>("characterId")!!.toInt()
            if(id !=-1) {
                repository.getLiveCharacterById(
                    savedStateHandle.get<String>("characterId")!!.toInt(),
                    character
                )
            }
        } catch (E: Exception) {
            id = -1
        }


        viewModelScope.launch {
            races = repository.getRaces()
        }
    }

    suspend fun setRace(newRace: Race) {
        if (id == -1)
            id = repository.createDefaultCharacter() ?: -1
        /*
        val character = repository.getCharacterById(id)
        newRace.abilityBonuses = getStateBonuses(
            races.value!![raceIndex].abilityBonuses!!,
            customRaceStatsMap
        )

        filterRaceFeatures(newRace).forEach { feature ->
            feature.choices?.forEachIndexed { index, it ->
                if (it.choose.num(
                        character?.totalClassLevels ?: 0
                    ) != 0 && it.options?.isEmpty() == false
                ) {
                    it.chosen =
                        raceFeaturesDropdownStates[index.toString() + feature.name + feature.grantedAtLevel]
                            ?.getSelected()
                }
            }
        }

        if (newRace.subrace?.languages.isNullOrEmpty() &&
            newRace.subrace?.languageChoices.isNullOrEmpty()
        )
            newRace.languageChoices.forEach {
                it.chosen = languageDropdownStates[it.name]
                    ?.getSelected(it.from) as List<Language>
            }

        newRace.proficiencyChoices.forEach {
            it.chosen = raceProficiencyChoiceDropdownStates[it.name]
                ?.getSelected(it.from) as List<Proficiency>
        }

        if(!newRace.subraces.isNullOrEmpty()) {
            newRace.subrace = newRace.subraces!![subraceIndex.value].run {
                this.traits.forEachIndexed { index, feature ->
                    feature.choices?.forEach {
                        if (it.choose.num(
                                character?.totalClassLevels ?: 0
                            ) != 0 && it.options?.isNullOrEmpty() == false
                        ) {
                            it.chosen =
                                subraceFeaturesDropdownStates[index.toString() + feature.name + feature.grantedAtLevel]
                                    ?.getSelected()
                        }
                    }
                }

                this.languageChoices.forEach {
                    it.chosen = languageDropdownStates[it.name]
                        ?.getSelected(it.from) as List<Language>
                }

                selectedSubraceASIs.let { result ->
                    val chosen = mutableListOf<AbilityBonus>()
                    result.forEach {
                        chosen.add(
                            AbilityBonus(
                                ability = it.first,
                                bonus= it.second
                            )
                        )
                    }
                    this.abilityBonusChoice?.chosen = chosen
                }

                featChoices?.let { choices ->
                    subraceFeatDropdownStates.forEachIndexed { index, _ ->
                        choices[index].chosen = getFeatsAt(
                            index,
                            1,
                            subraceFeatDropdownStates,
                            subraceFeatChoiceDropDownStates,
                            choices[index].from
                        )
                    }
                }

                this.abilityBonuses = this.abilityBonuses?.let {
                    getStateBonuses(
                        it,
                        customSubraceStatsMap
                    )
                }

                this
            }
        }
        *///TODO replace me
        //character!!.race = newRace
        //repository.insertCharacter(character)
    }

    private val selectedSubraceASIs : List<Pair<String, Int>>
        get() {
            return ((subraceASIDropdownState.value?.getSelected(
                races.value?.get(raceIndex)?.subraces?.get(subraceIndex.value)
                    ?.abilityBonusChoice?.from.let { from ->
                        val names = mutableListOf<Pair<String, Int>>()
                        from?.forEach { it ->
                            names.add(Pair(it.ability, it.bonus))
                        }
                        names
                    }) ?: listOf<Pair<String, Int>>()) as List<Pair<String, Int>>)
        }

    val proficiencies: List<Proficiency>
        get() {
            //TODO update me.
            val profs: MutableList<Proficiency> = mutableListOf()

            return profs
        }

    fun calculateAssumedSpells(): MutableList<Spell> {
        val result = mutableListOf<Spell>()
        character.value?.let { repository.getSpellsForCharacter(it) }?.let {
            it.forEach { (_, spells) ->
                spells.forEach { (_, spell) ->
                    result.add(spell)
                }
            }
        }
        //TODO spells from feats
        return result
    }

    private fun getStateBonuses(
        bonuses: List<AbilityBonus>,
        targetMap: MutableMap<String, String>
    ) : List<AbilityBonus> {
        return if(customizeStats.value) {
            val result = mutableListOf<AbilityBonus>()
            targetMap.forEach { stat ->
                val bonus = bonuses.first { it.ability == stat.key }.bonus
                result.add(
                        AbilityBonus(
                                ability = stat.value,
                                bonus = bonus
                        )
                )
            }
            result
        } else {
            bonuses
        }
    }

    fun calculateAssumedStatBonuses(): MutableMap<String, Int> {
        val result = mutableMapOf<String, Int>()
        val applyBonus = fun(name: String, amount: Int) {
            result[name.substring(0, 3)] =
                (result[name.substring(0, 3)] ?: 0) + amount
        }

        getStateBonuses(
            races.value!![raceIndex].abilityBonuses!!,
            customRaceStatsMap
        ).forEach {
            applyBonus(it.ability, it.bonus)
        }

        races.value!![raceIndex].subraces?.getOrNull(subraceIndex.value)?.abilityBonuses?.let { list ->
            getStateBonuses(
                list,
                customSubraceStatsMap
            ).forEach {
                applyBonus(it.ability, it.bonus)
            }
        }

        selectedSubraceASIs.forEach {
            applyBonus(it.first, it.second)
        }

        //TODO stats from feats
        return result
    }

    fun filterRaceFeatures(
        race: Race,
    ): List<Feature> {
        val tempRace = race.copy()
        tempRace.subrace = tempRace.subraces?.getOrNull(subraceIndex.value)
        return tempRace.filterRaceFeatures()
    }

    fun getStatOptions(ability: String, targetMap: MutableMap<String, String>) : MutableList<String> {
        val result = mutableListOf<String>()
        statNames.forEach {
            if(it == ability || !targetMap.values.contains(it)) {
                result += it
            }
        }
        return result
    }
}

