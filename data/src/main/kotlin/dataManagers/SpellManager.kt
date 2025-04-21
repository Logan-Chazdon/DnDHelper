package dataManagers

import model.Spell

class SpellManager(
    val getSpellIdByName : (String) -> Int,
    val insertSpell : suspend (Spell) -> Int,
)