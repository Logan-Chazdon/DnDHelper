package com.example.dndhelper.ui.utils

import com.example.dndhelper.model.ItemInterface

val <E> List<E>.allNames: String
    get() {
        if(this.distinct().count() == 1 && this.size != 1) {
            when(this[0]) {
                is ItemInterface -> {
                    return "${this.size} ${(this[0] as ItemInterface).name}s"
                }
            }
        }

        var result = ""
        this.forEachIndexed { index, it ->
            val name  = when(it) {
                is ItemInterface -> {
                    (it as ItemInterface).name
                }
                else -> {
                    ""
                }
            }

            result += when (index) {
                this.size - 1 -> {
                    if(result.isBlank()) {
                        name
                    } else {
                        "and $name."
                    }
                }
                this.size -> {
                    name
                }
                else -> {
                    "${name}, "
                }
            }
        }
        return result
    }