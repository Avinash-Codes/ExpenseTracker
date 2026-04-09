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

data class MonthBarEntry(val label: String, val value: Float)

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
    private val _showFavoritesOnly = MutableStateFlow(false)

    val timeFilter: StateFlow<TimeFilter> = _timeFilter.asStateFlow()
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly.asStateFlow()

    // Monthly transactions
    val transactions: StateFlow<List<Transaction>> = combine(
        _currentYear, _currentMonth, _timeFilter, _searchQuery, _showFavoritesOnly
    ) { year, month, filter, query, favOnly ->
        object {
            val year = year
            val month = month
            val filter = filter
            val query = query.trim().lowercase()
            val favOnly = favOnly
        }
    }.flatMapLatest { state ->
        val listFlow = when (state.filter) {
            TimeFilter.MONTHLY -> repository.getMonthlyTransactions(state.year, state.month)
            TimeFilter.WEEKLY -> repository.getWeeklyTransactions()
        }
        listFlow.map { list ->
            list
                .let { if (state.favOnly) it.filter { t -> t.isFavorited } else it }
                .let { if (state.query.isEmpty()) it else it.filter { t ->
                    t.category.lowercase().contains(state.query) ||
                    t.note.lowercase().contains(state.query)
                }}
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

    // All transactions (unfiltered) — used by edit screen and CSV export
    val allTransactions: StateFlow<List<Transaction>> = repository.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Favorites count — derived from allTransactions, drives badge dynamically
    val favoritesCount: StateFlow<Int> = allTransactions
        .map { list -> list.count { it.isFavorited } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Real monthly bar chart — last 6 months from Room
    val monthlyBarData: StateFlow<List<MonthBarEntry>> = flow {
        val monthNames = arrayOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
        val months = (5 downTo 0).map { offset ->
            val cal = Calendar.getInstance().apply { add(Calendar.MONTH, -offset) }
            Triple(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), monthNames[cal.get(Calendar.MONTH)])
        }
        // Combine 6 separate flows into one list
        combine(
            months.map { (y, m, _) -> repository.getMonthlyExpense(y, m) }
        ) { values ->
            months.mapIndexed { i, (_, _, label) ->
                MonthBarEntry(label, values[i].toFloat())
            }
        }.collect { emit(it) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setTimeFilter(filter: TimeFilter) {
        _timeFilter.value = filter
    }

    fun toggleFavoritesFilter() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
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

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }
}
