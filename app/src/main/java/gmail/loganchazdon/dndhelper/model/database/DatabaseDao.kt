package gmail.loganchazdon.dndhelper.model.database
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.room.*
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.choiceEntities.*
import gmail.loganchazdon.dndhelper.model.junctionEntities.*

private const val fullCharacterSql =
    """WITH subrace AS (SELECT id AS subraceid, name AS subracename, abilityBonuses AS subraceabilityBonuses, abilityBonusChoice AS subraceabilityBonusChoice, 
startingProficiencies AS subracestartingProficiencies, languages AS subracelanguages, languageChoices AS subracelanguageChoices, size AS subracesize, 
groundSpeed AS subracegroundSpeed FROM subraces),

background AS (SELECT id AS backgroundid, name AS backgroundname, [desc] AS backgrounddesc, spells AS backgroundspells, proficiencies AS backgroundproficiencies, 
languages AS backgroundlanguages, equipment AS backgroundequipment, equipmentChoices AS backgroundequipmentChoices
FROM backgrounds)

SELECT * FROM characters 
INNER JOIN CharacterRaceCrossRef ON characters.id IS CharacterRaceCrossRef.id
INNER JOIN races ON CharacterRaceCrossRef.raceId IS races.raceId
INNER JOIN CharacterSubraceCrossRef ON CharacterSubraceCrossRef.characterId IS characters.id
INNER JOIN subrace ON subrace.subraceid IS CharacterSubraceCrossRef.subraceId
INNER JOIN CharacterBackgroundCrossRef ON CharacterBackgroundCrossRef.characterId IS characters.id
INNER JOIN background ON background.backgroundid IS CharacterBackgroundCrossRef.backgroundId
WHERE characters.id = :id"""

@Dao
abstract class DatabaseDao {
    //Character Table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCharacter(character: CharacterEntity) : Long

    //Fill out choices which require lists. Cant be done in sql due to lack of support.
    //Does not need to set non list choices.
    private fun fillOutCharacterChoiceLists(character: Character) {
        //Fill out race choices
        character.race?.let { race ->
            getRaceChoiceData(raceId = race.raceId, charId = character.id).let { data ->
                race.proficiencyChoices.forEachIndexed { index, choice ->
                    choice.chosenByString = data.proficiencyChoice.getOrNull(index) ?: emptyList()
                }

                race.languageChoices.forEachIndexed { index, choice ->
                    choice.chosenByString = data.languageChoice.getOrNull(index) ?: emptyList()
                }

                val features = getRaceFeatures(race.raceId)
                fillOutFeatureList(features, character.id)
                race.traits = features
            }
        }

        character.race?.subrace?.let { subrace ->
            getSubraceChoiceData(subraceId = subrace.id, charId = character.id).let { data ->
                subrace.languageChoices.forEachIndexed { index, choice ->
                    choice.chosenByString = data.languageChoice.getOrNull(index) ?: emptyList()
                }

                val features = getSubraceFeatures(subrace.id)
                fillOutFeatureList(features, character.id)
                subrace.traits = features

                val featChoiceEntities = getSubraceFeatChoices(subrace.id)
                val featChoices = mutableListOf<FeatChoice>()
                featChoiceEntities.forEach {
                    featChoices.add(
                        it.toFeatChoice(getFeatChoiceChosen(characterId = character.id, choiceId = it.id))
                    )
                }
                subrace.featChoices =featChoices
            }
        }

        character.background?.let { background ->
            getBackgroundChoiceData(charId= character.id).let { data ->
                val features = getBackgroundFeatures(background.id)
                fillOutFeatureList(features, character.id)
                background.features = features

                background.spells = getBackgroundSpells(backgroundId = background.id)
            }
        }

        val classes = getCharactersClasses(character.id)
        classes.forEach { (_, clazz) ->
            val data = getClassChoiceData(characterId = character.id, classId = clazz.id)
            clazz.level = data.level
            clazz.abilityImprovementsGranted = data.abilityImprovementsGranted
            clazz.totalNumOnGoldDie = data.totalNumOnGoldDie
            clazz.tookGold =  data.tookGold
            clazz.isBaseClass = data.isBaseClass
            clazz.spellCasting?.known =
                getSpellCastingSpellsForClass(characterId = character.id, classId = clazz.id).toList()

            val features = getClassFeatures(classId = clazz.id, maxLevel = clazz.level)
            fillOutFeatureList(features, character.id)
            clazz.levelPath = features
            clazz.featsGranted = getClassFeats(classId = clazz.id, characterId = character.id)
            clazz.featsGranted?.forEach {
                it.features?.let { it1 -> fillOutFeatureList(it1, character.id) }
            }

            clazz.subclass?.let { subclass ->
                subclass.spellCasting?.known =
                    getSpellCastingSpellsForSubclass(characterId = character.id, subclassId = subclass.subclassId).toList()

                val subClassFeatures = getClassFeatures(classId = clazz.id, maxLevel = clazz.level)
                fillOutFeatureList(subClassFeatures, character.id)
                subclass.features = subClassFeatures
            }

            if(data.isBaseClass) {
                clazz.proficiencyChoices.forEachIndexed { i, it ->
                    it.chosenByString = data.proficiencyChoices[i]
                }
            }
        }

        character.classes = classes
    }


