package services

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import model.Background
import model.Character
import model.Class
import model.Race
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*
import kotlin.streams.asSequence


class CharacterServiceIntegrationTest {
    private val userOneService = CharacterService(client1)
    private val userTwoService = CharacterService(client2)


    private val users = listOf(
        userOneService to listOf(
            Character(
                name = "User one first character",
                id = 1,
            ).apply {
                classes = mutableMapOf(
                    "Barbarian" to Class(
                        id = 2,
                        name = "Barbarian",
                        subclassLevel = 3,
                        isBaseClass = true,
                        startingGoldD4s = 2
                    ),
                    "Artificer" to Class(
                        id = 1,
                        name = "Artificer",
                        subclassLevel = 3,
                        isBaseClass = false,
                        startingGoldD4s = 4
                    )
                )
                background = Background()
                race = Race()
            },
            Character(
                name = "User one second character",
                id = 2
            ).apply {
                classes = mutableMapOf(
                    "Wizard" to Class(
                        id = 13,
                        name = "Wizard",
                        subclassLevel = 3,
                        isBaseClass = true,
                        startingGoldD4s = 2,
                    ),
                    "Sorcerer" to Class(
                        id = 11,
                        name = "Sorcerer",
                        subclassLevel = 3,
                        isBaseClass = false,
                        startingGoldD4s = 4
                    )
                )
                background = Background()
                race = Race()
            }
        ),
        userTwoService to listOf(
            Character(
                name = "User two first character",
                id = 1
            ).apply {
                classes = mutableMapOf(
                    "Barbarian" to Class(
                        id = 2,
                        name = "Barbarian",
                        subclassLevel = 3,
                        isBaseClass = true,
                        startingGoldD4s = 2
                    ),
                    "Fighter" to Class(
                        id = 6,
                        name = "Fighter",
                        subclassLevel = 3,
                        isBaseClass = false,
                        startingGoldD4s = 4
                    )
                )
                background = Background()
                race = Race()
            },
            Character(
                name = "User two second character",
                id = 2
            ).apply {
                classes = mutableMapOf(
                    "Wizard" to Class(
                        id = 13,
                        name = "Wizard",
                        subclassLevel = 3,
                        isBaseClass = true,
                        startingGoldD4s = 2
                    ),
                    "Sorcerer" to Class(
                        id = 11,
                        name = "Sorcerer",
                        subclassLevel = 3,
                        isBaseClass = false,
                        startingGoldD4s = 4
                    )
                )
                background = Background()
                race = Race()
            }
        ),
    )

    private fun randString(outputStrLength: Long = 10): String {
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        return Random().ints(outputStrLength, 0, source.length)
            .asSequence()
            .map(source::get)
            .joinToString("")
    }


    @Test
    fun getAllCharacters() {

    }

    @Test
    fun postCharacter() = runBlocking {
        users.forEach { user: Pair<CharacterService, List<Character>> ->
            user.second.forEach { character ->
                assertEquals(user.first.postCharacter(character).toInt(), character.id)
            }
        }
    }

    @Test
    fun deleteCharacter() = runTest {
        users.forEach { user ->
            val tempChar = Character("testname", id = 3)
            user.first.postCharacter(tempChar)

            user.first.deleteCharacter(3)

            val userChars =  user.first.getAllCharacters().take(2).first{ it.isNotEmpty() }
            assert(userChars.none { it.id == 3 })
        }
    }

    @Test
    fun findLiveCharacterWithoutListChoices() {
    }

    @Test
    fun setHp() = runBlocking {
        users.forEach { user ->
            user.second.forEach { char ->
                val expectedHp = (0..char.maxHp).random()
                user.first.setHp(char.id, expectedHp)
                val realHp = user.first.findCharacterWithoutListChoices(char.id).currentHp
                assertEquals(realHp, expectedHp)
            }
        }
    }

    @Test
    fun updateDeathSaveSuccesses() = runBlocking {
        users.forEach { user ->
            user.second.forEach { char ->
                val expectedSaves = (0..3).random()
                user.first.updateDeathSaveSuccesses(char.id, expectedSaves)

                val realSaves = user.first.findCharacterWithoutListChoices(char.id).positiveDeathSaves
                assertEquals(realSaves, expectedSaves)
            }
        }
    }

    @Test
    fun updateDeathSaveFailures() = runBlocking {
        users.forEach { user ->
            user.second.forEach { char ->
                val expectedSaves = (0..3).random()
                user.first.updateDeathSaveFailures(char.id, expectedSaves)

                val realSaves = user.first.findCharacterWithoutListChoices(char.id).negativeDeathSaves
                assertEquals(realSaves, expectedSaves)
            }
        }
    }

    @Test
    fun insertSpellSlots() {
    }

    @Test
    fun removeCharacterClassSpellCrossRefs() = runBlocking {
        users.forEach { user ->
            user.second.forEach { char ->
                val newClassId = (1..13).random()
                //Insert a class to remove
                user.first.insertCharacterClassCrossRef(
                    characterId = char.id,
                    classId = newClassId
                )

                //Remove the class.
                user.first.removeCharacterClassCrossRef(
                    characterId = char.id,
                    classId = newClassId
                )

                //Get the classes
                val classes = user.first.getCharactersClasses(characterId = char.id).filter { it.value.id == newClassId}

                //Assert that the class is not present.
                assert(classes.isEmpty())
            }
        }
    }

    @Test
    fun getNumOfPreparedSpells() = runBlocking {
        users.forEach { user ->
            user.second.forEach { char ->
                char.classes.forEach {
                    //Ensure that all classes are present
                    user.first.insertCharacterClassCrossRef(
                        characterId = char.id,
                        classId = it.value.id
                    )

                    //Prepare some spells
                    user.first.insertCharacterClassSpellCrossRef(
                        classId = it.value.id,
                        spellId = TODO(),
                        characterId = char.id,
                        prepared = true
                    )
                }
            }
        }
    }

