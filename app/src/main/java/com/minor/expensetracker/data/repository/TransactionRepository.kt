package com.minor.expensetracker.data.repository

import com.minor.expensetracker.data.local.CategorySum
import com.minor.expensetracker.data.local.TransactionDao
import com.minor.expensetracker.data.model.Transaction
import com.minor.expensetracker.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.*

class TransactionRepository(private val dao: TransactionDao) {

    fun getAllTransactions(): Flow<List<Transaction>> = dao.getAllTransactions()

    fun getMonthlyTransactions(year: Int, month: Int): Flow<List<Transaction>> {
        val (start, end) = getMonthRange(year, month)
        return dao.getTransactionsByDateRange(start, end)
    }

    fun getWeeklyTransactions(): Flow<List<Transaction>> {
        val (start, end) = getCurrentWeekRange()
        return dao.getTransactionsByDateRange(start, end)
    }

    fun getMonthlyIncome(year: Int, month: Int): Flow<Double> {
        val (start, end) = getMonthRange(year, month)
        return dao.getTotalByTypeAndDateRange(TransactionType.INCOME, start, end)
    }

    fun getMonthlyExpense(year: Int, month: Int): Flow<Double> {
        val (start, end) = getMonthRange(year, month)
        return dao.getTotalByTypeAndDateRange(TransactionType.EXPENSE, start, end)
    }

    fun getMonthlyCategoryBreakdown(
        year: Int,
        month: Int,
        type: TransactionType
    ): Flow<List<CategorySum>> {
        val (start, end) = getMonthRange(year, month)
        return dao.getCategorySumsByTypeAndDateRange(type, start, end)
    }

    fun getTotalExpensesAllTime(): Flow<Double> = dao.getTotalExpensesAllTime()

    fun getTotalIncomeAllTime(): Flow<Double> = dao.getTotalIncomeAllTime()

    fun getTransactionCount(): Flow<Int> = dao.getTransactionCount()

    suspend fun addTransaction(transaction: Transaction): Long = dao.insert(transaction)

    suspend fun updateTransaction(transaction: Transaction) = dao.update(transaction)

    suspend fun deleteTransaction(transaction: Transaction) = dao.delete(transaction)

    suspend fun deleteTransactionById(id: Long) = dao.deleteById(id)

    suspend fun getTransactionById(id: Long): Transaction? = dao.getTransactionById(id)

    private fun getMonthRange(year: Int, month: Int): Pair<Long, Long> {
        val start = Calendar.getInstance().apply {
            set(year, month, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val end = Calendar.getInstance().apply {
            set(year, month, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MONTH, 1)
            add(Calendar.MILLISECOND, -1)
        }.timeInMillis

        return start to end
    }

    private fun getCurrentWeekRange(): Pair<Long, Long> {
        val start = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val end = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            add(Calendar.WEEK_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            add(Calendar.MILLISECOND, -1)
        }.timeInMillis

        return start to end
    }
}
