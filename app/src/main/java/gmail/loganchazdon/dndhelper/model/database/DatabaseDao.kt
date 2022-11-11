package gmail.loganchazdon.dndhelper.model.database
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.room.*
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.choiceEntities.RaceChoiceEntity
import gmail.loganchazdon.dndhelper.model.junctionEntities.CharacterRaceCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.RaceFeatureCrossRef
private const val fullCharacterSql =
    """SELECT * FROM characters 
INNER JOIN CharacterRaceCrossRef ON characters.id IS CharacterRaceCrossRef.id
INNER JOIN races ON CharacterRaceCrossRef.raceId IS races.raceId
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
            }
        }
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

    suspend fun findCharacterById(id: Int): Character {
        val character = findCharacterWithoutListChoices(id)
        fillOutCharacterChoiceLists(character)
        return character
    }

    fun findLiveCharacterById(id: Int, character : MediatorLiveData<Character> ) {
        val characterLiveData  =findLiveCharacterWithoutListChoices(id)
        character.addSource(characterLiveData) {
            fillOutCharacterChoiceLists(it)
            character.value = it
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

    //Class Table
    @Query("SELECT * FROM classes")
    abstract fun getAllClasses(): List<Class>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertClass(newClass: Class)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertClasses(newClasses: List<Class>)

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

    //Feature Table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFeature(feature: Feature) : Long

    @Query("SELECT * FROM features WHERE featureId = :id")
    abstract fun getLiveFeatureById(id: Int) : LiveData<Feature>

    @Query("SELECT * FROM features WHERE featureId = :id")
    abstract fun getFeatureById(id: Int) : Feature
}