    @Test
    fun changeName() = runBlocking {
        users.forEach { user ->
            user.second.forEach { char ->
                val expectedName = char.name + " updated"
                user.first.changeName(expectedName, char.id)

                val realName = user.first.findCharacterWithoutListChoices(char.id).name
                assertEquals(realName, expectedName)
            }
        }
    }

    @Test
    fun setPersonalityTraits() = runBlocking {
        users.forEach { user ->
            user.second.forEach { char ->
                val expected = randString()
                user.first.setPersonalityTraits(expected, char.id)

                val real = user.first.findCharacterWithoutListChoices(char.id).personalityTraits
                assertEquals(real, expected)
            }
        }
    }

    @Test
    fun setIdeals() = runBlocking {
        users.forEach { user ->
            user.second.forEach { char ->
                val expected = randString()
                user.first.setIdeals(expected, char.id)

                val real = user.first.findCharacterWithoutListChoices(char.id).ideals
                assertEquals(real, expected)
            }
        }
    }

    @Test
    fun setNotes() = runBlocking {
        users.forEach { user ->
            user.second.forEach { char ->
                val expected = randString()
                user.first.setNotes(expected, char.id)

                val real = user.first.findCharacterWithoutListChoices(char.id).notes
                assertEquals(real, expected)
            }
        }
    }

    @Test
    fun setFlaws() = runBlocking {
        users.forEach { user ->
            user.second.forEach { char ->
                val expected = randString()
                user.first.setFlaws(expected, char.id)

                val real = user.first.findCharacterWithoutListChoices(char.id).flaws
                assertEquals(real, expected)
            }
        }
    }

    @Test
    fun setBonds() = runBlocking {
        users.forEach { user ->
            user.second.forEach { char ->
                val expected = randString()
                user.first.setBonds(expected, char.id)

                val real = user.first.findCharacterWithoutListChoices(char.id).bonds
                assertEquals(real, expected)
            }
        }
    }

    @Test
    fun damage() = runBlocking {
        users.forEach { user ->
            user.second.forEach { char ->
                val maxHp = char.maxHp
                val dmg = (0..maxHp + 10).random()
                val oldHp = user.first.findCharacterWithoutListChoices(char.id).currentHp
                user.first.damage(char.id, dmg)

                val real = user.first.findCharacterWithoutListChoices(char.id).currentHp
                assertEquals(real, maxOf(oldHp - dmg, 0))
            }
        }
    }

    @Test
    fun heal() = runBlocking {
        users.forEach { user ->
            user.second.forEach { char ->
                val maxHp = char.maxHp
                val healing = (0..maxHp + 10).random()
                val oldHp = user.first.findCharacterWithoutListChoices(char.id).currentHp
                user.first.heal(
                    hp = healing,
                    id = char.id,
                    maxHp = maxHp
                )

                val real = user.first.findCharacterWithoutListChoices(char.id).currentHp
                assertEquals(real, minOf(oldHp + healing, maxHp))
            }
        }
    }


    @Test
    fun setTemp() = runBlocking {
        users.forEach { user ->
            user.second.forEach { char ->
                val temp = (0..10).random()
                user.first.setTemp(
                    id = char.id,
                    temp = temp
                )

                val real = user.first.findCharacterWithoutListChoices(char.id).tempHp
                assertEquals(real, temp)
            }
        }
    }

    @Test
    fun getClassFeatures() {
    }

    @Test
    fun getPactMagicSpells() {
    }

    @Test
    fun getCharacterPactSlots() {
    }

    @Test
    fun getCharactersClasses() {
    }

    @Test
    fun findCharacterWithoutListChoices() {
    }

    @Test
    fun getFeatureChoiceChosen() {
    }

    @Test
    fun isFeatureActive() {
    }

    @Test
    fun getAllSpellsByList() {
    }

    @Test
    fun insertCharacterRaceCrossRef() {
    }

    @Test
    fun insertCharacterBackPack() {
    }

    @Test
    fun getCharacterBackPack() {
    }

    @Test
    fun getFeatChoiceChosen() {
    }

    @Test
    fun getClassFeats() {
    }

    @Test
    fun getSpellCastingSpellsForClass() {
    }

    @Test
    fun getSpellCastingSpellsForSubclass() {
    }

    @Test
    fun getClassChoiceData() {
    }

    @Test
    fun insertPactMagicStateEntity() {
    }

    @Test
    fun insertCharacterSubraceCrossRef() {
    }

    @Test
    fun insertSubraceChoiceEntity() {
    }

    @Test
    fun insertCharacterSubclassCrossRef() {
    }

    @Test
    fun insertFeatureChoiceEntity() {
    }

    @Test
    fun insertCharacterClassSpellCrossRef() {
    }

    @Test
    fun insertSubClassSpellCastingCrossRef() {
    }

    @Test
    fun insertCharacterClassCrossRef() {
    }

    @Test
    fun insertClassChoiceEntity() {
    }

    @Test
    fun insertCharacterClassFeatCrossRef() {
    }

    @Test
    fun insertBackgroundChoiceEntity() {
    }

    @Test
    fun insertRaceChoice() {
    }

    @Test
    fun insertCharacterBackgroundCrossRef() {
    }

    @Test
    fun getRaceChoiceData() {
    }

    @Test
    fun getSubraceChoiceData() {
    }

    @Test
    fun getBackgroundChoiceData() {
    }

    @Test
    fun removeCharacterClassCrossRef() {
    }
}