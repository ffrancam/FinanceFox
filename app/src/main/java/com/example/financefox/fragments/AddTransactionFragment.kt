package com.example.financefox.fragments

import android.app.DatePickerDialog
import android.widget.ArrayAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.financefox.CategoryViewModel
import com.example.financefox.Transaction
import com.example.financefox.TransactionViewModel
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
            val categoryNames = categories.map { it.name }
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
            // Assign Category
            val categoryName = binding.transactionCategory.selectedItem as String
            val category = categoryViewModel.getCategoryByName(categoryName)
            // Convert amount to double
            val amount = binding.transactionAmount.text.toString()
            val amountDouble = if (amount.isNotEmpty()) {
                amount.toDouble()
            } else {
                0.0
            }
            // convert type to boolean
            val selectedRadioButton = view.findViewById<RadioButton>(binding.transactionType.checkedRadioButtonId)
            val type = if (selectedRadioButton.id == R.id.type_expense) {
                true
            } else {
                false
            }
            // Assign date
            val date = selectedDate.time
            transactionViewModel.addTransaction(Transaction(category, amountDouble, type, date))

            // Get the NavController
            val navController = Navigation.findNavController(view)
            binding.addTransactionBtn.setOnClickListener{
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
        datePicker.show()
    }

    private fun updateDateInView() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

        binding.transactionDate.setText(sdf.format(selectedDate.time))
    }
}