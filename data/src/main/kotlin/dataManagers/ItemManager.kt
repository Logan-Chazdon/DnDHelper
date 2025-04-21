package dataManagers

import model.Armor
import model.ItemInterface
import model.Weapon

class ItemManager(
    val getAll : () -> List<ItemInterface>,
    val getSimpleWeapons : () -> List<Weapon>,
    val getMartialWeapons : () -> List<Weapon>,
    val getArmors : () -> List<Armor>,
    val postMartialWeapons: (List<Weapon>) -> Unit,
    val postSimpleWeapons: (List<Weapon>) -> Unit,
    val postArmors: (List<Armor>) -> Unit,
    val postMisc: (List<ItemInterface>) -> Unit,
)