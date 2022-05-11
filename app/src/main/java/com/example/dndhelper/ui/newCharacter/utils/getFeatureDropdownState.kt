package com.example.dndhelper.ui.newCharacter.utils

import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.example.dndhelper.model.*
import com.example.dndhelper.ui.newCharacter.stateHolders.MultipleChoiceDropdownStateFeatureImpl

fun SnapshotStateMap<String, MultipleChoiceDropdownStateFeatureImpl>.getDropDownState (
    feature: Feature,
    character: Character?,
    assumedProficiencies: List<Proficiency>,
    level: Int,
    assumedClass: Class?,
    assumedSpells: List<Spell>,
    assumedStatBonuses: Map<String, Int>?,
    assumedFeatures: List<Feature>,
    overrideKey: String? = null
) : MultipleChoiceDropdownStateFeatureImpl {
    val applyData = fun (state: MultipleChoiceDropdownStateFeatureImpl): MultipleChoiceDropdownStateFeatureImpl {
        state.assumedClass = assumedClass
        state.assumedProficiencies = assumedProficiencies
        state.character = character
        state.assumedSpells = assumedSpells
        state.level = level
        state.assumedStatBonuses = assumedStatBonuses
        state.assumedFeatures = assumedFeatures
        return state
    }
    val key = overrideKey ?: feature.name + feature.grantedAtLevel
    return if(this.containsKey(key)) {
        applyData(this[key]!!)
    } else {
        this[key] = applyData(MultipleChoiceDropdownStateFeatureImpl(feature))
        this[key]!!
    }
}




