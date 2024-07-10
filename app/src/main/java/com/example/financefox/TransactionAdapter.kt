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


class TransactionAdapter(var mContext: Context, var transactionList: List<Transaction>, val transactionViewModel: TransactionViewModel, val balanceViewModel: BalanceViewModel) : RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {

    inner class TransactionHolder(val view: ItemTransactionBinding) : RecyclerView.ViewHolder(view.root)

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
        view.categoryItem.text = transaction.category
        view.typeItem.text = if (transaction.type) {
            "Expense"
        } else {
            "Income"
        }
        // Format the date
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        view.dateItem.text = sdf.format(transaction.date)

        // Set onClickListener for delete button
        view.deleteTransaction.setOnClickListener {
            balanceViewModel.updateBalance(transaction.amount, transaction.type, true)
            transactionViewModel.deleteTransaction(transaction)
            // Remove item from RecyclerView
            transactionList = transactionList.toMutableList().apply {
                removeAt(position)
            }
            notifyDataSetChanged()
        }
    }

    fun updateTransactions(newTransactionList: List<Transaction>) {
        transactionList = newTransactionList
        notifyDataSetChanged()
    }
}