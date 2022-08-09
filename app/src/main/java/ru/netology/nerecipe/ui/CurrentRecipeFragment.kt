package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.StepsAdapter
import ru.netology.nerecipe.databinding.CurrentRecipeFragmentBinding
import ru.netology.nerecipe.recipe.Recipe
import ru.netology.nerecipe.viewModel.RecipeViewModel

class CurrentRecipeFragment : Fragment() {
    private val viewModel: RecipeViewModel by activityViewModels()

    private val args by navArgs<CurrentRecipeFragmentArgs>()

    private lateinit var currentRecipe: Recipe

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return CurrentRecipeFragmentBinding.inflate(
            layoutInflater, container, false
        ).also { binding ->
            with(binding) {

                val adapter = StepsAdapter(viewModel)
                stepsRecyclerView.adapter = adapter

                currentRecipe = viewModel.data.value?.let { listRecipe ->
                    listRecipe.firstOrNull {
                        it.id == args.idCurrentRecipe
                    }
                } as Recipe
                render(currentRecipe)
                adapter.submitList(currentRecipe.content)
                adapter.differ.submitList(currentRecipe.content)

                viewModel.data.observe(viewLifecycleOwner) { listRecipe ->
                    if (listRecipe.none { it.id == args.idCurrentRecipe }) {
                        return@observe
                    }
                    currentRecipe = listRecipe.firstOrNull {
                        it.id == args.idCurrentRecipe
                    } as Recipe
                    render(currentRecipe)
                    adapter.submitList(currentRecipe.content)
                    adapter.differ.submitList(currentRecipe.content)
                }

                viewModel
                    .navigateToRecipeEditOrAddScreenEvent
                    .observe(viewLifecycleOwner) { initialContent ->
                        val direction =
                            CurrentRecipeFragmentDirections.currentRecipeFragmentToRecipeContentFragment(
                                initialContent.id,
                                RecipeContentFragment.REQUEST_CURRENT_RECIPE_KEY
                            )
                        findNavController().navigate(direction)
                    }

                viewModel.navigateToStepEditScreenEvent.observe(
                    viewLifecycleOwner
                ) { currentRecipe ->
                    val direction =
                        CurrentRecipeFragmentDirections.currentRecipeFragmentToStepContentFragment(
                            currentRecipe.stepText
                        )
                    findNavController().navigate(direction)
                }

                viewModel.navigateToStepAddScreenEvent.observe(
                    viewLifecycleOwner
                ) { stepText ->
                    val direction =
                        CurrentRecipeFragmentDirections.currentRecipeFragmentToStepContentFragment(
                            stepText
                        )
                    findNavController().navigate(direction)
                }

                setFragmentResultListener(
                    requestKey = StepContentFragment.REQUEST_CURRENT_RECIPE_KEY
                ) { requestKey, bundle ->
                    if (requestKey != StepContentFragment.REQUEST_CURRENT_RECIPE_KEY) return@setFragmentResultListener
                    val newTextStep = bundle.getString(
                        StepContentFragment.RESULT_KEY
                    ) ?: return@setFragmentResultListener
                    viewModel.onSaveButtonStepClicked(newTextStep)
                }

                likeIcon.setOnClickListener { viewModel.onAddFavoriteClicked(currentRecipe) }

                fabSteps.setOnClickListener {
                    viewModel.onAddStepClicked(currentRecipe)
                }

                val popupMenu by lazy {
                    PopupMenu(context, binding.options).apply {
                        inflate(R.menu.options_recipe)
                        setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {
                                R.id.remove -> {
                                    viewModel.onRemoveClicked(currentRecipe)
                                    findNavController().popBackStack()
                                    true
                                }
                                R.id.edit -> {
                                    viewModel.onEditClicked(currentRecipe)
                                    true
                                }
                                else -> false
                            }
                        }
                    }
                }

                options.setOnClickListener { popupMenu.show() }
            }

        }.root
    }

    private fun CurrentRecipeFragmentBinding.render(recipe: Recipe) {
        authorName.text = recipe.author
        title.text = recipe.title
        category.text = recipe.category
        likeIcon.isChecked = recipe.isFavorite
    }
}