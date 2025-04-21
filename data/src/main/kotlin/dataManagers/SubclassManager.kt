package dataManagers

import model.SubclassEntity

class SubclassManager(
    val insertSubclass: suspend (SubclassEntity) -> Unit,
    val insertSubclassFeatureCrossRef: (
        featureId: Int,
        subclassId: Int
    ) -> Unit,
    val insertSubclassSpellCrossRef: (
        subclassId: Int,
        spellId: Int
    ) -> Unit,
)