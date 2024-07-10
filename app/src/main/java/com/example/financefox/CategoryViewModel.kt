package com.example.financefox

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class Category(var id: String = "", var name: String = "") {
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
    }

    fun addCategory(categoryName: String) {
        val category = Category("", categoryName)
        val categoryId = db.collection("users")
            .document(firebaseAuth.currentUser!!.uid)
            .collection("categories").document().id

        category.id = categoryId

        db.collection("users").document(firebaseAuth.currentUser!!.uid)
            .collection("categories")
            .document(categoryId)
            .set(category)
            .addOnSuccessListener {
                val categoriesList = _categories.value ?: mutableListOf()
                // Check if the category already exists
                val existingCategory = categoriesList.find{ it.name == category.name }
                if (existingCategory == null) {
                    val currentCategories = _categories.value.orEmpty().toMutableList()
                    currentCategories.add(category)
                    _categories.value = currentCategories
                }
            }
    }

    fun getCategoryByName(categoryName: String): Category? {
        val categoriesList = _categories.value ?:  throw NoSuchElementException("Category not found")
        return categoriesList.find { it.name == categoryName }
    }

    fun deleteCategory(category: Category) {
        db.collection("users").document(firebaseAuth.currentUser!!.uid)
            .collection("categories")
            .document(category.id)
            .delete()
            .addOnSuccessListener {
                val currentCategories = _categories.value ?: mutableListOf()
                currentCategories.remove(category)
                _categories.postValue(currentCategories)
            }
    }

    fun updateCategory(category: Category?, newCategoryName: String) {
        val categoryRef = db.collection("users")
            .document(firebaseAuth.currentUser!!.uid)
            .collection("categories")
            .document(category!!.id)
        categoryRef
            .update("name", newCategoryName)
            .addOnSuccessListener {
                val currentCategories = _categories.value?.toMutableList() ?: mutableListOf()
                val index = currentCategories.indexOfFirst { it.id == category.id }
                if (index != -1) {
                    currentCategories[index].name = newCategoryName
                    _categories.postValue(currentCategories)
                }
            }
    }
}