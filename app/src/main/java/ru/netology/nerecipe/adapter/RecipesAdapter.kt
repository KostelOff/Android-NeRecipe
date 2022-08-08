package ru.netology.nerecipe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.databinding.RecipeBinding
import ru.netology.nerecipe.recipe.Recipe
import java.util.*


internal class RecipesAdapter(
    private val interactionListener: RecipeInteractionListener
) : ListAdapter<Recipe, RecipesAdapter.ViewHolder>(DiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipeBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, interactionListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recipeItem = currentList[position]
        holder.bind(recipeItem)
    }


    inner class ViewHolder(
        private val binding: RecipeBinding,
        listener: RecipeInteractionListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var recipe: Recipe

        init {
            binding.likeIcon.setOnClickListener { listener.onAddFavoriteClicked(recipe) }
            binding.root.setOnClickListener { listener.onRecipeClicked(recipe) }
        }

        fun bind(recipe: Recipe) {
            this.recipe = recipe

            with(binding) {
                authorName.text = recipe.author
                title.text = recipe.title
                category.text = recipe.category
                likeIcon.isChecked = recipe.isFavorite
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Recipe>() {

        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe) =
            oldItem.id == newItem.id


        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe) =
            oldItem.content == newItem.content

    }


    val differ = AsyncListDiffer(this, DiffCallback)

    fun moveItem(from: Int, to: Int) {
        val list = currentList.toMutableList()
        val fromLocation = list[from]
        list.removeAt(from)
        if (to < from) {
            list.add(to + 1, fromLocation)
        } else {
            list.add(to - 1, fromLocation)
        }

    }

    fun getIndexFrom(from: Int): Long {
        return currentList.toMutableList()[from].indexPosition
    }

    fun getIdFrom(from: Int): Long {
        return currentList.toMutableList()[from].id
    }

    fun getIndexTo(to: Int): Long {
        return currentList.toMutableList()[to].indexPosition
    }

    fun getIdTo(to: Int): Long {
        return currentList.toMutableList()[to].id
    }

}