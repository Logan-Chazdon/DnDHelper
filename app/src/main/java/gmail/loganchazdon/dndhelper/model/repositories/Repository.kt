package gmail.loganchazdon.dndhelper.model.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.choiceEntities.*
import gmail.loganchazdon.dndhelper.model.database.DatabaseDao
import gmail.loganchazdon.dndhelper.model.junctionEntities.*
import gmail.loganchazdon.dndhelper.model.localDataSources.LocalDataSource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class Repository @Inject constructor(
    LocalDataSource: LocalDataSource,
    private val dao: DatabaseDao?
) {
    private val _classes =
        dao!!.getAllClasses()
    private val _races =
        dao!!.getAllRaces()
    private val _backgrounds =
        dao!!.getAllBackgrounds()
    private val _languages = LocalDataSource.getLanguages(
        MutableLiveData()
    )
    private val _skills =
        LocalDataSource.getAbilitiesToSkills(
            MutableLiveData()
        )
    private val _items = LocalDataSource.getItems(
        MutableLiveData()
    )
    private val _feats = LocalDataSource.getFeats(MutableLiveData())
    private val _spells = LocalDataSource.getSpells(MutableLiveData())
    private val _infusions = LocalDataSource.getInfusions(MutableLiveData())


    fun getHomebrewRaces(): LiveData<List<Race>> {
        return dao!!.getHomebrewRaces()
    }


    fun getSkillsByIndex(index: String): MutableLiveData<Map<String, List<String>>>? {
        if (index == "skill_proficiencies") {
            return _skills
        }
        return null
    }

    fun getLanguagesByIndex(index: String): MutableLiveData<List<Language>>? {
        if (index == "all_languages") {
            return _languages
        }
        return null
    }

    fun getBackgrounds(): LiveData<List<Background>> {
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

    fun getAllCharacters(): LiveData<List<Character>>? {
        return dao?.getAllCharacters()
    }

    fun insertCharacter(character: CharacterEntity) {
        dao?.insertCharacter(character)
    }

    fun deleteCharacterById(id: Int) {
        GlobalScope.launch {
            dao?.deleteCharacter(id)
        }
    }

    suspend fun getCharacterById(id: Int): Character? {
        return dao?.findCharacterById(id)
    }

    fun getLiveCharacterById(id: Int, character: MediatorLiveData<Character>) {
        dao?.findLiveCharacterById(id, character)
    }

    fun getLiveRaceById(id: Int): LiveData<Race>? {
        return dao?.findLiveRaceById(id)
    }

    //Inserts a new character into the database and returns its ID
    fun createDefaultCharacter(): Int? {
        val newCharacter = Character(name = "My Character")
        return dao?.insertCharacter(newCharacter)?.toInt()
    }

    fun createDefaultRace(): Int? {
        val newRace = RaceEntity()
        return dao?.insertRace(newRace)?.toInt()
    }

    fun createDefaultFeature(): Int? {
        val newFeature = Feature(name = "", description = "")
        return dao?.insertFeature(newFeature)?.toInt()
    }

    fun insertRace(race: RaceEntity) {
        dao?.insertRace(race)
    }

    fun getAllItems(): LiveData<List<ItemInterface>> {
        return _items
    }

    fun getSpellsByClassId(classId: Int): MutableList<Spell> {
        return dao?.getSpellsByClassId(classId) ?: mutableListOf()
    }

    fun getAllSpells(): List<Spell> {
        return _spells.value ?: listOf()
    }

    fun getLiveSpells(): LiveData<List<Spell>> {
        return _spells
    }

    fun removeRaceFeatureCrossRef(ref: RaceFeatureCrossRef) {
        dao?.removeRaceFeatureCrossRef(ref)
    }

    //Returns a list of booleans to spells
    //If the boolean is null the spell does not require preparation.
    //Else the boolean represents whether or not the spell is prepared.
    fun getSpellsForCharacter(character: Character): MutableMap<Int, MutableList<Pair<Boolean?, Spell>>> {
        val spells: MutableMap<Int, MutableList<Pair<Boolean?, Spell>>> = mutableMapOf()
        character.classes.forEach {
            addSpellsFromSpellCasting(it.value.spellCasting, listOf(it.value.name), spells)
            it.value.subclass?.spellCasting?.let { spellCasting ->
                addSpellsFromSpellCasting(spellCasting, spellCasting.learnFrom, spells)
            }
        }

        character.additionalSpells.forEach {
            spells[it.key]?.addAll(it.value)
        }

        character.classes.forEach { clazz ->
            clazz.value.pactMagic?.known?.let { known ->
                known.forEach {
                    if (spells.getOrDefault(it.level, null) == null) {
                        spells[it.level] = mutableListOf()
                    }
                    spells[it.level]?.add(Pair(null, it))
                }
            }
        }
        return spells
    }

    private fun addSpellsFromSpellCasting(
        spellCasting: SpellCasting?,
        lists: List<String>?,
        spells: MutableMap<Int, MutableList<Pair<Boolean?, Spell>>>
    ) {
        //Preparation casters
        //TODO the time complexity here is really bad. Refactor this
        /*when(spellCasting?.prepareFrom) {
            null -> {
                //Non preparation casters
                spellCasting?.known?.forEach { spell ->
                    if(spells.getOrDefault(spell.level, null) == null) {
                        spells[spell.level] = mutableListOf()
                    }
                    spells[spell.level]?.add(Pair(first = null, second = spell))
                }
            }
            "all" -> {
                //Spell casters that prepare from all of their respective class spells
                spellCasting.known.forEach { spell ->
                    if(spell.level == 0) {
                        if (spells.getOrDefault(spell.level, null) == null) {
                            spells[spell.level] = mutableListOf()
                        }
                        spells[spell.level]?.add(Pair(first = null, second = spell))
                    }
                }

                val listsToCheck = mutableListOf<String>()

                lists?.let {
                    listsToCheck.addAll(it)
                }
                spellCasting.learnFrom?.let {
                    listsToCheck.addAll(it)
                }

                listsToCheck.forEach {
                    getAllSpellsByClassIndex(
                        getClassIndex(it)
                    ).forEach { spell ->
                        spellCasting.prepared.contains(spell).let { prepared ->
                            if (spell.level != 0) {
                                if (spells.getOrDefault(spell.level, null) == null) {
                                    spells[spell.level] = mutableListOf()
                                }
                                spells[spell.level]?.add(Pair(prepared, spell))
                            }
                        }
                    }
                }
            }
            "known" -> {
                //Spell casters that prepare from known spells
                spellCasting.known.forEach { spell ->
                    spellCasting.prepared.contains(spell).let { prepared ->
                        if(spells.getOrDefault(spell.level, null) == null) {
                            spells[spell.level] = mutableListOf()
                        }
                        spells[spell.level]?.add(Pair(prepared, spell))
                    }
                }
            }
        }
        */
    }

    fun insertRaceFeatureCrossRef(ref: RaceFeatureCrossRef) {
        dao?.insertRaceFeatureCrossRef(ref)
    }

    fun insertFeature(newFeature: Feature) {
        dao?.insertFeature(newFeature)
    }

    fun getLiveFeature(id: Int): LiveData<Feature>? {
        return dao?.getLiveFeatureById(id)
    }

    fun deleteRace(id: Int) {
        dao?.deleteRace(id)
    }

    fun getAllInfusions(): LiveData<List<Infusion>> {
        return _infusions
    }

    fun insertCharacterRaceCrossRef(characterRaceCrossRef: CharacterRaceCrossRef) {
        dao?.insertCharacterRaceCrossRef(characterRaceCrossRef)
    }

    fun insertRaceChoiceEntity(raceChoiceEntity: RaceChoiceEntity) {
        dao?.insertRaceChoice(raceChoiceEntity)
    }

    fun insertFeatureChoiceChoiceEntity(featureChoiceChoiceEntity: FeatureChoiceChoiceEntity) {
        dao?.insertFeatureChoiceEntity(featureChoiceChoiceEntity)
    }

    fun getSubracesByRaceId(id: Int): LiveData<List<Subrace>> {
        val result = MediatorLiveData<List<Subrace>>()
        dao?.bindSubraceOptions(id, result)
        return result
    }

    fun insertCharacterSubraceCrossRef(characterSubraceCrossRef: CharacterSubraceCrossRef) {
        dao?.insertCharacterSubRaceCrossRef(characterSubraceCrossRef)
    }

    fun insertSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntity) {
        dao?.insertSubraceChoiceEntity(subraceChoiceEntity)
    }

    fun getClass(id: Int): LiveData<Class> {
        return dao!!.getClass(id)
    }

    fun getSubclassesByClassId(id: Int): LiveData<List<Subclass>> {
        return dao!!.getSubclassesByClassId(id)
    }

    fun removeClassFromCharacter(classId: Int, characterId: Int) {
        //TODO update me when tests ui tests are completed.
        dao?.removeCharacterClassCrossRef(
            CharacterClassCrossRef(
                characterId = characterId,
                classId = classId
            )
        )
    }

    fun insertCharacterClassCrossRef(characterId: Int, classId: Int) {
        dao?.insertCharacterClassCrossRef(
            CharacterClassCrossRef(
                characterId = characterId,
                classId = classId
            )
        )
    }

    fun insertClassChoiceEntity(classChoiceEntity: ClassChoiceEntity) {
        dao?.insertClassChoiceEntity(classChoiceEntity)
    }

    fun addFeatsToCharacterClass(characterId: Int, classId: Int, feats: List<Feat>) {
        feats.forEach {
            dao?.insertCharacterClassFeatCrossRef(
                ClassFeatCrossRef(
                    characterId = characterId,
                    featId = it.id,
                    classId = classId
                )
            )
        }
    }

    fun insertClassSubclassCrossRef(characterSubclassCrossRef: CharacterSubclassCrossRef) {
        dao?.insertCharacterSubclassCrossRef(
            characterSubclassCrossRef
        )
    }

    fun insertCharacterClassSpellCrossRef(
        classId: Int,
        spellId: Int,
        characterId: Int,
        prepared: Boolean?
    ) {
        dao?.insertCharacterClassSpellCrossRef(
            CharacterClassSpellCrossRef(
                classId = classId,
                spellId = spellId,
                characterId = characterId,
                isPrepared = prepared
            )
        )
    }

    fun insertSubclassSpellCastingSpellCrossRef(
        subclassId: Int,
        spellId: Int,
        characterId: Int,
        isPrepared: Boolean?
    ) {
        dao?.insertSubClassSpellCastingCrossRef(
            SubclassSpellCastingSpellCrossRef(
                subclassId = subclassId,
                spellId = spellId,
                characterId = characterId,
                isPrepared = isPrepared
            )
        )
    }

    fun insertCharacterClassEquipment(
        equipmentChoices: List<ItemChoice>,
        equipment: List<ItemInterface>,
        characterId: Int
    ) {
        val backpack = dao?.getCharacterBackPack(characterId)
        if (backpack?.classItems?.isEmpty() == true) {
            backpack.classItems.addAll(equipment)
            //TODO consider adding some form of override system here.
            equipmentChoices.forEach {
                backpack.classItems.addAll(it.chosen?.flatten() ?: emptyList())
            }
        }

        if (backpack != null) {
            dao?.insertCharacterBackPack(backpack, characterId)
        }
    }

    fun getBackground(id: Int): LiveData<Background> {
        val result = MediatorLiveData<Background>()
        dao?.getBackground(id, result)
        return result
    }

    fun insertCharacterBackgroundCrossRef(backgroundId: Int, characterId: Int) {
        dao?.insertCharacterBackgroundCrossRef(
            CharacterBackgroundCrossRef(
                backgroundId = backgroundId,
                characterId = characterId
            )
        )
    }

    fun insertBackgroundChoiceEntity(backgroundChoiceEntity: BackgroundChoiceEntity) {
        dao?.insertBackgroundChoiceEntity(backgroundChoiceEntity)
    }

    fun insertSubraceFeatureCrossRef(subraceFeatureCrossRef: SubraceFeatureCrossRef) {
        dao?.insertSubraceFeatureCrossRef(subraceFeatureCrossRef)
    }

    fun getSubrace(id: Int): LiveData<Subrace> {
        return dao!!.getSubrace(id)
    }

    fun removeSubraceFeatureCrossRef(subraceFeatureCrossRef: SubraceFeatureCrossRef) {
        dao!!.removeSubraceFeatureCrossRef(subraceFeatureCrossRef)
    }

    fun insertRaceSubraceCrossRef(raceSubraceCrossRef: RaceSubraceCrossRef) {
        dao?.insertRaceSubraceCrossRef(raceSubraceCrossRef)
    }

    fun createDefaultSubrace(): Int {
        return dao!!.insertSubrace(
            SubraceEntity(
                name = "Homebrew Subrace"
            )
        ).toInt()
    }

    fun insertSubrace(subraceEntity: SubraceEntity) {
        dao?.insertSubrace(subraceEntity)
    }

    fun createDefaultClass(): Int {
        return dao!!.insertClass(
            ClassEntity(
                name = "Homebrew class",
                startingGoldD4s = 4,
                startingGoldMultiplier = 10,
                subclassLevel = 1
            )
        ).toInt()
    }

    fun insertClassFeatureCrossRef(classFeatureCrossRef: ClassFeatureCrossRef) {
        dao?.insertClassFeatureCrossRef(classFeatureCrossRef)
    }

    fun insertClass(classEntity: ClassEntity) {
        dao?.insertClass(classEntity)
    }

    fun removeClassSubclassCrossRef(classSubclassCrossRef: ClassSubclassCrossRef) {
        dao?.removeClassSubclassCrossRef(classSubclassCrossRef)
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

        val sizeClasses = listOf(
            "Tiny",
            "Small",
            "Medium",
            "Large",
            "Huge",
            "Gargantuan"
        )


        val statNames = listOf(
            "Strength",
            "Dexterity",
            "Constitution",
            "Intelligence",
            "Wisdom",
            "Charisma"
        )

        val shortStatNames = statNames.onEach {
            it.subSequence(0, 2)
        }
    }
}