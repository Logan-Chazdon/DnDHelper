package services

import io.ktor.client.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import model.Character
import model.ClassEntity
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.*
import kotlin.streams.asSequence


class CharacterServiceIntegrationTest {
    private data class User(
        val characters: List<CharacterData>,
        val client: HttpClient
    ) : ServiceProvider(client)

    private data class CharacterData(
        val entity: Character,
        val classes: List<ClassEntity> = emptyList()
    )


    private val users = listOf(
        User(
            client = client1,
            characters = listOf(
                CharacterData(
                    Character(
                        name = "User one first character",
                        id = 1,
                    ),
                ),
                CharacterData(
                    Character(
                        name = "User one second character",
                        id = 2,
                    ),
                )
            )
        ),
        User(
            client = client2,
            characters = listOf(
                CharacterData(
                    Character(
                        name = "User two first character",
                        id = 1,
                    ),
                ),
                CharacterData(
                    Character(
                        name = "User two second character",
                        id = 2,
                    ),
                )
            )
        )

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
        users.forEach { user ->
            user.characters.forEach { character ->
                assertEquals(user.characterService.postCharacter(character.entity).toInt(), character.entity.id)
            }
        }
    }

    @Test
    fun deleteCharacter() = runTest {
        users.forEach { user ->
            val tempChar = Character("testname", id = 3)
            user.characterService.postCharacter(tempChar)

            user.characterService.deleteCharacter(3)

            val userChars =  user.characterService.getAllCharacters().take(2).first{ it.isNotEmpty() }
            assert(userChars.none { it.id == 3 })
        }
    }

    @Test
    fun findLiveCharacterWithoutListChoices() {
    }

    @Test
    fun setHp() = runBlocking {
        users.forEach { user ->
            user.characters.forEach { char ->
                val expectedHp = (0..char.entity.maxHp).random()
                user.characterService.setHp(char.entity.id, expectedHp)
                val realHp = user.characterService.findCharacterWithoutListChoices(char.entity.id).currentHp
                assertEquals(realHp, expectedHp)
            }
        }
    }

    @Test
    fun updateDeathSaveSuccesses() = runBlocking {
        users.forEach { user ->
            user.characters.forEach { char ->
                val expectedSaves = (0..3).random()
                user.characterService.updateDeathSaveSuccesses(char.entity.id, expectedSaves)

                val realSaves = user.characterService.findCharacterWithoutListChoices(char.entity.id).positiveDeathSaves
                assertEquals(realSaves, expectedSaves)
            }
        }
    }

    @Test
    fun updateDeathSaveFailures() = runBlocking {
        users.forEach { user ->
            user.characters.forEach { char ->
                val expectedSaves = (0..3).random()
                user.characterService.updateDeathSaveFailures(char.entity.id, expectedSaves)

                val realSaves = user.characterService.findCharacterWithoutListChoices(char.entity.id).negativeDeathSaves
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
            user.characters.forEach { char ->
                val newClassId = (1..13).random()
                //Insert a class to remove
                user.characterService.insertCharacterClassCrossRef(
                    characterId = char.entity.id,
                    classId = newClassId
                )

                //Remove the class.
                user.characterService.removeCharacterClassCrossRef(
                    characterId = char.entity.id,
                    classId = newClassId
                )

                //Get the classes
                val classes = user.characterService.getCharactersClasses(characterId = char.entity.id).filter { it.value.id == newClassId}

                //Assert that the class is not present.
                assert(classes.isEmpty())
            }
        }
    }

    @Test
    fun getNumOfPreparedSpells() = runBlocking {
        users.forEach { user ->
            user.characters.forEach { char ->
                char.classes.forEach {
                    //Ensure that all classes are present
                    user.characterService.insertCharacterClassCrossRef(
                        characterId = char.entity.id,
                        classId = it.id
                    )

                    //Prepare some spells
                    user.characterService.insertCharacterClassSpellCrossRef(
                        classId = it.id,
                        spellId = TODO(),
                        characterId = char.entity.id,
                        prepared = true
                    )
                }
            }
        }
    }

    @Test
    fun changeName() = runBlocking {
        users.forEach { user ->
            user.characters.forEach { char ->
                val expectedName = char.entity.name + " updated"
                user.characterService.changeName(expectedName, char.entity.id)

                val realName = user.characterService.findCharacterWithoutListChoices(char.entity.id).name
                assertEquals(realName, expectedName)
            }
        }
    }

    @Test
    fun setPersonalityTraits() = runBlocking {
        users.forEach { user ->
            user.characters.forEach { char ->
                val expected = randString()
                user.characterService.setPersonalityTraits(expected, char.entity.id)

                val real = user.characterService.findCharacterWithoutListChoices(char.entity.id).personalityTraits
                assertEquals(real, expected)
            }
        }
    }

    @Test
    fun setIdeals() = runBlocking {
        users.forEach { user ->
            user.characters.forEach { char ->
                val expected = randString()
                user.characterService.setIdeals(expected, char.entity.id)

                val real = user.characterService.findCharacterWithoutListChoices(char.entity.id).ideals
                assertEquals(real, expected)
            }
        }
    }

    @Test
    fun setNotes() = runBlocking {
        users.forEach { user ->
            user.characters.forEach { char ->
                val expected = randString()
                user.characterService.setNotes(expected, char.entity.id)

                val real = user.characterService.findCharacterWithoutListChoices(char.entity.id).notes
                assertEquals(real, expected)
            }
        }
    }

    @Test
    fun setFlaws() = runBlocking {
        users.forEach { user ->
            user.characters.forEach { char ->
                val expected = randString()
                user.characterService.setFlaws(expected, char.entity.id)

                val real = user.characterService.findCharacterWithoutListChoices(char.entity.id).flaws
                assertEquals(real, expected)
            }
        }
    }

    @Test
    fun setBonds() = runBlocking {
        users.forEach { user ->
            user.characters.forEach { char ->
                val expected = randString()
                user.characterService.setBonds(expected, char.entity.id)

                val real = user.characterService.findCharacterWithoutListChoices(char.entity.id).bonds
                assertEquals(real, expected)
            }
        }
    }

    @Test
    fun damage() = runBlocking {
        users.forEach { user ->
            user.characters.forEach { char ->
                val maxHp = char.entity.maxHp
                val dmg = (0..maxHp + 10).random()
                val oldHp = user.characterService.findCharacterWithoutListChoices(char.entity.id).currentHp
                user.characterService.damage(char.entity.id, dmg)

                val real = user.characterService.findCharacterWithoutListChoices(char.entity.id).currentHp
                assertEquals(real, maxOf(oldHp - dmg, 0))
            }
        }
    }

    @Test
    fun heal() = runBlocking {
        users.forEach { user ->
            user.characters.forEach { char ->
                val maxHp = char.entity.maxHp
                val healing = (0..maxHp + 10).random()
                val oldHp = user.characterService.findCharacterWithoutListChoices(char.entity.id).currentHp
                user.characterService.heal(
                    hp = healing,
                    id = char.entity.id,
                    maxHp = maxHp
                )

                val real = user.characterService.findCharacterWithoutListChoices(char.entity.id).currentHp
                assertEquals(real, minOf(oldHp + healing, maxHp))
            }
        }
    }


    @Test
    fun setTemp() = runBlocking {
        users.forEach { user ->
            user.characters.forEach { char ->
                val temp = (0..10).random()
                user.characterService.setTemp(
                    id = char.entity.id,
                    temp = temp
                )

                val real = user.characterService.findCharacterWithoutListChoices(char.entity.id).tempHp
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