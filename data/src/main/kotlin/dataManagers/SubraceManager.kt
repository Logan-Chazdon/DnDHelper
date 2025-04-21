package dataManagers

import model.Subrace

class SubraceManager(
    val insertSubrace: suspend (Subrace) -> Unit,
    val insertSubraceFeatureCrossRef: (
        subraceId: Int,
        featureId: Int
    ) -> Unit,
    val insertSubraceFeatChoiceCrossRef: (
        featChoiceId: Int,
        subraceId: Int
    ) -> Unit,
)