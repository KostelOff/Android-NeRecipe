package ru.netology.nerecipe.viewModel

import SingleLiveEvent
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import ru.netology.nerecipe.adapter.FilterInteractionListener
import ru.netology.nerecipe.adapter.RecipeInteractionListener
import ru.netology.nerecipe.adapter.StepInteractionListener
import ru.netology.nerecipe.data.RecipeRepository
import ru.netology.nerecipe.data.impl.RecipeRepositoryImpl
import ru.netology.nerecipe.db.AppDb
import ru.netology.nerecipe.recipe.Recipe
import ru.netology.nerecipe.recipe.Step
import java.util.*

class RecipeViewModel(
    application: Application
) : AndroidViewModel(application), RecipeInteractionListener, StepInteractionListener,
    FilterInteractionListener {

    private val repository: RecipeRepository = RecipeRepositoryImpl(
        dao = AppDb.getInstance(
            context = application
        ).recipeDao
    )

    val data by repository::data

    private val currentRecipe = MutableLiveData<Recipe?>(null)
    private val currentStep = MutableLiveData<Step?>(null)

    val currentImageStep = MutableLiveData<String>("")

    private val filters = MutableLiveData<MutableSet<String>?>(mutableSetOf())
    var filterResult = Transformations.switchMap(filters) { filter ->
        repository.getFilteredList(filter)
    }


    val navigateToRecipeEditOrAddScreenEvent = SingleLiveEvent<Recipe>()
    val navigateToCurrentRecipeScreenEvent = SingleLiveEvent<Recipe>()
    val navigateToStepEditScreenEvent = SingleLiveEvent<Step>()
    val navigateToStepAddScreenEvent = SingleLiveEvent<String>()

    fun onSaveButtonClicked(category: String, title: String, content: List<Step>?) {
        val recipeForSave = currentRecipe.value?.copy(
            category = category,
            content = content,
            title = title
        ) ?: Recipe(
            id = RecipeRepository.NEW_RECIPE_ID,
            author = "Sasha",
            category = category,
            content = content,
            title = title,
            indexPosition = repository.getNextIndexId()
        )
        repository.save(recipeForSave)
        currentRecipe.value = null
    }

    fun onSaveButtonStepClicked(textStep: String) {
        if (textStep.isBlank()) return

        val stepForSave = currentStep.value?.copy(
            stepText = textStep,
            picture = currentImageStep.value.toString()
        ) ?: Step(
            idStep = RecipeRepository.NEW_STEP_ID,
            idRecipe = currentRecipe.value?.id ?: 0,
            stepText = textStep,
            picture = currentImageStep.value.toString()
        )

        repository.saveStep(stepForSave)

        currentStep.value = null
        currentRecipe.value = null
        currentImageStep.value = ""
    }

    fun onAddClicked() {
        navigateToRecipeEditOrAddScreenEvent.call()
    }

    fun onAddStepClicked(recipe: Recipe) {
        currentRecipe.value = recipe
        navigateToStepAddScreenEvent.call()
    }

    fun updateListOnMove(from: Long, to: Long, fromId: Long, toId: Long) {
        repository.updateListOnMove(from, to, fromId, toId)
    }

    fun filterRecipeByFavorite(recipes: List<Recipe>): List<Recipe> {
        return recipes.filter { it.isFavorite }
    }

    fun filterSearch(charForSearch: CharSequence?): MutableList<Recipe> {
        val filterRecipes = mutableListOf<Recipe>()
        val recipes = filterResult.value
        if (charForSearch?.isBlank() == true) {
            if (recipes != null) {
                filterRecipes.addAll(recipes)
            }
        } else if (recipes != null) {
            for (recipe in recipes) {
                if (
                    recipe.title
                        .lowercase(Locale.getDefault())
                        .contains(
                            charForSearch.toString().lowercase(Locale.getDefault())
                        )
                ) {
                    filterRecipes.add(recipe)
                }
            }
        }
        return filterRecipes

    }

    // region RecipeInteractionListener

    override fun onAddFavoriteClicked(recipe: Recipe) = repository.addFavorite(recipe.id)

    override fun onRemoveClicked(recipe: Recipe) = repository.delete(recipe.id)

    override fun onEditClicked(recipe: Recipe) {
        currentRecipe.value = recipe
        navigateToRecipeEditOrAddScreenEvent.value = recipe
    }

    override fun onRecipeClicked(recipe: Recipe) {
        navigateToCurrentRecipeScreenEvent.value = recipe
    }

    // endregion RecipeInteractionListener

    // region StepInteractionListener

    override fun onRemoveStepClicked(step: Step) = repository.deleteStep(step)

    override fun onEditStepClicked(step: Step) {
        currentStep.value = step
        navigateToStepEditScreenEvent.value = step
    }

    //endregion StepInteractionListener


    //region FilterInteractionListener

    override fun checkboxFilterPressedOn(category: String) {
        val filterList = filters.value
        filterList?.add(category)
        filters.value = filterList
    }

    override fun checkboxFilterPressedOff(category: String) {
        val filterList = filters.value
        filterList?.remove(category)
        filters.value = filterList
    }

    override fun getStatusCheckBox(category: String): Boolean {
        return filters.value?.contains(category) == true
    }

    //endregion FilterInteractionListener
}