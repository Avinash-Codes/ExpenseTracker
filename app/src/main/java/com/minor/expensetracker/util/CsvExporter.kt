package com.minor.expensetracker.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.minor.expensetracker.data.model.Transaction
import com.minor.expensetracker.data.model.TransactionType
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object CsvExporter {

    fun exportAndShare(context: Context, transactions: List<Transaction>) {
        val file = buildCsvFile(context, transactions)
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "ExpenseTracker — transaction export")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Export transactions via"))
    }

    private fun buildCsvFile(context: Context, transactions: List<Transaction>): File {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val exportDate = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

        val cacheDir = File(context.cacheDir, "exports").also { it.mkdirs() }
        val file = File(cacheDir, "transactions_$exportDate.csv")

        FileWriter(file).use { writer ->
            // Header row
            writer.appendLine("Date,Type,Category,Amount (INR),Note")
            // Data rows — sorted newest first
            transactions
                .sortedByDescending { it.date }
                .forEach { tx ->
                    val date = dateFormatter.format(Date(tx.date))
                    val type = if (tx.type == TransactionType.INCOME) "Income" else "Expense"
                    val note = tx.note.replace(",", ";").replace("\n", " ")
                    writer.appendLine("$date,$type,${tx.category},${tx.amount},$note")
                }
        }
        return file
    }
}
