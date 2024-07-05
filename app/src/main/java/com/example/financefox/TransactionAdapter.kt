package com.example.financefox

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.financefox.databinding.ItemTransactionBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale


class TransactionAdapter(var mContext: Context, var transactionList: List<Transaction>, val transactionViewModel: TransactionViewModel) : RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {

    inner class TransactionHolder(val view: ItemTransactionBinding) : RecyclerView.ViewHolder(view.root)

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return TransactionHolder(binding)
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val transaction = transactionList.get(position)
        val view = holder.view
        view.amountItem.text = transaction.amount.toString()
        view.categoryItem.text = transaction.category?.name ?: "--"
        view.typeItem.text = if (transaction.type) {
            "Expense"
        } else {
            "Entry"
        }
        // Format the date
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        view.dateItem.text = sdf.format(transaction.date)

        // Set onClickListener for delete button
        view.deleteTransaction.setOnClickListener {
            transactionViewModel.deleteTransaction(transaction)
            // Remove item from RecyclerView

            transactionList = transactionList.toMutableList().apply {
                removeAt(position)
            }
            notifyDataSetChanged()



            /*val userId = firebaseAuth.currentUser?.uid
            val transactionId = transaction.id
            val documentPath = "user/$userId/transactions/$transactionId"

            Log.d("FinanceFox", "Attempting to delete transaction at path: $documentPath")

            // Check if userId or transactionId is null or empty
            if (userId.isNullOrEmpty() || transactionId.isNullOrEmpty()) {
                Log.d("FinanceFox", "User ID or Transaction ID is null or empty.")
                return@setOnClickListener
            }

            // Delete transaction from Firestore
            db.collection("user").document(firebaseAuth.currentUser!!.uid)
                .collection("transactions")
                .document(transaction.id)
                .delete()
                .addOnSuccessListener {
                    Log.d("FinanceFox", "Transaction successfully deleted: ${transaction.id}")
                    // Remove item from RecyclerView
                    transactionList = transactionList.toMutableList().apply {
                        removeAt(position)
                    }
                    notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.d("FinanceFox", "Failed to delete transaction: $exception")
                    // Handle failure as needed
                }*/
        }
    }
}