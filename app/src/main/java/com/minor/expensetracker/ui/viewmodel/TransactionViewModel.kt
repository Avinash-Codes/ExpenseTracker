package com.minor.expensetracker.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.minor.expensetracker.data.local.AppDatabase
import com.minor.expensetracker.data.local.CategorySum
import com.minor.expensetracker.data.model.Transaction
import com.minor.expensetracker.data.model.TransactionType
import com.minor.expensetracker.data.repository.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class MonthlySummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0
)

enum class TimeFilter { WEEKLY, MONTHLY }

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository

    init {
        val dao = AppDatabase.getInstance(application).transactionDao()
        repository = TransactionRepository(dao)
    }

    private val _currentYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    private val _currentMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH))
    private val _timeFilter = MutableStateFlow(TimeFilter.MONTHLY)
    private val _searchQuery = MutableStateFlow("")

    val timeFilter: StateFlow<TimeFilter> = _timeFilter.asStateFlow()
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Monthly transactions
    val transactions: StateFlow<List<Transaction>> = combine(
        _currentYear, _currentMonth, _timeFilter, _searchQuery
    ) { year, month, filter, query ->
        // Combine inputs needed to generate our flow
        object {
            val year = year
            val month = month
            val filter = filter
            val query = query.trim().lowercase()
        }
    }.flatMapLatest { state ->
        val listFlow = when (state.filter) {
            TimeFilter.MONTHLY -> repository.getMonthlyTransactions(state.year, state.month)
            TimeFilter.WEEKLY -> repository.getWeeklyTransactions()
        }
        
        listFlow.map { list ->
            if (state.query.isEmpty()) list
            else list.filter { 
                it.category.lowercase().contains(state.query) || 
                it.note.lowercase().contains(state.query) 
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Monthly summary
    val monthlySummary: StateFlow<MonthlySummary> = combine(
        _currentYear, _currentMonth
    ) { year, month ->
        Pair(year, month)
    }.flatMapLatest { (year, month) ->
        combine(
            repository.getMonthlyIncome(year, month),
            repository.getMonthlyExpense(year, month)
        ) { income, expense ->
            MonthlySummary(
                totalIncome = income,
                totalExpense = expense,
                balance = income - expense
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MonthlySummary())

    // Category breakdown for current month
    val categoryBreakdown: StateFlow<List<CategorySum>> = combine(
        _currentYear, _currentMonth
    ) { year, month ->
        Pair(year, month)
    }.flatMapLatest { (year, month) ->
        repository.getMonthlyCategoryBreakdown(year, month, TransactionType.EXPENSE)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All-time totals
    val totalExpensesAllTime: StateFlow<Double> = repository.getTotalExpensesAllTime()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalIncomeAllTime: StateFlow<Double> = repository.getTotalIncomeAllTime()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val transactionCount: StateFlow<Int> = repository.getTransactionCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setTimeFilter(filter: TimeFilter) {
        _timeFilter.value = filter
    }

    fun addTransaction(
        amount: Double,
        type: TransactionType,
        category: String,
        note: String,
        date: Long
    ) {
        viewModelScope.launch {
            val transaction = Transaction(
                amount = amount,
                type = type,
                category = category,
                note = note,
                date = date
            )
            repository.addTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    fun deleteTransactionById(id: Long) {
        viewModelScope.launch {
            repository.deleteTransactionById(id)
        }
    }

    fun getCurrentMonthName(): String {
        val monthNames = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        return monthNames[_currentMonth.value]
    }

    fun navigateMonth(delta: Int) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, _currentYear.value)
            set(Calendar.MONTH, _currentMonth.value)
            add(Calendar.MONTH, delta)
        }
        _currentYear.value = cal.get(Calendar.YEAR)
        _currentMonth.value = cal.get(Calendar.MONTH)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(transaction: Transaction) {
        viewModelScope.launch {
            val updated = transaction.copy(isFavorited = !transaction.isFavorited)
            repository.updateTransaction(updated)
        }
    }
}
