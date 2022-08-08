package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ru.netology.nerecipe.adapter.RecipesAdapter
import ru.netology.nerecipe.databinding.FavoriteFragmentBinding
import ru.netology.nerecipe.viewModel.RecipeViewModel

class FavoriteFragment : Fragment() {
    private val viewModel: RecipeViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigateToRecipeEditOrAddScreenEvent.observe(this) {
            val direction = FeedFragmentDirections.toRecipeContentFragment(
                0,
                RecipeContentFragment.REQUEST_FEED_FAVORITE_KEY
            )
            findNavController().navigate(direction)
        }

        viewModel.navigateToCurrentRecipeScreenEvent.observe(this) { currentRecipe ->
            val direction =
                FavoriteFragmentDirections.toCurrentRecipeFragment(currentRecipe.id)
            findNavController().navigate(direction)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FavoriteFragmentBinding.inflate(
        layoutInflater, container, false
    ).also { binding ->


        val recyclerViewAdapter = RecipesAdapter(viewModel)

        viewModel.data.observe(viewLifecycleOwner) { recipes ->
            val favoriteRecipes = viewModel.filterRecipeByFavorite(recipes)
            if (favoriteRecipes.isEmpty()) binding.emptyStateGroup.visibility = View.VISIBLE
            else binding.emptyStateGroup.visibility = View.GONE
            recyclerViewAdapter.submitList(favoriteRecipes)
        }

        binding.recipeRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.recipeRecyclerView.adapter = recyclerViewAdapter


    }.root


}