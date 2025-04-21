package dataManagers

import model.Metamagic

class MetaMagicManager(
    val postAll: (List<Metamagic>) -> Unit
)