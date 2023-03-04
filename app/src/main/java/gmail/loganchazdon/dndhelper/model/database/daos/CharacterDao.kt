package gmail.loganchazdon.dndhelper.model.database.daos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.room.*
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.choiceEntities.*
import gmail.loganchazdon.dndhelper.model.junctionEntities.*
import gmail.loganchazdon.dndhelper.model.stateEntities.CharacterFeatureState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Dao
abstract class CharacterDao {
    companion object {
        private const val fullCharacterSql =
            """WITH subrace AS (SELECT id AS subraceid, name AS subracename, abilityBonuses AS subraceabilityBonuses, abilityBonusChoice AS subraceabilityBonusChoice, 
    startingProficiencies AS subracestartingProficiencies, languages AS subracelanguages, languageChoices AS subracelanguageChoices, size AS subracesize, 
    groundSpeed AS subracegroundSpeed FROM subraces),
    
    background AS (SELECT id AS backgroundid, name AS backgroundname, [desc] AS backgrounddesc, spells AS backgroundspells, proficiencies AS backgroundproficiencies, 
    languages AS backgroundlanguages, equipment AS backgroundequipment, equipmentChoices AS backgroundequipmentChoices
    FROM backgrounds)
    
    SELECT characters.*, races.*, background.*, subrace.* FROM characters 
    LEFT JOIN CharacterRaceCrossRef ON characters.id IS CharacterRaceCrossRef.id
    LEFT JOIN races ON CharacterRaceCrossRef.raceId IS races.raceId
    LEFT JOIN CharacterSubraceCrossRef ON CharacterSubraceCrossRef.characterId IS characters.id
    LEFT JOIN subrace ON subrace.subraceid IS CharacterSubraceCrossRef.subraceId
    LEFT JOIN CharacterBackgroundCrossRef ON CharacterBackgroundCrossRef.characterId IS characters.id
    LEFT JOIN background ON background.backgroundid IS CharacterBackgroundCrossRef.backgroundId
    WHERE characters.id = :id"""
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertCharacter(character: CharacterEntity): Long

    @Update
    abstract fun updateCharacter(character: CharacterEntity)

    @MapInfo(valueColumn = "isPrepared")
    @Query(
        """SELECT * FROM spells
JOIN SubclassSpellCastingSpellCrossRef ON SubclassSpellCastingSpellCrossRef.spellId IS spells.id
WHERE characterId IS :characterId AND subclassId IS :subclassId
"""
    )
    abstract fun getSpellCastingSpellsForSubclass(
        characterId: Int,
        subclassId: Int
    ): Map<Spell, Boolean?>

    @MapInfo(valueColumn = "isPrepared")
    @Query(
        """SELECT * FROM spells
JOIN CharacterClassSpellCrossRef ON spells.id IS CharacterClassSpellCrossRef.spellId
WHERE characterId IS :characterId AND classId IS :classId
"""
    )
    abstract fun getSpellCastingSpellsForClass(characterId: Int, classId: Int): Map<Spell, Boolean?>

    @Query(
        """SELECT * FROM feats
JOIN ClassFeatCrossRef ON ClassFeatCrossRef.featId IS feats.id
WHERE ClassFeatCrossRef.classId IS :classId AND ClassFeatCrossRef.characterId IS :characterId
    """
    )
    abstract fun getClassFeats(classId: Int, characterId: Int): MutableList<Feat>

    @Query(
        """SELECT * FROM ClassChoiceEntity
WHERE characterId IS :characterId AND classId IS :classId
    """
    )
    abstract fun getClassChoiceData(characterId: Int, classId: Int): ClassChoiceEntity

    @Query(
        """SELECT * FROM feats
JOIN FeatChoiceChoiceEntity ON FeatChoiceChoiceEntity.featId IS feats.id
WHERE choiceId IS :choiceId AND characterId IS :characterId
    """
    )
    abstract fun getFeatChoiceChosen(characterId: Int, choiceId: Int): List<Feat>

    @RewriteQueriesToDropUnusedColumns
    @Query(fullCharacterSql)
    @Transaction
    abstract fun findCharacterWithoutListChoices(id: Int): Character

    @Query(fullCharacterSql)
    @RewriteQueriesToDropUnusedColumns
    @Transaction
    abstract fun findLiveCharacterWithoutListChoices(id: Int): LiveData<Character>

    @Query("SELECT * FROM RaceChoiceEntity WHERE raceId = :raceId AND characterId = :charId")
    abstract fun getRaceChoiceData(raceId: Int, charId: Int): RaceChoiceEntity

    @Query("SELECT * FROM SubraceChoiceEntity WHERE subraceId = :subraceId AND characterId = :charId")
    abstract fun getSubraceChoiceData(subraceId: Int, charId: Int): SubraceChoiceEntity

    @Query("DELETE FROM characters WHERE id = :id")
    abstract fun deleteCharacter(id: Int)

    @Query(
        """SELECT * FROM features
JOIN ClassFeatureCrossRef ON ClassFeatureCrossRef.featureId IS features.featureId
WHERE ClassFeatureCrossRef.id IS :classId AND features.grantedAtLevel <= :maxLevel
    """
    )
    abstract fun getClassFeatures(classId: Int, maxLevel: Int = 20): MutableList<Feature>

    @Query("SELECT * FROM BackgroundChoiceEntity WHERE characterId IS :charId")
    abstract fun getBackgroundChoiceData(charId: Int): BackgroundChoiceEntity

    @Query("SELECT * FROM characters")
    protected abstract fun getAllCharactersWithoutClasses(): LiveData<List<Character>>

    @Query(
        """SELECT * FROM races 
        JOIN CharacterRaceCrossRef ON CharacterRaceCrossRef.raceId IS races.raceId
        where id IS :id
    """
    )
    protected abstract fun getCharacterRace(id: Int): Race?


    @Query(
        """SELECT * FROM backgrounds
        JOIN CharacterBackgroundCrossRef ON CharacterBackgroundCrossRef.backgroundId IS backgrounds.id
        where CharacterBackgroundCrossRef.characterId IS :id
    """
    )
    protected abstract fun getCharacterBackground(id: Int): Background?

    fun getAllCharacters(): LiveData<List<Character>> {
        val result = MediatorLiveData<List<Character>>()
        result.addSource(getAllCharactersWithoutClasses()) { characterList ->
            if (characterList != null) {
                GlobalScope.launch {
                    characterList.forEach {
                        it.classes = getCharactersClasses(it.id)
                        it.race = getCharacterRace(it.id)
                        it.background = getCharacterBackground(it.id)
                    }
                    result.postValue(characterList)
                }
            }
        }
        return result
    }

    @Insert
    abstract fun insertCharacterRaceCrossRef(ref: CharacterRaceCrossRef)

    @Delete
    abstract fun removeCharacterRaceCrossRef(ref: CharacterRaceCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRaceChoice(choice: RaceChoiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCharacterClassSpellCrossRef(ref: CharacterClassSpellCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertBackgroundChoiceEntity(backgroundChoiceEntity: BackgroundChoiceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCharacterClassCrossRef(ref: CharacterClassCrossRef)

    @Delete
    abstract fun removeCharacterClassCrossRef(ref: CharacterClassCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertClassChoiceEntity(entity: ClassChoiceEntity)

    @Query("DELETE FROM ClassChoiceEntity WHERE classId IS :classId AND characterId IS :characterId")
    abstract fun removeClassChoiceEntity(classId: Int, characterId: Int)

    @MapInfo(keyColumn = "name")
    @Query(
        """SELECT * FROM classes
JOIN CharacterClassCrossRef ON CharacterClassCrossRef.classId IS classes.id
JOIN ClassChoiceEntity ON ClassChoiceEntity.classId IS CharacterClassCrossRef.classId AND ClassChoiceEntity.characterId IS CharacterClassCrossRef.characterId
LEFT JOIN CharacterSubclassCrossRef ON CharacterSubclassCrossRef.characterId IS CharacterClassCrossRef.characterId AND CharacterSubclassCrossRef.classId IS classes.id
LEFT JOIN subclasses ON subclasses.subclassId IS CharacterSubclassCrossRef.subClassId
WHERE CharacterClassCrossRef.characterId IS :characterId
    """
    )
    abstract fun getCharactersClasses(characterId: Int): MutableMap<String, Class>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCharacterSubclassCrossRef(ref: CharacterSubclassCrossRef)

    //This returns all features which belong in the chosen field of a featureChoice.
    @Query(
        """SELECT * FROM features 
JOIN FeatureChoiceChoiceEntity ON features.featureId IS FeatureChoiceChoiceEntity.featureId
WHERE FeatureChoiceChoiceEntity.characterId IS :characterId AND FeatureChoiceChoiceEntity.choiceId IS :choiceId"""
    )
    abstract fun getFeatureChoiceChosen(characterId: Int, choiceId: Int): List<Feature>

    @Query("SELECT backpack FROM characters WHERE id IS :id")
    abstract fun getCharacterBackPack(id: Int): Backpack

    @Query("UPDATE characters SET backpack = :backpack WHERE id IS :id")
    abstract fun insertCharacterBackPack(backpack: Backpack, id: Int)


    @Insert
    abstract fun insertCharacterClassFeatCrossRef(classFeatCrossRef: ClassFeatCrossRef)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCharacterSubRaceCrossRef(characterSubraceCrossRef: CharacterSubraceCrossRef)

    @Insert
    abstract fun insertSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntity)

    @Delete
    abstract fun removeSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntity)

    @Insert
    abstract fun insertSubClassSpellCastingCrossRef(subclassSpellCastingSpellCrossRef: SubclassSpellCastingSpellCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCharacterBackgroundCrossRef(ref: CharacterBackgroundCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeatureChoiceEntity(choice: FeatureChoiceChoiceEntity): Long

    @Query("UPDATE characters SET tempHp = :temp WHERE id IS :id")
    abstract fun setTemp(id: Int, temp: Int)

    @Query("UPDATE characters SET currentHp =MIN(currentHp + :hp, :maxHp) WHERE id = :id")
    abstract fun heal(id: Int, hp: Int, maxHp: Int)

    @Query("UPDATE characters SET currentHp =:hp WHERE id = :id")
    abstract fun setHp(id: Int, hp: Int)

    @Query("UPDATE characters SET currentHp = MAX(currentHp - :damage, 0) WHERE id = :id")
    abstract fun damage(id: Int, damage: Int)

    @Query("UPDATE characters SET positiveDeathSaves = positiveDeathSaves + :it WHERE id = :id")
    abstract fun updateDeathSaveSuccesses(id: Int, it: Int)

    @Query("UPDATE characters SET negativeDeathSaves = negativeDeathSaves + :it WHERE id = :id")
    abstract fun updateDeathSaveFailures(id: Int, it: Int)

    @MapInfo(valueColumn = "isPrepared")
    @Query(
        """
        SELECT * FROM spells
        JOIN ClassSpellCrossRef ON  INSTR(:list, ClassSpellCrossRef.classId) > 0 AND ClassSpellCrossRef.spellId IS spells.id
        LEFT JOIN CharacterClassSpellCrossRef ON CharacterClassSpellCrossRef.characterId = :id AND CharacterClassSpellCrossRef.spellId = spells.id
    """
    )
    abstract fun getAllSpellsByList(id: Int, list: List<Int>): Map<Spell, Boolean?>

    @Query("UPDATE characters SET spellSlots = :spellSlots WHERE id = :id")
    abstract fun insertSpellSlots(spellSlots: List<Resource>, id: Int)

    @Query("DELETE FROM CharacterClassSpellCrossRef WHERE classId IS :classId AND characterId IS :characterId")
    abstract fun removeCharacterClassSpellCrossRefs(classId: Int, characterId: Int)

    @Query("SELECT COUNT(*) FROM CharacterClassSpellCrossRef WHERE classId IS :classId AND characterId IS :characterId AND isPrepared IS '1'")
    abstract fun getNumOfPreparedSpells(classId: Int, characterId: Int): Int

    @Query("UPDATE characters SET name = :it WHERE id IS :id")
    abstract fun changeName(it: String, id: Int)

    @Query("UPDATE characters SET personalityTraits = :it WHERE id IS :id")
    abstract fun setPersonalityTraits(it: String, id: Int)

    @Query("UPDATE characters SET ideals = :it WHERE id IS :id")
    abstract fun setIdeals(it: String, id: Int)

    @Query("UPDATE characters SET bonds = :it WHERE id IS :id")
    abstract fun setBonds(it: String, id: Int)

    @Query("UPDATE characters SET flaws = :it WHERE id IS :id")
    abstract fun setFlaws(it: String, id: Int)

    @Query("UPDATE characters SET notes = :it WHERE id IS :id")
    abstract fun setNotes(it: String, id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCharacterFeatureState(characterFeatureState: CharacterFeatureState)

    @Query("SELECT isActive FROM CharacterFeatureState WHERE featureId IS :featureId AND characterId IS :characterId")
    abstract fun isFeatureActive(featureId: Int, characterId: Int): Boolean
}