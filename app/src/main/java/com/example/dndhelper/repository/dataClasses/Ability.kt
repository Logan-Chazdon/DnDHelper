package com.example.dndhelper.repository.dataClasses

@Serializable
sealed class Ability(val name: String) {
    @Serializable
    class Strength : Ability("Strength")
    @Serializable
    class Dexterity: Ability("Dexterity")

    class Constitution: Ability("Constitution")
    class Intelligence: Ability("Intelligence")
    class Wisdom: Ability("Wisdom")
    class Charisma: Ability("Charisma")

    override fun toString(): String {
        return name
    }

}
