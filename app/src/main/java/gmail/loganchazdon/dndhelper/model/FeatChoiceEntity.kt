package gmail.loganchazdon.dndhelper.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "featChoices")
open class FeatChoiceEntity(
    val name: String,
    val choose : Int,
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    fun toFeatChoice(chosen: List<Feat>, from : List<Feat>) : FeatChoice{
        return FeatChoice(
            name = name,
            choose = choose,
            from = from,
            chosen = chosen
        )
    }
}