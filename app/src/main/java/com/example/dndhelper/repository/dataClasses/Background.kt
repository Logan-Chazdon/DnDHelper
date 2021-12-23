package com.example.dndhelper.repository.dataClasses

data class Background (
        val name: String,
        val desc: String,
        val proficiencies : List<Proficiency>,
        val toolProficiencies : List<ToolProficiency>,
        val features : List<Feature>,
        val languages : List<Language>,
        val equipment : List<Item>,
        val equipmentChoices: List<ItemChoice>
)