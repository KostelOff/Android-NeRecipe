package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.RecipesAdapter
import ru.netology.nerecipe.databinding.FeedFragmentBinding
import ru.netology.nerecipe.viewModel.RecipeViewModel

class FeedFragment : Fragment() {

    private val viewModel: RecipeViewModel by activityViewModels()

    private lateinit var recyclerViewAdapter: RecipesAdapter


    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP
                    or ItemTouchHelper.DOWN
                    or ItemTouchHelper.START
                    or ItemTouchHelper.END,
            0
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val adapter = recyclerView.adapter as RecipesAdapter
                val from = viewHolder.absoluteAdapterPosition
                val to = target.absoluteAdapterPosition

                adapter.moveItem(from, to)
                adapter.notifyItemMoved(from, to)

                viewModel.updateListOnMove(
                    adapter.getIndexFrom(from),
                    adapter.getIndexTo(to),
                    adapter.getIdFrom(from),
                    adapter.getIdTo(to)
                )
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.alpha = 0.5f
                }
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.alpha = 1.0f
            }
        }

        ItemTouchHelper(simpleItemTouchCallback)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.navigateToRecipeEditOrAddScreenEvent.observe(this) {
            val direction = FeedFragmentDirections.toRecipeContentFragment(
                0,
                RecipeContentFragment.REQUEST_FEED_KEY
            )
            findNavController().navigate(direction)
        }

        viewModel.navigateToCurrentRecipeScreenEvent.observe(this) { currentRecipe ->
            val direction =
                FeedFragmentDirections.toCurrentRecipeFragment(currentRecipe.id)
            findNavController().navigate(direction)
        }

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_filter_menu, menu)
        val search = menu.findItem(R.id.search)
        val searchView = search.actionView as SearchView
        val text = String.format(resources.getString(R.string.search_by_recipe))
        searchView.queryHint = text

        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                searchView.clearFocus()

                val resultForSubmit = viewModel.filterSearch(p0)
                recyclerViewAdapter.submitList(resultForSubmit)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                val resultForSubmit = viewModel.filterSearch(p0)
                recyclerViewAdapter.submitList(resultForSubmit)
                return true
            }
        })
        return super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.filter) {
            val direction =
                FeedFragmentDirections.toFilterFragment()
            findNavController().navigate(direction)
        }

        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FeedFragmentBinding.inflate(
        layoutInflater, container, false
    ).also { binding ->

        setHasOptionsMenu(true)

        itemTouchHelper.attachToRecyclerView(binding.recipeRecyclerView)
        recyclerViewAdapter = RecipesAdapter(viewModel)

        viewModel.filterResult.observe(viewLifecycleOwner) { recipes ->
            if (recipes.isNullOrEmpty()) binding.emptyStateGroup.visibility = View.VISIBLE
            else binding.emptyStateGroup.visibility = View.GONE
            recyclerViewAdapter.submitList(recipes)
        }

        binding.recipeRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.recipeRecyclerView.adapter = recyclerViewAdapter

        binding.fab.setOnClickListener {
            viewModel.onAddClicked()
        }
    }.root

}