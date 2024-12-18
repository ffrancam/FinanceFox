package com.example.financefox.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financefox.CategoryAdapter
import com.example.financefox.CategoryViewModel
import com.example.financefox.R
import com.example.financefox.TransactionViewModel
import com.example.financefox.databinding.FragmentCategoryBinding

class CategoryFragment : Fragment() {

    // Binding connected to the specific layout of the fragment
    private lateinit var binding: FragmentCategoryBinding

    // Use of viewModel among fragments to share data
    private val viewModel: CategoryViewModel by activityViewModels()
    private val transactionViewModel: TransactionViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using data binding
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addCategoryBtn.setOnClickListener {
            val name = binding.categoryName.text.toString()

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (viewModel.getCategoryByName(name) != null) {
                Toast.makeText(requireContext(), "Category Already Exits", Toast.LENGTH_SHORT).show()
            }
            else {
                viewModel.addCategory(name)
                binding.categoryName.setText("")
                Toast.makeText(requireContext(), "Category Successfully Added", Toast.LENGTH_SHORT).show()
            }
        }

        binding.editCategoriesBtn.setOnClickListener {
            // Get the NavController
            val navController = Navigation.findNavController(view)
            navController.navigate(R.id.action_categoryFragment_to_editCategoryFragment)
        }

        // RV
        binding.rvCategory.layoutManager = LinearLayoutManager(requireContext())
        viewModel.categories.observe(viewLifecycleOwner, Observer { categories ->
            // Update the RecyclerView
            val categoryAdapter = CategoryAdapter(requireContext(), categories, viewModel, transactionViewModel)
            binding.rvCategory.adapter = categoryAdapter
        })
    }
}