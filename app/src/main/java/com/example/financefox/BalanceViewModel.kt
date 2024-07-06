package com.example.financefox

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class Balance(var amount: Double = 0.0) {
    override fun toString(): String {
        return "Amouunt:$amount"
    }
}

class BalanceViewModel: ViewModel() {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    var userName: String
    var userID: String

    private val _balance = MutableLiveData<Balance>()
    val balance: LiveData<Balance>
        get() = _balance

    init {
        loadBalanceFromFirestore()
        userName = firebaseAuth.currentUser!!.displayName.toString()
        userID = firebaseAuth.currentUser!!.email.toString()
    }

    fun loadBalanceFromFirestore() {
        val balanceRef = db.collection("users").document(firebaseAuth.currentUser!!.uid).collection("balance").document("balanceDoc")

        balanceRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val balance = document.toObject(Balance::class.java)
                    _balance.value = balance!!
                } else {
                    // Document does not exist, create it with initial value
                    val initialBalance = Balance(0.0)
                    balanceRef.set(initialBalance)
                        .addOnSuccessListener {
                            _balance.value = initialBalance
                            Log.d("BalanceViewModel", "Balance document created with initial value.")
                        }
                        .addOnFailureListener { e ->
                            Log.w("BalanceViewModel", "Error creating balance document", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("BalanceViewModel", "Error loading balance document", e)
            }
    }

    fun addBalance(amountToAdd: Double) {
        val currentBalance = _balance.value?.amount ?: 0.0
        val newBalance = currentBalance + amountToAdd

        val balanceRef = db.collection("users").document(firebaseAuth.currentUser!!.uid)
        val balance = Balance(newBalance)

        balanceRef
            .set(balance)
            .addOnSuccessListener {
                _balance.value = balance
                Log.d("BalanceViewModel", "Balance increased by $amountToAdd to $newBalance in Firestore")
            }
            .addOnFailureListener { e ->
                Log.w("BalanceViewModel", "Error adding to balance in Firestore", e)
            }
    }
}

/*
class CategoryViewModel: ViewModel() {

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

    fun deleteCategory(category: Category) {
        db.collection("users").document(firebaseAuth.currentUser!!.uid)
            .collection("categories")
            .document(category.id)
            .delete()
            .addOnSuccessListener {
                val currentCategories = _categories.value ?: mutableListOf()
                currentCategories.remove(category)
                _categories.postValue(currentCategories)
                Log.d("FinanceFox", "Category successfully deleted: ${category.name}")
                //_categories.value = updatedCategories
            }
            .addOnFailureListener { exception ->
                Log.d("FinanceFox", "Failed to delete category: $exception")
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
                Log.d("CategoryViewModel", "Category name updated to null in Firestore")
            }
            .addOnFailureListener { e ->
                Log.w("CategoryViewModel", "Error updating Category name category in Firestore", e)
            }
    }
}*/