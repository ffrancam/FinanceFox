package com.example.financefox.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.example.financefox.BalanceViewModel
import com.example.financefox.R
import com.google.firebase.auth.FirebaseAuth
import com.example.financefox.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    // Binding connected to the specific layout of the fragment
    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth

    private val balanceViewModel: BalanceViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using data binding
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the NavController
        val navController = Navigation.findNavController(view)
        binding.editBalanceBtn.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_editBalanceFragment)
        }
        binding.callAddTransactionBtn.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_addTransactionFragment)
        }

        // Observe the balance and update the userBalance TextView
        balanceViewModel.balance.observe(viewLifecycleOwner, Observer { balance ->
            binding.userBalance.text = balance.amount.toString() + " €"

            if (balance.amount < 0) {
                binding.userBalance.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            }
            else {
                binding.userBalance.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
        })

        // Load the balance from Firestore
        balanceViewModel.loadBalanceFromFirestore()
    }
}