package com.example.financefox

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class Category(val name: String = "") {
    override fun toString(): String {
        return "Name:$name"
    }
}

class CategoryViewModel: ViewModel() {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    var userName: String
    var userID: String

    private val _categories = MutableLiveData<MutableList<Category>>()
    val categories: LiveData<MutableList<Category>>
        get() = _categories

    init {
        loadCategoriesFromFirestore()
        userName = firebaseAuth.currentUser!!.displayName.toString()
        userID = firebaseAuth.currentUser!!.email.toString()
    }

    fun loadCategoriesFromFirestore() {
        db.collection("users").document(firebaseAuth.currentUser!!.uid)
            .collection("categories")
            .get()
            .addOnSuccessListener { documents ->
                val categoryList = mutableListOf<Category>()
                for (document in documents) {
                    val category = document.toObject(Category::class.java)
                    categoryList.add(category)
                }
                _categories.value = categoryList
            }
            .addOnFailureListener { exception ->
                Log.d("FinanceFox", "Collection empty or not available: $exception")
            }
    }

    fun addCategory(category: Category) {
        db.collection("users").document(firebaseAuth.currentUser!!.uid)
            .collection("categories")
            .add(category)
            .addOnSuccessListener {
                val categoriesList = _categories.value ?: mutableListOf()
                // Check if the category already exists
                val existingCategory = categoriesList.find{ it.name == category.name }
                if (existingCategory != null) {
                    Log.d("FinanceFox", "Category Already Exist")
                } else {
                    val currentCategories = _categories.value.orEmpty().toMutableList()
                    currentCategories.add(category)
                    _categories.value = currentCategories
                    Log.d("FinanceFox", "Category Added")
                }
            }
    }

    fun getCategory(index: Int): Category? {
        return _categories.value?.get(index)
    }

    fun getCategoryByName(categoryName: String): Category? {
        val categoriesList = _categories.value ?:  throw NoSuchElementException("Category not found")
        return categoriesList.find { it.name == categoryName }
    }

    fun getCategoryList(): List<String> {
        val result = mutableListOf<String>()
        _categories.value?.forEach{
            result += it.name
        }
        return result.toList()
    }
}