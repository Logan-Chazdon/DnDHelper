package model.sync.workers

import com.google.gson.reflect.TypeToken
import model.*
import model.choiceEntities.BackgroundChoiceEntity
import model.choiceEntities.ClassChoiceEntity
import model.choiceEntities.RaceChoiceEntity
import model.choiceEntities.SubraceChoiceEntity

class PostCharacterWorker : SyncWorker<CharacterEntity>(TypeToken.get(CharacterEntity::class.java)) {
    override suspend fun sync(it: CharacterEntity) {
        characterService.postCharacter(it)
    }
}

class DeleteCharacterWorker : SyncWorker<Int>(TypeToken.get(Int::class.java)) {
    override suspend fun sync(it: Int) {
        characterService.deleteCharacter(it)
    }
}



val stringIdPairToken = TypeToken.get(Pair("", 1)::class.java) as TypeToken<Pair<String, Int>>
val intIdPairToken = TypeToken.get(Pair(1, 1)::class.java) as TypeToken<Pair<Int, Int>>

class PostCharacterNameWorker : SyncWorker<Pair<String, Int>>(stringIdPairToken) {
    override suspend fun sync(it: Pair<String, Int>) {
        characterService.changeName(
            name = it.first,
            id = it.second
        )
    }
}

class PostCharacterTraitsWorker : SyncWorker<Pair<String, Int>>(stringIdPairToken) {
    override suspend fun sync(it: Pair<String, Int>) {
        characterService.setPersonalityTraits(
            it = it.first,
            id = it.second
        )
    }
}

class PostCharacterIdealsWorker : SyncWorker<Pair<String, Int>>(stringIdPairToken) {
    override suspend fun sync(it: Pair<String, Int>) {
        characterService.setIdeals(
            it = it.first,
            id = it.second
        )
    }
}


class PostCharacterBondsWorker : SyncWorker<Pair<String, Int>>(stringIdPairToken) {
    override suspend fun sync(it: Pair<String, Int>) {
        characterService.setBonds(
            it = it.first,
            id = it.second
        )
    }
}


class PostCharacterFlawsWorker : SyncWorker<Pair<String, Int>>(stringIdPairToken) {
    override suspend fun sync(it: Pair<String, Int>) {
        characterService.setFlaws(
            it = it.first,
            id = it.second
        )
    }
}

class PostCharacterNotesWorker : SyncWorker<Pair<String, Int>>(stringIdPairToken) {
    override suspend fun sync(it: Pair<String, Int>) {
        characterService.setNotes(
            it = it.first,
            id = it.second
        )
    }
}

class PostCharacterPactMagicStateEntityWorker : SyncWorker<PactMagicStateEntity>(TypeToken.get(PactMagicStateEntity::class.java)) {
    override suspend fun sync(it: PactMagicStateEntity) {
        characterService.insertPactMagicStateEntity(
            characterId = it.characterId,
            classId = it.classId,
            slotsCurrentAmount = it.slotsCurrentAmount
        )
    }
}

class PostCharacterSubraceCrossRefWorker : SyncWorker<CharacterSubraceCrossRef>(TypeToken.get(CharacterSubraceCrossRef::class.java)) {
    override suspend fun sync(it: CharacterSubraceCrossRef) {
        characterService.insertCharacterSubraceCrossRef(
            characterId = it.characterId,
            subraceId = it.subraceId,
        )
    }
}


class PostSubraceChoiceEntityWorker : SyncWorker<SubraceChoiceEntity>(TypeToken.get(SubraceChoiceEntity::class.java)) {
    override suspend fun sync(it: SubraceChoiceEntity) {
        characterService.insertSubraceChoiceEntity(
            subraceChoiceEntity = it
        )
    }
}

class PostCharacterSubclassCrossRefWorker : SyncWorker<CharacterSubclassCrossRef>(TypeToken.get(CharacterSubclassCrossRef::class.java)) {
    override suspend fun sync(it: CharacterSubclassCrossRef) {
        characterService.insertCharacterSubclassCrossRef(
            characterId = it.characterId,
            subClassId = it.subClassId,
            classId = it.classId,
        )
    }
}


class PostFeatureChoiceEntityWorker : SyncWorker<CharacterSubclassCrossRef>(TypeToken.get(CharacterSubclassCrossRef::class.java)) {
    override suspend fun sync(it: CharacterSubclassCrossRef) {
        characterService.insertCharacterSubclassCrossRef(
            characterId = it.characterId,
            subClassId = it.subClassId,
            classId = it.classId,
        )
    }
}


class PostCharacterClassSpellCrossRefWorker : SyncWorker<CharacterClassSpellCrossRef>(TypeToken.get(CharacterClassSpellCrossRef::class.java)) {
    override suspend fun sync(it: CharacterClassSpellCrossRef) {
        characterService.insertCharacterClassSpellCrossRef(
            classId = it.classId,
            spellId = it.spellId,
            characterId = it.characterId,
            prepared = it.isPrepared
        )
    }
}


class PostSubclassSpellCastingSpellCrossRefWorker : SyncWorker<SubclassSpellCastingSpellCrossRef>(TypeToken.get(SubclassSpellCastingSpellCrossRef::class.java)) {
    override suspend fun sync(it: SubclassSpellCastingSpellCrossRef) {
        characterService.insertSubClassSpellCastingCrossRef(
            spellId = it.spellId,
            characterId = it.characterId,
            prepared = it.isPrepared,
            subclassId =it.subclassId
        )
    }
}

