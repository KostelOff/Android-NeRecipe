package ru.netology.nerecipe.data.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nerecipe.data.RecipeRepository
import ru.netology.nerecipe.db.RecipeDao
import ru.netology.nerecipe.db.toEntity
import ru.netology.nerecipe.db.toRecipe
import ru.netology.nerecipe.recipe.Recipe
import ru.netology.nerecipe.recipe.Step

class RecipeRepositoryImpl(
    private val dao: RecipeDao
) : RecipeRepository {

    private var nextIndexId: Long = 1

    override val data = dao.getAll().map { entities ->
        entities.map { it.toRecipe() }
    }

    override fun getNextIndexId(): Long {
        return nextIndexId++
    }

    override fun addFavorite(recipeId: Long) {
        dao.addFavorite(recipeId)
    }

    override fun delete(recipeId: Long) {
        dao.delete(recipeId)
    }

    override fun save(recipe: Recipe) {
        if (recipe.id == RecipeRepository.NEW_RECIPE_ID) dao.insert(recipe.toEntity())
        else dao.update(recipe.id, recipe.author, recipe.title, recipe.category)
    }

    override fun updateListOnMove(from: Long, to: Long, fromId: Long, toId: Long) {
        if (to < from) {
            dao.updateItemMoveDown(fromId, toId)
        } else {
            dao.updateItemMoveUp(fromId, toId)
        }
    }

    override fun deleteStep(step: Step) {
        dao.deleteStep(step.idStep)
    }

    override fun saveStep(step: Step) {
        if (step.idStep == RecipeRepository.NEW_STEP_ID) dao.insertStep(step.toEntity())
        else dao.updateStep(
            step.idStep,
            step.idRecipe,
            step.stepText,
            step.picture
        )
    }

    override fun getFilteredList(filters: MutableSet<String>?): LiveData<List<Recipe>> {
        if (filters.isNullOrEmpty()) {
            return data
        }
        val filteredRecipe = data.map { recipeList ->
            val newRecipes = recipeList.filter {
                it.category in filters
            }
            newRecipes
        }
        return filteredRecipe
    }
}