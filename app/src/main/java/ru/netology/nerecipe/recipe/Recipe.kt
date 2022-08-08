package ru.netology.nerecipe.recipe

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: Long,
    val author: String,
    val title: String,
    val category: String,
    val content: List<Step>?,
    val isFavorite: Boolean = false,
    val indexPosition: Long
)