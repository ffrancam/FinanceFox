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

        val navController = Navigation.findNavController(view)
        binding.entryStatBtn.setOnClickListener {
            navController.navigate(R.id.action_statsFragment_to_entryStatsFragment)
        }
        binding.expenseStatBtn.setOnClickListener {
            navController.navigate(R.id.action_statsFragment_to_expenseStatsFragment)
        }
    }
}