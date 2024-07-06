package com.example.financefox.fragments

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financefox.BalanceViewModel
import com.example.financefox.R
import com.example.financefox.TransactionAdapter
import com.example.financefox.TransactionViewModel
import com.example.financefox.databinding.FragmentCategoryBinding
import com.example.financefox.databinding.FragmentTransactionBinding

class TransactionFragment : Fragment() {

    //binding connected to the specific layout of the fragment
    private lateinit var binding: FragmentTransactionBinding

    // Use of viewModel among fragments to share data
    private val viewModel: TransactionViewModel by activityViewModels()
    private val balanceViewModel : BalanceViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using data binding
        binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RV
        binding.rvTransaction.layoutManager = LinearLayoutManager(requireContext())
        viewModel.transactions.observe(viewLifecycleOwner, Observer { transactions ->
            // Update the RecyclerView
            val transactionAdapter = TransactionAdapter(requireContext(), transactions, viewModel, balanceViewModel)
            binding.rvTransaction.adapter = transactionAdapter
        })

        // Get the NavController
        val navController = Navigation.findNavController(view)
        binding.callAddTransactionBtn.setOnClickListener{
            navController.navigate(R.id.action_transactionFragment_to_addTransactionFragment)
        }
    }


}