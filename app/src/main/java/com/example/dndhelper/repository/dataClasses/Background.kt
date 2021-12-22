package com.example.dndhelper.repository.dataClasses

data class Background (
        val name: String,
        val desc: String,
        val proficiencies : List<Proficiency>,
        val features : List<Feature>,
        val languages : List<Language>,
        val equipment: List<ItemChoice>
)