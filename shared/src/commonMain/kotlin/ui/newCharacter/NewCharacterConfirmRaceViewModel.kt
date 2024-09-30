package ui.newCharacter

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import model.*
import model.choiceEntities.SubraceChoiceEntity
import model.repositories.CharacterRepository
import model.repositories.CharacterRepository.Companion.statNames
import model.repositories.RaceRepository
import org.koin.android.annotation.KoinViewModel
import ui.newCharacter.stateHolders.MultipleChoiceDropdownStateFeatureImpl
import ui.newCharacter.stateHolders.MultipleChoiceDropdownStateImpl
import ui.utils.toStringList
import kotlin.properties.Delegates

@KoinViewModel
public class NewCharacterConfirmRaceViewModel constructor(
    raceRepository: RaceRepository,
    private val characterRepository: CharacterRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val subraceFeatDropdownStates = mutableStateListOf<MultipleChoiceDropdownStateImpl>()
    val subraceFeatChoiceDropDownStates = mutableStateMapOf<String, MultipleChoiceDropdownStateImpl>()
    val languageDropdownStates = mutableStateMapOf<String, MultipleChoiceDropdownStateImpl>()
    var subraceFeaturesDropdownStates = mutableStateMapOf<String, MultipleChoiceDropdownStateFeatureImpl>()
    val customSubraceStatsMap = mutableStateMapOf<String, String>()
    var raceFeaturesDropdownStates = mutableStateMapOf<String, MultipleChoiceDropdownStateFeatureImpl>()
    val raceProficiencyChoiceDropdownStates = mutableStateMapOf<String, MultipleChoiceDropdownStateImpl>()
    var subraceASIDropdownState = mutableStateOf<MultipleChoiceDropdownStateImpl?>(null)
    var subraceIndex = mutableStateOf(0)
    var id by Delegates.notNull<Int>()
    val character= MutableStateFlow(Character())
    val customizeStats = mutableStateOf(false)
    val customRaceStatsMap = mutableStateMapOf<String, String>()
    val race = raceRepository.getLiveRaceById(savedStateHandle.get<String>("raceId")!!.toInt())
    val subraces = raceRepository.getSubracesByRaceId(savedStateHandle.get<String>("raceId")!!.toInt())
    private val selectedSubraceASIs  = mutableStateOf<List<Pair<String, Int>>>(emptyList())

    init {
        try {
            id = savedStateHandle.get<String>("characterId")!!.toInt()
            if(id !=-1) {
                characterRepository.getLiveCharacterById(
                    savedStateHandle.get<String>("characterId")!!.toInt(),
                    character
                )
            }
        } catch (E: Exception) {
            id = -1
        }
        viewModelScope.launch {
            selectedSubraceASIs.value = (subraceASIDropdownState.value?.getSelected(
                subraces.last()?.get(subraceIndex.value)
                    ?.abilityBonusChoice?.from.let { from ->
                        val names = mutableListOf<Pair<String, Int>>()
                        from?.forEach { it ->
                            names.add(Pair(it.ability, it.bonus))
                        }
                        names
                    }) ?: listOf<Pair<String, Int>>()) as List<Pair<String, Int>>
        }
    }

     suspend fun setRace() {
        if (id == -1)
            id = characterRepository.createDefaultCharacter()
        val value= race.first()
        characterRepository.insertCharacterRaceCrossRef(
            raceId = value!!.raceId,
            characterId = id
        )
         val languageChoices = mutableListOf<List<String>>()
         value!!.languageChoices.forEach { languageChoice ->
             languageChoices.add((
                     languageDropdownStates[languageChoice.name]?.getSelected(languageChoice.from)
                             as List<Language>).map { it.name ?: "" } )
         }

        value!!.proficiencyChoices.forEach {
            it.chosen = raceProficiencyChoiceDropdownStates[it.name]
                ?.getSelected(it.from) as List<Proficiency>
        }

        characterRepository.insertRaceChoiceEntity(
                raceId = value!!.raceId,
                characterId = id,
                abilityBonusChoice = emptyList(),
                proficiencyChoice = value!!.proficiencyChoices.toStringList(),
                languageChoice = languageChoices,
                abilityBonusOverrides = getStateBonuses(
                    value?.abilityBonuses ?: emptyList(),
                    customRaceStatsMap
                )
        )

        storeFeatureChoices(filterRaceFeatures(value!!),raceFeaturesDropdownStates)


        subraces.firstOrNull()?.getOrNull(subraceIndex.value)?.let { subrace ->
            characterRepository.insertCharacterSubraceCrossRef(
                characterId = id,
                subraceId = subrace.id
            )

            subrace.languageChoices.forEach {
                it.chosen = languageDropdownStates[it.name]
                    ?.getSelected(it.from) as List<Language>
            }

            val languageChoices = mutableListOf<List<String>>()
            subrace.languageChoices.forEach { languageChoice ->
                languageChoices.add((
                        languageDropdownStates[languageChoice.name]?.getSelected(languageChoice.from)
                                as List<Language>).map { it.name ?: "" } )
            }
            characterRepository.insertSubraceChoiceEntity(
                SubraceChoiceEntity(
                    subraceId = subrace.id,
                    characterId = id,
                    languageChoice = languageChoices,
                    abilityBonusChoice = subraceASIDropdownState.value?.getSelected(
                        subrace.abilityBonusChoice?.from?.toStringList() ?: emptyList()
                    ) as List<String>? ?: emptyList(),
                    abilityBonusOverrides = getStateBonuses(
                        subrace.abilityBonuses ?: emptyList(),
                        customSubraceStatsMap
                    )
                )
            )
        }
         val temp = if(character.value != null) {
             character.value!!
         } else {
             characterRepository.getCharacterById(id)
         }
         characterRepository.setHp(id, temp.maxHp.toString())
    }

    private fun storeFeatureChoices(features: List<Feature>, dropdownStates: SnapshotStateMap<String, MultipleChoiceDropdownStateFeatureImpl>) {
        features.forEach { feature ->
            feature.choices?.forEachIndexed { index, it ->
                if (it.choose.num(
                        character.value?.totalClassLevels ?: 0
                    ) != 0 && it.options?.isEmpty() == false
                ) {
                    it.chosen =
                        dropdownStates[index.toString() + feature.name + feature.grantedAtLevel]
                            ?.getSelected()
                    it.chosen?.let { chosen -> storeFeatureChoices(chosen, dropdownStates) }
                    it.chosen?.forEach { chosen ->
                        characterRepository.insertFeatureChoiceChoiceEntity(
                            featureId = chosen.featureId,
                            choiceId = it.id,
                            characterId = id
                        )
                    }
                }
            }
        }
    }

    fun calculateAssumedSpells(): MutableList<Spell> {
        val result = mutableListOf<Spell>()
        character.value?.let { characterRepository.getSpellsForCharacter(it) }?.let {
            it.forEach { (_, spells) ->
                spells.forEach { (_, spell) ->
                    result.add(spell)
                }
            }
        }
        //TODO spells from feats
        return result
    }

    val proficiencies: List<Proficiency>
        get() {
            //TODO update me.
            val profs: MutableList<Proficiency> = mutableListOf()

            return profs
        }

    suspend fun calculateAssumedStatBonuses(): MutableMap<String, Int> {
        val result = mutableMapOf<String, Int>()
        val applyBonus = fun(name: String, amount: Int) {
            result[name.substring(0, 3)]    =
                (result[name.substring(0, 3)] ?: 0) + amount
        }
        val value =  race.last()
        val subracesValue =  subraces.last()
        getStateBonuses(
            value?.abilityBonuses ?: emptyList(),
            customRaceStatsMap
        ).forEach {
            applyBonus(it.ability, it.bonus)
        }

        subracesValue?.getOrNull(subraceIndex.value)?.abilityBonuses?.let { list ->
            getStateBonuses(
                list,
                customSubraceStatsMap
            ).forEach {
                applyBonus(it.ability, it.bonus)
            }
        }

        selectedSubraceASIs.value.forEach {
            applyBonus(it.first, it.second)
        }

        //TODO stats from feats
        return result
    }

    suspend fun filterRaceFeatures(
        race: Race?,
    ): List<Feature> {
        val value =  subraces.last()
        race?.subrace = value.getOrNull(subraceIndex.value)
        return race?.filterRaceFeatures() ?: listOf()
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
}