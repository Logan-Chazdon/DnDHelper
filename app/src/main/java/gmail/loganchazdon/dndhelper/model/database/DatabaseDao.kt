package gmail.loganchazdon.dndhelper.model.database
import androidx.lifecycle.LiveData
import androidx.room.*
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.choiceEntities.RaceChoiceEntity
import gmail.loganchazdon.dndhelper.model.junctionEntities.CharacterRaceCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.RaceFeatureCrossRef
private const val fullCharacterSql =
    """SELECT * FROM characters 
JOIN CharacterRaceCrossRef ON characters.id IS CharacterRaceCrossRef.id
JOIN races ON CharacterRaceCrossRef.raceId IS races.raceId
JOIN RaceChoiceEntity ON races.raceId IS RaceChoiceEntity.raceId AND characters.id IS RaceChoiceEntity.characterId
WHERE characters.id = :id"""

@Dao
interface DatabaseDao {
    //Character Table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCharacter(character: CharacterEntity) : Long

    @Query(fullCharacterSql)
    @Transaction
    suspend fun findCharacterById(id: Int): Character

    @Query(fullCharacterSql)
    @Transaction
    fun findLiveCharacterById(id: Int): LiveData<Character>

    @Query("DELETE FROM characters WHERE id = :id")
    fun deleteCharacter(id: Int)

    @Query("SELECT * FROM characters " +
            "JOIN CharacterRaceCrossRef ON characters.id IS CharacterRaceCrossRef.id\n" +
            "JOIN races ON CharacterRaceCrossRef.raceId IS races.raceId")
    fun getAllCharacters(): LiveData<List<Character>>

    @Insert
    fun insertCharacterRaceCrossRef(ref: CharacterRaceCrossRef)

    @Delete
    fun removeCharacterRaceCrossRef(ref: CharacterRaceCrossRef)

    @Insert
    fun insertRaceChoice(choice: RaceChoiceEntity)

    //Class Table
    @Query("SELECT * FROM classes")
    fun getAllClasses(): List<Class>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertClass(newClass: Class)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertClasses(newClasses: List<Class>)

    //Race Table
    @Query("SELECT * FROM races")
    @Transaction
    fun getAllRaces(): LiveData<List<Race>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRace(newRace: RaceEntity) : Long

    @Query("DELETE FROM races WHERE raceId = :id")
    fun deleteRace(id: Int)

    @Query("SELECT * FROM races WHERE raceId = :id")
    @Transaction
    fun findLiveRaceById(id: Int) : LiveData<Race>

    @Insert
    fun insertRaceFeatureCrossRef(ref: RaceFeatureCrossRef)

    @Delete
    fun removeRaceFeatureCrossRef(ref: RaceFeatureCrossRef)

    //Feature Table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFeature(feature: Feature) : Long

    @Query("SELECT * FROM features WHERE featureId = :id")
    fun getLiveFeatureById(id: Int) : LiveData<Feature>

    @Query("SELECT * FROM features WHERE featureId = :id")
    fun getFeatureById(id: Int) : Feature
}