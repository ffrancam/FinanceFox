package com.example.financefox.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.financefox.R
import com.example.financefox.TransactionViewModel
import com.example.financefox.databinding.FragmentStatsBinding

class StatsFragment : Fragment() {

    private lateinit var binding: FragmentStatsBinding
    private val transactionViewModel: TransactionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Stat in amount
        var totalExpenses: Double = 0.0
        var totalEntries: Double = 0.0
        binding.totalExpense.setText(totalExpenses.toString())
        binding.totalEntry.setText(totalEntries.toString())

        transactionViewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            transactions.filter { it.type }.forEach { transaction ->
                totalExpenses = totalExpenses + transaction.amount
                binding.totalExpense.setText(totalExpenses.toString())
            }
        }
        transactionViewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            transactions.filter { !it.type }.forEach { transaction ->
                totalEntries = totalEntries + transaction.amount
                binding.totalEntry.setText(totalEntries.toString())
            }
        }

        // Stat in count
        var numExpenses: Int = 0
        var numEntries: Int = 0
        binding.nExpense.setText(numExpenses.toString())
        binding.nEntry.setText(numEntries.toString())

        transactionViewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            transactions.filter { it.type }.forEach { transaction ->
                numExpenses += 1
                binding.nExpense.setText(numExpenses.toString())
            }
        }
        transactionViewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            transactions.filter { !it.type }.forEach { transaction ->
                numEntries += 1
                binding.nEntry.setText(numEntries.toString())
            }
        }


        val navController = Navigation.findNavController(view)
        binding.entryStatBtn.setOnClickListener {
            navController.navigate(R.id.action_statsFragment_to_entryStatsFragment)
        }
        binding.expenseStatBtn.setOnClickListener {
            navController.navigate(R.id.action_statsFragment_to_expenseStatsFragment)
        }
    }
}