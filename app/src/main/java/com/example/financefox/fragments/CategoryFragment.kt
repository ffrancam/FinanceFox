package com.example.financefox.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financefox.Category
import com.example.financefox.CategoryAdapter
import com.example.financefox.CategoryViewModel
import com.example.financefox.R
import com.example.financefox.databinding.FragmentCategoryBinding

class CategoryFragment : Fragment() {

    //binding connected to the specific layout of the fragment
    private lateinit var binding: FragmentCategoryBinding

    // Use of viewModel among fragments to share data
    private val viewModel: CategoryViewModel by activityViewModels()

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

            viewModel.addCategory(Category(name))
            binding.categoryName.setText("")
        }

        // RV
        binding.rvCategory.layoutManager = LinearLayoutManager(requireContext())
        viewModel.categories.observe(viewLifecycleOwner, Observer { categories ->
            // Update the RecyclerView
            val categoryAdapter = CategoryAdapter(requireContext(), categories)
            binding.rvCategory.adapter = categoryAdapter
        })
    }
}