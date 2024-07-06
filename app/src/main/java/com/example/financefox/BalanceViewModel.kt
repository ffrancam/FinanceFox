package com.example.financefox

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
        val balanceRef = db.collection("users").document(firebaseAuth.currentUser!!.uid)
            .collection("balance")
            .document("balanceDoc")

        balanceRef
            .get()
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

    fun getUserBalance(): Double {
        return _balance.value?.amount ?: 0.0
    }

    fun editBalance(newBalance: Double) {
        val balanceRef = db.collection("users")
            .document(firebaseAuth.currentUser!!.uid)
            .collection("balance")
            .document("balanceDoc")

        balanceRef
            .update("amount", newBalance)
            .addOnSuccessListener {
                _balance.value = Balance(newBalance)
                Log.d("BalanceViewModel", "Balance updated to $newBalance in Firestore")
            }
            .addOnFailureListener { e ->
                Log.w("BalanceViewModel", "Error updating balance in Firestore", e)
            }
    }

    // actionType -> true = deleteTransaction
    //            -> false = addTransaction
    fun updateBalance(transactionAmount: Double, transactionType: Boolean, actionType: Boolean) {
        val balanceRef = db.collection("users")
            .document(firebaseAuth.currentUser!!.uid)
            .collection("balance")
            .document("balanceDoc")

        val currentBalance = _balance.value?.amount ?: 0.0
        var newBalance = 0.0

        if(actionType) {
            // Transaction Deleted
            newBalance = if(transactionType) {
                // Expense
                currentBalance + transactionAmount
            } else {
                // Entry
                currentBalance - transactionAmount
            }
        } else {
            // Transaction Added
            newBalance = if(transactionType) {
                // Expense
                currentBalance - transactionAmount
            } else {
                // Entry
                currentBalance + transactionAmount
            }
        }

        balanceRef
            .update("amount", newBalance)
            .addOnSuccessListener {
                _balance.value = Balance(newBalance)
                Log.d("BalanceViewModel", "Balance updated to $newBalance in Firestore")
            }
            .addOnFailureListener { e ->
                Log.w("BalanceViewModel", "Error updating balance in Firestore", e)
            }
    }
}
