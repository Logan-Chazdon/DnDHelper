package com.example.dndhelper.repository.dataClasses

sealed class SizeClass(val name: String) {
 class Tiny : SizeClass("Tiny")
 class Small : SizeClass("Small")
 class Medium : SizeClass("Medium")
 class Large : SizeClass("Large")
 class Huge : SizeClass("Huge")
 class Gargantuan : SizeClass("Gargantuan")

}
