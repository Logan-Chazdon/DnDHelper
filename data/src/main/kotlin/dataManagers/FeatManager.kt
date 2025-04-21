package dataManagers

import model.FeatChoiceEntity
import model.FeatEntity

class FeatManager(
    val insertFeat: (FeatEntity) -> Unit,
    val insertFeatFeatureCrossRef: (featId: Int, featureId: Int) -> Unit,
    val insertFeatChoice: (FeatChoiceEntity) -> Unit,
)