    @MapInfo(valueColumn = "isPrepared")
    @Query("""SELECT * FROM spells
JOIN SubclassSpellCastingSpellCrossRef ON SubclassSpellCastingSpellCrossRef.spellId IS spells.id
WHERE characterId IS :characterId AND subclassId IS :subclassId
""")
    abstract fun getSpellCastingSpellsForSubclass(characterId: Int, subclassId: Int) : Map<Spell, Boolean?>


    @MapInfo(valueColumn = "isPrepared")
    @Query("""SELECT * FROM spells
JOIN CharacterClassSpellCrossRef ON spells.id IS CharacterClassSpellCrossRef.spellId
WHERE characterId IS :characterId AND classId IS :classId
""")
    abstract fun getSpellCastingSpellsForClass(characterId: Int, classId: Int) : Map<Spell, Boolean?>

    @Insert
    abstract fun insertCharacterClassSpellCrossRef(ref: CharacterClassSpellCrossRef)

    @Query("""SELECT * FROM spells 
JOIN BackgroundSpellCrossRef ON BackgroundSpellCrossRef.spellId IS spells.id
WHERE backgroundId IS :backgroundId
    """)
    abstract fun getBackgroundSpells(backgroundId: Int): List<Spell>?


    @Query("""SELECT * FROM feats
JOIN ClassFeatCrossRef ON ClassFeatCrossRef.featId IS feats.id
WHERE ClassFeatCrossRef.classId IS :classId AND ClassFeatCrossRef.characterId IS :characterId
    """)
    abstract fun getClassFeats(classId: Int, characterId: Int): MutableList<Feat>

    @Query("""SELECT * FROM features
JOIN ClassFeatureCrossRef ON ClassFeatureCrossRef.featureId IS features.featureId
WHERE ClassFeatureCrossRef.id IS :classId AND features.grantedAtLevel <= :maxLevel
    """)
    abstract fun getClassFeatures(classId: Int, maxLevel: Int = 20): MutableList<Feature>

    @Query("""SELECT * FROM ClassChoiceEntity
WHERE characterId IS :characterId AND classId IS :classId
    """)
    abstract fun getClassChoiceData(characterId: Int, classId: Int): ClassChoiceEntity

    @Query("""SELECT * FROM feats
JOIN FeatChoiceChoiceEntity ON FeatChoiceChoiceEntity.featId IS feats.id
WHERE choiceId IS :choiceId AND characterId IS :characterId
    """)
    abstract fun getFeatChoiceChosen(characterId: Int, choiceId: Int): List<Feat>

    @Query("""SELECT * FROM featChoices
JOIN SubraceFeatChoiceCrossRef ON SubraceFeatChoiceCrossRef.featChoiceId IS featChoices.id
WHERE SubraceFeatChoiceCrossRef.subraceId IS :id
    """)
    abstract fun getSubraceFeatChoices(id: Int): List<FeatChoiceEntity>


    private fun fillOutFeatureList(features: List<Feature>, characterId: Int) {
        features.forEach { feature ->
            feature.choices = fillOutChoices(getFeatureChoices(feature.featureId), characterId = characterId)
            feature.spells = getFeatureSpells(feature.featureId)
        }
    }

    @Query("""SELECT * FROM spells
JOIN FeatureSpellCrossRef ON FeatureSpellCrossRef.spellId IS spells.id
WHERE featureId IS :featureId
""")
    abstract fun getFeatureSpells(featureId: Int): List<Spell>?

