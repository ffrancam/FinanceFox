package com.example.financefox

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.financefox.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.Locale


class TransactionAdapter(var mContext: Context, var transactionList: List<Transaction>) : RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {

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
        view.categoryItem.text = transaction.category?.name ?: "--"
        view.typeItem.text = if (transaction.type) {
            "Expense"
        } else {
            "Entry"
        }
        // Format the date
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        view.dateItem.text = sdf.format(transaction.date)
    }
}