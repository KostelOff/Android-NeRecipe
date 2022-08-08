package ru.netology.nerecipe.recipe

import kotlinx.serialization.Serializable

@Serializable
data class Step(
    val idStep: Long,
    val idRecipe: Long,
    val stepText: String,
    val picture: String = ""
)