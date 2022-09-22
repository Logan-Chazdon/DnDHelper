package gmail.loganchazdon.dndhelper.model.database
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.junctionEntities.RaceFeatureCrossRef

@Dao
interface DatabaseDao {
    //Character Table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCharacter(character: Character) : Long

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun findCharacterById(id: Int): Character?

    @Query("SELECT * FROM characters WHERE id = :id")
    fun findLiveCharacterById(id: Int): LiveData<Character>

    @Query("DELETE FROM characters WHERE id = :id")
    fun deleteCharacter(id: Int)

    @Query("SELECT * FROM characters")
    fun getAllCharacters(): LiveData<List<Character>>

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

    @Query("SELECT * FROM races WHERE id = :id")
    @Transaction
    fun findLiveRaceById(id: Int) : LiveData<Race>

    @Insert
    fun insertRaceFeatureCrossRef(ref: RaceFeatureCrossRef)

    //Feature Table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFeature(feature: Feature) : Long

    @Query("SELECT * FROM features WHERE featureId = :id")
    fun getLiveFeatureById(id: Int) : LiveData<Feature>

    @Query("SELECT * FROM features WHERE featureId = :id")
    fun getFeatureById(id: Int) : Feature
}