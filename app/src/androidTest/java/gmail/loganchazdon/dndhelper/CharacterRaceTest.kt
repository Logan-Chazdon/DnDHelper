package gmail.loganchazdon.dndhelper



import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.choiceEntities.FeatureChoiceChoiceEntity
import gmail.loganchazdon.dndhelper.model.choiceEntities.RaceChoiceEntity
import gmail.loganchazdon.dndhelper.model.database.DatabaseDao
import gmail.loganchazdon.dndhelper.model.database.RoomDataBase
import gmail.loganchazdon.dndhelper.model.junctionEntities.CharacterRaceCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.FeatureOptionsCrossRef
import gmail.loganchazdon.dndhelper.model.junctionEntities.RaceFeatureCrossRef
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
        val abilityBonusChoice = AbilityBonusChoice(
            choose = 2,
            from = AbilityBonusChoice.allStatsArray
        )
        val proficiencyChoice = ProficiencyChoice(
            name = "Test name",
            choose = 1,
            from = listOf(
                Proficiency(name = "Acrobatics"),
                Proficiency(name = "Athletics"),
                Proficiency(name = "Alchemists tools")
            )
        )
        val languageChoice = LanguageChoice(
            name = "Test name",
            choose = 1,
            from = listOf(
                Language("Common"),
                Language("Draconic")
            )
        )
        val feature = FeatureEntity(
            name = "Test name",
            description = "Test desc"
        )
        val featureChoice = FeatureChoice(
            choose = Choose(1),
            options = mutableListOf()
        )
        val option = FeatureEntity(
            name = "Choice Feature",
            description = "Test"
        )

        val choiceId = dao.insertFeatureChoice(featureChoice).toInt()
        val featureId = dao.insertFeature(feature).toInt()
        val optionId = dao.insertFeature(option).toInt()

        val charId = dao.insertCharacter(CharacterEntity()).toInt()
        val raceId = dao.insertRace(RaceEntity(
            abilityBonusChoice = abilityBonusChoice,
            proficiencyChoices = listOf(proficiencyChoice),
            languageChoices = listOf(languageChoice)
        )).toInt()

        dao.insertFeatureChoiceEntity(
            FeatureChoiceChoiceEntity(
                featureId = optionId,
                choiceId = choiceId,
                characterId = charId
            )
        )
        dao.insertFeatureOptionsCrossRef(
            FeatureOptionsCrossRef(
                featureId = featureId,
                id = choiceId
            )
        )

        dao.insertRaceFeatureCrossRef(
            RaceFeatureCrossRef(
                raceId = raceId,
                featureId = featureId
            )
        )

        dao.insertCharacterRaceCrossRef(
            CharacterRaceCrossRef(
                id = charId,
                raceId = raceId
            )
        )

        val abilityBonusChosen = listOf(
            abilityBonusChoice.from[0].toString(),
            abilityBonusChoice.from[5].toString()
        )
        val proficiencyChosen=listOf(listOf(proficiencyChoice.from[0].name!!))
        val languageChosen = listOf(listOf(languageChoice.from[0].name!!))

        dao.insertRaceChoice(
            RaceChoiceEntity(
                characterId = charId,
                raceId = raceId,
                abilityBonusChoice = abilityBonusChosen,
                proficiencyChoice = proficiencyChosen,
                languageChoice = languageChosen
            )
        )

        val character = dao.findCharacterById(charId)
        assert(character.race?.abilityBonusChoice?.chosen != null)
        assert(character.race?.proficiencyChoices?.get(0)?.chosen != null)
        assert(character.race?.languageChoices?.get(0)?.chosen != null)
        assert(character.race!!.traits!![0].allChosen.size == 1)
        assert(character.race!!.traits!![0].allChosen[0].name == "Choice Feature")
    }

    @After
    fun closeDatabase() {
        database.close()
    }
}