    //This only fills out chosen not options.
    //We don't want options as it is not used inside of the character object.
    private fun fillOutChoices(choiceEntities: List<FeatureChoiceEntity>, characterId: Int) : List<FeatureChoice> {
        val choices = mutableListOf<FeatureChoice>()
        choiceEntities.forEach { featureChoiceEntity ->
            val features = getFeatureChoiceChosen(choiceId = featureChoiceEntity.id, characterId = characterId)
            fillOutFeatureList(features, characterId)
            choices.add(
                FeatureChoice(
                    entity = featureChoiceEntity,
                    options = emptyList(),
                    chosen = features
                )
            )
        }
        return choices
    }

    @RewriteQueriesToDropUnusedColumns
    @Query(fullCharacterSql)
    @Transaction
    protected abstract fun findCharacterWithoutListChoices(id: Int) : Character

    @Query(fullCharacterSql)
    @RewriteQueriesToDropUnusedColumns
    @Transaction
    protected abstract fun findLiveCharacterWithoutListChoices(id: Int) : LiveData<Character>


    @Query("SELECT * FROM RaceChoiceEntity WHERE raceId = :raceId AND characterId = :charId")
    protected abstract fun getRaceChoiceData(raceId: Int, charId: Int): RaceChoiceEntity

    @Query("SELECT * FROM SubraceChoiceEntity WHERE subraceId = :subraceId AND characterId = :charId")
    abstract fun getSubraceChoiceData(subraceId: Int, charId: Int): SubraceChoiceEntity

    suspend fun findCharacterById(id: Int): Character {
        val character = findCharacterWithoutListChoices(id)
        fillOutCharacterChoiceLists(character)
        return character
    }

    fun findLiveCharacterById(id: Int, character : MediatorLiveData<Character> ) {
        val characterLiveData  =findLiveCharacterWithoutListChoices(id)
        character.addSource(characterLiveData) {
            if(it != null) {
                fillOutCharacterChoiceLists(it)
                character.value = it
            }
        }
    }

    @Query("DELETE FROM characters WHERE id = :id")
    abstract fun deleteCharacter(id: Int)

    @Transaction
    @Query("SELECT * FROM characters " +
            "INNER JOIN CharacterRaceCrossRef ON characters.id IS CharacterRaceCrossRef.id\n" +
            "INNER JOIN races ON CharacterRaceCrossRef.raceId IS races.raceId")
    abstract fun getAllCharacters(): LiveData<List<Character>>

    @Insert
    abstract fun insertCharacterRaceCrossRef(ref: CharacterRaceCrossRef)

