package com.example.financefox

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date


data class Transaction(var id:String = "",val category: Category? = null, val amount: Double = 0.0, val type: Boolean = false, val date: Date = Date()) {
    override fun toString(): String {
        return "Type:$type amount:$amount cat:$category date:$date"
    }
}

class TransactionViewModel: ViewModel() {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    var userName: String
    var userID: String

    private val _transactions = MutableLiveData<MutableList<Transaction>>()
    val transactions: LiveData<MutableList<Transaction>>
        get() = _transactions

    init {
        loadTransactionsFromFirestore()
        userName = firebaseAuth.currentUser!!.displayName.toString()
        userID = firebaseAuth.currentUser!!.email.toString()
    }

    fun loadTransactionsFromFirestore() {
        db.collection("users").document(firebaseAuth.currentUser!!.uid)
            .collection("transactions")
            .get()
            .addOnSuccessListener { documents ->
                val transactionList = mutableListOf<Transaction>()
                for (document in documents) {
                    val transaction = document.toObject(Transaction::class.java)
                    transactionList.add(transaction)
                }
                _transactions.value = transactionList
            }
            .addOnFailureListener { exception ->
                Log.d("FinanceFox", "Collection empty or not available: $exception")
            }
    }

    fun addTransaction(category: Category?, amount: Double, type: Boolean, date: Date) {
        val transaction = Transaction("", category, amount, type, date)
        val transactionId = db.collection("users")
            .document(firebaseAuth.currentUser!!.uid)
            .collection("transactions").document().id

        transaction.id = transactionId

        db.collection("users").document(firebaseAuth.currentUser!!.uid)
            .collection("transactions")
            .document(transactionId)
            .set(transaction)
            .addOnSuccessListener {
                val transactionsList = _transactions.value ?: mutableListOf()
                val currentTransactions = _transactions.value.orEmpty().toMutableList()
                currentTransactions.add(transaction)
                _transactions.value = currentTransactions
                Log.d("FinanceFox", "Transaction Added")
            }
    }

    fun deleteTransaction(transaction: Transaction) {
        val userId = firebaseAuth.currentUser?.uid
        val transactionId = transaction.id
        val documentPath = "users/$userId/transactions/$transactionId"

        Log.d("FinanceFox", "Attempting to delete transaction at path: $documentPath")

        // Delete transaction from Firestore
        db.collection("users").document(firebaseAuth.currentUser!!.uid)
            .collection("transactions")
            .document(transaction.id)
            .delete()
            .addOnSuccessListener {
                Log.d("FinanceFox", "Transaction successfully deleted: ${transaction.id}")
            }
            .addOnFailureListener { exception ->
                Log.d("FinanceFox", "Failed to delete transaction: $exception")
                // Handle failure as needed
            }
    }

}