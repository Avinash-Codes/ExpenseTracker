package com.minor.expensetracker.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.minor.expensetracker.data.local.AppDatabase
import com.minor.expensetracker.data.local.PreferencesManager
import com.minor.expensetracker.data.model.TransactionType
import com.minor.expensetracker.ui.theme.ThemeState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesManager = PreferencesManager(application)
    private val transactionDao = AppDatabase.getInstance(application).transactionDao()

    val userName: StateFlow<String> = preferencesManager.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Alex")

    val userEmail: StateFlow<String> = preferencesManager.userEmail
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "alex@gmail.com")

    val isDarkMode: StateFlow<Boolean> = preferencesManager.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isLoggedIn: StateFlow<Boolean> = preferencesManager.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    /**
     * Financial Health Score (0-900) calculated from real data:
     * - Savings ratio (income vs expense): up to 400 points
     * - Consistency (having transactions): up to 200 points
     * - Expense diversity (using multiple categories): up to 150 points
     * - Positive balance: up to 150 points
     */
    val creditScore: StateFlow<Int> = combine(
        transactionDao.getTotalByType(TransactionType.INCOME.name),
        transactionDao.getTotalByType(TransactionType.EXPENSE.name),
        transactionDao.getAllTransactions()
    ) { totalIncome, totalExpense, transactions ->
        calculateFinancialHealthScore(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            transactionCount = transactions.size,
            uniqueCategories = transactions.map { it.category }.toSet().size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 300)

    init {
        viewModelScope.launch {
            preferencesManager.isDarkMode.collect { isDark ->
                ThemeState.isDarkMode = isDark
            }
        }
    }

    private fun calculateFinancialHealthScore(
        totalIncome: Double,
        totalExpense: Double,
        transactionCount: Int,
        uniqueCategories: Int
    ): Int {
        var score = 300 // Base score for having the app

        // 1. Savings ratio (up to 400 points)
        if (totalIncome > 0) {
            val savingsRatio = (totalIncome - totalExpense) / totalIncome
            score += (savingsRatio.coerceIn(0.0, 1.0) * 400).toInt()
        }

        // 2. Activity score — are you tracking? (up to 200 points)
        val activityPoints = when {
            transactionCount >= 20 -> 200
            transactionCount >= 10 -> 150
            transactionCount >= 5 -> 100
            transactionCount >= 1 -> 50
            else -> 0
        }
        score += activityPoints

        // 3. Diversification — tracking multiple categories (up to 150 points)
        score += (uniqueCategories * 25).coerceAtMost(150)

        // Penalty if spending > income
        if (totalExpense > totalIncome && totalIncome > 0) {
            score -= 100
        }

        return score.coerceIn(100, 900)
    }

    fun updateUserName(name: String) {
        viewModelScope.launch { preferencesManager.updateUserName(name) }
    }

    fun updateUserEmail(email: String) {
        viewModelScope.launch { preferencesManager.updateUserEmail(email) }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            val newValue = !ThemeState.isDarkMode
            ThemeState.isDarkMode = newValue
            preferencesManager.updateDarkMode(newValue)
        }
    }

    fun login(name: String, email: String) {
        viewModelScope.launch {
            preferencesManager.updateUserName(name)
            preferencesManager.updateUserEmail(email)
            preferencesManager.updateLoggedIn(true)
        }
    }

    fun logout() {
        viewModelScope.launch { preferencesManager.updateLoggedIn(false) }
    }
}
