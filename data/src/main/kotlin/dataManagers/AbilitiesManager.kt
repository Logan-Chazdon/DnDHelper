package dataManagers

class AbilitiesManager(
    val postAbilitiesToSkills: (Map<String, List<String>>) -> Unit,
    val getAbilitiesToSkills: () -> Map<String, List<String>>
)