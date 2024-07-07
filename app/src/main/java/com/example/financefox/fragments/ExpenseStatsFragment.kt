package com.example.financefox.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.financefox.R
import com.example.financefox.TransactionViewModel
import com.example.financefox.databinding.FragmentAddTransactionBinding
import com.example.financefox.databinding.FragmentExpenseStatsBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlin.random.Random

class ExpenseStatsFragment : Fragment() {
    private lateinit var binding: FragmentExpenseStatsBinding
    private val transactionViewModel: TransactionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExpenseStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe changes in transactions LiveData
        transactionViewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            // Sum transaction amounts per category for type = true (spesa)
            val categorySumMap = mutableMapOf<String, Double>()
            transactions.filter { it.type }.forEach { transaction ->
                val category = transaction.category.takeIf { it.isNotBlank() } ?: "none"
                val currentSum = categorySumMap.getOrDefault(category, 0.0)
                categorySumMap[category] = currentSum + transaction.amount
            }

            // Log per verificare le categorie e il totale degli importi
            for ((category, totalAmount) in categorySumMap) {
                Log.d("StatsFragment", "Category: $category, Total Amount: $totalAmount")
            }

            // Prepare data for BarChart
            val entries = categorySumMap.keys.mapIndexed { index, category ->
                BarEntry(index.toFloat(), categorySumMap[category]!!.toFloat())
            }

            // Create BarDataSet with entries and random colors
            val dataSet = BarDataSet(entries, "Total Amount per Category (Spesa)").apply {
                colors = List(entries.size) { ColorTemplate.COLORFUL_COLORS.random() }
            }

            // Set chart properties
            val barData = BarData(dataSet)
            barData.barWidth = 0.9f // Set the bar width

            // Set data to chart
            binding.expenseBarChart.apply {
                data = barData
                setFitBars(true)
                invalidate() // refresh chart

                // Format x-axis to show category names
                xAxis.valueFormatter = IndexAxisValueFormatter(categorySumMap.keys.toList())
                xAxis.granularity = 1f
                xAxis.setDrawGridLines(false)
                xAxis.textSize = 14f

                // Format y-axis to show only integers
                axisLeft.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                        return value.toInt().toString()
                    }
                }
                axisLeft.granularity = 1f
                axisLeft.setDrawGridLines(false)
                axisLeft.textSize = 14f

                axisRight.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                        return value.toInt().toString()
                    }
                }
                axisRight.granularity = 1f
                axisRight.setDrawGridLines(false)
                axisRight.textSize = 14f // Set the text size of the y-axis labels
            }
        }
    }
}