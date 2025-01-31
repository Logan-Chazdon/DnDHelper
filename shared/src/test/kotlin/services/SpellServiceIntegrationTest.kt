package services

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import model.Spell
import org.junit.Test

class SpellServiceIntegrationTest {
    private val userOneService = SpellService(client1)
    private val userTwoService = SpellService(client2)

    val users = listOf(
        userOneService to listOf(
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
            },
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
            }
        ),
        userTwoService to listOf(
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
            },
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
        )
    )


    @Test
    fun insertSpell() = runTest {
        users.forEach { user ->
            user.second.forEach {
                user.first.insertSpell(it)
            }
        }

        users.forEach { user ->
            val spells = user.first.getHomebrewSpells().first()
            assert(spells.isNotEmpty())
        }
    }


    @Test
    fun addAndRemoveClassSpellCrossRef() {
        users.forEach { user ->
            user.second.forEach {
            }
        }
    }

    @Test
    fun getAllSpells() = runTest {
        users.forEach { user ->
            user.second.forEach {
                user.first.insertSpell(it)
            }
        }

        users.forEach { user ->
            val spells = user.first.getAllSpells().first()
            assert(spells.isNotEmpty())
        }
    }

    @Test
    fun getLiveSpell() = runTest {
        users.forEach { user ->
            user.second.forEach {
                user.first.insertSpell(it)
            }
        }

        users.forEach { user ->
            user.second.forEach { spell ->
                val serverSpell = user.first.getLiveSpell(spell.id).first()
                assert(serverSpell.name == spell.name)
            }
        }
    }

    @Test
    fun getSpellClasses() {
    }

    @Test
    fun removeSpellById() = runTest {
        users.forEach { user ->
            user.second.forEach {
                user.first.removeSpellById(it.id)
            }
        }

        users.forEach { user ->
            val spells = user.first.getAllSpells().first()
            assert(spells.isEmpty())
        }
    }
}