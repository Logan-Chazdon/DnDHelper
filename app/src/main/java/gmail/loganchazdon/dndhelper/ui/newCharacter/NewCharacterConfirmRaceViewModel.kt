package gmail.loganchazdon.dndhelper.ui.newCharacter

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.choiceEntities.FeatureChoiceChoiceEntity
import gmail.loganchazdon.dndhelper.model.choiceEntities.RaceChoiceEntity
import gmail.loganchazdon.dndhelper.model.choiceEntities.SubraceChoiceEntity
import gmail.loganchazdon.dndhelper.model.junctionEntities.CharacterRaceCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.CharacterSubraceCrossRef
import gmail.loganchazdon.dndhelper.model.repositories.Repository
import gmail.loganchazdon.dndhelper.model.repositories.Repository.Companion.statNames
import gmail.loganchazdon.dndhelper.ui.newCharacter.stateHolders.MultipleChoiceDropdownStateFeatureImpl
import gmail.loganchazdon.dndhelper.ui.newCharacter.stateHolders.MultipleChoiceDropdownStateImpl
import gmail.loganchazdon.dndhelper.ui.utils.toStringList
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
public class NewCharacterConfirmRaceViewModel @Inject constructor(
    private val repository: Repository,
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
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
    val character: MediatorLiveData<Character> = MediatorLiveData()
    val customizeStats = mutableStateOf(false)
    val customRaceStatsMap = mutableStateMapOf<String, String>()
    val race = repository.getLiveRaceById(savedStateHandle.get<String>("raceId")!!.toInt())!!
    val subraces = repository.getSubracesByRaceId(savedStateHandle.get<String>("raceId")!!.toInt())

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
    }

    suspend fun setRace() {
        if (id == -1)
            id = repository.createDefaultCharacter() ?: -1

        repository.insertCharacterRaceCrossRef(
            CharacterRaceCrossRef(
                raceId = race.value!!.raceId,
                id = id
            )
        )

        race.value!!.proficiencyChoices.forEach {
            it.chosen = raceProficiencyChoiceDropdownStates[it.name]
                ?.getSelected(it.from) as List<Proficiency>
        }

        repository.insertRaceChoiceEntity(
            RaceChoiceEntity(
                raceId = race.value!!.raceId,
                characterId = id,
                abilityBonusChoice = getStateBonuses(
                    race.value?.abilityBonuses ?: emptyList(),
                    customRaceStatsMap
                ).toStringList(),
                proficiencyChoice = race.value!!.proficiencyChoices.toStringList(),
                languageChoice = race.value!!.languageChoices.toStringList()
            )
        )

        storeFeatureChoices(filterRaceFeatures(race.value!!),raceFeaturesDropdownStates)


        subraces.value?.getOrNull(subraceIndex.value)?.let { subrace ->
            repository.insertCharacterSubraceCrossRef(
                CharacterSubraceCrossRef(
                    characterId = id,
                    subraceId = subrace.id
                )
            )

            subrace.languageChoices.forEach {
                it.chosen = languageDropdownStates[it.name]
                    ?.getSelected(it.from) as List<Language>
            }

            subrace.abilityBonuses = subrace.abilityBonuses?.let {
                getStateBonuses(
                    it,
                    customSubraceStatsMap
                )
            }

            repository.insertSubraceChoiceEntity(
                SubraceChoiceEntity(
                    subraceId = subrace.id,
                    characterId = id,
                    languageChoice = subrace.languageChoices.toStringList(),
                    abilityBonusChoice = subrace.abilityBonuses?.toStringList() ?: emptyList()
                )
            )
        }
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
                        repository.insertFeatureChoiceChoiceEntity(
                            FeatureChoiceChoiceEntity(
                                featureId = chosen.featureId,
                                choiceId = it.id,
                                characterId = id
                            )
                        )
                    }
                }
            }
        }
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

    val proficiencies: List<Proficiency>
        get() {
            //TODO update me.
            val profs: MutableList<Proficiency> = mutableListOf()

            return profs
        }

    fun calculateAssumedStatBonuses(): MutableMap<String, Int> {
        val result = mutableMapOf<String, Int>()
        val applyBonus = fun(name: String, amount: Int) {
            result[name.substring(0, 3)]    =
                (result[name.substring(0, 3)] ?: 0) + amount
        }

        getStateBonuses(
            race.value?.abilityBonuses ?: emptyList(),
            customRaceStatsMap
        ).forEach {
            applyBonus(it.ability, it.bonus)
        }

        subraces.value?.getOrNull(subraceIndex.value)?.abilityBonuses?.let { list ->
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
        race: Race?,
    ): List<Feature> {
        //TODO check this
        race?.subrace = subraces.value?.getOrNull(subraceIndex.value)
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

    private val selectedSubraceASIs : List<Pair<String, Int>>
        get() {
            return ((subraceASIDropdownState.value?.getSelected(
                subraces.value?.get(subraceIndex.value)
                    ?.abilityBonusChoice?.from.let { from ->
                        val names = mutableListOf<Pair<String, Int>>()
                        from?.forEach { it ->
                            names.add(Pair(it.ability, it.bonus))
                        }
                        names
                    }) ?: listOf<Pair<String, Int>>()) as List<Pair<String, Int>>)
        }

}