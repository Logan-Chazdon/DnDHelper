package gmail.loganchazdon.dndhelper.model.database.migrations

import android.annotation.SuppressLint
import android.content.ContentValues
import androidx.core.database.getStringOrNull
import androidx.room.OnConflictStrategy
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


//In version 58 we extracted many of the elements of a character into separate tables to allow for homebrew and take
//advantage of rooms relational abilities.
val MIGRATION_57_58 = object : Migration(57, 58) {
    lateinit var db: SupportSQLiteDatabase
    val gson = Gson()
    lateinit var classesJson: JSONArray
    lateinit var backgroundsJson: JSONArray
    lateinit var racesJson: JSONArray
    lateinit var featsJson: JSONArray
    lateinit var infusionsJson: JSONArray
    lateinit var invocationsJson: JSONArray
    lateinit var spellsJson: JSONArray
    lateinit var maneuversJson: JSONArray
    lateinit var fightingStylesJson: JSONArray
    lateinit var metamagicJson: JSONArray
    lateinit var languagesJson: JSONArray
    lateinit var artisansToolsJson: JSONArray
    val proficienciesJson = JSONArray()
    val expertiseJson = JSONArray()

    private fun getJsonArrayFromFile(filePath: String, arrayName: String): JSONArray {
        val reader = this.javaClass.classLoader!!.getResourceAsStream(filePath).bufferedReader()
        return JSONObject(reader.readText()).getJSONArray(arrayName)
    }

    private fun fillOutProficienciesAndExpertise() {
        var i = 0
        val abilitiesJson = getJsonArrayFromFile("res/raw/skills.json", "baseStats")
        for (abilityIndex in 0 until abilitiesJson.length()) {
            val statJson = abilitiesJson.getJSONObject(abilityIndex)
            val skillsJson = statJson.getJSONArray("skills")
            for (skillIndex in 0 until skillsJson.length()) {
                val skillJson = skillsJson.getJSONObject(skillIndex)
                val name = skillJson.getString("name")
                proficienciesJson.put("{name: ${name}, id : ${i++}}")
                expertiseJson.put("{name: ${name}, id : ${i++}}")
            }
        }
    }

    @SuppressLint("Range")
    override fun migrate(database: SupportSQLiteDatabase) {
        db = database

        classesJson = getJsonArrayFromFile("res/raw/classes.json", "classes")
        backgroundsJson = getJsonArrayFromFile("res/raw/backgrounds.json", "backgrounds")
        racesJson = getJsonArrayFromFile("res/raw/races.json", "races")
        featsJson = getJsonArrayFromFile("res/raw/feats.json", "feats")
        infusionsJson = getJsonArrayFromFile("res/raw/infusions.json", "infusions")
        invocationsJson = getJsonArrayFromFile("res/raw/invocations.json", "invocations")
        spellsJson = getJsonArrayFromFile("res/raw/spells.json", "spells")
        maneuversJson = getJsonArrayFromFile("res/raw/maneuvers.json", "maneuvers")
        fightingStylesJson = getJsonArrayFromFile("res/raw/fighting_styles.json", "fighting_styles")
        metamagicJson = getJsonArrayFromFile("res/raw/metamagic.json", "metamagic")
        languagesJson = getJsonArrayFromFile("res/raw/languages.json", "languages")
        artisansToolsJson =
            getJsonArrayFromFile("res/raw/item_proficiencies.json", "artisans_tools")
        fillOutProficienciesAndExpertise()

        //Remove old version of unused tables
        db.execSQL("DROP TABLE classes")
        db.execSQL("DROP TABLE races")

        //Create all new tables
        db.execSQL("CREATE TABLE IF NOT EXISTS `classes` (`name` TEXT NOT NULL, `hitDie` INTEGER NOT NULL, `subclassLevel` INTEGER NOT NULL, `proficiencyChoices` TEXT NOT NULL, `proficiencies` TEXT NOT NULL, `equipmentChoices` TEXT NOT NULL, `equipment` TEXT NOT NULL, `spellCasting` TEXT, `pactMagic` TEXT, `startingGoldD4s` INTEGER NOT NULL, `startingGoldMultiplier` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `isHomebrew` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `races` (`raceId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `raceName` TEXT NOT NULL, `groundSpeed` INTEGER NOT NULL, `abilityBonuses` TEXT, `alignment` TEXT, `age` TEXT NOT NULL, `size` TEXT NOT NULL, `sizeDesc` TEXT NOT NULL, `startingProficiencies` TEXT NOT NULL, `proficiencyChoices` TEXT NOT NULL, `languages` TEXT NOT NULL, `languageChoices` TEXT NOT NULL, `languageDesc` TEXT NOT NULL, `isHomebrew` INTEGER NOT NULL, `abcchoose` INTEGER, `abcfrom` TEXT, `abcmaxOccurrencesOfAbility` INTEGER, `abcchosenByString` TEXT)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `features` (`featureId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `index` TEXT, `grantedAtLevel` INTEGER NOT NULL, `maxTimesChosen` INTEGER, `prerequisite` TEXT, `activationRequirement` TEXT NOT NULL, `speedBoost` TEXT, `spells` TEXT, `infusion` TEXT, `maxActive` TEXT NOT NULL, `hpBonusPerLevel` INTEGER, `armorContingentAcBonus` INTEGER, `acBonus` INTEGER, `ac` TEXT, `proficiencies` TEXT, `expertises` TEXT, `languages` TEXT, `extraAttackAndDamageRollStat` TEXT, `rangedAttackBonus` INTEGER)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_features_featureId` ON `features` (`featureId`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `FeatureChoiceEntity` (`choose` TEXT NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `subclasses` (`subclass_name` TEXT NOT NULL, `spellAreFree` INTEGER NOT NULL, `subclass_spell_casting` TEXT, `subclass_isHomebrew` INTEGER NOT NULL, `subclassId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `backgrounds` (`name` TEXT NOT NULL, `desc` TEXT NOT NULL, `spells` TEXT, `proficiencies` TEXT NOT NULL, `proficiencyChoices` TEXT, `languages` TEXT NOT NULL, `languageChoices` TEXT, `equipment` TEXT NOT NULL, `equipmentChoices` TEXT NOT NULL, `isHomebrew` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `subraces` (`name` TEXT NOT NULL, `abilityBonuses` TEXT, `abilityBonusChoice` TEXT, `startingProficiencies` TEXT, `languages` TEXT NOT NULL, `languageChoices` TEXT NOT NULL, `size` TEXT, `groundSpeed` INTEGER, `isHomebrew` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `feats` (`name` TEXT NOT NULL, `desc` TEXT NOT NULL, `prerequisite` TEXT, `abilityBonuses` TEXT, `abilityBonusChoice` TEXT, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `featChoices` (`name` TEXT NOT NULL, `choose` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `FeatChoiceChoiceEntity` (`characterId` INTEGER NOT NULL, `choiceId` INTEGER NOT NULL, `featId` INTEGER NOT NULL, PRIMARY KEY(`characterId`, `choiceId`, `featId`), FOREIGN KEY(`characterId`) REFERENCES `characters`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`choiceId`) REFERENCES `featChoices`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`featId`) REFERENCES `feats`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `ClassChoiceEntity` (`characterId` INTEGER NOT NULL, `classId` INTEGER NOT NULL, `level` INTEGER NOT NULL, `isBaseClass` INTEGER NOT NULL, `totalNumOnGoldDie` INTEGER, `abilityImprovementsGranted` TEXT NOT NULL, `tookGold` INTEGER NOT NULL, `proficiencyChoicesByString` TEXT NOT NULL, PRIMARY KEY(`characterId`, `classId`), FOREIGN KEY(`characterId`) REFERENCES `characters`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`classId`) REFERENCES `classes`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `spells` (`name` TEXT NOT NULL, `level` INTEGER NOT NULL, `components` TEXT NOT NULL, `itemComponents` TEXT NOT NULL, `school` TEXT NOT NULL, `desc` TEXT NOT NULL, `range` TEXT NOT NULL, `area` TEXT NOT NULL, `castingTime` TEXT NOT NULL, `duration` TEXT NOT NULL, `classes` TEXT NOT NULL, `damage` TEXT NOT NULL, `isRitual` INTEGER NOT NULL, `isHomebrew` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `RaceFeatureCrossRef` (`featureId` INTEGER NOT NULL, `raceId` INTEGER NOT NULL, PRIMARY KEY(`featureId`, `raceId`), FOREIGN KEY(`featureId`) REFERENCES `features`(`featureId`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(`raceId`) REFERENCES `races`(`raceId`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `CharacterRaceCrossRef` (`raceId` INTEGER NOT NULL, `id` INTEGER NOT NULL, PRIMARY KEY(`raceId`, `id`), FOREIGN KEY(`raceId`) REFERENCES `races`(`raceId`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`id`) REFERENCES `characters`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `FeatureOptionsCrossRef` (`featureId` INTEGER NOT NULL, `id` INTEGER NOT NULL, PRIMARY KEY(`id`, `featureId`), FOREIGN KEY(`id`) REFERENCES `FeatureChoiceEntity`(`id`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(`featureId`) REFERENCES `features`(`featureId`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `SubraceFeatureCrossRef` (`subraceId` INTEGER NOT NULL, `featureId` INTEGER NOT NULL, PRIMARY KEY(`subraceId`, `featureId`), FOREIGN KEY(`subraceId`) REFERENCES `subraces`(`id`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(`featureId`) REFERENCES `features`(`featureId`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `RaceSubraceCrossRef` (`subraceId` INTEGER NOT NULL, `raceId` INTEGER NOT NULL, PRIMARY KEY(`subraceId`, `raceId`), FOREIGN KEY(`subraceId`) REFERENCES `subraces`(`id`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(`raceId`) REFERENCES `races`(`raceId`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `SubraceFeatChoiceCrossRef` (`subraceId` INTEGER NOT NULL, `featChoiceId` INTEGER NOT NULL, PRIMARY KEY(`subraceId`, `featChoiceId`), FOREIGN KEY(`subraceId`) REFERENCES `subraces`(`id`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(`featChoiceId`) REFERENCES `featChoices`(`id`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `FeatChoiceFeatCrossRef` (`featChoiceId` INTEGER NOT NULL, `featId` INTEGER NOT NULL, PRIMARY KEY(`featChoiceId`, `featId`), FOREIGN KEY(`featChoiceId`) REFERENCES `featChoices`(`id`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(`featId`) REFERENCES `feats`(`id`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `BackgroundFeatureCrossRef` (`backgroundId` INTEGER NOT NULL, `featureId` INTEGER NOT NULL, PRIMARY KEY(`backgroundId`, `featureId`), FOREIGN KEY(`backgroundId`) REFERENCES `backgrounds`(`id`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(`featureId`) REFERENCES `features`(`featureId`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `CharacterClassCrossRef` (`characterId` INTEGER NOT NULL, `classId` INTEGER NOT NULL, PRIMARY KEY(`characterId`, `classId`), FOREIGN KEY(`characterId`) REFERENCES `characters`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`classId`) REFERENCES `classes`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )");
        db.execSQL("CREATE TABLE IF NOT EXISTS `OptionsFeatureCrossRef` (`featureId` INTEGER NOT NULL, `choiceId` INTEGER NOT NULL, PRIMARY KEY(`featureId`, `choiceId`), FOREIGN KEY(`featureId`) REFERENCES `features`(`featureId`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(`choiceId`) REFERENCES `FeatureChoiceEntity`(`id`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `ClassFeatureCrossRef` (`featureId` INTEGER NOT NULL, `id` INTEGER NOT NULL, PRIMARY KEY(`featureId`, `id`), FOREIGN KEY(`featureId`) REFERENCES `features`(`featureId`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(`id`) REFERENCES `classes`(`id`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `CharacterSubclassCrossRef` (`subClassId` INTEGER NOT NULL, `characterId` INTEGER NOT NULL, `classId` INTEGER NOT NULL, PRIMARY KEY(`subClassId`, `characterId`, `classId`), FOREIGN KEY(`subClassId`) REFERENCES `subclasses`(`subclassId`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`characterId`) REFERENCES `characters`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`classId`) REFERENCES `classes`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE TABLE IF NOT EXISTS `CharacterBackgroundCrossRef` (`characterId` INTEGER NOT NULL, `backgroundId` INTEGER NOT NULL, PRIMARY KEY(`characterId`, `backgroundId`), FOREIGN KEY(`characterId`) REFERENCES `characters`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`backgroundId`) REFERENCES `backgrounds`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE TABLE IF NOT EXISTS `CharacterSubraceCrossRef` (`subraceId` INTEGER NOT NULL, `characterId` INTEGER NOT NULL, PRIMARY KEY(`subraceId`, `characterId`), FOREIGN KEY(`subraceId`) REFERENCES `subraces`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`characterId`) REFERENCES `characters`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE TABLE IF NOT EXISTS `ClassFeatCrossRef` (`characterId` INTEGER NOT NULL, `classId` INTEGER NOT NULL, `featId` INTEGER NOT NULL, PRIMARY KEY(`characterId`, `classId`, `featId`), FOREIGN KEY(`characterId`) REFERENCES `characters`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`classId`) REFERENCES `classes`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`featId`) REFERENCES `feats`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE TABLE IF NOT EXISTS `FeatureSpellCrossRef` (`spellId` INTEGER NOT NULL, `featureId` INTEGER NOT NULL, PRIMARY KEY(`spellId`, `featureId`), FOREIGN KEY(`spellId`) REFERENCES `spells`(`id`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(`featureId`) REFERENCES `features`(`featureId`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `BackgroundSpellCrossRef` (`backgroundId` INTEGER NOT NULL, `spellId` INTEGER NOT NULL, PRIMARY KEY(`backgroundId`, `spellId`), FOREIGN KEY(`backgroundId`) REFERENCES `backgrounds`(`id`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(`spellId`) REFERENCES `spells`(`id`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `SubclassSpellCrossRef` (`subclassId` INTEGER NOT NULL, `spellId` INTEGER NOT NULL, PRIMARY KEY(`subclassId`, `spellId`), FOREIGN KEY(`subclassId`) REFERENCES `subclasses`(`subclassId`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(`spellId`) REFERENCES `spells`(`id`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `ClassSpellCrossRef` (`classId` INTEGER NOT NULL, `spellId` INTEGER NOT NULL, PRIMARY KEY(`classId`, `spellId`), FOREIGN KEY(`classId`) REFERENCES `classes`(`id`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(`spellId`) REFERENCES `spells`(`id`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `CharacterClassSpellCrossRef` (`characterId` INTEGER NOT NULL, `classId` INTEGER NOT NULL, `spellId` INTEGER NOT NULL, `isPrepared` INTEGER, PRIMARY KEY(`characterId`, `classId`, `spellId`), FOREIGN KEY(`characterId`) REFERENCES `characters`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`classId`) REFERENCES `classes`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`spellId`) REFERENCES `spells`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE TABLE IF NOT EXISTS `SubclassSpellCastingSpellCrossRef` (`subclassId` INTEGER NOT NULL, `spellId` INTEGER NOT NULL, `characterId` INTEGER NOT NULL, `isPrepared` INTEGER, PRIMARY KEY(`subclassId`, `spellId`, `characterId`), FOREIGN KEY(`subclassId`) REFERENCES `subclasses`(`subclassId`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`spellId`) REFERENCES `spells`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`characterId`) REFERENCES `characters`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE TABLE IF NOT EXISTS `ClassSubclassCrossRef` (`classId` INTEGER NOT NULL, `subclassId` INTEGER NOT NULL, PRIMARY KEY(`classId`, `subclassId`), FOREIGN KEY(`classId`) REFERENCES `classes`(`id`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(`subclassId`) REFERENCES `subclasses`(`subclassId`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `SubclassFeatureCrossRef` (`subclassId` INTEGER NOT NULL, `featureId` INTEGER NOT NULL, PRIMARY KEY(`subclassId`, `featureId`), FOREIGN KEY(`subclassId`) REFERENCES `subclasses`(`subclassId`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(`featureId`) REFERENCES `features`(`featureId`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `RaceChoiceEntity` (`raceId` INTEGER NOT NULL, `characterId` INTEGER NOT NULL, `abcchosenByString` TEXT NOT NULL, `proficiencyChoice` TEXT NOT NULL, `languageChoice` TEXT NOT NULL, `abilityBonusOverrides` TEXT, PRIMARY KEY(`raceId`, `characterId`), FOREIGN KEY(`raceId`) REFERENCES `races`(`raceId`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`characterId`) REFERENCES `characters`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE TABLE IF NOT EXISTS `FeatureChoiceChoiceEntity` (`featureId` INTEGER NOT NULL, `characterId` INTEGER NOT NULL, `choiceId` INTEGER NOT NULL, PRIMARY KEY(`featureId`, `characterId`, `choiceId`), FOREIGN KEY(`featureId`) REFERENCES `features`(`featureId`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`characterId`) REFERENCES `characters`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`choiceId`) REFERENCES `FeatureChoiceEntity`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE TABLE IF NOT EXISTS `SubraceChoiceEntity` (`subraceId` INTEGER NOT NULL, `characterId` INTEGER NOT NULL, `languageChoice` TEXT NOT NULL, `abcchosenByString` TEXT NOT NULL, `abilityBonusOverrides` TEXT, PRIMARY KEY(`subraceId`, `characterId`), FOREIGN KEY(`subraceId`) REFERENCES `subraces`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`characterId`) REFERENCES `characters`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE TABLE IF NOT EXISTS `BackgroundChoiceEntity` (`characterId` INTEGER NOT NULL, `backgroundId` INTEGER NOT NULL, `languageChoices` TEXT NOT NULL, PRIMARY KEY(`characterId`, `backgroundId`), FOREIGN KEY(`characterId`) REFERENCES `characters`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`characterId`) REFERENCES `backgrounds`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE TABLE IF NOT EXISTS `IndexRef` (`index` TEXT NOT NULL, `ids` TEXT NOT NULL, PRIMARY KEY(`index`))")
        db.execSQL("CREATE TABLE IF NOT EXISTS `FeatureChoiceIndexCrossRef` (`choiceId` INTEGER NOT NULL, `index` TEXT NOT NULL, `levels` TEXT, `classes` TEXT, `schools` TEXT, PRIMARY KEY(`choiceId`, `index`), FOREIGN KEY(`choiceId`) REFERENCES `FeatureChoiceEntity`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE TABLE IF NOT EXISTS `CharacterFeatureState` (`characterId` INTEGER NOT NULL, `featureId` INTEGER NOT NULL, `isActive` INTEGER NOT NULL, PRIMARY KEY(`characterId`, `featureId`), FOREIGN KEY(`characterId`) REFERENCES `characters`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`featureId`) REFERENCES `features`(`featureId`) ON UPDATE CASCADE ON DELETE CASCADE )")
        db.execSQL("CREATE TABLE IF NOT EXISTS `FeatFeatureCrossRef` (`featId` INTEGER NOT NULL, `featureId` INTEGER NOT NULL, PRIMARY KEY(`featureId`, `featId`), FOREIGN KEY(`featureId`) REFERENCES `features`(`featureId`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(`featId`) REFERENCES `feats`(`id`) ON UPDATE CASCADE ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `CharacterFeatureState` (`characterId` INTEGER NOT NULL, `featureId` INTEGER NOT NULL, `isActive` INTEGER NOT NULL, PRIMARY KEY(`characterId`, `featureId`), FOREIGN KEY(`characterId`) REFERENCES `characters`(`id`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`featureId`) REFERENCES `features`(`featureId`) ON UPDATE CASCADE ON DELETE CASCADE )")

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
        db.execSQL(
            """INSERT INTO characters 
            (name, personalityTraits, ideals, bonds, flaws, notes, currentHp, tempHp, conditions, resistances, statGenerationMethodIndex, id, baseStats, backpack, 
inspiration, positiveDeathSaves, negativeDeathSaves, spellSlots, addedLanguages, addedProficiencies)
SELECT name, personalityTraits, ideals, bonds, flaws, notes, currentHp, tempHp, conditions, resistances, statGenerationMethodIndex, id, baseStats, backpack, 
inspiration, positiveDeathSaves, negativeDeathSaves, spellSlots, addedLanguages, addedProficiencies FROM characters_old
        """.trimIndent()
        )

        //Delete the old table.
        db.execSQL("DROP TABLE characters_old")
    }

    private fun getIdByNameInList(name: String, list: JSONArray): Int? {
        for (i in 0 until list.length()) {
            val item = list.getJSONObject(i)
            if (item.optString("name") == name) {
                return item.getInt("id")
            }
        }
        return null
    }

    private fun getClassId(name: String): Int {
        for (i in 0 until classesJson.length()) {
            val clazz = classesJson.getJSONObject(i)
            if (clazz.getString("name") == name) {
                return i + 1
            }
        }
        throw Exception("Name not found")
    }

    private fun getBackgroundId(name: String): Int {
        for (i in 0 until backgroundsJson.length()) {
            val background = backgroundsJson.getJSONObject(i)
            if (background.getString("name") == name) {
                return i + 1
            }
        }
        throw Exception("Name not found")
    }

    private fun getRaceId(name: String): Int {
        for (i in 0 until racesJson.length()) {
            val races = racesJson.getJSONObject(i)
            if (races.getString("name") == name) {
                return i + 1
            }
        }
        throw Exception("Name not found")
    }

    private fun getSubraceId(name: String, raceId: Int): Int {
        val subracesJson = racesJson.getJSONObject(raceId - 1).getJSONArray("subraces")

        for (i in 0 until subracesJson.length()) {
            val subrace = subracesJson.getJSONObject(i)
            if (subrace.getString("name") == name) {
                return subrace.getInt("id")
            }
        }
        throw Exception("Name not found")
    }

    private fun getClassFeatureList(name: String): JSONArray {
        for (i in 0 until classesJson.length()) {
            val clazz = classesJson.getJSONObject(i)
            if (clazz.getString("name") == name) {
                return clazz.getJSONArray("features")
            }
        }
        throw Exception("Name not found")
    }

    private fun getRaceFeatures(name: String): JSONArray {
        for (i in 0 until racesJson.length()) {
            val race = racesJson.getJSONObject(i)
            if (race.getString("name") == name) {
                return race.getJSONArray("features")
            }
        }
        throw Exception("Name not found")
    }

    private fun migrateFeatureList(
        featureList: JSONArray,
        featureListWithIds: JSONArray,
        characterId: Int
    ) {
        for (i in 0 until featureList.length()) {
            val featureObj = featureList.getJSONObject(i)
            val featureObjWithIds = featureListWithIds.getJSONObject(i)
            val featureId = featureObjWithIds.getInt("id")
            migrateFeature(characterId, featureObj, featureObjWithIds, featureId)
        }
    }

    private fun migrateClasses(id: Int, classes: JSONObject) {
        classes.keys().forEach { key ->
            val classObj = classes.getJSONObject(key)
            val classId = getClassId(classObj.getString("name"))
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
                migrateFeatureList(features, getClassFeatureList(classObj.getString("name")), id)
            }

            //Insert a class choice object
            val values = ContentValues()
            values.put("characterId", id)
            values.put("classId", classId)
            values.put("level", classObj.getInt("level"))
            values.put("isBaseClass", classObj.getBoolean("isBaseClass"))
            values.put("totalNumOnGoldDie", classObj.getInt("totalNumOnGoldDie"))
            values.put("abilityImprovementsGranted", classObj.getJSONArray("abilityImprovementsGranted").toString())
            values.put("tookGold", classObj.getBoolean("tookGold"))
            values.put("proficiencyChoicesByString", proficiencyChoices)
            db.insert("ClassChoiceEntity", OnConflictStrategy.IGNORE, values)
        }
    }

    private fun migrateSubclass(id: Int, classId: Int, subclass: JSONObject) {
        val subclassId = getIdByNameInList(
            subclass.getString("name"),
            classesJson.getJSONObject(classId - 1).getJSONArray("subclasses")
        )

        subclass.optJSONArray("features")?.let { features ->
            migrateFeatureList(
                features,
                getSubclassFeatures(classId, subclass.getString("name")),
                id
            )
        }

        db.execSQL(
            "INSERT INTO CharacterSubclassCrossRef (subClassId, characterId, classId) VALUES ($subclassId, $id, $classId)"
        )
    }

    private fun getSubclassFeatures(classId: Int, name: String): JSONArray {
        val subclasses = classesJson.getJSONObject(classId - 1).getJSONArray("subclasses")
        for (i in 0 until subclasses.length()) {
            val subclass = subclasses.getJSONObject(i)
            if (subclass.getString("name") == name) {
                return subclass.getJSONArray("features")
            }
        }
        throw Exception("Subclass not found")
    }

    private fun migrateBackground(id: Int, background: JSONObject) {
        val backgroundId = getBackgroundId(background.getString("name"))

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

    private fun convertAbilityBonuses(json: JSONArray): String {
        val result = mutableListOf<String>()
        for (i in 0 until json.length()) {
            val obj = json.getJSONObject(i)
            val bonus = obj.getString("bonus")
            val ability = obj.getString("ability")
            result.add("+$bonus $ability")
        }
        return gson.toJson(result)
    }

    private fun migrateRace(id: Int, race: JSONObject) {
        val raceId = getRaceId(race.getString("name"))

        race.optJSONObject("subrace")?.let {
            migrateSubrace(id, it, raceId)
        }

        race.optJSONArray("traits")?.let { features ->
            migrateFeatureList(features, getRaceFeatures(race.getString("name")), id)
        }

        //Insert a CharacterRaceCrossRef
        db.execSQL("INSERT INTO CharacterRaceCrossRef (id, raceId) VALUES ($id, $raceId)")

        //Extract the ability bonus choice and convert it so it can be stored in a race choice object.
        val abilityBonusChoice =
            race.optJSONObject("abilityBonusChoice")?.getJSONArray("chosen")?.let {
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

    private fun migrateSubrace(id: Int, subrace: JSONObject, raceId: Int) {
        val subraceId = getSubraceId(subrace.getString("name"), raceId)

        subrace.optJSONArray("featChoices")?.let {
            for (i in 0 until it.length()) {
                val obj = it.getJSONObject(i)
                //This can be set to 1 because version 57 only contains one feat choice.
                val choiceId = 1
                db.execSQL("INSERT INTO SubraceFeatChoiceCrossRef (subraceId, featChoiceId) VALUES($subraceId, $choiceId)")
                migrateFeatChoice(id, choiceId, obj)
            }
        }

        subrace.optJSONArray("traits")?.let { features ->
            migrateFeatureList(features, getSubraceFeatureList(subraceId, raceId), id)
        }

        db.execSQL("INSERT INTO CharacterSubraceCrossRef (subraceId, characterId) VALUES($id, $subraceId) ")

        val languageChoice = subrace.optJSONArray("languageChoice")?.let {
            convertLanguageChoices(it)
        } ?: "[]"

        val abilityBonusChoice =
            subrace.optJSONObject("abilityBonusChoice")?.getJSONArray("chosen")?.let {
                convertAbilityBonuses(it)
            } ?: "[]"

        val values = ContentValues()
        values.put("subraceId", subraceId)
        values.put("characterId", id)
        values.put("languageChoice", languageChoice)
        values.put("abcchosenByString", abilityBonusChoice)

        db.insert("SubraceChoiceEntity", OnConflictStrategy.IGNORE, values)
    }

    private fun getSubraceFeatureList(subraceId: Int, raceId: Int): JSONArray {
        val subraces = racesJson.getJSONObject(raceId - 1).getJSONArray("subraces")
        for (i in 0 until subraces.length()) {
            val subrace = subraces.getJSONObject(i)
            if (subrace.getInt("id") == subraceId) {
                return subrace.optJSONArray("features") ?: JSONArray("[]")
            }
        }
        throw Exception("Subrace not found")
    }

    private fun getFeatId(name: String): Int {
        for (i in 0 until featsJson.length()) {
            val feat = featsJson.getJSONObject(i)
            if (feat.getString("name") == name) {
                return i + 1
            }
        }
        throw Exception("Feat not found")
    }

    private fun migrateFeatChoice(characterId: Int, choiceId: Int, obj: JSONObject) {
        obj.optJSONArray("chosen")?.let {
            for (i in 0 until it.length()) {
                val feat = it.getJSONObject(i)
                val featId = getFeatId(feat.getString("name"))
                val featureListWithIds =
                    featsJson.getJSONObject(featId - 1).getJSONArray("features")
                db.execSQL("INSERT INTO FeatChoiceChoiceEntity(characterId, choiceId, featId) VALUES($characterId, $choiceId, $featId)")
                migrateFeat(characterId, feat, featId, featureListWithIds)
            }
        }
    }

    private fun getFeatFeatureId(name: String, featId: Int): Int {
        val featOptions = featsJson.getJSONObject(featId - 1).getJSONArray("features")
        for (i in 0 until featOptions.length()) {
            val feature = featOptions.getJSONObject(i)
            if (feature.getString("name") == name) {
                return feature.getInt("id")
            }
        }
        throw Exception("Feat not found")
    }

    private fun migrateFeat(
        characterId: Int,
        feat: JSONObject,
        featId: Int,
        featureListWithIds: JSONArray
    ) {
        feat.optJSONArray("features")?.let {
            for (i in 0 until it.length()) {
                val obj = it.getJSONObject(i)
                val featureObjWithIds = featureListWithIds.getJSONObject(i)
                val featureId = getFeatFeatureId(obj.getString("name"), featId)
                migrateFeature(characterId, obj, featureObjWithIds, featureId)
            }
        }
    }


    private fun migrateFeature(
        characterId: Int,
        feature: JSONObject,
        featureObjWithIds: JSONObject,
        featureId: Int
    ) {
        feature.optJSONArray("choices")?.let { choices ->
            for (i in 0 until choices.length()) {
                val obj = choices.getJSONObject(i)
                if (
                    (obj.optJSONObject("choose")?.optInt("static", 1) ?: 0) != 0
                ) {
                    val objWithIds = featureObjWithIds
                    val featureChoiceId =
                        objWithIds.optInt("choice_id", featureObjWithIds.getInt("choice_id"))
                    migrateFeatureChoice(characterId, obj, objWithIds, featureChoiceId)
                }
            }
        }
    }

    private fun getFeatureChoiceFromIndex(index: String): JSONArray {
        return when (index) {
            "infusions" -> infusionsJson
            "invocations" -> invocationsJson
            "proficiencies" -> expertiseJson
            "skills" -> proficienciesJson
            "all_spells" -> spellsJson
            "spells" -> spellsJson
            "maneuvers" -> maneuversJson
            "metamagic" -> metamagicJson
            "all_languages" -> languagesJson
            "artisans_tools" -> artisansToolsJson
            "fighting_styles" -> fightingStylesJson
            "Draconic Ancestry" -> throw JSONException("")
            else -> fightingStylesJson
        }
    }

    private fun getFeatureChoiceFromList(choiceWithIds: JSONObject): JSONArray {
        return try {
            getFeatureChoiceFromIndex(choiceWithIds.getString("index"))
        } catch (e: JSONException) {
            val result = choiceWithIds.getJSONArray("from")
            val subResults = JSONArray()
            val indexesToRemove = mutableListOf<Int>()
            for (i in 0 until result.length()) {
                val obj = result.getJSONObject(i)
                indexesToRemove.add(i)
                try {
                    val sublist = getFeatureChoiceFromIndex(obj.getString("index"))
                    for (j in 0 until sublist.length()) {
                        subResults.put(sublist.getJSONObject(j))
                    }
                } catch (_: JSONException) {
                }
            }
            if (subResults.length() == 0) {
                result
            } else {
                indexesToRemove.asReversed().forEach {
                    result.remove(it)
                }
                for(i in 0 until subResults.length()) {
                    result.put(subResults.getJSONObject(i))
                }
                result
            }
        }
    }

    private fun getJsonObjectFromList(name: String, list: JSONArray): JSONObject {
        for (i in 0 until list.length()) {
            val obj = list.getJSONObject(i)
            if (obj.getString("name") == name) {
                return obj
            }
        }
        throw Exception("name not found")
    }

    private fun migrateFeatureChoice(
        characterId: Int,
        choice: JSONObject,
        choiceWithIds: JSONObject,
        choiceId: Int
    ) {
        choice.optJSONArray("chosen")?.let { chosen ->
            for (i in 0 until chosen.length()) {
                val obj = chosen.getJSONObject(i)
                val objWithId = getJsonObjectFromList(
                    obj.getString("name"),
                    getFeatureChoiceFromList(choiceWithIds)
                )
                val featureId = objWithId.getInt("id")
                migrateFeature(characterId, obj, objWithId, featureId)
                db.execSQL("INSERT INTO FeatureChoiceChoiceEntity (featureId, characterId, choiceId) VALUES ($featureId, $characterId, $choiceId)")
            }
        }
    }
}