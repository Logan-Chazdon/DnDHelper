package dataManagers

import model.Class

class ClassManager(
    val insertClassSpellCrossRef: (classId: Int, spellId: Int) -> Unit,
    val insertClassSubclassId: (
        classId: Int,
        subclassId: Int,
    ) -> Unit,
    val insertClass: suspend (Class) -> Unit,
    val insertClassFeatureCrossRef: (
        id: Int,
        featureId: Int
    ) -> Unit,
)