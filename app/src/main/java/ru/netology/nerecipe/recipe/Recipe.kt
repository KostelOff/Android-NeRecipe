package ru.netology.nerecipe.recipe

import kotlinx.serialization.Serializable
import ru.netology.nerecipe.data.RecipeRepository

@Serializable
data class Recipe(
    val id: Long,
    val author: String,
    val title: String,
    val category: String,
    val content: List<Step>?,
    val isFavorite: Boolean = false,
    val indexPosition: Long = RecipeRepository.NEW_RECIPE_ID
)