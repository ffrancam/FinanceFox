package com.example.financefox.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.financefox.CategoryViewModel
import com.example.financefox.R
import com.example.financefox.TransactionViewModel
import com.example.financefox.databinding.FragmentEditCategoryBinding

class EditCategoryFragment : Fragment() {

    // Binding connected to the specific layout of the fragment
    private lateinit var binding: FragmentEditCategoryBinding

    // Use of viewModel among fragments to share data
    private val transactionViewModel : TransactionViewModel by activityViewModels()
    private val categoryViewModel : CategoryViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using data binding
        binding = FragmentEditCategoryBinding.inflate(inflater, container, false)

        categoryViewModel.categories.observe(viewLifecycleOwner) { categories ->
            val categoryNames = categories.map { it.name }.distinct().toMutableList()
            categoryNames.add(0, "--")
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categoryToEdit.adapter = adapter
        }

        categoryViewModel.loadCategoriesFromFirestore()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.updateCategoryBtn.setOnClickListener {

            val categoryToEdit = binding.categoryToEdit.selectedItem as String
            val newName = binding.newCategoryName.text.toString()

            if (newName.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (categoryViewModel.getCategoryByName(newName) != null) {
                Toast.makeText(requireContext(), "Category Already Exits", Toast.LENGTH_SHORT).show()
            }
            else {
                val category = categoryViewModel.getCategoryByName(categoryToEdit)

                categoryViewModel.updateCategory(category, newName)
                categoryViewModel.categories.observe(viewLifecycleOwner) { updatedCategories ->
                    val updatedCategory = updatedCategories.find { it.id == category!!.id }
                    if (updatedCategory != null) {
                        transactionViewModel.updateTransactionAfterCategoryEdit(categoryToEdit, newName)
                    }
                }
                Toast.makeText(requireContext(), "Category Successfully Updated", Toast.LENGTH_SHORT).show()
            }

            // Get the NavController
            val navController = Navigation.findNavController(view)
            navController.navigate(R.id.action_editCategoryFragment_to_categoryFragment)
        }
    }
}