    @Delete
    abstract fun removeCharacterRaceCrossRef(ref: CharacterRaceCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRaceChoice(choice: RaceChoiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeatureChoiceEntity(choice : FeatureChoiceChoiceEntity) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCharacterSubRaceCrossRef(characterSubraceCrossRef: CharacterSubraceCrossRef)

    //Class Table
    @Query("SELECT * FROM classes")
    protected abstract fun getAllClassEntities(): LiveData<List<ClassEntity>>

    fun getAllClasses() : LiveData<List<Class>> {
        val classes = MediatorLiveData<List<Class>>()
        classes.addSource(getAllClassEntities()) {
            val temp = mutableListOf<Class>()
            it.forEachIndexed { index, classEntity ->
                temp.add(index, classEntity.toClass(
                    levelPath = getLevelPath(classEntity.id)
                ))
            }
            classes.value = temp
        }
        return classes
    }

    private fun getLevelPath(id: Int): MutableList<Feature> {
        val features = getUnfilledLevelPath(id)
        fillOutFeatureListWithoutChosen(getUnfilledLevelPath(id))
        return features
    }

    private fun fillOutFeatureListWithoutChosen(features : List<Feature>) {
        features.forEach { feature ->
            feature.spells = getFeatureSpells(feature.featureId)
            feature.choices = getFeatureChoices(feature.featureId).let { choiceEntities ->
                val temp = mutableListOf<FeatureChoice>()
                choiceEntities.forEach { choice ->
                    val filledChoice =FeatureChoice(
                        entity = choice,
                        options = getFeatureChoiceOptions(choice.id),
                        chosen = null
                    )
                    filledChoice.options?.let { fillOutFeatureListWithoutChosen(it) }
                    temp.add(
                        filledChoice
                    )
                }
                temp
            }
        }
    }

    @Query("""SELECT * FROM features
JOIN ClassFeatureCrossRef ON ClassFeatureCrossRef.featureId IS features.featureId
WHERE ClassFeatureCrossRef.id IS :id""")
    protected abstract fun getUnfilledLevelPath(id: Int) : MutableList<Feature>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertClass(newClass: ClassEntity) : Long

    @Insert
    abstract fun insertSubclass(subClass: SubclassEntity) : Long

    @Insert
    abstract fun insertCharacterClassCrossRef(ref: CharacterClassCrossRef)

    @Delete
    abstract fun removeCharacterClassCrossRef(ref: CharacterClassCrossRef)

    @Insert
    abstract fun insertClassChoiceEntity(entity: ClassChoiceEntity)

    @Query("DELETE FROM ClassChoiceEntity WHERE classId IS :classId AND characterId IS :characterId")
    abstract fun removeClassChoiceEntity(classId: Int, characterId: Int)

    @Insert
    abstract fun insertClassFeatureCrossRef(ref: ClassFeatureCrossRef)

    @Delete
    abstract fun removeClassFeatureCrossRef(ref: ClassFeatureCrossRef)


    @MapInfo(keyColumn = "name")
    @Query("""SELECT * FROM classes
JOIN CharacterClassCrossRef ON CharacterClassCrossRef.classId IS classes.id
JOIN ClassChoiceEntity ON ClassChoiceEntity.classId IS CharacterClassCrossRef.classId AND ClassChoiceEntity.characterId IS CharacterClassCrossRef.characterId
JOIN CharacterSubclassCrossRef ON CharacterSubclassCrossRef.characterId IS CharacterClassCrossRef.characterId AND CharacterSubclassCrossRef.classId IS classes.id
JOIN subclasses ON subclasses.subclassId IS CharacterSubclassCrossRef.subClassId
WHERE CharacterClassCrossRef.characterId IS :characterId
    """)
    protected abstract fun getCharactersClasses(characterId: Int) : MutableMap<String, Class>

    @Insert
    abstract fun insertCharacterSubclassCrossRef(ref: CharacterSubclassCrossRef)

    //Race Table
    @Query("SELECT * FROM races")
    @Transaction
    abstract fun getAllRaces(): LiveData<List<Race>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRace(newRace: RaceEntity) : Long

    @Query("DELETE FROM races WHERE raceId = :id")
    abstract fun deleteRace(id: Int)

    @Query("SELECT * FROM races WHERE raceId = :id")
    @Transaction
    abstract fun findLiveRaceById(id: Int) : LiveData<Race>

    @Insert
    abstract fun insertRaceFeatureCrossRef(ref: RaceFeatureCrossRef)

    @Delete
    abstract fun removeRaceFeatureCrossRef(ref: RaceFeatureCrossRef)

    @Insert
    abstract fun insertSubraceFeatChoiceCrossRef(subraceFeatChoiceCrossRef: SubraceFeatChoiceCrossRef)

    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("""SELECT * FROM features 
JOIN RaceFeatureCrossRef ON features.featureId IS RaceFeatureCrossRef.featureId 
WHERE raceId is :raceId""")
    protected abstract fun getRaceFeatures(raceId: Int) : List<Feature>


    @Transaction
    @RewriteQueriesToDropUnusedColumns
    @Query("""SELECT * FROM features 
JOIN SubraceFeatureCrossRef ON features.featureId IS SubraceFeatureCrossRef.featureId 
WHERE subraceId is :subraceId""")
    protected abstract fun getSubraceFeatures(subraceId: Int) : List<Feature>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSubrace(subrace: SubraceEntity) : Long

    @Query("""SELECT * FROM subraces
JOIN RaceSubraceCrossRef ON RaceSubraceCrossRef.subraceId IS subraces.id
WHERE raceId IS :raceId
    """)
    @Transaction
    protected abstract fun getSubraceOptionsWithoutFeatures(raceId: Int): LiveData<List<SubraceEntity>>

    @Query("""SELECT * FROM features
JOIN SubraceFeatureCrossRef ON SubraceFeatureCrossRef.featureId IS features.featureId
WHERE subraceId IS :subraceId
    """)
    protected abstract fun getSubraceTraits(subraceId: Int) : List<Feature>

    fun bindSubraceOptions(raceId: Int, subraces:  MediatorLiveData<List<Subrace>>)  {
        subraces.addSource(getSubraceOptionsWithoutFeatures(raceId)) { entityList ->
            val temp : List<Subrace> = entityList as List<Subrace>
            temp.forEach {
                it.traits = getSubraceTraits(it.id)
            }
            subraces.value = temp
        }
    }

    @Insert
    abstract fun insertSubraceFeatureCrossRef(subraceFeatureCrossRef: SubraceFeatureCrossRef)

    @Delete
    abstract fun removeSubraceFeatureCrossRef(subraceFeatureCrossRef: SubraceFeatureCrossRef)

    @Insert
    abstract fun insertSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntity)

    @Delete
    abstract fun removeSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntity)

