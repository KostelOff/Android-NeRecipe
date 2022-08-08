package ru.netology.nerecipe.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nerecipe.databinding.AddOrEditStepFragmentBinding
import ru.netology.nerecipe.util.focusAndShowKeyboard
import ru.netology.nerecipe.viewModel.RecipeViewModel

class StepContentFragment : Fragment() {

    private val args by navArgs<StepContentFragmentArgs>()

    private val viewModel: RecipeViewModel by activityViewModels()

    private val contract = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            requireActivity().contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        viewModel.currentImageStep.value = uri.toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = AddOrEditStepFragmentBinding.inflate(
        layoutInflater, container, false
    ).also { binding ->
        binding.edit.setText(args.initialStepText)
        binding.edit.focusAndShowKeyboard()
        binding.ok.setOnClickListener {
            onOkButtonClicked(binding)
        }

        binding.addPicture.setOnClickListener{
            contract.launch(arrayOf("image/*"))
        }
    }.root

    private fun onOkButtonClicked(binding: AddOrEditStepFragmentBinding) {
        val text = binding.edit.text

        if (!text.isNullOrBlank()) {
            val resultBundle = Bundle(1)
            resultBundle.putString(RESULT_KEY, text.toString())
            setFragmentResult(
                REQUEST_CURRENT_RECIPE_KEY,
                resultBundle
            )
        }
        findNavController().popBackStack()
    }

    companion object {
        const val REQUEST_CURRENT_RECIPE_KEY = "requestForCurrentRecipeFragmentKey"
        const val RESULT_KEY = "recipeNewContent"
    }
}