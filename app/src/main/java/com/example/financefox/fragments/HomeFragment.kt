package com.example.financefox.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.firebase.ui.auth.AuthUI
import com.google.firebase.firestore.FirebaseFirestore
import com.example.financefox.R
import com.example.financefox.Transaction
import com.example.financefox.TransactionViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.financefox.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    //binding connected to the specific layout of the fragment
    private lateinit var binding: FragmentHomeBinding
    private lateinit var auth: FirebaseAuth

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
        binding.callAddTransactionBtn.setOnClickListener{
            navController.navigate(R.id.action_homeFragment_to_addTransactionFragment)
        }
    }
}