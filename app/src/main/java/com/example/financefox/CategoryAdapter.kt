package com.example.financefox

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.financefox.databinding.ItemCategoryBinding


class CategoryAdapter(var mContext: Context, var categoryList: List<Category>, val categoryViewModel: CategoryViewModel, val transactionViewModel: TransactionViewModel) : RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {

    inner class CategoryHolder(val view: ItemCategoryBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return CategoryHolder(binding)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        val category = categoryList.get(position)
        val categoryName = category.name
        val view = holder.view
        view.categoryItem.text = category.name

        // Set onClickListener for delete button
        view.deleteCategory.setOnClickListener {
            transactionViewModel.updateTransactionsAfterCategoryDelete(categoryName)
            categoryViewModel.deleteCategory(category)
            // Remove item from RecyclerView
            categoryList = categoryList.toMutableList().apply {
                removeAt(position)
            }
            notifyDataSetChanged()
        }

    }

}