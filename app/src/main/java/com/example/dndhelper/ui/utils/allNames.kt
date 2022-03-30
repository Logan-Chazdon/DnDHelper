package com.example.dndhelper.ui.utils

import com.example.dndhelper.repository.dataClasses.ItemInterface

val <E> List<E>.allNames: String
    get() {
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