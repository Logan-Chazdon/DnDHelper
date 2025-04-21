package dataManagers

import model.BackgroundEntity

class BackgroundManager(
    val insertBackgroundSpellCrossRef : (backgroundId: Int, spellId : Int) -> Unit,
    val insertBackground : suspend (BackgroundEntity) -> Unit,
    val insertBackgroundFeatureCrossRef :(backgroundId : Int, featureId : Int) -> Unit,
)