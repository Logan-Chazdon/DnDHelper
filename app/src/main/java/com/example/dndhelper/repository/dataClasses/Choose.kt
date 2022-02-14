package com.example.dndhelper.repository.dataClasses

class Choose{
    private var dynamicByLevel: List<Int>? = null
    private var static: Int? = null

    constructor(dynamicByLevel: List<Int>) {
        this.dynamicByLevel = dynamicByLevel
    }
    constructor(static: Int) {
        this.static = static
    }

    fun num(level: Int): Int {
        static?.let {
            return it
        }
        dynamicByLevel?.runCatching {
            return this[level]
        }
        return 0
    }

    fun num(level: String) : Int {
        level.runCatching {
            return num(level.toInt())
        }
        return 0
    }

}
