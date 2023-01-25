package gmail.loganchazdon.dndhelper.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import gmail.loganchazdon.dndhelper.model.*
import gmail.loganchazdon.dndhelper.model.choiceEntities.*
import gmail.loganchazdon.dndhelper.model.database.daos.*
import gmail.loganchazdon.dndhelper.model.junctionEntities.*


@Database(
    entities = [
        CharacterEntity::class, ClassEntity::class, RaceEntity::class, FeatureEntity::class, FeatureChoiceEntity::class, SubclassEntity::class, BackgroundEntity::class,
        SubraceEntity::class, FeatEntity::class, FeatChoiceEntity::class, FeatChoiceChoiceEntity::class, ClassChoiceEntity::class, Spell::class,
        RaceFeatureCrossRef::class, CharacterRaceCrossRef::class, FeatureOptionsCrossRef::class, SubraceFeatureCrossRef::class, RaceSubraceCrossRef::class,
        SubraceFeatChoiceCrossRef::class, FeatChoiceFeatCrossRef::class, BackgroundFeatureCrossRef::class, CharacterClassCrossRef::class,
        OptionsFeatureCrossRef::class, ClassFeatureCrossRef::class, CharacterSubclassCrossRef::class, CharacterBackgroundCrossRef::class, CharacterSubraceCrossRef::class,
        ClassFeatCrossRef::class, FeatureSpellCrossRef::class, BackgroundSpellCrossRef::class, SubclassSpellCrossRef::class, ClassSpellCrossRef::class,
        CharacterClassSpellCrossRef::class, SubclassSpellCastingSpellCrossRef::class, ClassSubclassCrossRef::class, SubclassFeatureCrossRef::class,
        RaceChoiceEntity::class, FeatureChoiceChoiceEntity::class, SubraceChoiceEntity::class, BackgroundChoiceEntity::class
    ],
    version = 58,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class RoomDataBase: RoomDatabase() {
    abstract fun backgroundDao() : BackgroundDao
    abstract fun characterDao() : CharacterDao
    abstract fun classDao() : ClassDao
    abstract fun featDao() : FeatDao
    abstract fun featureDao() : FeatureDao
    abstract fun raceDao() : RaceDao
    abstract fun spellDao() : SpellDao
    abstract fun subclassDao() : SubclassDao
    abstract fun subraceDao() : SubraceDao
}