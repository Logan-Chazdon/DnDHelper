package dataManagers

import model.Language

class LanguageManager(
    val getAllLanguages: () -> List<Language>,
    val postAll: (List<Language>) -> Unit,
)