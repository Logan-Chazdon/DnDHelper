package gmail.loganchazdon.dndhelper



import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.core.app.ApplicationProvider
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.choiceEntities.ClassChoiceEntity
import gmail.loganchazdon.dndhelper.model.choiceEntities.FeatChoiceChoiceEntity
import gmail.loganchazdon.dndhelper.model.choiceEntities.FeatureChoiceChoiceEntity
import gmail.loganchazdon.dndhelper.model.choiceEntities.RaceChoiceEntity
import gmail.loganchazdon.dndhelper.model.database.DatabaseDao
import gmail.loganchazdon.dndhelper.model.database.RoomDataBase
import gmail.loganchazdon.dndhelper.model.junctionEntities.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class CharacterRelationsTest{
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

        val subraceId = dao.insertSubrace(SubraceEntity(
            name = "Subrace",
            abilityBonusChoice = null,
            abilityBonuses = null,
            startingProficiencies = null,
            languages = listOf(),
            languageChoices = listOf(),
            size = "Medium",
            groundSpeed = 30
        )).toInt()

        val subraceFeatureId = dao.insertFeature(Feature(name = "subrace feature", description = "test desc")).toInt()

        dao.insertCharacterSubRaceCrossRef(CharacterSubraceCrossRef(subraceId = subraceId, characterId = charId))
        dao.insertSubraceFeatureCrossRef(
            SubraceFeatureCrossRef(subraceId, subraceFeatureId)
        )

        val featChoiceId = dao.insertFeatChoice(FeatChoiceEntity(name = "", choose = 1)).toInt()
        val featId = dao.insertFeat(FeatEntity(name = "Test feat", desc = "")).toInt()
        dao.insertSubraceFeatChoiceCrossRef(SubraceFeatChoiceCrossRef(subraceId = subraceId, featChoiceId))
        dao.insertFeatChoiceFeatCrossRef(FeatChoiceFeatCrossRef(featChoiceId = featChoiceId, featId= featId))
        dao.insertFeatChoiceChoiceEntity(FeatChoiceChoiceEntity(characterId = charId, choiceId = featChoiceId, featId = featId))

        val backgroundId = dao.insertBackground(
            BackgroundEntity(name = "Test", desc = "desc",spells = null, languages = listOf(), equipment = listOf(), equipmentChoices = listOf(), proficiencies = listOf())
        ).toInt()
        dao.insertCharacterBackgroundCrossRef(CharacterBackgroundCrossRef(
            backgroundId = backgroundId,
            characterId = charId
        ))
        val backgroundFeatureId = dao.insertFeature(
            FeatureEntity(
                name = "Background feature",
                description = ""
            )
        ).toInt()
        dao.insertBackgroundFeatureCrossRef(
            BackgroundFeatureCrossRef(
                featureId = backgroundFeatureId,
                backgroundId = backgroundId
            )
        )

        val classId = dao.insertClass(
            ClassEntity(
                name = "Test class",
                hitDie = 8,
                startingGoldD4s = 1,
                subclassLevel = 1,
                spellCasting = SpellCasting(
                    type = 1.0,
                    hasSpellBook = true,
                    castingAbility = "Int",
                    prepareFrom = "known",
                    preparationModMultiplier = 1.0,
                    spellsKnown = listOf(),
                    known = listOf(),
                    cantripsKnown = listOf()
                )
            )
        ).toInt()

        val spellId = dao.insertSpell(
            Spell(
                name = "Test spell",
                level = 1,
                area = "",
                range = "",
                damage = "",
                desc = "",
                duration = "",
                isRitual = false,
                castingTime = "",
                classes = listOf("Test class"),
                components = listOf(),
                school ="Necromancy",
                itemComponents = listOf()
            )
        ).toInt()

        dao.insertCharacterClassSpellCrossRef(
            CharacterClassSpellCrossRef(
                characterId = charId,
                classId = classId,
                spellId = spellId,
                isPrepared = true
            )
        )

        dao.insertCharacterClassCrossRef(
            CharacterClassCrossRef(characterId = charId, classId = classId)
        )

        val classFeatureId = dao.insertFeature(
            FeatureEntity(
                name = "class feature",
                description = ""
            )
        ).toInt()

        dao.insertClassFeatureCrossRef(
            ClassFeatureCrossRef(
                id = classId,
                featureId = classFeatureId
            )
        )

        dao.insertClassChoiceEntity(
            ClassChoiceEntity(
                characterId = charId,
                classId = classId,
                isBaseClass = true,
                level = 1,
                totalNumOnGoldDie = 10,
                abilityImprovementsGranted = mutableListOf(),
                tookGold = true
            )
        )

        val subClassId = dao.insertSubclass(SubclassEntity(
            name = "Subclass",
            spells = emptyList(),
            spellAreFree = false,
            spellCasting = null
        )).toInt()

        dao.insertCharacterSubclassCrossRef(
            CharacterSubclassCrossRef(
                characterId = charId,
                subClassId = subClassId,
                classId = classId
            )
        )

        val featureSpellId = dao.insertSpell(
            Spell(
                name = "Feature Spell",
                level = 1,
                area = "",
                range = "",
                damage = "",
                desc = "",
                duration = "",
                isRitual = false,
                castingTime = "",
                classes = listOf("Test class"),
                components = listOf(),
                school ="Necromancy",
                itemComponents = listOf()
            )
        ).toInt()

        dao.insertFeatureSpellCrossRef(
            FeatureSpellCrossRef(
                featureId = featureId,
                spellId = featureSpellId
            )
        )

        val character = dao.findCharacterById(charId)
        assert(character.race?.abilityBonusChoice?.chosen != null)
        assert(character.race?.proficiencyChoices?.get(0)?.chosen != null)
        assert(character.race?.languageChoices?.get(0)?.chosen != null)
        assert(character.race!!.traits!![0].allChosen.size == 1)
        assert(character.race!!.traits!![0].spells!![0].name == "Feature Spell")
        assert(character.race!!.traits!![0].allChosen[0].name == "Choice Feature")
        assert(character.race!!.subrace!!.traits!![0].name == "subrace feature")
        assert(character.race!!.subrace!!.featChoices!![0].chosen!![0].name == "Test feat")
        assert(character.background!!.features!![0].name == "Background feature")
        assert(character.classes["Test class"]!!.subclass!!.name == "Subclass")
        assert(character.classes["Test class"]!!.spellCasting!!.known[0].second == true)
    }

    @After
    fun closeDatabase() {
        database.close()
    }
}
