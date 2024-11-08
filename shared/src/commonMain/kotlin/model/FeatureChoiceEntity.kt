package model

import kotlinx.serialization.Serializable

@Serializable
open class FeatureChoiceEntity(
    open var choose: Choose = Choose(0),
) {
    open var id: Int = 0
}