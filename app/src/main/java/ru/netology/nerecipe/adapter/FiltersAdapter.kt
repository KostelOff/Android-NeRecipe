package ru.netology.nerecipe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.databinding.FilterBinding

internal class FiltersAdapter(
    private val interactionListener: FilterInteractionListener
) : ListAdapter<String, FiltersAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FilterBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, interactionListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: FilterBinding,
        listener: FilterInteractionListener

    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var category: String

        init {
            binding.checkboxCategoryFilter.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    listener.checkboxFilterPressedOn(category)
                } else listener.checkboxFilterPressedOff(category)
            }
        }

        fun bind(category: String) {
            this.category = category

            with(binding) {
                textCategoryFilter.text = category
                binding.checkboxCategoryFilter.isChecked =
                    interactionListener.getStatusCheckBox(category)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) =
            oldItem == newItem


        override fun areContentsTheSame(oldItem: String, newItem: String) =
            oldItem == newItem
    }
}