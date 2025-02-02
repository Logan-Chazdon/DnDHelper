package services

import io.ktor.client.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import model.Spell
import org.junit.Test

class SpellServiceIntegrationTest {


    private data class User(
        val spells: List<SpellData>,
        val client: HttpClient
    ) : ServiceProvider(client)

    private data class SpellData(
        val entity: Spell,
    )

    private val users = listOf(
        User(
            client = client1,
            spells = listOf(SpellData(
                Spell(
                    name = "UserOneSpellOne",
                    level = 2,
                    components = emptyList(),
                    itemComponents = emptyList(),
                    school = "",
                    desc = "",
                    range = "",
                    area = "",
                    castingTime = "",
                    duration = "",
                    classes = emptyList(),
                    damage = "",
                    isRitual = false,
                    isHomebrew = true
                ).apply {
                    id = 200
                }),
            SpellData(
                Spell(
                    name = "UserOneSpellTwo",
                    level = 2,
                    components = emptyList(),
                    itemComponents = emptyList(),
                    school = "",
                    desc = "",
                    range = "",
                    area = "",
                    castingTime = "",
                    duration = "",
                    classes = emptyList(),
                    damage = "",
                    isRitual = false,
                    isHomebrew = true
                ).apply {
                    id = 201
                })
        )),
        User(
            client = client2,
            spells = listOf(
            SpellData(
                Spell(
                    name = "UserTwoSpellOne",
                    level = 2,
                    components = emptyList(),
                    itemComponents = emptyList(),
                    school = "",
                    desc = "",
                    range = "",
                    area = "",
                    castingTime = "",
                    duration = "",
                    classes = emptyList(),
                    damage = "",
                    isRitual = false,
                    isHomebrew = true
                ).apply {
                    id = 200
                }),
            SpellData(
                Spell(
                    name = "UserTwoSpellTwo",
                    level = 2,
                    components = emptyList(),
                    itemComponents = emptyList(),
                    school = "",
                    desc = "",
                    range = "",
                    area = "",
                    castingTime = "",
                    duration = "",
                    classes = emptyList(),
                    damage = "",
                    isRitual = false,
                    isHomebrew = true
                ).apply {
                    id = 201
                }
            ))
    ))


    @Test
    fun insertSpell() = runTest {
        users.forEach { user ->
            user.spells.forEach {
                user.spellService.insertSpell(it.entity)
            }
        }

        users.forEach { user ->
            val spells = user.spellService.getHomebrewSpells().first()
            assert(spells.isNotEmpty())
        }
    }


    @Test
    fun addAndRemoveClassSpellCrossRef() {
        users.forEach { user ->
            user.spells.forEach {
            }
        }
    }

    @Test
    fun getAllSpells() = runTest {
        users.forEach { user ->
            user.spells.forEach {
                user.spellService.insertSpell(it.entity)
            }
        }

        users.forEach { user ->
            val spells = user.spellService.getAllSpells().first()
            assert(spells.isNotEmpty())
        }
    }

    @Test
    fun getLiveSpell() = runTest {
        users.forEach { user ->
            user.spells.forEach {
                user.spellService.insertSpell(it.entity)
            }
        }

        users.forEach { user ->
            user.spells.forEach { spell ->
                val serverSpell = user.spellService.getLiveSpell(spell.entity.id).first()
                assert(serverSpell.name == spell.entity.name)
            }
        }
    }

    @Test
    fun getSpellClasses() {
    }

    @Test
    fun removeSpellById() = runTest {
        users.forEach { user ->
            user.spells.forEach {
                user.spellService.removeSpellById(it.entity.id)
            }
        }

        users.forEach { user ->
            val spells = user.spellService.getAllSpells().first()
            assert(spells.isEmpty())
        }
    }
}