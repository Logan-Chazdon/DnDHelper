package model.sync

import android.content.Context
import model.*
import model.choiceEntities.*
import model.sync.workers.*

actual class CharacterSyncManager(context: Context) : SyncManager(context) {
    actual fun postCharacter(character: CharacterEntity) {
        pushSync<PostCharacterWorker>(gson.toJson(character))
    }

    actual fun deleteCharacter(id: Int) {
        pushSync<DeleteCharacterWorker>(gson.toJson(id))
    }

    actual fun updateCharacterName(id: Int, name: String) {
        pushSync<PostCharacterNameWorker>(gson.toJson(Pair(name, id)))
    }

    actual fun updateCharacterTraits(id: Int, name: String) {
        pushSync<PostCharacterTraitsWorker>(gson.toJson(Pair(name, id)))
    }

    actual fun updateCharacterIdeals(id: Int, name: String) {
        pushSync<PostCharacterIdealsWorker>(gson.toJson(Pair(name, id)))
    }

    actual fun updateCharacterBonds(id: Int, name: String) {
        pushSync<PostCharacterBondsWorker>(gson.toJson(Pair(name, id)))
    }

    actual fun updateCharacterFlaws(id: Int, name: String) {
        pushSync<PostCharacterFlawsWorker>(gson.toJson(Pair(name, id)))
    }

    actual fun updateCharacterNotes(id: Int, name: String) {
        pushSync<PostCharacterNotesWorker>(gson.toJson(Pair(name, id)))
    }

    actual fun postPactMagicStateEntity(characterId: Int, classId: Int, slotsCurrentAmount: Int) {
        pushSync<PostCharacterPactMagicStateEntityWorker>(
            gson.toJson(
                PactMagicStateEntity(
                    characterId = characterId,
                    classId = classId,
                    slotsCurrentAmount = slotsCurrentAmount
                )
            )
        )
    }

    actual fun postCharacterSubraceCrossRef(characterId: Int, subraceId: Int) {
        pushSync<PostCharacterSubraceCrossRefWorker>(
            gson.toJson(
                CharacterSubraceCrossRef(
                    subraceId = subraceId,
                    characterId = characterId
                )
            )
        )
    }

    actual fun postSubraceChoiceEntity(subraceChoiceEntity: SubraceChoiceEntity) {
        pushSync<PostSubraceChoiceEntityWorker>(
            gson.toJson(
                subraceChoiceEntity
            )
        )
    }

    actual fun postCharacterSubclassCrossRef(subclassId: Int, characterId: Int, classId: Int) {
        pushSync<PostCharacterSubclassCrossRefWorker>(
            gson.toJson(
                CharacterSubclassCrossRef(
                    subClassId = subclassId,
                    characterId = characterId,
                    classId = classId
                )
            )
        )
    }

    actual fun postFeatureChoiceEntity(featureId: Int, characterId: Int, choiceId: Int) {
        pushSync<PostFeatureChoiceEntityWorker>(
            gson.toJson(
                FeatureChoiceChoiceEntity(
                    featureId = featureId,
                    choiceId = choiceId,
                    characterId = characterId
                )
            )
        )
    }

    actual fun postCharacterClassSpellCrossRef(
        classId: Int,
        spellId: Int,
        characterId: Int,
        isPrepared: Boolean?
    ) {
        pushSync<PostCharacterClassSpellCrossRefWorker>(
            gson.toJson(
                CharacterClassSpellCrossRef(
                    characterId = characterId,
                    classId = classId,
                    spellId = spellId,
                    isPrepared = isPrepared
                )
            )
        )
    }

    actual fun postSubclassSpellCastingSpellCrossRef(
        subclassId: Int,
        spellId: Int,
        characterId: Int,
        isPrepared: Boolean?
    ) {
        pushSync<PostSubclassSpellCastingSpellCrossRefWorker>(gson.toJson(
            SubclassSpellCastingSpellCrossRef(
                subclassId = subclassId,
                spellId = spellId,
                characterId = characterId,
                isPrepared = isPrepared
            )
        ))
    }

    actual fun postCharacterBackPack(backpack: Backpack, characterId: Int) {
        pushSync<PostCharacterBackpackWorker>(gson.toJson(
            CharacterEntity(id = characterId, backpack = backpack)
        ))
    }

    actual fun deleteClassCharacterCrossRef(characterId: Int, classId: Int) {
        pushSync<DeleteClassCharacterCrossRefWorker>(gson.toJson(
            CharacterClassCrossRef(
                characterId = characterId,
                classId = classId
            )
        ))
    }

    actual fun postCharacterClassCrossRef(characterId: Int, classId: Int) {
        pushSync<PostCharacterClassCrossRefWorker>(gson.toJson(
            CharacterClassCrossRef(
                characterId = characterId,
                classId = classId
            )
        ))
    }

    actual fun postClassChoiceEntity(classChoiceEntity: ClassChoiceEntity) {
        pushSync<PostClassChoiceEntityWorker>(gson.toJson(
            classChoiceEntity
        ))
    }

    actual fun postCharacterClassFeatCrossRef(characterId: Int, featId: Int, classId: Int) {
        pushSync<PostCharacterClassFeatCrossRefWorker>(gson.toJson(
            ClassFeatCrossRef(
                characterId = characterId,
                classId = classId,
                featId = featId
            )
        ))
    }

    actual fun postBackgroundChoiceEntity(backgroundChoiceEntity: BackgroundChoiceEntity) {
        pushSync<PostBackgroundChoiceEntityWorker>(gson.toJson(
            backgroundChoiceEntity
        ))
    }

    actual fun postRaceChoice(raceChoiceEntity: RaceChoiceEntity) {
        pushSync<PostRaceChoiceWorker>(gson.toJson(
            raceChoiceEntity
        ))
    }

    actual fun postCharacterRaceCrossRef(id: Int, raceId: Int) {
        pushSync<PostCharacterRaceCrossRefWorker>(gson.toJson(
            CharacterRaceCrossRef(
                raceId = raceId,
                id = id
            )
        ))
    }

    actual fun postCharacterBackgroundCrossRef(backgroundId: Int, characterId: Int) {
        pushSync<PostCharacterBackgroundCrossRefWorker>(gson.toJson(
            CharacterBackgroundCrossRef(
                characterId = characterId,
                backgroundId = backgroundId
            )
        ))
    }

    actual fun postTemp(id: Int?, temp: String) {
        pushSync<PostCharacterTempWorker>(gson.toJson(
            Pair(temp, id)
        ))
    }

    actual fun postHeal(id: Int?, hp: String, maxHp: Int) {
        pushSync<PostCharacterHealWorker>(gson.toJson(
            Triple(id, hp, maxHp)
        ))
    }

    actual fun postHp(id: Int, toInt: Int) {
        pushSync<PostCharacterHpWorker>(gson.toJson(
            Pair(id, toInt)
        ))
    }

    actual fun postDamage(id: Int, toInt: Int) {
        pushSync<PostCharacterDamageWorker>(gson.toJson(
            Pair(id, toInt)
        ))
    }

    actual fun postDeathSaveSuccess(id: Int, sign: Int) {
        pushSync<PostDeathSaveSuccessWorker>(gson.toJson(
            Pair(id, sign)
        ))
    }

    actual fun postDeathSaveFailures(id: Int, sign: Int) {
        pushSync<PostDeathSaveFailureWorker>(gson.toJson(
            Pair(id, sign)
        ))
    }

    actual fun postSpellSlots(spellSlots: List<Resource>, id: Int) {
        pushSync<PostSpellSlotsWorker>(gson.toJson(
            CharacterEntity(
                spellSlots = spellSlots,
                id = id
            )
        ))
    }

    actual fun deleteCharacterClassSpellCrossRefs(classId: Int, characterId: Int) {
        pushSync<DeleteCharacterClassSpellCrossRefsWorker>(gson.toJson(
            Pair(classId, characterId)
        ))
    }

    actual fun postCharacterFeatureState(featureId: Int, characterId: Int, isActive: Boolean) {
        pushSync<PostCharacterFeatureStateWorker>(gson.toJson(
            CharacterFeatureState(
                characterId = characterId,
                featureId = featureId,
                isActive = isActive
            )
        ))
    }

    actual fun deleteFeatureFeatureChoice(choiceId: Int, characterId: Int) {
        pushSync<DeleteFeatureFeatureChoiceWorker>(gson.toJson(
            Pair(choiceId, characterId)
        ))
    }
}