package com.bbox.boxlife.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bbox.boxlife.data.Expense
import com.bbox.boxlife.data.ExpenseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class ExpensesViewModel(private val repository: ExpenseRepository) : ViewModel() {

    // Get all expenses
    val allExpenses: StateFlow<List<Expense>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Weekly total
    val weeklyTotal: StateFlow<Double> = allExpenses.map { expenses ->
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfWeek = calendar.time
        
        expenses.filter { it.date >= startOfWeek }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Statistics: List of (Category, Total Amount, Percentage)
    val statistics: StateFlow<List<Triple<String, Double, Float>>> = allExpenses.map { expenses ->
        val total = expenses.sumOf { it.amount }
        if (total == 0.0) return@map emptyList()

        expenses.groupBy { it.category }
            .map { (category, categoryExpenses) ->
                val categoryTotal = categoryExpenses.sumOf { it.amount }
                Triple(category, categoryTotal, (categoryTotal / total).toFloat())
            }
            .sortedByDescending { it.second }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addExpense(category: String, amount: Double) {
        viewModelScope.launch {
            repository.insert(Expense(date = Date(), category = category, amount = amount))
        }
    }
}

class ExpensesViewModelFactory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpensesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpensesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