    //Feature Table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeature(feature: FeatureEntity) : Long

    @Query("SELECT * FROM features WHERE featureId = :id")
    abstract fun getLiveFeatureById(id: Int) : LiveData<Feature>

    @Query("SELECT * FROM features WHERE featureId = :id")
    abstract fun getFeatureById(id: Int) : Feature

    @Insert
    abstract fun insertFeatureOptionsCrossRef(ref: FeatureOptionsCrossRef)

    @Delete
    abstract fun removeFeatureOptionsCrossRef(ref: FeatureOptionsCrossRef)

    @Insert
    abstract fun insertOptionsFeatureCrossRef(ref: OptionsFeatureCrossRef)

    @Delete
    abstract fun removeOptionsFeatureCrossRef(ref: OptionsFeatureCrossRef)

    @Insert
    abstract fun insertFeatureChoice(option: FeatureChoiceEntity) : Long

    @Query("DELETE FROM features WHERE featureId = :id")
    abstract fun removeFeatureChoice(id : Int)

    //This returns all featureChoices associate with a feature. It doesn't not contain the options or the chosen fields.
    @Query("""SELECT * FROM FeatureChoiceEntity 
JOIN FeatureOptionsCrossRef ON FeatureOptionsCrossRef.id IS FeatureChoiceEntity.id
WHERE FeatureOptionsCrossRef.featureId IS :featureId""")
    protected abstract fun getFeatureChoices(featureId: Int) : List<FeatureChoiceEntity>

    //This returns all features which belong in the options field of a featureChoice.
    @Query("""SELECT * FROM features
JOIN OptionsFeatureCrossRef ON OptionsFeatureCrossRef.featureId IS features.featureId
WHERE OptionsFeatureCrossRef.choiceId IS :featureChoiceId""")
    protected abstract fun getFeatureChoiceOptions(featureChoiceId: Int) : List<Feature>

    //This returns all features which belong in the chosen field of a featureChoice.
    @Query("""SELECT * FROM features 
JOIN FeatureChoiceChoiceEntity ON features.featureId IS FeatureChoiceChoiceEntity.featureId
WHERE FeatureChoiceChoiceEntity.characterId IS :characterId AND FeatureChoiceChoiceEntity.choiceId IS :choiceId""")
    protected abstract fun getFeatureChoiceChosen(characterId: Int, choiceId: Int) : List<Feature>

    //Feats
    @Insert
    abstract fun insertFeat(feat: FeatEntity) : Long

    @Insert
    abstract fun insertFeatChoice(featChoiceEntity: FeatChoiceEntity): Long

    @Insert
    abstract fun insertFeatChoiceFeatCrossRef(featChoiceFeatCrossRef: FeatChoiceFeatCrossRef)

    @Insert
    abstract fun insertFeatChoiceChoiceEntity(featChoiceChoiceEntity: FeatChoiceChoiceEntity)

    //Backgrounds
    @Insert
    abstract fun insertBackground(backgroundEntity: BackgroundEntity): Long

    @Query("DELETE FROM backgrounds WHERE id = :id")
    abstract fun removeBackgroundById(id: Int)

    @Insert
    abstract fun insertCharacterBackgroundCrossRef(ref: CharacterBackgroundCrossRef)

    @Insert
    abstract fun insertBackgroundFeatureCrossRef(ref: BackgroundFeatureCrossRef)

    @Query("SELECT * FROM BackgroundChoiceEntity WHERE characterId IS :charId")
    abstract fun getBackgroundChoiceData(charId: Int) : BackgroundChoiceEntity

    @Query("""SELECT * FROM features 
JOIN BackgroundFeatureCrossRef ON features.featureId IS BackgroundFeatureCrossRef.featureId
WHERE backgroundId IS :id
    """)
    abstract fun getBackgroundFeatures(id: Int): List<Feature>

