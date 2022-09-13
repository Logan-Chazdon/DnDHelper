package gmail.loganchazdon.dndhelper.model.database
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import gmail.loganchazdon.dndhelper.model.Character
import gmail.loganchazdon.dndhelper.model.Class
import gmail.loganchazdon.dndhelper.model.Race

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
    fun getAllRaces(): List<Race>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRace(newClass: Race) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRaces(newClasses: List<Race>)
}