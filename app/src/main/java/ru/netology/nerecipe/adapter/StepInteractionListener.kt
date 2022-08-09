package ru.netology.nerecipe.adapter

import ru.netology.nerecipe.recipe.Step

interface StepInteractionListener {

    fun onRemoveStepClicked(step: Step)

    fun onEditStepClicked(step: Step)
}