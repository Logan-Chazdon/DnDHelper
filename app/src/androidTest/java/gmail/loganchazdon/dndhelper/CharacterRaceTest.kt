package gmail.loganchazdon.dndhelper



import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import gmail.loganchazdon.dndhelper.model.AbilityBonus
import gmail.loganchazdon.dndhelper.model.AbilityBonusChoice
import gmail.loganchazdon.dndhelper.model.CharacterEntity
import gmail.loganchazdon.dndhelper.model.RaceEntity
import gmail.loganchazdon.dndhelper.model.choiceEntities.RaceChoiceEntity
import gmail.loganchazdon.dndhelper.model.database.DatabaseDao
import gmail.loganchazdon.dndhelper.model.database.RoomDataBase
import gmail.loganchazdon.dndhelper.model.junctionEntities.CharacterRaceCrossRef
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class CharacterRaceTest{
    private lateinit var database: RoomDatabase
    private lateinit var dao: DatabaseDao
    private val abilityBonusChoice = AbilityBonusChoice(
         choose = 2,
         from = listOf()
    ).run {
        this.chosen = listOf(
            AbilityBonus(
                ability = "Strength",
                bonus = 2
            )
        )
        this
    }
    @Before
    fun setupDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RoomDataBase::class.java
        ).allowMainThreadQueries().build()

        dao = (database as RoomDataBase).databaseDao()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @Throws(IOException::class)
    fun test() = runTest {
        val charId = dao.insertCharacter(CharacterEntity()).toInt()
        val raceId = dao.insertRace(RaceEntity()).toInt()
        dao.insertCharacterRaceCrossRef(
            CharacterRaceCrossRef(
                id = charId,
                raceId = raceId
            )
        )

        dao.insertRaceChoice(
            RaceChoiceEntity(
                characterId = charId,
                raceId = raceId,
                chosen = abilityBonusChoice.chosen
            )
        )

        val character = dao.findCharacterById(charId)
        assert(character.race?.abilityBonusChoice?.chosen == abilityBonusChoice.chosen)
    }

    @After
    fun closeDatabase() {
        database.close()
    }
}