    //Spells
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSpell(spell: Spell) : Long

    @Query("DELETE FROM spells WHERE id IS :id")
    abstract fun removeSpellById(id: Int)

    @Insert
    abstract fun insertFeatureSpellCrossRef(ref: FeatureSpellCrossRef)

    @Delete
    abstract fun removeFeatureSpellCrossRef(ref: FeatureSpellCrossRef)

    @Insert
    abstract fun insertBackgroundSpellCrossRef(ref: BackgroundSpellCrossRef)

    @Delete
    abstract fun removeBackgroundSpellCrossRef(ref: BackgroundSpellCrossRef)

    @Insert
    abstract fun insertClassSpellCrossRef(ref: ClassSpellCrossRef)

    @Delete
    abstract fun removeClassSpellCrossRef(ref: ClassSpellCrossRef)

    @Insert
    abstract fun insertSubclassSpellCrossRef(ref: SubclassSpellCrossRef)

    @Delete
    abstract fun removeSubclassSpellCrossRef(ref: SubclassSpellCrossRef)

    @Query("SELECT * FROM classes WHERE id IS :id")
    protected abstract fun getUnfilledClass(id: Int) : LiveData<ClassEntity>

    fun getClass(id : Int): LiveData<Class> {
        val result = MediatorLiveData<Class>()
        result.addSource(getUnfilledClass(id)) { entity ->
            entity as Class
            entity.levelPath = getLevelPath(id)
            result.value = entity
        }
        return result
    }

    @Query("""SELECT * FROM spells
JOIN ClassSpellCrossRef ON spells.id IS ClassSpellCrossRef.spellId
WHERE classId IS :classId""")
    abstract fun getSpellsByClassId(classId: Int): MutableList<Spell>

    @Query("""SELECT * FROM subclasses
JOIN ClassSubclassCrossRef ON ClassSubclassCrossRef.subclassId IS subclasses.subclassId
WHERE classId IS :id""")
    protected abstract fun getUnfilledSubclassesByClassId(id: Int) : LiveData<List<SubclassEntity>>

    fun getSubclassesByClassId(id: Int): LiveData<List<Subclass>> {
        val result = MediatorLiveData<List<Subclass>>()
        result.addSource(getUnfilledSubclassesByClassId(id)) { entities ->
            (entities as List<Subclass>).forEach {
                it.features = getSubclassFeaturesById(it.subclassId)
            }
            result.value = entities
        }
        return result
    }

    @Query("""SELECT * FROM features 
JOIN SubclassFeatureCrossRef ON SubclassFeatureCrossRef.featureId IS features.featureId
WHERE subclassId IS :id""")
    protected abstract fun getSubclassFeaturesById(id: Int) : List<Feature>

    @Insert
    abstract fun insertCharacterClassFeatCrossRef(classFeatCrossRef: ClassFeatCrossRef)

    @Insert
    abstract fun insertSubClassSpellCastingCrossRef(subclassSpellCastingSpellCrossRef: SubclassSpellCastingSpellCrossRef)

    @Query("SELECT backpack FROM characters WHERE id IS :id")
    abstract fun getCharacterBackPack(id: Int) : Backpack

    @Query("UPDATE characters SET backpack = :backpack WHERE id IS :id")
    abstract fun insertCharacterBackPack(backpack: Backpack, id: Int)

    @Query("SELECT * FROM backgrounds WHERE id IS :id")
    protected abstract fun getUnfilledBackground(id: Int) : LiveData<BackgroundEntity>

    @Query("""SELECT * FROM features
JOIN BackgroundFeatureCrossRef ON BackgroundFeatureCrossRef.featureId IS features.featureId 
WHERE backgroundId IS :id""")
    protected abstract fun getUnfilledBackgroundFeatures(id: Int) : List<Feature>

    fun getBackground(id: Int, result: MediatorLiveData<Background>) {
        result.addSource(getUnfilledBackground(id)) {
            if(it != null) {
                val background = it as Background
                background.features = getUnfilledBackgroundFeatures(id)
                fillOutFeatureListWithoutChosen(background.features!!)
                result.value = background
            }

        }
    }

    @Insert
    abstract fun insertBackgroundChoiceEntity(backgroundChoiceEntity: BackgroundChoiceEntity)
}