class PostCharacterBackpackWorker : SyncWorker<CharacterEntity>(TypeToken.get(CharacterEntity::class.java)) {
    override suspend fun sync(it: CharacterEntity) {
        characterService.insertCharacterBackPack(
            backpack = it.backpack,
            id = it.id
        )
    }
}


class DeleteClassCharacterCrossRefWorker : SyncWorker<CharacterClassCrossRef>(TypeToken.get(CharacterClassCrossRef::class.java)) {
    override suspend fun sync(it: CharacterClassCrossRef) {
        characterService.insertCharacterClassCrossRef(
            characterId = it.characterId,
            classId = it.classId
        )
    }
}

class PostCharacterClassCrossRefWorker : SyncWorker<CharacterClassCrossRef>(TypeToken.get(CharacterClassCrossRef::class.java)) {
    override suspend fun sync(it: CharacterClassCrossRef) {
        characterService.insertCharacterClassCrossRef(
            characterId = it.characterId,
            classId = it.classId
        )
    }
}

class PostClassChoiceEntityWorker : SyncWorker<ClassChoiceEntity>(TypeToken.get(ClassChoiceEntity::class.java)) {
    override suspend fun sync(it: ClassChoiceEntity) {
        characterService.insertClassChoiceEntity(
            classChoiceEntity = it
        )
    }
}

class PostCharacterClassFeatCrossRefWorker : SyncWorker<ClassFeatCrossRef>(TypeToken.get(ClassFeatCrossRef::class.java)) {
    override suspend fun sync(it: ClassFeatCrossRef) {
        characterService.insertCharacterClassFeatCrossRef(
            characterId = it.characterId,
            featId = it.featId,
            classId = it.classId
        )
    }
}

class PostBackgroundChoiceEntityWorker : SyncWorker<BackgroundChoiceEntity>(TypeToken.get(BackgroundChoiceEntity::class.java)) {
    override suspend fun sync(it: BackgroundChoiceEntity) {
        characterService.insertBackgroundChoiceEntity(
            it
        )
    }
}

class PostRaceChoiceWorker : SyncWorker<RaceChoiceEntity>(TypeToken.get(RaceChoiceEntity::class.java)) {
    override suspend fun sync(it: RaceChoiceEntity) {
        characterService.insertRaceChoice(it)
    }
}

class PostCharacterRaceCrossRefWorker : SyncWorker<CharacterRaceCrossRef>(TypeToken.get(CharacterRaceCrossRef::class.java)) {
    override suspend fun sync(it: CharacterRaceCrossRef) {
        characterService.insertCharacterRaceCrossRef(
            id = it.id,
            raceId = it.raceId
        )
    }
}

class PostCharacterBackgroundCrossRefWorker : SyncWorker<CharacterBackgroundCrossRef>(TypeToken.get(CharacterBackgroundCrossRef::class.java)) {
    override suspend fun sync(it: CharacterBackgroundCrossRef) {
        characterService.insertCharacterBackgroundCrossRef(
            backgroundId = it.backgroundId,
            characterId = it.characterId
        )
    }
}

class PostCharacterTempWorker : SyncWorker<Pair<String, Int>>(stringIdPairToken) {
    override suspend fun sync(it: Pair<String, Int>) {
        characterService.setTemp(
            id = it.second,
            temp = it.first.toIntOrNull() ?: 0
        )
    }
}

class PostCharacterHealWorker : SyncWorker<Triple<Int, String, Int>>(TypeToken.get(Triple(1, "", 1)::class.java) as TypeToken<Triple<Int, String, Int>>) {
    override suspend fun sync(it: Triple<Int, String, Int>) {
        characterService.heal(
            id = it.first,
            hp = it.second.toIntOrNull() ?: 0,
            maxHp = it.third
        )
    }
}


class PostCharacterHpWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        characterService.setHp(
            id = it.first,
            hp = it.second
        )
    }
}

class PostCharacterDamageWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        characterService.damage(
            id = it.first,
            damage = it.second
        )
    }
}

class PostDeathSaveSuccessWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        characterService.updateDeathSaveSuccesses(
            id = it.first,
            it = it.second
        )
    }
}

class PostDeathSaveFailureWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        characterService.updateDeathSaveFailures(
            id = it.first,
            it = it.second
        )
    }
}

// Using characterEntity as a wrapper is a bit wasteful but for some reason gson causes issues with other wrappers.
// TODO: Consider creating custom wrappers to improve performance.
// Also present in PostBackpackWorker.
class PostSpellSlotsWorker : SyncWorker<CharacterEntity>(TypeToken.get(CharacterEntity::class.java)) {
    override suspend fun sync(it: CharacterEntity) {
        characterService.insertSpellSlots(
            spellSlots = it.spellSlots,
            id = it.id
        )
    }
}

class DeleteCharacterClassSpellCrossRefsWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        characterService.removeCharacterClassSpellCrossRefs(
            classId = it.first,
            characterId = it.second
        )
    }
}

class PostCharacterFeatureStateWorker : SyncWorker<CharacterFeatureState>(TypeToken.get(CharacterFeatureState::class.java)) {
    override suspend fun sync(it: CharacterFeatureState) {
        characterService.insertCharacterFeatureState(
            featureId = it.featureId,
            characterId = it.characterId,
            active = it.isActive
        )
    }
}

class DeleteFeatureFeatureChoiceWorker : SyncWorker<Pair<Int, Int>>(intIdPairToken) {
    override suspend fun sync(it: Pair<Int, Int>) {
        featureService.removeFeatureFeatureChoice(
            choiceId = it.first,
            characterId = it.second
        )
    }
}
