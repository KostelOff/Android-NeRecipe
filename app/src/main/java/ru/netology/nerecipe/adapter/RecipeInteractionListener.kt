package ru.netology.nerecipe.adapter

import ru.netology.nerecipe.recipe.Recipe

interface RecipeInteractionListener {

    fun onAddFavoriteClicked(recipe: Recipe)
    fun onRemoveClicked(recipe: Recipe)
    fun onEditClicked(recipe: Recipe)
    fun onRecipeClicked(recipe: Recipe)

}