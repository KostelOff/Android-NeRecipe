package ru.netology.nerecipe.db

import ru.netology.nerecipe.recipe.Recipe
import ru.netology.nerecipe.recipe.Step

internal fun RecipeWithSteps.toRecipe() = Recipe(
    id = recipe.id,
    author = recipe.author,
    title = recipe.title,
    category = recipe.category,
    content = step.map {
        it.toStep()
    },
    isFavorite = recipe.isFavorite,
    indexPosition = recipe.indexNumber
)

internal fun Recipe.toEntity() = RecipeEntity(
    id = id,
    author = author,
    title = title,
    category = category,
    isFavorite = isFavorite,
    indexNumber = indexPosition
)

internal fun StepEntity.toStep() = Step(
    idStep = idStep,
    idRecipe = idRecipe,
    stepText = stepText,
    picture = picture
)

internal fun Step.toEntity() = StepEntity(
    idStep = idStep,
    idRecipe = idRecipe,
    stepText = stepText,
    picture = picture
)