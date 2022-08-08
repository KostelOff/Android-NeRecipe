package ru.netology.nerecipe.adapter

interface FilterInteractionListener {

    fun checkboxFilterPressedOn(category: String)

    fun checkboxFilterPressedOff(category: String)

    fun getStatusCheckBox(category: String): Boolean
}