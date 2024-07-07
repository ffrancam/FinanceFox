package com.example.financefox.fragments

import android.app.DatePickerDialog
import android.widget.ArrayAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.financefox.CategoryViewModel
import com.example.financefox.Transaction
import com.example.financefox.TransactionViewModel
import com.example.financefox.BalanceViewModel
import com.example.financefox.R
import com.example.financefox.databinding.FragmentAddTransactionBinding
import com.example.financefox.databinding.FragmentCategoryBinding
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionFragment : Fragment() {

    //binding connected to the specific layout of the fragment
    private lateinit var binding: FragmentAddTransactionBinding
    private lateinit var selectedDate: Calendar

    // Use of viewModel among fragments to share data
    private val transactionViewModel : TransactionViewModel by activityViewModels()
    private val categoryViewModel : CategoryViewModel by activityViewModels()
    private val balanceViewModel : BalanceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using data binding
        binding = FragmentAddTransactionBinding.inflate(inflater, container, false)

        selectedDate = Calendar.getInstance()
        updateDateInView()

        binding.transactionDate.setOnClickListener {
            showDatePickerDialog()
        }

        categoryViewModel.categories.observe(viewLifecycleOwner) { categories ->
            val categoryNames = categories.map { it.name }.distinct().toMutableList()
            categoryNames.add(0, "--")
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.transactionCategory.adapter = adapter
        }

        categoryViewModel.loadCategoriesFromFirestore()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addTransactionBtn.setOnClickListener{

            var categoryName = binding.transactionCategory.selectedItem as String
            val amount = binding.transactionAmount.text.toString()
            val selectedRadioButton =
                view.findViewById<RadioButton>(binding.transactionType.checkedRadioButtonId)

            if (categoryName.isEmpty() || amount.isEmpty() || selectedRadioButton == null){
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Assign Category
                if (categoryName == "--") {
                    categoryName = "none"
                }
                // Convert amount to double
                val amountDouble = if (amount.isNotEmpty()) {
                    amount.toDouble()
                } else {
                    0.0
                }
                // convert type to boolean
                val type = if (selectedRadioButton.id == R.id.type_expense) {
                    true
                } else {
                    false
                }
                // Assign date
                val date = selectedDate.time

                // Add transaction
                transactionViewModel.addTransaction(categoryName, amountDouble, type, date)
                balanceViewModel.updateBalance(amountDouble, type, false)

                Toast.makeText(requireContext(), "Transaction Successfully Added", Toast.LENGTH_SHORT).show()

                // Get the NavController
                val navController = Navigation.findNavController(view)
                navController.navigate(R.id.action_addTransactionFragment_to_homeFragment)
            }
        }
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.maxDate = System.currentTimeMillis()
        datePicker.show()
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

        binding.transactionDate.setText(sdf.format(selectedDate.time))
    }
}