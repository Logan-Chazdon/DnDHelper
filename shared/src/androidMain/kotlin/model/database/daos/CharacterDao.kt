package model.database.daos

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import model.*
import model.choiceEntities.BackgroundChoiceEntity
import model.choiceEntities.ClassChoiceEntity
import model.choiceEntities.RaceChoiceEntity
import model.choiceEntities.SubraceChoiceEntity

@Dao
actual abstract class CharacterDao {
    companion object {
        //The query to PactMagicStateEntity is just so that any liveData created by this query will be invalidated and updated when
        //pactMagicStateEntity is changed.
        private const val fullCharacterSql =
            """WITH subrace  AS (SELECT id AS subraceid, name AS subracename, abilityBonuses AS subraceabilityBonuses, abilityBonusChoice AS subraceabilityBonusChoice, 
    startingProficiencies AS subracestartingProficiencies, languages AS subracelanguages, languageChoices AS subracelanguageChoices, size AS subracesize, 
    groundSpeed AS subracegroundSpeed FROM subraces),
    
    background AS (SELECT id AS backgroundid, name AS backgroundname, [desc] AS backgrounddesc, spells AS backgroundspells, proficiencies AS backgroundproficiencies, 
    languages AS backgroundlanguages, equipment AS backgroundequipment, equipmentChoices AS backgroundequipmentChoices
    FROM backgrounds),
    _ AS (SELECT characterId FROM PactMagicStateEntity WHERE characterId IS :id)

    
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
    abstract fun insertCharacter(character: CharacterEntityTable): Long
    actual suspend fun insertCharacter(character: CharacterEntity): Long {
        return insertCharacter(character.asTable())
    }

    @Update
    abstract fun updateCharacter(character: CharacterEntityTable)
    actual fun updateCharacter(character: CharacterEntity) {
        updateCharacter(character.asTable())
    }

    @MapInfo(valueColumn = "isPrepared")
    @Query(
        """SELECT * FROM spells
JOIN SubclassSpellCastingSpellCrossRef ON SubclassSpellCastingSpellCrossRef.spellId IS spells.id
WHERE characterId IS :characterId AND subclassId IS :subclassId
"""
    )
    actual abstract fun getSpellCastingSpellsForSubclass(
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
    actual abstract fun getSpellCastingSpellsForClass(characterId: Int, classId: Int): Map<Spell, Boolean?>

    @Query(
        """SELECT * FROM feats
JOIN ClassFeatCrossRef ON ClassFeatCrossRef.featId IS feats.id
WHERE ClassFeatCrossRef.classId IS :classId AND ClassFeatCrossRef.characterId IS :characterId
    """
    )
    actual abstract fun getClassFeats(classId: Int, characterId: Int): MutableList<Feat>

    @Query(
        """SELECT * FROM ClassChoiceEntity
WHERE characterId IS :characterId AND classId IS :classId
    """
    )
    protected abstract fun getClassChoiceDataTable(characterId: Int, classId: Int): ClassChoiceEntityTable

    actual fun getClassChoiceData(characterId: Int, classId: Int): ClassChoiceEntity {
        return getClassChoiceDataTable(characterId, classId)
    }


    @Query(
        """SELECT * FROM feats
JOIN FeatChoiceChoiceEntity ON FeatChoiceChoiceEntity.featId IS feats.id
WHERE choiceId IS :choiceId AND characterId IS :characterId
    """
    )
    actual abstract fun getFeatChoiceChosen(characterId: Int, choiceId: Int): List<Feat>

    @RewriteQueriesToDropUnusedColumns
    @Query(fullCharacterSql)
    @Transaction
    actual abstract fun findCharacterWithoutListChoices(id: Int): Character

    @Query(fullCharacterSql)
    @RewriteQueriesToDropUnusedColumns
    @Transaction
    actual abstract fun findLiveCharacterWithoutListChoices(id: Int): Flow<Character>

    @Query("SELECT * FROM RaceChoiceEntity WHERE raceId = :raceId AND characterId = :charId")
    abstract fun getRaceChoiceDataTable(raceId: Int, charId: Int): RaceChoiceEntityTable
    actual fun getRaceChoiceData(raceId: Int, charId: Int): RaceChoiceEntity {
        return getRaceChoiceData(raceId, charId)
    }

    @Query("SELECT * FROM SubraceChoiceEntity WHERE subraceId = :subraceId AND characterId = :charId")
    abstract fun getSubraceChoiceDataTable(subraceId: Int, charId: Int): SubraceChoiceEntityTable
    actual fun getSubraceChoiceData(subraceId: Int, charId: Int): SubraceChoiceEntity {
        return getSubraceChoiceDataTable(subraceId, charId)
    }

    @Query("DELETE FROM characters WHERE id = :id")
    actual abstract fun deleteCharacter(id: Int)

    @Query(
        """SELECT * FROM features
JOIN ClassFeatureCrossRef ON ClassFeatureCrossRef.featureId IS features.featureId
WHERE ClassFeatureCrossRef.id IS :classId AND features.grantedAtLevel <= :maxLevel
    """
    )
    actual abstract fun getClassFeatures(classId: Int, maxLevel: Int): MutableList<Feature>

    @Query("SELECT * FROM BackgroundChoiceEntity WHERE characterId IS :charId")
    abstract fun getBackgroundChoiceDataTable(charId: Int): BackgroundChoiceEntityTable
    actual fun getBackgroundChoiceData(charId: Int): BackgroundChoiceEntity {
        return getBackgroundChoiceDataTable(charId)
    }

    @Query("SELECT * FROM characters")
    protected abstract fun getAllCharactersWithoutClasses(): Flow<List<Character>>

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

    actual fun getAllCharacters(): Flow<List<Character>> {
        return getAllCharactersWithoutClasses().transform { characterList ->
            if (characterList != null) {
                characterList.forEach {
                    it.classes = getCharactersClasses(it.id)
                    it.race = getCharacterRace(it.id)
                    it.background = getCharacterBackground(it.id)
                }
                emit(characterList)
            }
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCharacterRaceCrossRef(ref: CharacterRaceCrossRef)

    @Delete
    abstract fun removeCharacterRaceCrossRef(ref: CharacterRaceCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRaceChoice(choice: RaceChoiceEntityTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCharacterClassSpellCrossRef(ref: CharacterClassSpellCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertBackgroundChoiceEntity(backgroundChoiceEntity: BackgroundChoiceEntityTable)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCharacterClassCrossRef(ref: CharacterClassCrossRef)

    @Delete
    abstract fun removeCharacterClassCrossRef(ref: CharacterClassCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertClassChoiceEntity(entity: ClassChoiceEntityTable)
    actual fun insertClassChoiceEntity(classChoiceEntity: ClassChoiceEntity) {
        insertClassChoiceEntity(classChoiceEntity.asTable())
    }

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
    actual abstract fun getCharactersClasses(characterId: Int): MutableMap<String, Class>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCharacterSubclassCrossRef(ref: CharacterSubclassCrossRef)

    //This returns all features which belong in the chosen field of a featureChoice.
    @Query(
        """SELECT * FROM features 
JOIN FeatureChoiceChoiceEntity ON features.featureId IS FeatureChoiceChoiceEntity.featureId
WHERE FeatureChoiceChoiceEntity.characterId IS :characterId AND FeatureChoiceChoiceEntity.choiceId IS :choiceId"""
    )
    actual abstract fun getFeatureChoiceChosen(choiceId: Int, characterId: Int): List<Feature>

    @Query("SELECT backpack FROM characters WHERE id IS :id")
    actual abstract fun getCharacterBackPack(id: Int): Backpack

    @Query("UPDATE characters SET backpack = :backpack WHERE id IS :id")
    actual abstract fun insertCharacterBackPack(backpack: Backpack, id: Int)


    @Insert
    abstract fun insertCharacterClassFeatCrossRef(classFeatCrossRef: ClassFeatCrossRef)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCharacterSubRaceCrossRef(characterSubraceCrossRef: CharacterSubraceCrossRef)

    actual fun insertCharacterSubRaceCrossRef(characterId: Int, subraceId: Int) {
        insertCharacterSubRaceCrossRef(
            CharacterSubraceCrossRef(
                characterId,
                subraceId
            )
        )
    }

    actual fun insertCharacterSubclassCrossRef(
        subClassId: Int,
        characterId: Int,
        classId: Int,
    ) {
        insertCharacterSubclassCrossRef(
            CharacterSubclassCrossRef(
                subClassId, characterId, classId
            )
        )
    }

    actual fun insertCharacterClassSpellCrossRef(classId: Int, spellId: Int, characterId: Int, isPrepared: Boolean?) {
        insertCharacterClassSpellCrossRef(
            CharacterClassSpellCrossRef(
                characterId = characterId,
                classId = classId,
                spellId = spellId,
                isPrepared = isPrepared
            )
        )
    }

    actual fun insertFeatureChoiceEntity(featureId: Int, characterId: Int, choiceId: Int) {
        insertFeatureChoiceEntity(
            FeatureChoiceChoiceEntityTable(
                featureId, characterId, choiceId
            )
        )
    }

    @Insert
    abstract fun insertSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntityTable)
    actual fun insertSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntity) {
        insertSubraceChoiceEntity(subraceChoiceEntity as SubraceChoiceEntityTable)
    }

    actual fun insertSubClassSpellCastingCrossRef(
        subclassId: Int,
        spellId: Int,
        characterId: Int,
        isPrepared: Boolean?
    ) {
        insertSubClassSpellCastingCrossRef(
            SubclassSpellCastingSpellCrossRef(
                subclassId = subclassId,
                spellId = spellId,
                characterId = characterId,
                isPrepared = isPrepared
            )
        )
    }

    @Delete
    abstract fun removeSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntityTable)

    @Insert
    abstract fun insertSubClassSpellCastingCrossRef(subclassSpellCastingSpellCrossRef: SubclassSpellCastingSpellCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCharacterBackgroundCrossRef(ref: CharacterBackgroundCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeatureChoiceEntity(choice: FeatureChoiceChoiceEntityTable): Long

    @Query("UPDATE characters SET tempHp = :temp WHERE id IS :id")
    actual abstract fun setTemp(id: Int, temp: Int)

    @Query("UPDATE characters SET currentHp =MIN(currentHp + :hp, :maxHp) WHERE id = :id")
    actual abstract fun heal(id: Int, hp: Int, maxHp: Int)

    @Query("UPDATE characters SET currentHp =:hp WHERE id = :id")
    actual abstract fun setHp(id: Int, hp: Int)

    @Query("UPDATE characters SET currentHp = MAX(currentHp - :damage, 0) WHERE id = :id")
    actual abstract fun damage(id: Int, damage: Int)

    @Query("UPDATE characters SET positiveDeathSaves = positiveDeathSaves + :it WHERE id = :id")
    actual abstract fun updateDeathSaveSuccesses(id: Int, it: Int)

    @Query("UPDATE characters SET negativeDeathSaves = negativeDeathSaves + :it WHERE id = :id")
    actual abstract fun updateDeathSaveFailures(id: Int, it: Int)

    @MapInfo(valueColumn = "isPrepared")
    @Query(
        """
        SELECT * FROM spells
        JOIN ClassSpellCrossRef ON  INSTR(:classIdsByName, ClassSpellCrossRef.classId) > 0 AND ClassSpellCrossRef.spellId IS spells.id
        LEFT JOIN CharacterClassSpellCrossRef ON CharacterClassSpellCrossRef.characterId = :id AND CharacterClassSpellCrossRef.spellId = spells.id
    """
    )
    actual abstract fun getAllSpellsByList(id: Int, classIdsByName: List<Int>): Map<Spell, Boolean?>

    @Query("UPDATE characters SET spellSlots = :spellSlots WHERE id = :id")
    actual abstract fun insertSpellSlots(spellSlots: List<Resource>, id: Int)

    @Query("DELETE FROM CharacterClassSpellCrossRef WHERE classId IS :classId AND characterId IS :characterId")
    actual abstract fun removeCharacterClassSpellCrossRefs(classId: Int, characterId: Int)

    @Query("SELECT COUNT(*) FROM CharacterClassSpellCrossRef WHERE classId IS :classId AND characterId IS :characterId AND isPrepared IS '1'")
    actual abstract fun getNumOfPreparedSpells(classId: Int, characterId: Int): Int

    @Query("UPDATE characters SET name = :it WHERE id IS :id")
    actual abstract fun changeName(it: String, id: Int)

    @Query("UPDATE characters SET personalityTraits = :it WHERE id IS :id")
    actual abstract fun setPersonalityTraits(it: String, id: Int)

    @Query("UPDATE characters SET ideals = :it WHERE id IS :id")
    actual abstract fun setIdeals(it: String, id: Int)

    @Query("UPDATE characters SET bonds = :it WHERE id IS :id")
    actual abstract fun setBonds(it: String, id: Int)

    @Query("UPDATE characters SET flaws = :it WHERE id IS :id")
    actual abstract fun setFlaws(it: String, id: Int)

    @Query("UPDATE characters SET notes = :it WHERE id IS :id")
    actual abstract fun setNotes(it: String, id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCharacterFeatureState(characterFeatureState: CharacterFeatureState)
    actual fun insertCharacterFeatureState(
        featureId: Int,
        characterId: Int,
        isActive: Boolean
    ) {
        insertCharacterFeatureState(
            CharacterFeatureState(
                characterId = characterId,
                featureId = featureId,
                isActive = isActive
            )
        )
    }

    @Query("SELECT isActive FROM CharacterFeatureState WHERE featureId IS :featureId AND characterId IS :characterId")
    actual abstract fun isFeatureActive(featureId: Int, characterId: Int): Boolean

    @Query("SELECT slotsCurrentAmount FROM PactMagicStateEntity WHERE classId = :classId AND characterId = :characterId")
    actual abstract fun getCharacterPactSlots(classId: Int, characterId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertPactMagicStateEntity(entity: PactMagicStateEntity)

    actual fun insertPactMagicStateEntity(
        characterId: Int,
        classId: Int,
        slotsCurrentAmount: Int
    ) {
        insertPactMagicStateEntity(
            PactMagicStateEntity(
                characterId = characterId,
                classId = classId,
                slotsCurrentAmount = slotsCurrentAmount
            )
        )
    }

    @Query(
        """SELECT * FROM spells
JOIN CharacterClassSpellCrossRef ON spells.id IS CharacterClassSpellCrossRef.spellId
WHERE characterId IS :characterId AND classId IS :classId    """
    )
    actual abstract fun getPactMagicSpells(characterId: Int, classId: Int): MutableList<Spell>

    actual fun removeCharacterClassCrossRef(characterId: Int, classId: Int) {
        removeCharacterClassCrossRef(
            CharacterClassCrossRef(
                characterId = characterId,
                classId = classId
            )
        )
    }

    actual fun insertCharacterClassCrossRef(characterId: Int, classId: Int) {
        insertCharacterClassCrossRef(
            CharacterClassCrossRef(
                characterId, classId
            )
        )
    }

    actual fun insertCharacterClassFeatCrossRef(characterId: Int, featId: Int, classId: Int) {
        insertCharacterClassFeatCrossRef(
            ClassFeatCrossRef(
                characterId = characterId,
                classId = classId,
                featId = featId
            )
        )
    }

    actual fun insertBackgroundChoiceEntity(backgroundChoiceEntity: BackgroundChoiceEntity) {
        insertBackgroundChoiceEntity(backgroundChoiceEntity.asTable())
    }

    actual fun insertRaceChoice(raceChoiceEntity: RaceChoiceEntity) {
        insertRaceChoice(raceChoiceEntity.asTable())
    }

    @Query("INSERT INTO CharacterRaceCrossRef (id, raceId) VALUES (:id, :raceId)")
    actual abstract fun insertCharacterRaceCrossRef(id: Int, raceId: Int)

    actual fun insertCharacterBackgroundCrossRef(backgroundId: Int, characterId: Int) {
        insertCharacterBackgroundCrossRef(
            CharacterBackgroundCrossRef(
                characterId = characterId,
                backgroundId = backgroundId
            )
        )
    }
}