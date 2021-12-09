package com.example.dndhelper.repository.model
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dndhelper.repository.dataClasses.Class
import com.example.dndhelper.repository.dataClasses.Character
import com.example.dndhelper.repository.dataClasses.Race

@Dao
interface DatabaseDao {
    //Character Table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCharacter(character: Character)

    @Query("SELECT * FROM characters WHERE id = :id")
    fun findCharacterById(id: Int): LiveData<Character?>

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
    fun insertRace(newClass: Race)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRaces(newClasses: List<Race>)
}