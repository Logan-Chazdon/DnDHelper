package gmail.loganchazdon.dndhelper.model.database.migrations

import android.annotation.SuppressLint
import android.content.ContentValues
import androidx.core.database.getStringOrNull
import androidx.room.OnConflictStrategy
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

//In version 58 we extracted many of the elements of a character into separate tables to allow for homebrew and take
//advantage of rooms relational abilities.
val MIGRATION_57_58 = object : Migration(57, 58) {
    lateinit var db: SupportSQLiteDatabase
    val gson = Gson()

    @SuppressLint("Range")
    override fun migrate(database: SupportSQLiteDatabase) {
        db = database

        //Remove old version of unused tables
        db.execSQL("DROP TABLE classes")
        db.execSQL("DROP TABLE races")

        //Create all new tables
        db.execSQL("CREATE TABLE IF NOT EXISTS `classes` (`name` TEXT NOT NULL, `hitDie` INTEGER NOT NULL, `subclassLevel` INTEGER NOT NULL, `proficiencyChoices` TEXT NOT NULL, `proficiencies` TEXT NOT NULL, `equipmentChoices` TEXT NOT NULL, `equipment` TEXT NOT NULL, `spellCasting` TEXT, `pactMagic` TEXT, `startingGoldD4s` INTEGER NOT NULL, `startingGoldMultiplier` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `isHomebrew` INTEGER NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `races` (`raceId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `raceName` TEXT NOT NULL, `groundSpeed` INTEGER NOT NULL, `abilityBonuses` TEXT, `alignment` TEXT, `age` TEXT NOT NULL, `size` TEXT NOT NULL, `sizeDesc` TEXT NOT NULL, `startingProficiencies` TEXT NOT NULL, `proficiencyChoices` TEXT NOT NULL, `languages` TEXT NOT NULL, `languageChoices` TEXT NOT NULL, `languageDesc` TEXT NOT NULL, `isHomebrew` INTEGER NOT NULL, `abcchoose` INTEGER, `abcfrom` TEXT, `abcmaxOccurrencesOfAbility` INTEGER, `abcchosenByString` TEXT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `features` (`featureId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `index` TEXT, `grantedAtLevel` INTEGER NOT NULL, `maxTimesChosen` INTEGER, `prerequisite` TEXT, `activationRequirement` TEXT NOT NULL, `speedBoost` TEXT, `spells` TEXT, `infusion` TEXT, `maxActive` TEXT NOT NULL, `hpBonusPerLevel` INTEGER, `armorContingentAcBonus` INTEGER, `acBonus` INTEGER, `ac` TEXT, `proficiencies` TEXT, `expertises` TEXT, `languages` TEXT, `extraAttackAndDamageRollStat` TEXT, `rangedAttackBonus` INTEGER)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_features_featureId` ON `features` (`featureId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `FeatureChoiceEntity` (`choose` TEXT NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `subclasses` (`subclass_name` TEXT NOT NULL, `spellAreFree` INTEGER NOT NULL, `subclass_spell_casting` TEXT, `subclassId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `backgrounds` (`name` TEXT NOT NULL, `desc` TEXT NOT NULL, `spells` TEXT, `proficiencies` TEXT NOT NULL, `proficiencyChoices` TEXT, `languages` TEXT NOT NULL, `languageChoices` TEXT, `equipment` TEXT NOT NULL, `equipmentChoices` TEXT NOT NULL, `isHomebrew` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `subraces` (`name` TEXT NOT NULL, `abilityBonuses` TEXT, `abilityBonusChoice` TEXT, `startingProficiencies` TEXT, `languages` TEXT NOT NULL, `languageChoices` TEXT NOT NULL, `size` TEXT, `groundSpeed` INTEGER, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `feats` (`name` TEXT NOT NULL, `desc` TEXT NOT NULL, `prerequisite` TEXT, `abilityBonuses` TEXT, `abilityBonusChoice` TEXT, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `featChoices` (`name` TEXT NOT NULL, `choose` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `FeatChoiceChoiceEntity` (`characterId` INTEGER NOT NULL, `choiceId` INTEGER NOT NULL, `featId` INTEGER NOT NULL, PRIMARY KEY(`characterId`, `choiceId`, `featId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `ClassChoiceEntity` (`characterId` INTEGER NOT NULL, `classId` INTEGER NOT NULL, `level` INTEGER NOT NULL, `isBaseClass` INTEGER NOT NULL, `totalNumOnGoldDie` INTEGER, `abilityImprovementsGranted` TEXT NOT NULL, `tookGold` INTEGER NOT NULL, `proficiencyChoicesByString` TEXT NOT NULL, PRIMARY KEY(`characterId`, `classId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `spells` (`name` TEXT NOT NULL, `level` INTEGER NOT NULL, `components` TEXT NOT NULL, `itemComponents` TEXT NOT NULL, `school` TEXT NOT NULL, `desc` TEXT NOT NULL, `range` TEXT NOT NULL, `area` TEXT NOT NULL, `castingTime` TEXT NOT NULL, `duration` TEXT NOT NULL, `classes` TEXT NOT NULL, `damage` TEXT NOT NULL, `isRitual` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `RaceFeatureCrossRef` (`featureId` INTEGER NOT NULL, `raceId` INTEGER NOT NULL, PRIMARY KEY(`raceId`, `featureId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `CharacterRaceCrossRef` (`raceId` INTEGER NOT NULL, `id` INTEGER NOT NULL, PRIMARY KEY(`id`, `raceId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `FeatureOptionsCrossRef` (`featureId` INTEGER NOT NULL, `id` INTEGER NOT NULL, PRIMARY KEY(`id`, `featureId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `SubraceFeatureCrossRef` (`subraceId` INTEGER NOT NULL, `featureId` INTEGER NOT NULL, PRIMARY KEY(`subraceId`, `featureId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `RaceSubraceCrossRef` (`subraceId` INTEGER NOT NULL, `raceId` INTEGER NOT NULL, PRIMARY KEY(`subraceId`, `raceId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `SubraceFeatChoiceCrossRef` (`subraceId` INTEGER NOT NULL, `featChoiceId` INTEGER NOT NULL, PRIMARY KEY(`subraceId`, `featChoiceId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `FeatChoiceFeatCrossRef` (`featChoiceId` INTEGER NOT NULL, `featId` INTEGER NOT NULL, PRIMARY KEY(`featChoiceId`, `featId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `BackgroundFeatureCrossRef` (`backgroundId` INTEGER NOT NULL, `featureId` INTEGER NOT NULL, PRIMARY KEY(`backgroundId`, `featureId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `CharacterClassCrossRef` (`characterId` INTEGER NOT NULL, `classId` INTEGER NOT NULL, PRIMARY KEY(`characterId`, `classId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `OptionsFeatureCrossRef` (`featureId` INTEGER NOT NULL, `choiceId` INTEGER NOT NULL, PRIMARY KEY(`featureId`, `choiceId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `ClassFeatureCrossRef` (`featureId` INTEGER NOT NULL, `id` INTEGER NOT NULL, PRIMARY KEY(`id`, `featureId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `CharacterSubclassCrossRef` (`subClassId` INTEGER NOT NULL, `characterId` INTEGER NOT NULL, `classId` INTEGER NOT NULL, PRIMARY KEY(`subClassId`, `characterId`, `classId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `CharacterBackgroundCrossRef` (`characterId` INTEGER NOT NULL, `backgroundId` INTEGER NOT NULL, PRIMARY KEY(`characterId`, `backgroundId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `CharacterSubraceCrossRef` (`subraceId` INTEGER NOT NULL, `characterId` INTEGER NOT NULL, PRIMARY KEY(`subraceId`, `characterId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `ClassFeatCrossRef` (`characterId` INTEGER NOT NULL, `classId` INTEGER NOT NULL, `featId` INTEGER NOT NULL, PRIMARY KEY(`characterId`, `classId`, `featId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `FeatureSpellCrossRef` (`spellId` INTEGER NOT NULL, `featureId` INTEGER NOT NULL, PRIMARY KEY(`spellId`, `featureId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `BackgroundSpellCrossRef` (`backgroundId` INTEGER NOT NULL, `spellId` INTEGER NOT NULL, PRIMARY KEY(`backgroundId`, `spellId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `SubclassSpellCrossRef` (`subclassId` INTEGER NOT NULL, `spellId` INTEGER NOT NULL, PRIMARY KEY(`subclassId`, `spellId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `ClassSpellCrossRef` (`classId` INTEGER NOT NULL, `spellId` INTEGER NOT NULL, PRIMARY KEY(`classId`, `spellId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `CharacterClassSpellCrossRef` (`characterId` INTEGER NOT NULL, `classId` INTEGER NOT NULL, `spellId` INTEGER NOT NULL, `isPrepared` INTEGER, PRIMARY KEY(`characterId`, `classId`, `spellId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `SubclassSpellCastingSpellCrossRef` (`subclassId` INTEGER NOT NULL, `spellId` INTEGER NOT NULL, `characterId` INTEGER NOT NULL, `isPrepared` INTEGER, PRIMARY KEY(`subclassId`, `spellId`, `characterId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `ClassSubclassCrossRef` (`classId` INTEGER NOT NULL, `subclassId` INTEGER NOT NULL, PRIMARY KEY(`classId`, `subclassId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `SubclassFeatureCrossRef` (`subclassId` INTEGER NOT NULL, `featureId` INTEGER NOT NULL, PRIMARY KEY(`subclassId`, `featureId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `RaceChoiceEntity` (`raceId` INTEGER NOT NULL, `characterId` INTEGER NOT NULL, `abcchosenByString` TEXT NOT NULL, `proficiencyChoice` TEXT NOT NULL, `languageChoice` TEXT NOT NULL, PRIMARY KEY(`raceId`, `characterId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `FeatureChoiceChoiceEntity` (`featureId` INTEGER NOT NULL, `characterId` INTEGER NOT NULL, `choiceId` INTEGER NOT NULL, PRIMARY KEY(`featureId`, `characterId`, `choiceId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `SubraceChoiceEntity` (`subraceId` INTEGER NOT NULL, `characterId` INTEGER NOT NULL, `languageChoice` TEXT NOT NULL, `abcchosenByString` TEXT NOT NULL, PRIMARY KEY(`subraceId`, `characterId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `BackgroundChoiceEntity` (`characterId` INTEGER NOT NULL, `backgroundId` INTEGER NOT NULL, `languageChoices` TEXT NOT NULL, PRIMARY KEY(`characterId`, `backgroundId`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `IndexRef` (`index` TEXT NOT NULL, `ids` TEXT NOT NULL, PRIMARY KEY(`index`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `FeatureChoiceIndexCrossRef` (`choiceId` INTEGER NOT NULL, `index` TEXT NOT NULL, `levels` TEXT, `classes` TEXT, `schools` TEXT, PRIMARY KEY(`choiceId`, `index`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `CharacterFeatureState` (`characterId` INTEGER NOT NULL, `featureId` INTEGER NOT NULL, `isActive` INTEGER NOT NULL, PRIMARY KEY(`characterId`, `featureId`))")

        //Migrate all characters
        val cursor = db.query("SELECT * FROM characters")
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))

                cursor.getStringOrNull(cursor.getColumnIndex("race"))?.let {
                    migrateRace(id, JSONObject(it))
                }

                cursor.getStringOrNull(cursor.getColumnIndex("background"))?.let {
                    migrateBackground(id, JSONObject(it))
                }

                cursor.getStringOrNull(cursor.getColumnIndex("classes"))?.let {
                    migrateClasses(id, JSONObject(it))
                }
            } while (cursor.moveToNext())
        }

        //Rename the old characters table
        db.execSQL("ALTER TABLE characters RENAME TO characters_old;")

        //Create a new characters table
        db.execSQL("CREATE TABLE IF NOT EXISTS `characters` (`name` TEXT NOT NULL, `personalityTraits` TEXT NOT NULL, `ideals` TEXT NOT NULL, `bonds` TEXT NOT NULL, `flaws` TEXT NOT NULL, `notes` TEXT NOT NULL, `currentHp` INTEGER NOT NULL, `tempHp` INTEGER NOT NULL, `conditions` TEXT NOT NULL, `resistances` TEXT NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `statGenerationMethodIndex` INTEGER NOT NULL, `baseStats` TEXT NOT NULL, `backpack` TEXT NOT NULL, `inspiration` INTEGER NOT NULL, `positiveDeathSaves` INTEGER NOT NULL, `negativeDeathSaves` INTEGER NOT NULL, `spellSlots` TEXT NOT NULL, `addedLanguages` TEXT NOT NULL, `addedProficiencies` TEXT NOT NULL)")

        //Copy all data into the new table
        db.execSQL("""INSERT INTO characters 
            (name, personalityTraits, ideals, bonds, flaws, notes, currentHp, tempHp, conditions, resistances, statGenerationMethodIndex, id, baseStats, backpack, 
inspiration, positiveDeathSaves, negativeDeathSaves, spellSlots, addedLanguages, addedProficiencies)
SELECT name, personalityTraits, ideals, bonds, flaws, notes, currentHp, tempHp, conditions, resistances, statGenerationMethodIndex, id, baseStats, backpack, 
inspiration, positiveDeathSaves, negativeDeathSaves, spellSlots, addedLanguages, addedProficiencies FROM characters_old
        """.trimIndent())

        //Delete the old table.
        db.execSQL("DROP TABLE characters_old")
    }

    private fun migrateClasses(id: Int, classes: JSONObject) {
        classes.keys().forEach { key ->
            val classObj = classes.getJSONObject(key)
            val classId = reserveSpaceForClassAndGetId(classObj.getString("name"))
            //Insert CharacterClassCrossRef
            db.execSQL(
                "INSERT INTO CharacterClassCrossRef (characterId, classId) VALUES ($id, $classId)"
            )

            val proficiencyChoices =
                convertProficiencyChoices(classObj.getJSONArray("proficiencyChoices"))

            classObj.optJSONObject("subclass")?.let { subclass ->
                migrateSubclass(id, classId, subclass)
            }

            classObj.optJSONArray("levelPath")?.let { features ->
                for(i in 0 until features.length()) {
                    val featureObj = features.getJSONObject(i)
                    val featureId = reserveSpaceForFeatureAndGetId(featureObj.getString("name"))
                    migrateFeature(id, featureObj, featureId)
                    db.execSQL("INSERT INTO ClassFeatureCrossRef (id, featureId) VALUES ($classId, $featureId)")
                }
            }

            //Insert a class choice object
            val values = ContentValues()
            values.put("characterId", id)
            values.put("classId", classId)
            values.put("level", 0)
            values.put("isBaseClass", false)
            values.put("totalNumOnGoldDie", 10)
            values.put("abilityImprovementsGranted", "[]")
            values.put("tookGold", false)
            values.put("proficiencyChoicesByString", proficiencyChoices)
            db.insert("ClassChoiceEntity", OnConflictStrategy.IGNORE, values)
        }
    }

    private fun migrateSubclass(id: Int, classId: Int, subclass: JSONObject) {
        val subclassId = reserveSpaceForSubclassAndGetId(subclass.getString("name"))

        subclass.optJSONArray("features")?.let { features ->
            for(i in 0 until features.length()) {
                val featureObj = features.getJSONObject(i)
                val featureId = reserveSpaceForFeatureAndGetId(featureObj.getString("name"))
                migrateFeature(id, featureObj, featureId)
                db.execSQL("INSERT INTO SubclassFeatureCrossRef (id, featureId) VALUES ($subclassId, $featureId)")
            }
        }


        db.execSQL(
            "INSERT INTO CharacterSubclassCrossRef (subClassId, characterId, classId) VALUES ($subclassId, $id, $classId)"
        )
    }

    private fun migrateBackground(id: Int, background: JSONObject) {
        val backgroundId = reserveSpaceForBackgroundAndGetId(background.getString("name"))


        //Insert a CharacterBackgroundCrossRef
        db.execSQL(
            "INSERT INTO CharacterBackgroundCrossRef (characterId, backgroundId) VALUES ($id, $backgroundId)"
        )

        val languageChoices = convertLanguageChoices(background.getJSONArray("languageChoices"))

        //Insert a backgroundChoice object
        val values = ContentValues()
        values.put("characterId", id)
        values.put("backgroundId", backgroundId)
        values.put("languageChoices", languageChoices)
        db.insert("BackgroundChoiceEntity", OnConflictStrategy.IGNORE, values)
    }

    private fun convertLanguageChoices(list: JSONArray): String {
        val languageChoices = mutableListOf<List<String>>()
        for (i in 0 until list.length()) {
            val choice = list.getJSONObject(i)
            val choices = choice.getJSONArray("chosen")
            val temp = mutableListOf<String>()
            for (j in 0 until choices.length()) {
                val obj = choices.getJSONObject(j)
                temp.add(obj.getString("name"))
            }
            languageChoices.add(temp)
        }
        return gson.toJson(languageChoices)
    }

    private fun convertProficiencyChoices(list: JSONArray): String {
        val proficiencyChoices = mutableListOf<List<String>>()
        for (i in 0 until list.length()) {
            val choice = list.getJSONObject(i)
            val choices = choice.getJSONArray("chosen")
            val temp = mutableListOf<String>()
            for (j in 0 until choices.length()) {
                val obj = choices.getJSONObject(j)
                temp.add(obj.getString("name"))
            }
            proficiencyChoices.add(temp)
        }
        return gson.toJson(proficiencyChoices)
    }

    private fun convertAbilityBonuses(json : JSONArray) : String {
        val result = mutableListOf<String>()
        for (i in 0 until json.length()) {
            val obj = json.getJSONObject(i)
            val bonus = obj.getString("bonus")
            val ability = obj.getJSONObject("ability")
            result.add("+$bonus $ability")
        }
        return gson.toJson(result)
    }

    private fun migrateRace(id: Int, race: JSONObject) {
        val raceId = reserveSpaceForRaceAndGetId(race.getString("name"))

        race.optJSONObject("subrace")?.let {
            migrateSubrace(id, it)
        }

        race.optJSONArray("traits")?.let { features ->
            for(i in 0 until features.length()) {
                val featureObj = features.getJSONObject(i)
                val featureId = reserveSpaceForFeatureAndGetId(featureObj.getString("name"))
                migrateFeature(id, featureObj, featureId)
                db.execSQL("INSERT INTO RaceFeatureCrossRef (id, featureId) VALUES ($raceId, $featureId)")
            }
        }

        //Insert a CharacterRaceCrossRef
        db.execSQL("INSERT INTO CharacterRaceCrossRef (id, raceId) VALUES ($id, $raceId)")

        //Extract the ability bonus choice and convert it so it can be stored in a race choice object.
        val abilityBonusChoice = race.optJSONObject("abilityBonusChoice")?.getJSONArray("chosen")?.let {
            convertAbilityBonuses(it)
        } ?: "[]"

        //Extract the proficiency choices and convert them so they can be stored in a race choice object.
        val proficiencyChoices = convertProficiencyChoices(race.getJSONArray("proficiencyChoices"))

        //Extract the language choice and convert it so it can be stored in a race choice object.
        val languageChoices = convertLanguageChoices(race.getJSONArray("languageChoices"))

        //Insert a RaceChoice object
        val values = ContentValues()
        values.put("characterId", id)
        values.put("raceId", raceId)
        values.put("abcchosenByString", abilityBonusChoice)
        values.put("proficiencyChoice", proficiencyChoices)
        values.put("languageChoice", languageChoices)
        db.insert("RaceChoiceEntity", OnConflictStrategy.IGNORE, values)
    }

    private fun migrateSubrace(id: Int, subrace: JSONObject) {
        val subraceId = reserveSpaceForSubraceAndGetId(subrace.getString("name"))

        subrace.optJSONArray("featChoices")?.let {
            for(i in 0 until it.length()) {
                val obj = it.getJSONObject(i)
                val choiceId = reserveSpaceForFeatChoiceAndGetId(obj.getString("name"))
                db.execSQL("INSERT INTO SubraceFeatChoiceCrossRef (subraceId, featChoiceId) VALUES($subraceId, $choiceId)")
                migrateFeatChoice(id, choiceId, obj)
            }
        }


        subrace.optJSONArray("traits")?.let { features ->
            for(i in 0 until features.length()) {
                val featureObj = features.getJSONObject(i)
                val featureId = reserveSpaceForFeatureAndGetId(featureObj.getString("name"))
                migrateFeature(id, featureObj, featureId)
                db.execSQL("INSERT INTO SubraceFeatureCrossRef (id, featureId) VALUES ($subraceId, $featureId)")
            }
        }


        db.execSQL("INSERT INTO CharacterSubraceCrossRef (subraceId, characterId) VALUES($id, $subraceId) ")

        val languageChoice = subrace.optJSONArray("languageChoice")?.let {
            convertLanguageChoices(it)
        } ?: "[]"

        val abilityBonusChoice = subrace.optJSONObject("abilityBonusChoice")?.getJSONArray("chosen")?.let {
            convertAbilityBonuses(it)
        } ?: "[]"

        val values = ContentValues()
        values.put("subraceId", subraceId)
        values.put("characterId", id)
        values.put("languageChoice", languageChoice)
        values.put("abcchosenByString", abilityBonusChoice)

        db.insert("SubraceChoiceEntity", OnConflictStrategy.IGNORE, values)
    }

    private fun migrateFeatChoice(characterId : Int,choiceId : Int, obj: JSONObject) {
        obj.optJSONArray("chosen")?.let {
            for(i in 0 until it.length()) {
                val feat = it.getJSONObject(i)
                val featId = reserveSpaceForFeatAndGetId(feat.getString("name"))
                db.execSQL("INSERT INTO FeatChoiceChoiceEntity(characterId, choiceId, featId) VALUES($characterId, $choiceId, $featId)")
                migrateFeat(characterId, feat)
            }
        }
    }

    private fun migrateFeat(characterId: Int, feat: JSONObject) {
        feat.optJSONArray("features")?.let {
            for(i in 0 until it.length()) {
                val obj =it.getJSONObject(i)
                val featureId = reserveSpaceForFeatureAndGetId(obj.getString("name"))
                migrateFeature(characterId, obj, featureId)
                //db.execSQL("INSERT INTO ") FeatFeatureCrossRef
            }
        }
    }


    private fun migrateFeature(characterId: Int, feature: JSONObject, featureId: Int) {
        feature.optJSONArray("choices")?.let { choices ->
            for(i in 0 until choices.length()) {
                val obj = choices.getJSONObject(i)
                val featureChoiceId = reserveSpaceForFeatureChoiceAndGetId()
                migrateFeatureChoice(characterId, obj, featureChoiceId)
                db.execSQL("INSERT INTO FeatureChoiceChoiceEntity (featureId, choiceId, characterId) VALUES($featureId, $featureChoiceId, $characterId)")
            }
        }
    }


    private fun migrateFeatureChoice(characterId: Int, choice: JSONObject, choiceId: Int) {
        choice.optJSONArray("chosen")?.let { chosen ->
            for(i in 0 until chosen.length()) {
                val obj = chosen.getJSONObject(i)
                val featureId = reserveSpaceForFeatureAndGetId(obj.getString("name"))
                migrateFeature(characterId, obj, featureId)
                db.execSQL("INSERT INTO FeatureChoiceChoiceEntity (featureId, characterId, choiceId) VALUES ($featureId, $characterId, $choiceId)")
            }
        }
    }

    private fun reserveSpaceForFeatChoiceAndGetId(name: String): Int {
        val values = ContentValues()
        values.put("name", name)
        db.insert("FeatChoiceEntity", OnConflictStrategy.IGNORE, values)
        val cursor =db.query("SELECT last_insert_rowid()")
        cursor.moveToFirst()
        return cursor.getInt(0)
    }

    private fun reserveSpaceForSubraceAndGetId(name: String): Int {
        val values = ContentValues()
        values.put("name", name)
        values.put("languages", "[]")
        values.put("languageChoices", "[]")
        db.insert("subraces", OnConflictStrategy.IGNORE, values)
        val cursor =db.query("SELECT last_insert_rowid()")
        cursor.moveToFirst()
        return cursor.getInt(0)
    }


    private fun reserveSpaceForClassAndGetId(name: String): Int {
        val values = ContentValues()
        values.put("name", name)
        values.put("hitDie", 8)
        values.put("proficiencyChoices", "[]")
        values.put("proficiencies", "[]")
        values.put("equipment", "[]")
        values.put("equipmentChoices", "[]")
        values.put("startingGoldD4s", 10)
        values.put("startingGoldMultiplier", 10)
        values.put("subclassLevel", 1)
        values.put("isHomebrew", false)
        db.insert("classes", OnConflictStrategy.IGNORE, values)
        val cursor =db.query("SELECT last_insert_rowid()")
        cursor.moveToFirst()
        return cursor.getInt(0)
    }

    private fun reserveSpaceForRaceAndGetId(name: String): Int {
        val values = ContentValues()
        values.put("raceName", name)
        values.put("groundSpeed", 30)
        values.put("age", "")
        values.put("size", "")
        values.put("sizeDesc", "")
        values.put("startingProficiencies", "[]")
        values.put("proficiencyChoices", "[]")
        values.put("languages", "[]")
        values.put("languageChoices", "[]")
        values.put("languageDesc", "")
        values.put("isHomebrew", false)
        db.insert("races", OnConflictStrategy.IGNORE, values)
        val cursor =db.query("SELECT last_insert_rowid()")
        cursor.moveToFirst()
        return cursor.getInt(0)
    }

    private fun reserveSpaceForBackgroundAndGetId(name: String): Int {
        val values = ContentValues()
        values.put("name", name)
        values.put("desc", "")
        values.put("proficiencies", "[]")
        values.put("languages", "[]")
        values.put("equipment", "[]")
        values.put("equipmentChoices", "[]")
        values.put("isHomebrew", false)
        db.insert("backgrounds", OnConflictStrategy.IGNORE, values)
        val cursor =db.query("SELECT last_insert_rowid()")
        cursor.moveToFirst()
        return cursor.getInt(0)
    }

    private fun reserveSpaceForSubclassAndGetId(name: String): Int {
        val values = ContentValues()
        values.put("name", name)
        db.insert("subclasses", OnConflictStrategy.IGNORE, values)
        val cursor =db.query("SELECT last_insert_rowid()")
        cursor.moveToFirst()
        return cursor.getInt(0)
    }

    private fun reserveSpaceForFeatAndGetId(name: String): Int {
        val values = ContentValues()
        values.put("name", name)
        db.insert("feats", OnConflictStrategy.IGNORE, values)
        val cursor =db.query("SELECT last_insert_rowid()")
        cursor.moveToFirst()
        return cursor.getInt(0)
    }

    private fun reserveSpaceForFeatureAndGetId(name: String): Int {
        val values = ContentValues()
        values.put("name", name)
        values.put("description", "")
        values.put("grantedAtLevel", 1)
        values.put("activationRequirement", "[]")
        values.put("maxActive", "[]")
        db.insert("features", OnConflictStrategy.IGNORE, values)
        val cursor =db.query("SELECT last_insert_rowid()")
        cursor.moveToFirst()
        return cursor.getInt(0)
    }

    private fun reserveSpaceForFeatureChoiceAndGetId(): Int {
        val values = ContentValues()
        values.put("choose", "[]")
        db.insert("FeatureChoiceEntity", OnConflictStrategy.IGNORE, values)
        val cursor =db.query("SELECT last_insert_rowid()")
        cursor.moveToFirst()
        return cursor.getInt(0)
    }
}