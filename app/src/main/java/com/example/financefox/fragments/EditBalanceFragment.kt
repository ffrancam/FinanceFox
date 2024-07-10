package com.example.financefox.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.financefox.BalanceViewModel
import com.example.financefox.R
import com.example.financefox.databinding.FragmentEditBalanceBinding
import com.google.firebase.auth.FirebaseAuth

class EditBalanceFragment : Fragment() {

    // Binding connected to the specific layout of the fragment
    private lateinit var binding: FragmentEditBalanceBinding
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
        binding = FragmentEditBalanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newBalance.setText(balanceViewModel.getUserBalance().toString())

        binding.updateBalanceBtn.setOnClickListener {
            balanceViewModel.editBalance(binding.newBalance.text.toString().toDouble())
            // Get the NavController
            val navController = Navigation.findNavController(view)
            navController.navigate(R.id.action_editBalanceFragment_to_homeFragment)
        }
    }
}