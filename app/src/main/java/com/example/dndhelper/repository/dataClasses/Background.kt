package com.example.dndhelper.repository.dataClasses

data class Background (
        val name: String,
        val desc: String,
        val proficiencies : List<Proficiency>,
        val features : List<Feature>,
        val languages : List<Language>,
        val languageChoices : List<LanguageChoice>,
        val equipment : List<ItemInterface>,
        val equipmentChoices: List<ItemChoice>
)